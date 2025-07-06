package com.niclauscott.jetdrive.core.http_client.token_provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TokenHolder(
    private val tokenStorage: TokenStorage,
    coroutineScope: CoroutineScope
) {
    @Volatile
    private var accessToken: String? = null

    @Volatile
    private var refreshToken: String? = null

    init {
        coroutineScope.launch {
            tokenStorage.accessTokenFlow().collect {
                accessToken = it
            }
        }

        coroutineScope.launch {
            tokenStorage.refreshTokenFlow().collect {
                refreshToken = it
            }
        }
    }

    fun getAccessToken(): String? = accessToken

    fun getRefreshToken(): String? = refreshToken
}
