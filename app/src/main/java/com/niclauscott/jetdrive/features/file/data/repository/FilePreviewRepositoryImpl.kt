package com.niclauscott.jetdrive.features.file.data.repository

import android.content.Context
import com.niclauscott.jetdrive.core.cache.InMemoryCache
import com.niclauscott.jetdrive.core.domain.dto.ErrorMessageDTO
import com.niclauscott.jetdrive.core.domain.util.getNetworkErrorMessage
import com.niclauscott.jetdrive.features.file.data.model.dto.AudioMetadataResponseDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileUrlResponseDTO
import com.niclauscott.jetdrive.features.file.domain.constant.FileProgress
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.mapper.toAudioMetadata
import com.niclauscott.jetdrive.features.file.domain.model.AudioMetadata
import com.niclauscott.jetdrive.features.file.domain.repository.FilePreviewRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import kotlin.coroutines.coroutineContext

class FilePreviewRepositoryImpl(
    baseUrl: String,
    private val context: Context,
    private val client: HttpClient,
    private val unAuthClient: HttpClient,
    private val cache: InMemoryCache<String, FileUrlResponseDTO>
): FilePreviewRepository {

    private val fileUrl = "$baseUrl/files"

    override suspend fun getFileUri(fileId: String): FileResponse<String> {
        return try {
            val cached = cache.get(fileId)

            if (cached != null && cached.expiresAt.isAfter(LocalDateTime.now())) {
                return FileResponse.Successful(cached.url)
            }

            val response = client.request("$fileUrl/url/$fileId") {
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

            val result = response.body<FileUrlResponseDTO>()

            cache.put(fileId, result)

            FileResponse.Successful(result.url)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun getAudioMetadata(fileId: String): FileResponse<AudioMetadata> {
        return try {
            val response = client.request("$fileUrl/$fileId/metadata") {
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

            val result = response.body<AudioMetadataResponseDTO>().toAudioMetadata()
            FileResponse.Successful(result)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override fun downloadToCacheFile(url: String): Flow<FileProgress<File>> = flow {
        try {
            val response = unAuthClient.get(url)
            if (response.status != HttpStatusCode.OK) throw Exception("Failed: ${response.status}")

            val fileName = url.substringAfterLast("/")
                .take(60)
                .replace("%20", "_")
                .ifBlank { "temp_file" }

            val tempFile = File(context.cacheDir, fileName)
            val contentLength = response.contentLength()

            response.bodyAsChannel().copyToWithProgress(
                out = FileOutputStream(tempFile),
                totalBytes = contentLength
            ) { bytesCopied, percent ->
                emit(FileProgress.Loading(percent))
            }

            emit(FileProgress.Success(tempFile))

        } catch (ex: Throwable) {
            emit(FileProgress.Failure(getNetworkErrorMessage(ex)))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun ByteReadChannel.copyToWithProgress(
        out: OutputStream,
        totalBytes: Long?,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        onProgress: suspend (bytesCopied: Long, percent: Float?) -> Unit
    ): Long {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L

        while (!isClosedForRead) {
            val read = readAvailable(buffer, 0, bufferSize)
            if (read == -1) break

            withContext(Dispatchers.IO) {
                out.write(buffer, 0, read)
            }
            bytesCopied += read

            coroutineContext.ensureActive()

            val percent = totalBytes?.let {
                (bytesCopied.toFloat() / it).coerceIn(0f, 1f)
            }

            onProgress(bytesCopied, percent)
        }

        withContext(Dispatchers.IO) {
            out.flush()
        }
        return bytesCopied
    }

}