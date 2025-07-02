package com.niclauscott.jetdrive.core.http_client.di

import android.util.Log
import androidx.datastore.core.DataStore
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import com.niclauscott.jetdrive.core.http_client.model.RefreshRequestDTO
import com.niclauscott.jetdrive.core.model.dto.TokenPairResponseDTO
import com.niclauscott.jetdrive.core.util.TAG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val httpClientModule = module {

    single {
        val dataStore: DataStore<UserPreferences> = get()
        val baseUrl: String = get()
        val unauthenticatedClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val tokens = dataStore.data.first()
                        BearerTokens(
                            tokens.accessToken ?: "",
                            tokens.refreshToken ?: ""
                        )
                    }

                    refreshTokens {
                        val tokens = dataStore.data.first()
                        if (tokens.refreshToken != null && tokens.refreshToken.isBlank()) {
                            return@refreshTokens null
                        }
                        try {
                            val response = unauthenticatedClient.request("$baseUrl/auth/refresh") {
                                method = HttpMethod.Post
                                headers {
                                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                }
                                setBody(RefreshRequestDTO(tokens.refreshToken ?: ""))
                            }

                            if (response.status != HttpStatusCode.OK) {
                                dataStore.updateData { UserPreferences("", "") }
                                return@refreshTokens null
                            }

                            val result = response.body<TokenPairResponseDTO>()
                            dataStore.updateData { UserPreferences(result.access, result.refresh) }
                            BearerTokens(accessToken = result.access, refreshToken = result.refresh)
                        } catch (_: Exception) { null }
                    }
                }
            }
        }
    }

}