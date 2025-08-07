package com.niclauscott.jetdrive.features.transfer.data.repository

import com.niclauscott.jetdrive.core.database.data.dao.TransferDao
import com.niclauscott.jetdrive.core.database.domain.constant.Transfer
import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import com.niclauscott.jetdrive.core.database.domain.mapper.toEntity
import com.niclauscott.jetdrive.core.database.domain.mapper.toModel
import com.niclauscott.jetdrive.core.database.domain.model.Download
import com.niclauscott.jetdrive.core.database.domain.model.Upload
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferServiceController
import com.niclauscott.jetdrive.features.transfer.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.UUID

class TransferRepositoryImpl(
    private val dao: TransferDao,
    private val serviceController: TransferServiceController
): TransferRepository {
    override suspend fun pauseAllTransfer() {
        val uploads = dao.getIncompleteUploads().first()
        uploads.forEach { dao.saveUploadStatus(it.copy(status = TransferStatus.PAUSED)) }
        val downloads = dao.getIncompleteDownloads().first()
        downloads.forEach { dao.saveDownloadStatus(it.copy(status = TransferStatus.PAUSED)) }
    }

    override suspend fun startAllTransfer() {
        val uploads = dao.getIncompleteUploads().first()
        uploads.forEach { dao.saveUploadStatus(it.copy(status = TransferStatus.PENDING)) }
        val downloads = dao.getIncompleteDownloads().first()
        downloads.forEach { dao.saveDownloadStatus(it.copy(status = TransferStatus.PENDING)) }
        serviceController.ensureServiceRunning()
    }

    override suspend fun cancelAllTransfer() {
        val uploads = dao.getIncompleteUploads().first()
        uploads.forEach { dao.deleteUploadById(it.id) }
        val downloads = dao.getIncompleteDownloads().first()
        downloads.forEach { dao.deleteDownloadById(it.id) }
    }

    override suspend fun pauseAllSpecificTransfer(type: TransferType) {
        if (type == TransferType.UPLOAD) {
            val uploads = dao.getIncompleteUploads().first()
            uploads.forEach { dao.saveUploadStatus(it.copy(status = TransferStatus.PAUSED)) }
        }

        val downloads = dao.getIncompleteDownloads().first()
        downloads.forEach { dao.saveDownloadStatus(it.copy(status = TransferStatus.PAUSED)) }
    }

    override suspend fun startAllSpecificTransfer(type: TransferType) {
        if (type == TransferType.UPLOAD) {
            val uploads = dao.getIncompleteUploads().first()
            uploads.forEach { dao.saveUploadStatus(it.copy(status = TransferStatus.PENDING)) }
        }

        val uploads = dao.getIncompleteDownloads().first()
        uploads.forEach { dao.saveDownloadStatus(it.copy(status = TransferStatus.PENDING)) }
        serviceController.ensureServiceRunning()
    }

    override suspend fun cancelAllSpecificTransfer(type: TransferType) {
        if (type == TransferType.UPLOAD) {
            val uploads = dao.getIncompleteUploads().first()
            uploads.forEach { dao.deleteUploadById(it.id) }
            return
        }

        val downloads = dao.getIncompleteDownloads().first()
        downloads.forEach { dao.deleteDownloadById(it.id) }
    }

    override suspend fun toggleTransfer(transferId: UUID, type: TransferType) {
        if (type == TransferType.UPLOAD) {
            val uploadStatus = dao.getUploadById(transferId)
            uploadStatus?.let {
                val status = when (it.status) {
                    in listOf(TransferStatus.PENDING , TransferStatus.ACTIVE) -> TransferStatus.PAUSED
                    else -> TransferStatus.PENDING
                }
                dao.saveUploadStatus(it.copy(status = status))
            }
            serviceController.ensureServiceRunning()
            return
        }

        val downloadStatus = dao.getDownloadStatus(transferId)
        downloadStatus?.let {
            val status = when (it.status) {
                in listOf(TransferStatus.PENDING , TransferStatus.ACTIVE) -> TransferStatus.PAUSED
                else -> TransferStatus.PENDING
            }
            dao.saveDownloadStatus(it.copy(status = status))
        }
        serviceController.ensureServiceRunning()
    }

    override suspend fun cancelTransfer(transferId: UUID, type: TransferType) {
        if (type == TransferType.UPLOAD) {
            val uploadStatus = dao.getUploadById(transferId)
            uploadStatus?.let {
                dao.saveUploadStatus(it.copy(status = TransferStatus.CANCELLED))
            }
            return
        }

        val downloadStatus = dao.getDownloadStatus(transferId)
        downloadStatus?.let {
            dao.saveDownloadStatus(it.copy(status = TransferStatus.CANCELLED))
        }
    }

    override fun getAllIncompleteTransfer(): Flow<List<Transfer>> {
        return combine(
            dao.getIncompleteDownloads(), dao.getIncompleteUploads()
        ) { downloads, uploads ->
            downloads.map { it.toModel() } + uploads.map { it.toModel() }
        }
    }

    override suspend fun updateUploadStatus(items: List<Upload>) {
        dao.updateUploadStatus(items.map { it.toEntity() })
    }

    override suspend fun updateDownloadStatus(items: List<Download>) {
        dao.updateDownloadStatus(items.map { it.toEntity() })
    }
}