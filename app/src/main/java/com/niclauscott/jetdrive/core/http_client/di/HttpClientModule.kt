package com.niclauscott.jetdrive.core.http_client.di

import com.niclauscott.jetdrive.core.http_client.token_provider.AuthPlugin
import com.niclauscott.jetdrive.core.http_client.token_provider.DataStoreTokenStorage
import com.niclauscott.jetdrive.core.http_client.token_provider.TokenHolder
import com.niclauscott.jetdrive.core.http_client.token_provider.TokenRefresher
import com.niclauscott.jetdrive.core.http_client.token_provider.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val httpClientModule = module {

    factory<String> {
        val baseUrl = if ((1..10).random() % 2 == 0) "http://localhost:8001"
        else "http://127.0.0.1:8001"
        //baseUrl

        //"http://192.168.4.3:8001"
        "http://192.168.54.127:8001"
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

    single(named("unAuthClient")) {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
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
                level = LogLevel.NONE
                logger = Logger.SIMPLE
            }
        }
    }

}