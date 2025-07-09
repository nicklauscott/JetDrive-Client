package com.niclauscott.jetdrive.core.http_client.token_provider

import com.niclauscott.jetdrive.core.http_client.model.RefreshRequestDTO
import com.niclauscott.jetdrive.core.domain.dto.TokenPairResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod

class TokenRefresher(
    private val tokenStorage: TokenStorage,
    private val tokenHolder: TokenHolder,
    private val baseUrl: String,
    private val client: HttpClient
) {

    suspend fun refreshAccessToken(): Boolean {
        val refreshToken = tokenHolder.getRefreshToken() ?: return false

        return try {
            val response = client.request("$baseUrl/auth/refresh") {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(RefreshRequestDTO(refreshToken))
            }

            val result = response.body<TokenPairResponseDTO>()
            tokenStorage.saveTokens(result.access, result.refresh)
            true
        } catch (e: Exception) {
            false
        }
    }
}
