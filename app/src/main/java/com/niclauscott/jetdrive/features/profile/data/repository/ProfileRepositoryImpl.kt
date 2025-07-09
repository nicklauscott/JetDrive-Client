package com.niclauscott.jetdrive.features.profile.data.repository

import android.util.Log
import com.niclauscott.jetdrive.core.domain.dto.ErrorMessageDTO
import com.niclauscott.jetdrive.core.domain.dto.UserFileStatsResponseDTO
import com.niclauscott.jetdrive.core.domain.mapper.toUserFileStats
import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.domain.util.getNetworkErrorMessage
import com.niclauscott.jetdrive.features.profile.data.model.dto.UpdateUserRequestDTO
import com.niclauscott.jetdrive.features.profile.data.model.dto.UserResponseDTO
import com.niclauscott.jetdrive.features.profile.domain.constant.ProfileResponse
import com.niclauscott.jetdrive.features.profile.domain.mapper.toUser
import com.niclauscott.jetdrive.features.profile.domain.model.User
import com.niclauscott.jetdrive.features.profile.domain.repository.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class ProfileRepositoryImpl(baseUrl: String, private val client: HttpClient,
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
            Log.d(TAG("ProfileRepositoryImpl"), "getFileStats: $result")
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

    override suspend fun uploadProfilePhoto(): ProfileResponse<User> {
        return try {


            TODO()
        } catch (ex: Throwable) {
            ProfileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }
}