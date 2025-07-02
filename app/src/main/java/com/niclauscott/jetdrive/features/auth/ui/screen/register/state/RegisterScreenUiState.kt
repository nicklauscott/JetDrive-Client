package com.niclauscott.jetdrive.features.auth.ui.screen.register.state

data class RegisterScreenUiState(
    val isRegistering: Boolean = false,
    val successful: Boolean? = null,
    val errorMessage: String? = null,
)
