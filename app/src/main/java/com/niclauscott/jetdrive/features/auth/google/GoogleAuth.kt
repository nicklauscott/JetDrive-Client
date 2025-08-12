package com.niclauscott.jetdrive.features.auth.google

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.datastore.core.DataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.niclauscott.jetdrive.R
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import com.niclauscott.jetdrive.core.domain.dto.TokenPairResponseDTO
import com.niclauscott.jetdrive.core.domain.util.TAG
import com.niclauscott.jetdrive.core.domain.util.getNetworkErrorMessage
import com.niclauscott.jetdrive.features.auth.domain.exception.OAuthClientException
import com.niclauscott.jetdrive.features.auth.domain.model.constant.AuthResponse
import com.niclauscott.jetdrive.features.auth.domain.repository.OAuthClient
import com.niclauscott.jetdrive.features.auth.google.dto.GoogleLoginRequestDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first

class GoogleAuth(
    private val baseUrl: String, private val client: HttpClient,
    private val clientId: String,
    private val dataStore: DataStore<UserPreferences>
): OAuthClient {

    private val request: (Context) -> GetCredentialRequest = { context ->
        GetCredentialRequest(
            listOf(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(clientId)
                    .build()
            )
        )
    }

    override suspend fun login(context: Context, jetDriveLogin: () -> Unit): AuthResponse {
        val idToken = try {
             getGoogleId(context)
        } catch (ex: OAuthClientException) {
            return AuthResponse.LoginFailed(ex.message)
        } catch (ex: Exception) {
            return AuthResponse.LoginFailed("Network error occurred.")
        }

        jetDriveLogin()
        return loginWithGoodId(idToken)
    }

    private suspend fun getGoogleId(context: Context): String {
        return try {
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context, request(context))
            val googleCredential = result.credential as? GoogleIdTokenCredential
            googleCredential?.idToken ?: throw OAuthClientException("Error login in with google.")
        } catch (ex: Exception) {
            when (ex) {
                is GetCredentialCancellationException -> {
                    throw OAuthClientException(null)
                }
                is GetCredentialProviderConfigurationException -> {
                    throw OAuthClientException(null)
                }
                else -> {
                    throw OAuthClientException("Network error occurred.")
                }
            }
        }
    }

    private suspend fun loginWithGoodId(idToken: String): AuthResponse {
        return try {
            val response = client.request("$baseUrl/auth/google/login") {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(GoogleLoginRequestDTO(idToken))
            }

            if (response.status != HttpStatusCode.OK) {
                val message = response.bodyAsText()
                return AuthResponse.LoginFailed(message)
            }
            val loginDTO = response.body<TokenPairResponseDTO>()
            dataStore.updateData { UserPreferences(loginDTO.access, loginDTO.refresh) }
            Log.d(TAG("GoogleAuth"), "loginWithGoodId: loginDTO: $loginDTO")
            Log.d(TAG("GoogleAuth"), "loginWithGoodId: from datastore: ${dataStore.data.first()}")
            AuthResponse.LoginSuccessful
        } catch (ex: ResponseException) {
            val errorMessage = when (val statusCode = ex.response.status) {
                HttpStatusCode.BadRequest -> "Invalid credentials"
                HttpStatusCode.InternalServerError -> "Server error, try again later"
                else -> "Login failed with status ${statusCode.value}"
            }
            AuthResponse.LoginFailed(errorMessage)
        } catch (ex: Throwable) {
            AuthResponse.LoginFailed(getNetworkErrorMessage(ex))
        }
    }

}