package com.niclauscott.jetdrive.features.auth.ui.screen.login.state

sealed interface LoginScreenUIEffect {
    data class ShowSnackbar(val message: String) : LoginScreenUIEffect
}