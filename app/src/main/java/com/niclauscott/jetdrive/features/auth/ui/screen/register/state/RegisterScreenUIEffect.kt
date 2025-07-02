package com.niclauscott.jetdrive.features.auth.ui.screen.register.state

sealed interface RegisterScreenUIEffect {
    data class ShowSnackbar(val message: String) : RegisterScreenUIEffect
}