package com.niclauscott.jetdrive.core.http_client.di

import androidx.datastore.core.DataStore
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import com.niclauscott.jetdrive.core.http_client.model.RefreshRequestDTO
import com.niclauscott.jetdrive.core.http_client.token_provider.AuthPlugin
import com.niclauscott.jetdrive.core.http_client.token_provider.DataStoreTokenStorage
import com.niclauscott.jetdrive.core.http_client.token_provider.TokenHolder
import com.niclauscott.jetdrive.core.http_client.token_provider.TokenRefresher
import com.niclauscott.jetdrive.core.http_client.token_provider.TokenStorage
import com.niclauscott.jetdrive.core.model.dto.TokenPairResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClientModule = module {

    factory<String> {
        //"http://localhost:8080"
        //"http://192.168.107.127:8080"

        val baseUrl = if ((1..10).random() % 2 == 0) "http://localhost:9001"
        else "http://127.0.0.1:9001"
        //baseUrl

        "http://192.168.39.127:9001"
        //"http://localhost:8080"
        //"http://10.0.2.2:9001"
    }

    single<TokenStorage> { DataStoreTokenStorage(get()) }

    single {
        TokenHolder(
            tokenStorage = get(),
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )
    }

    single {
        TokenRefresher(
            tokenStorage = get(),
            tokenHolder = get(),
            baseUrl = get(),
            client = HttpClient(Android)
        )
    }

    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(AuthPlugin) {
                tokenHolder = get()
                tokenRefresher = get()
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.SIMPLE
            }
        }
    }

}