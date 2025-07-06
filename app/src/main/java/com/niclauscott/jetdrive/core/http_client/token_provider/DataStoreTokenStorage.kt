package com.niclauscott.jetdrive.core.http_client.token_provider

import androidx.datastore.core.DataStore
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreTokenStorage(
    private val dataStore: DataStore<UserPreferences>
) : TokenStorage {
    override suspend fun getAccessToken() = dataStore.data.first().accessToken
    override suspend fun getRefreshToken() = dataStore.data.first().refreshToken

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.updateData { UserPreferences(accessToken, refreshToken) }
    }

    override suspend fun clearTokens() {
        dataStore.updateData { UserPreferences(null, null) }
    }

    override fun accessTokenFlow(): Flow<String?> = dataStore.data.map { it.accessToken }
    override fun refreshTokenFlow(): Flow<String?> = dataStore.data.map { it.refreshToken }

}
