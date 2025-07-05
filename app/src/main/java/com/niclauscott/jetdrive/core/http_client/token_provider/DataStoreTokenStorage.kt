package com.niclauscott.jetdrive.core.http_client.token_provider

import androidx.datastore.core.DataStore
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import kotlinx.coroutines.flow.first

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
}
