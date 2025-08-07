package com.niclauscott.jetdrive.core.sync.domain.service

import android.util.Log
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.sync.data.model.dto.ChangeType
import com.niclauscott.jetdrive.core.sync.data.model.dto.FileChangeEventDTO
import com.niclauscott.jetdrive.core.sync.data.repository.FileSyncRepository
import com.niclauscott.jetdrive.core.sync.domain.maper.toModel
import com.niclauscott.jetdrive.core.sync.domain.model.FileChangeEvent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class FileSyncService(
    baseUrl: String,
    private val client: HttpClient,
    private val repository: FileSyncRepository
) {
    private val syncUrl = "$baseUrl/sync"
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastSync = LocalDateTime.now()

    fun start() {
        syncScope.launch {
            while (true) {
                try {
                    val response = client.request("$syncUrl/changes") {
                        method = HttpMethod.Get
                        headers {
                            append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            parameter("since", lastSync)
                        }
                    }

                    if (response.status == HttpStatusCode.OK) {
                        response.body<List<FileChangeEventDTO>>()
                            .forEach { FileEventManager.emit(mapToEvent(it.toModel())) }
                    }
                    lastSync = LocalDateTime.now()
                } catch (ex: Exception) {
                    Log.d(TAG("FileSyncService"), "start -> Error syncing file: ${ex.message}")
                }
                delay(10_000)
            }
        }
    }

    private fun mapToEvent(change: FileChangeEvent): FileSystemEvent {
        repository.invalidate(change.parentId ?: "root")
        repository.invalidate(change.oldParentId ?: "root")
        return when(change.eventType) {
            ChangeType.CREATED -> FileSystemEvent.FileCreated(change.fileNode, change.parentId)
            ChangeType.MODIFIED -> FileSystemEvent.FileModified(change.fileNode, change.parentId)
            ChangeType.DELETED -> FileSystemEvent.FileDelete(change.fileNode, change.parentId)
            ChangeType.COPIED -> FileSystemEvent.FileCopied(change.fileNode, change.parentId)
            ChangeType.MOVED -> FileSystemEvent.FileMoved(change.fileNode, change.oldParentId, change.parentId)
        }
    }

}