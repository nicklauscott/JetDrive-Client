package com.niclauscott.jetdrive.core.splash.data.repository

import androidx.datastore.core.DataStore
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import com.niclauscott.jetdrive.core.splash.domain.model.constant.AuthValidationResponse
import com.niclauscott.jetdrive.core.splash.domain.model.dto.ValidateTokenRequestDTO
import com.niclauscott.jetdrive.core.splash.domain.repository.AuthValidationRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first

class AuthValidationRepositoryImpl(
    private val baseUrl: String, private val client: HttpClient,
    private val dataStore: DataStore<UserPreferences>
): AuthValidationRepository {
    override suspend fun validate(): AuthValidationResponse {
        return try {
            val userPreferences = dataStore.data.first()
            if (userPreferences.accessToken == null && userPreferences.refreshToken == null) {
                return AuthValidationResponse.ValidationFailed
            }

            val response = client.request("$baseUrl/auth/validate") {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(ValidateTokenRequestDTO(access = userPreferences.accessToken ?: ""))
            }

            if (response.status == HttpStatusCode.OK) {
                return AuthValidationResponse.ValidationSuccessful
            }
            else {
                return AuthValidationResponse.ValidationFailed
            }
        } catch (e: ResponseException) {
            AuthValidationResponse.ValidationFailed
        } catch (e: Exception) {
            AuthValidationResponse.NetworkFailed
        }
    }
}