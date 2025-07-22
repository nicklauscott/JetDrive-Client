package com.niclauscott.jetdrive.features.profile.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.niclauscott.jetdrive.core.domain.dto.ErrorMessageDTO
import com.niclauscott.jetdrive.core.domain.dto.UserFileStatsResponseDTO
import com.niclauscott.jetdrive.core.domain.mapper.toUserFileStats
import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.domain.util.getFileInfo
import com.niclauscott.jetdrive.core.domain.util.getNetworkErrorMessage
import com.niclauscott.jetdrive.features.profile.data.model.dto.UpdateUserRequestDTO
import com.niclauscott.jetdrive.features.profile.data.model.dto.UserResponseDTO
import com.niclauscott.jetdrive.features.profile.domain.constant.ProfileResponse
import com.niclauscott.jetdrive.features.profile.domain.mapper.toUser
import com.niclauscott.jetdrive.features.profile.domain.model.User
import com.niclauscott.jetdrive.features.profile.domain.repository.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlinx.io.IOException
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileRepositoryImpl(
    baseUrl: String, private val client: HttpClient, private val context: Context,
): ProfileRepository {
    private val fileUrl = "$baseUrl/files"
    private val userUrl = "$baseUrl/user"

    override suspend fun getProfile(): ProfileResponse<User> {
        return try {
            val response = client.request(userUrl) {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            }

            if (response.status != HttpStatusCode.OK) {
                val error = response.body<ErrorMessageDTO>()
                return ProfileResponse.Failure(error.message)
            }

            val result = response.body<UserResponseDTO>().toUser()

            ProfileResponse.Successful(result)
        } catch (ex: Throwable) {
            ProfileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun getFileStats(): ProfileResponse<UserFileStats> {
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
                return ProfileResponse.Failure(error.message)
            }

            val result = response.body<UserFileStatsResponseDTO>().toUserFileStats()
            ProfileResponse.Successful(result)
        } catch (ex: Throwable) {
            ProfileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    override suspend fun updateProfileName(firstName: String, lastName: String): ProfileResponse<User> {
        return try {
            val response = client.request(userUrl) {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(UpdateUserRequestDTO(email = null, firstName, lastName))
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return ProfileResponse.Failure(error.message)
            }

            val result = response.body<UserResponseDTO>().toUser()
            ProfileResponse.Successful(result)
        } catch (ex: Throwable) {
            ProfileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    @OptIn(InternalAPI::class)
    override suspend fun uploadProfilePhoto(uri: String): ProfileResponse<User> {
        return try {
            val fileBytes = context.contentResolver.openInputStream(uri.toUri())
                ?.getFileByteFromUri() ?: throw IOException("failed to read stream")
            val fileDetails = getFileInfo(uri, context)
            if ((fileDetails?.fileSize ?: 0L) > (1 * 1024 * 1024)) {
                return ProfileResponse.Failure("File size too large")
            }
            val response = client.post(userUrl) {
                body = MultiPartFormDataContent(
                    formData {
                        appendInput(
                            key = "file",
                            headers = Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=${fileDetails?.fileName}")
                            },
                            size = fileDetails?.fileSize ?: 0L,
                        ) { buildPacket { writeFully(fileBytes) } }
                    }
                )
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return ProfileResponse.Failure(error.message)
            }

            getProfile()
        } catch (ex: Throwable) {
            ProfileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    private fun InputStream.getFileByteFromUri(): ByteArray {
        val buffer = ByteArrayOutputStream()
        val data = ByteArray(1024)
        var count: Int
        while (read(data).also { count = it } != -1) { buffer.write(data, 0, count) }
        return buffer.toByteArray()
    }
}