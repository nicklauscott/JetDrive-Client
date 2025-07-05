package com.niclauscott.jetdrive.core.http_client.token_provider

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey

object AuthPlugin : HttpClientPlugin<AuthPlugin.Config, AuthPlugin> {
    class Config {
        lateinit var tokenStorage: TokenStorage
        lateinit var tokenRefresher: TokenRefresher
    }

    private lateinit var tokenStorage: TokenStorage
    private lateinit var tokenRefresher: TokenRefresher

    override val key: AttributeKey<AuthPlugin> = AttributeKey("AuthPlugin")

    override fun prepare(block: Config.() -> Unit): AuthPlugin {
        val config = Config().apply(block)
        tokenStorage = config.tokenStorage
        tokenRefresher = config.tokenRefresher
        return this
    }

    override fun install(plugin: AuthPlugin, scope: HttpClient) {
        scope.requestPipeline.intercept(HttpRequestPipeline.State) {
            val token = tokenStorage.getAccessToken()
            if (!token.isNullOrBlank()) {
                context.headers.remove(HttpHeaders.Authorization)
                context.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        scope.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
            try {
                proceed()
            } catch (e: ClientRequestException) {
                if (e.response.status == HttpStatusCode.Unauthorized) {
                    val refreshed = tokenRefresher.refreshAccessToken()
                    if (refreshed) {
                        val newToken = tokenStorage.getAccessToken()

                        // Rebuild the original request manually
                        val retryRequest = HttpRequestBuilder().apply {
                            takeFrom(context) // copies method, URL, headers, etc.

                            headers.remove(HttpHeaders.Authorization)
                            if (!newToken.isNullOrBlank()) {
                                headers.append(HttpHeaders.Authorization, "Bearer $newToken")
                            }
                        }

                        proceedWith(scope.request(retryRequest))
                    } else {
                        tokenStorage.clearTokens()
                        throw e
                    }
                } else {
                    throw e
                }
            }
        }

    }
}


