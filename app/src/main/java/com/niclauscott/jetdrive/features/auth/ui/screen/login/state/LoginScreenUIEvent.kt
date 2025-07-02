package com.niclauscott.jetdrive.features.auth.ui.screen.login.state

import android.content.Context

sealed interface LoginScreenUIEvent {
    data class LoginScreen(val email: String, val password: String): LoginScreenUIEvent
    data class GoogleLoginScreen(val context: Context): LoginScreenUIEvent
    data object Register: LoginScreenUIEvent
}