package com.niclauscott.jetdrive.features.file.data.repository

import android.content.Context
import android.util.Log
import com.niclauscott.jetdrive.core.cache.InMemoryCache
import com.niclauscott.jetdrive.core.cache.model.CachedEntry
import com.niclauscott.jetdrive.core.database.data.dao.TransferDao
import com.niclauscott.jetdrive.core.database.data.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import com.niclauscott.jetdrive.core.domain.dto.ErrorMessageDTO
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.domain.util.getFileInfo
import com.niclauscott.jetdrive.core.domain.util.getNetworkErrorMessage
import com.niclauscott.jetdrive.core.sync.data.repository.FileSyncRepository
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferServiceController
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeCopyRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeCreateRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeMoveRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeRenameRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeTreeResponse
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.mapper.toFileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.profile.domain.constant.ProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime
import java.util.UUID

class FileRepositoryImpl(
    private val baseUrl: String,
    private val client: HttpClient,
    private val dao: TransferDao,
    private val context: Context,
    private val syncRepository: FileSyncRepository,
    private val serviceController: TransferServiceController
): FileRepository {
    private val fileUrl = "$baseUrl/files"

    override suspend fun getRootFiles(): FileResponse<List<FileNode>> {
        return syncRepository.getFolder(null)
    }

    override suspend fun search(searchQuery: String): FileResponse<List<FileNode>> {
        return try {
            val response = client.request("$fileUrl/search/$searchQuery") {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            }

            if (response.status != HttpStatusCode.OK) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeTreeResponse>()
            val fileNodes = result.children.map { it.toFileNode() }
            FileResponse.Successful(fileNodes)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun getChildren(parentId: String): FileResponse<List<FileNode>> {
        return syncRepository.getFolder(parentId)
    }

    override fun invalidate(folderId: String) {
        syncRepository.invalidate(folderId)
    }

    override fun subScribe(folderId: String, onUpdate: (List<FileNode>) -> Unit) {
        syncRepository.subScribe(folderId, onUpdate)
    }

    override suspend fun createFolder(name: String, parentId: String?): FileResponse<FileNode> {
       return try {
           val response = client.request("$fileUrl/create") {
               method = HttpMethod.Post
               headers {
                   append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                   append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
               }
               setBody(FileNodeCreateRequestDTO(name = name, parentId = parentId))
           }

           if (response.status != HttpStatusCode.Created) {
               val error = response.body<ErrorMessageDTO>()
               return FileResponse.Failure(error.message)
           }

           val result = response.body<FileNodeDTO>()
           val fileNode = result.toFileNode()
           FileResponse.Successful(fileNode)
       } catch (ex: Throwable) {
           FileResponse.Failure(getNetworkErrorMessage(ex))
       }
    }

    override suspend fun renameFileNode(fileId: String, newName: String): FileResponse<Unit>  {
        return try {
            val response = client.request("$fileUrl/rename") {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(FileNodeRenameRequestDTO(fileId, newName))
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            FileResponse.Successful(Unit)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun copyFileNode(fileId: String, parentId: String?): FileResponse<FileNode>  {
        return try {
            val response = client.request("$fileUrl/copy") {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(FileNodeCopyRequestDTO(fileId, parentId))
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeDTO>()
            val fileNode = result.toFileNode()
            FileResponse.Successful(fileNode)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun moveFileNode(fileId: String, newParentId: String?): FileResponse<FileNode>  {
        return try {
            val response = client.request("$fileUrl/move") {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(FileNodeMoveRequestDTO(fileId, newParentId))
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeDTO>()
            val fileNode = result.toFileNode()
            FileResponse.Successful(fileNode)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun deleteFileNode(fileId: String): FileResponse<Unit>  {
        return try {
            val response = client.request("$fileUrl/delete/$fileId") {
                method = HttpMethod.Delete
            }

            if (response.status != HttpStatusCode.NoContent) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            FileResponse.Successful(Unit)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override fun getAllActiveTransferProgress(): Flow<Float?> {
        return combine(
            dao.getIncompleteDownloads(),
            dao.getIncompleteUploads()
        ) { downloads, uploads ->

            if (downloads.isEmpty() && uploads.isEmpty()) {
                null
            } else {
                val totalDownloadBytes = downloads.sumOf { it.fileSize }
                val totalDownloadedBytes = downloads.sumOf { it.downloadedBytes }

                val totalUploadBytes = uploads.sumOf { it.totalBytes }
                val totalUploadedBytes = uploads.sumOf { it.uploadedBytes }

                val totalBytes = totalUploadBytes + totalDownloadBytes
                val transferredBytes = totalUploadedBytes + totalDownloadedBytes

                if (totalBytes == 0L) 0f else transferredBytes.toFloat() / totalBytes
            }
        }
    }

    override suspend fun upload(uri: String, parentId: String?) {
        val uriData = getFileInfo(uri, context)
        val maxQueuePosition = dao.getUploadMaxQueuePosition() ?: 0
        val uploadStatus = UploadStatus(
            id = UUID.randomUUID(),
            uri = uri,
            fileName = uriData?.fileName ?: "Unknown file",
            totalBytes = uriData?.fileSize ?: -1L,
            parentId = parentId,
            queuePosition = maxQueuePosition + 1
        )
        dao.saveUploadStatus(uploadStatus)
        serviceController.ensureServiceRunning()
    }

    override suspend fun download(fileNode: FileNode) {
        val maxQueuePosition = dao.getUploadMaxQueuePosition() ?: 0
        val downloadStatus = DownloadStatus(
            fileId = UUID.fromString(fileNode.id),
            fileName = fileNode.name,
            fileSize = fileNode.size,
            mimeType = fileNode.mimeType ?: "unknown/unknown",
            queuePosition = maxQueuePosition + 1
        )
        dao.saveDownloadStatus(downloadStatus)
        serviceController.ensureServiceRunning()
    }
}