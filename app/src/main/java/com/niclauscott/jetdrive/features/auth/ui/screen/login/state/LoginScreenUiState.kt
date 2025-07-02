package com.niclauscott.jetdrive.features.auth.ui.screen.login.state

data class LoginScreenUiState(
    val isLoginIn: Boolean = false,
    val isLError: Boolean = false,
    val errorMessage: String? = null,
)
