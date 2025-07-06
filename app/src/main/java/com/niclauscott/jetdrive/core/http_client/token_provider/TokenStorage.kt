package com.niclauscott.jetdrive.core.http_client.token_provider

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()

    fun accessTokenFlow(): Flow<String?>
    fun refreshTokenFlow(): Flow<String?>
}
