package com.niclauscott.jetdrive.features.home.data.repository

import android.content.Context
import com.niclauscott.jetdrive.core.database.data.dao.TransferDao
import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import com.niclauscott.jetdrive.core.domain.dto.ErrorMessageDTO
import com.niclauscott.jetdrive.core.domain.dto.UserFileStatsResponseDTO
import com.niclauscott.jetdrive.core.domain.util.getNetworkErrorMessage
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeCreateRequestDTO
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.core.domain.mapper.toUserFileStats
import com.niclauscott.jetdrive.core.domain.util.getFileInfo
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferServiceController
import com.niclauscott.jetdrive.features.home.domain.repository.HomeRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class HomeRepositoryImpl(
    baseUrl: String,
    private val client: HttpClient,
    private val dao: TransferDao,
    private val context: Context,
    private val serviceController: TransferServiceController
): HomeRepository {

    private val fileUrl = "$baseUrl/files"

    override suspend fun getStats(): FileResponse<UserFileStats> {
        return try {
            val response = client.request("$fileUrl/stats") {
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

            val result = response.body<UserFileStatsResponseDTO>().toUserFileStats()

            FileResponse.Successful(result)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun createFolder(name: String): FileResponse<Unit> {
        return try {
            val response = client.request("$fileUrl/create") {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(FileNodeCreateRequestDTO(name = name, parentId = null))
            }

            if (response.status != HttpStatusCode.Created) {
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

    override suspend fun upload(uri: String) {
        val uriData = getFileInfo(uri, context)
        val maxQueuePosition = dao.getUploadMaxQueuePosition() ?: 0
        val uploadStatus = UploadStatus(
            id = UUID.randomUUID(),
            uri = uri,
            fileName = uriData?.fileName ?: "Unknown file",
            totalBytes = uriData?.fileSize ?: -1L,
            queuePosition = maxQueuePosition + 1
        )
        dao.saveUploadStatus(uploadStatus)
        serviceController.ensureServiceRunning()
    }

}