package com.niclauscott.jetdrive.core.splash.domain.model.constant

sealed interface AuthValidationResponse {
    data object ValidationFailed: AuthValidationResponse
    data object ValidationSuccessful: AuthValidationResponse
    data class NetworkFailed(val message: String): AuthValidationResponse
}