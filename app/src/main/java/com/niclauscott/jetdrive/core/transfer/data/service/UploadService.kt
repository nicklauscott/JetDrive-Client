package com.niclauscott.jetdrive.core.transfer.data.service

import android.util.Log
import com.niclauscott.jetdrive.core.database.data.entities.TransferStatus
import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.transfer.data.model.dto.upload.UploadInitiateRequest
import com.niclauscott.jetdrive.core.transfer.data.model.dto.upload.UploadInitiateResponse
import com.niclauscott.jetdrive.core.transfer.data.model.dto.upload.UploadProgressResponse
import com.niclauscott.jetdrive.core.transfer.domain.repository.TransferRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class UploadService(
    baseUrl: String, private val client: HttpClient,
    upload: UploadStatus, private val inputStream: InputStream,
    private val repository: TransferRepository,
) {
    private val initiateUrl = "$baseUrl/files/upload/initiate"
    private val uploadChunkUrl: (String) -> String = { "$baseUrl/files/upload/$it" }
    private val statusUrl: (String) -> String = { "$baseUrl/files/upload/status/$it" }
    private val completeUrl: (String) -> String = { "$baseUrl/files/upload/$it/complete" }

    private var uploadStatus = upload
    private var run = AtomicBoolean(true)
    private val recentSpeed = mutableListOf<Double>()

    suspend fun init() {
        val initiateResponse: UploadInitiateResponse = initiateUpload()
        uploadStatus = repository.saveUploadStatus(uploadStatus.copy(
            uploadId = UUID.fromString(initiateResponse.uploadId),
            chunkSize = initiateResponse.chunkSize
        ))
        while (run.get()) {
            val status = getUploadStats(initiateResponse.uploadId)
            uploadOrResumeChunks(
                chunkSize = initiateResponse.chunkSize,
                uploadedChunks = status.uploadedChunks.toSet()
            )
        }
    }

    private fun calculateSpeed(elapsedSeconds: Double, chunkSize: Long): Double {
        return chunkSize / 1024.0 / 1024.0 / elapsedSeconds
    }

    private fun updateSpeedDisplay(speed: Double): Double {
        recentSpeed.add(speed)
        if (recentSpeed.size > 5) recentSpeed.removeAt(0)
        return if (recentSpeed.size < 2) speed else recentSpeed.average()
    }

    private fun eta(totalBytes: Long, uploadedBytes: Long, averageSpeedMBps: Double): Double {
        val remainingBytes = totalBytes - uploadedBytes
        return remainingBytes / (averageSpeedMBps * 1024 * 1024)
    }

    private suspend fun getUploadStats(uploadId: String): UploadProgressResponse {
        return client.get(statusUrl(uploadId)).body()
    }

    private suspend fun initiateUpload(): UploadInitiateResponse {
        val initiateResponse: UploadInitiateResponse = client.post(initiateUrl) {
            contentType(ContentType.Application.Json)
            setBody(UploadInitiateRequest(uploadStatus.fileName, uploadStatus.totalBytes, uploadStatus.parentId))
        }.body()
        return initiateResponse
    }

    private suspend fun uploadOrResumeChunks(chunkSize: Int, uploadedChunks: Set<Int> = emptySet()) {
        val inputStream = inputStream.buffered()
        val total = uploadStatus.totalBytes
        var start = 0L
        var chunkIndex = 1

        while (start < total) {
            val buffer = ByteArray(chunkSize)
            val read = withContext(Dispatchers.IO) { inputStream.read(buffer) }
            if (read == -1) break

            if (uploadedChunks.isNotEmpty() && uploadedChunks.contains(chunkIndex)) {
                start += read; chunkIndex++
                continue
            }

            val end = start + read - 1
            val actualChunk = buffer.copyOf(read)
            val startTime = System.nanoTime()

            val rangeHeader = "bytes $start-$end/$total"
            val response: HttpResponse = client.put(uploadChunkUrl(uploadStatus.uploadId.toString())) {
                header(HttpHeaders.ContentRange, rangeHeader)
                header(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                setBody(actualChunk)
            }
            if (!response.status.isSuccess()) continue

            val endTime = System.nanoTime()
            val elapsedTime = (endTime - startTime) / 1_000_000_000.0
            val speed = calculateSpeed(elapsedTime, chunkSize = actualChunk.size.toLong())
            if (response.status.isSuccess()) {
                val progress: UploadProgressResponse = response.body()
                val eta = eta(progress.totalBytes, progress.uploadedBytes, speed)
                uploadStatus = repository.saveUploadStatus(
                    uploadStatus.copy(
                        eta = eta, speed = updateSpeedDisplay(speed),
                        uploadedBytes = uploadStatus.uploadedBytes + actualChunk.size.toLong(),
                        uploadedChunks = uploadStatus.uploadedChunks + chunkIndex,
                        status = TransferStatus.ACTIVE
                    )
                )
            }
            start = end + 1
            chunkIndex++
        }

        val completeResponse = client.post(completeUrl(uploadStatus.uploadId.toString()))

        if (completeResponse.status == HttpStatusCode.PartialContent) return
        if (!completeResponse.status.isSuccess()) return

        if (completeResponse.status.isSuccess()) {
            Log.d(TAG("UploadService"), "uploadOrResumeChunks: completeResponse body: ${completeResponse.bodyAsText()}")
            repository.saveUploadStatus(uploadStatus.copy(status = TransferStatus.COMPLETED))
            run.set(false)
        }
    }

}