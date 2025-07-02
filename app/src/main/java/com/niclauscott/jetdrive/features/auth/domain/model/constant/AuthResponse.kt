package com.niclauscott.jetdrive.features.auth.domain.model.constant

interface AuthResponse {
    data class RegistrationFailure(val message: String? = null): AuthResponse
    data object RegistrationSuccessful: AuthResponse
    data object LoginSuccessful: AuthResponse
    data class LoginFailed(val message: String? = null): AuthResponse
}