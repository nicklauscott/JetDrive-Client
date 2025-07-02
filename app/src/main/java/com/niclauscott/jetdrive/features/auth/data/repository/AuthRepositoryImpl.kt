package com.niclauscott.jetdrive.features.auth.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.niclauscott.jetdrive.core.datastore.UserPreferences
import com.niclauscott.jetdrive.core.model.dto.TokenPairResponseDTO
import com.niclauscott.jetdrive.core.util.TAG
import com.niclauscott.jetdrive.features.auth.domain.model.constant.AuthResponse
import com.niclauscott.jetdrive.features.auth.domain.model.dto.LoginRequestDTO
import com.niclauscott.jetdrive.features.auth.domain.model.dto.RegisterRequestDTO
import com.niclauscott.jetdrive.features.auth.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class AuthRepositoryImpl(
    private val baseUrl: String,
    private val client: HttpClient,
    private val dataStore: DataStore<UserPreferences>
): AuthRepository {

    override suspend fun login(loginRequestDTO: LoginRequestDTO): AuthResponse {
        Log.d(TAG("AuthRepositoryImpl"), "login: address $baseUrl")
        return try {
            val response = client.request("$baseUrl/auth/login") {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(loginRequestDTO)
            }


            if (response.status != HttpStatusCode.OK) {
                val message = response.bodyAsText()
                return AuthResponse.LoginFailed(message)
            }

            val loginDTO = response.body<TokenPairResponseDTO>()
            dataStore.updateData { UserPreferences(loginDTO.access, loginDTO.refresh) }
            AuthResponse.LoginSuccessful
        } catch (e: ResponseException) {
            val errorMessage = when (val statusCode = e.response.status) {
                HttpStatusCode.BadRequest -> "Invalid credentials"
                HttpStatusCode.InternalServerError -> "Server error, try again later"
                else -> "Login failed with status ${statusCode.value}"
            }
            AuthResponse.LoginFailed(errorMessage)
        } catch (ex: ConnectTimeoutException) {
            AuthResponse.LoginFailed("Connection timeout. Try again")
        } catch (ex: Exception) {
            AuthResponse.LoginFailed("Google SignIn Failed. Check your internet connection")
        }
    }

    override suspend fun register(registerRequestDTO: RegisterRequestDTO): AuthResponse {
        return try {
            val response = client.request("$baseUrl/auth/register") {
                method = HttpMethod.Post
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(registerRequestDTO)
            }

            if (response.status != HttpStatusCode.OK) {
                val message = response.bodyAsText()
                return AuthResponse.RegistrationFailure(message)
            }

            //val responseDTO = response.body<RegisterResponseDTO>()
            AuthResponse.RegistrationSuccessful
        } catch (e: ResponseException) {
            val errorMessage = when (val statusCode = e.response.status) {
                HttpStatusCode.Conflict -> "Email already exist"
                HttpStatusCode.InternalServerError -> "Server error, try again later"
                else -> "Login failed with status ${statusCode.value}"
            }
            AuthResponse.RegistrationFailure(errorMessage)
        } catch (ex: ConnectTimeoutException) {
            AuthResponse.LoginFailed("Connection timeout. Try again")
        } catch (ex: Exception) {
            AuthResponse.LoginFailed("Google SignIn Failed. Check your internet connection")
        }
    }

}