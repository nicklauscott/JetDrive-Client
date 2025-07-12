package com.niclauscott.jetdrive.core.transfer.data

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.niclauscott.jetdrive.core.database.data.entities.TransferStatus
import com.niclauscott.jetdrive.core.database.data.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.transfer.data.service.DownloadService
import com.niclauscott.jetdrive.core.transfer.data.service.UploadService
import com.niclauscott.jetdrive.core.transfer.domain.model.IncompleteTransfer
import com.niclauscott.jetdrive.core.transfer.domain.model.NotificationProgress
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

object TransferManager: KoinComponent {

    private val baseUrl: String by inject()
    private val context: Context by inject()
    private val client: HttpClient by inject()
    private val repository: TransferRepository by inject()

    private val dbScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val progressScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val transfers = MutableStateFlow<List<IncompleteTransfer>>(emptyList())
    private val activeJobs = mutableMapOf<UUID, Pair<String, Job>>()

    private val _notificationProgress = MutableSharedFlow<NotificationProgress>(extraBufferCapacity = 64)
    val notificationProgress: SharedFlow<NotificationProgress> = _notificationProgress.asSharedFlow()

    fun init() {
        dbScope.launch {
            repository.getAllIncompleteTransfer().collect { list ->
                //Log.d(TAG("TransferManager"), "init: list: $list")
                transfers.value = list

                val prioritizedTransfer = list.minByOrNull { it.task.transferQueuePosition }
                val currentlyRunning = activeJobs.keys

                if (prioritizedTransfer != null && !currentlyRunning.contains(prioritizedTransfer.task.transferId)) {
                    val prioritizedType = prioritizedTransfer.type

                    // Find currently running job of the same type with lower priority
                    val lowerPrioritySameTypeJob = activeJobs.entries.find { (transferId, _) ->
                        val runningTransfer = list.find { it.task.transferId == transferId }
                        runningTransfer != null &&
                                runningTransfer.type == prioritizedType &&
                                runningTransfer.task.transferQueuePosition > prioritizedTransfer.task.transferQueuePosition
                    }

                    if (lowerPrioritySameTypeJob != null) {
                        // Cancel the lower-priority job of the same type
                        lowerPrioritySameTypeJob.value.second.cancel()
                        activeJobs.remove(lowerPrioritySameTypeJob.key)

                        processNextTransfer(prioritizedTransfer.type, list)
                    } else {
                        // No job to preempt — check for idle slots per type
                        val hasIdleUpload = list.any {
                            it.type == IncompleteTransfer.TransferType.UPLOAD &&
                                    !activeJobs.containsKey(it.task.transferId)
                        }
                        val hasIdleDownload = list.any {
                            it.type == IncompleteTransfer.TransferType.DOWNLOAD &&
                                    !activeJobs.containsKey(it.task.transferId)
                        }

                        if (hasIdleUpload || hasIdleDownload) {
                            processNextTransfer(getIdleType(hasIdleUpload, hasIdleDownload), list)
                        }
                    }
                }
            }
        }
        progressScope.launch { calculateTransferProgress() }
    }

    fun init1() {
        dbScope.launch {
            repository.getAllIncompleteTransfer().collect { list ->
                transfers.value = list

                val prioritizedTransfer = list.minByOrNull { it.task.transferQueuePosition }
                val currentlyRunning = activeJobs.keys

                // Check if a new transfer with higher priority is not running
                if (prioritizedTransfer != null && !currentlyRunning.contains(prioritizedTransfer.task.transferId)) {
                    // Cancel current job if it's lower priority
                    val lowerPriorityJob = activeJobs.entries.find {
                        val currentTransfer = list.find { t -> t.task.transferId == it.key }
                        currentTransfer != null &&
                                currentTransfer.task.transferQueuePosition > prioritizedTransfer.task.transferQueuePosition
                    }

                    if (lowerPriorityJob != null) {
                        // Cancel the lower-priority one
                        lowerPriorityJob.value.second.cancel()
                        activeJobs.remove(lowerPriorityJob.key)

                        // Restart the higher-priority one
                        processNextTransfer(prioritizedTransfer.type, list)
                    } else {
                        // No preemption needed, process normally
                        val hasIdleUpload = list.any {
                            it.type == IncompleteTransfer.TransferType.UPLOAD &&
                                    !activeJobs.containsKey(it.task.transferId)
                        }
                        val hasIdleDownload = list.any {
                            it.type == IncompleteTransfer.TransferType.DOWNLOAD &&
                                    !activeJobs.containsKey(it.task.transferId)
                        }

                        if (hasIdleUpload || hasIdleDownload) {
                            processNextTransfer(getIdleType(hasIdleUpload, hasIdleDownload), list)
                        }
                    }
                }
            }
        } // 2nd version
        dbScope.launch {
            repository.getAllIncompleteTransfer().collect { list ->
                transfers.value = list
                if (list.isNotEmpty()) {
                    val hasIdleUpload = list.any {
                        it.type == IncompleteTransfer.TransferType.UPLOAD &&
                           !activeJobs.containsKey(it.task.transferId)
                    }
                    val hasIdleDownload = list.any {
                        it.type == IncompleteTransfer.TransferType.DOWNLOAD &&
                           !activeJobs.containsKey(it.task.transferId)
                    }

                    if (hasIdleUpload || hasIdleDownload) {
                        processNextTransfer(getIdleType(hasIdleUpload, hasIdleDownload), list)
                    }
                }
            }
        } //  1st version
        progressScope.launch { calculateTransferProgress() }
    }

    private suspend fun calculateTransferProgress() {
        repository.getAllActiveTransferProgress().collect { progress ->
            val currentFiles = activeJobs.values.joinToString { "${it.first} " }
            _notificationProgress.emit(
                NotificationProgress("", currentFiles,
                    if (progress == null) null else (progress * 100).toInt()
                ))
        }
    }

    private fun processNextTransfer(
        isIdleType: IncompleteTransfer.TransferType, transfer: List<IncompleteTransfer>
    ) {
        Log.d(TAG("UploadService"), "processNextTransfer !!! isIdleType: $isIdleType")
        when(isIdleType) {
            IncompleteTransfer.TransferType.UPLOAD -> {
                transfer.find { it.type == IncompleteTransfer.TransferType.UPLOAD }?.let {
                    upload(it)
                }
            }
            IncompleteTransfer.TransferType.DOWNLOAD -> {
                transfer.find { it.type == IncompleteTransfer.TransferType.DOWNLOAD }?.let {
                    download(it)
                }
            }
            IncompleteTransfer.TransferType.BOTH -> {
                transfer
                    .find { it.type == IncompleteTransfer.TransferType.UPLOAD }
                    ?.let { upload(it) }
                transfer
                    .find { it.type == IncompleteTransfer.TransferType.DOWNLOAD }
                    ?.let { download(it) }
            }
            IncompleteTransfer.TransferType.NONE -> { }
        }
    }

    private fun upload(task: IncompleteTransfer) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val uploadTask = task.task as UploadStatus
                context.contentResolver.openInputStream(uploadTask.uri.toUri())?.use { stream ->
                    val uploadService = UploadService(
                        baseUrl = baseUrl, client = client,
                        upload = uploadTask, inputStream = stream,
                        repository = repository,
                    )
                    uploadService.init()
                }
            } catch (e: Exception) {
                val uploadStatus = repository.getUploadStatusById(task.task.transferId)
                uploadStatus?.let { repository.saveUploadStatus(it.copy(status = TransferStatus.FAILED)) }
            } finally {
                activeJobs.remove(task.task.transferId)
            }
        }
        activeJobs[task.task.transferId] = task.task.transferName to job
    }

    private fun download(task: IncompleteTransfer) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getDownloadStatusById(task.task.transferId)?.let {
                    val downloadService = DownloadService(
                        baseUrl = baseUrl, client = client,
                        context = context, downloadStatus = it,
                        repository = repository,
                    )
                    downloadService.init()
                }
            } catch (e: Exception) {
                Log.d(TAG("DownloadService"), "Transfer mng: download error: ${e.message}")
                val downloadStatus = repository.getDownloadStatusById(task.task.transferId)
                downloadStatus?.let {
                    repository.saveDownloadStatus(it.copy(status = TransferStatus.FAILED))
                }
            } finally {
                activeJobs.remove(task.task.transferId)
            }
        }
        activeJobs[task.task.transferId] = task.task.transferName to job
    }

    fun cancelAllTransfer() {
        progressScope.cancel()
        dbScope.cancel()
        activeJobs.values.forEach { it.second.cancel() }
    }

    private fun getIdleType(upload: Boolean, download: Boolean): IncompleteTransfer.TransferType  {
        if (upload && download) return IncompleteTransfer.TransferType.BOTH
        if (upload) return IncompleteTransfer.TransferType.UPLOAD
        if (download) return IncompleteTransfer.TransferType.DOWNLOAD
        return IncompleteTransfer.TransferType.NONE
    }

}
