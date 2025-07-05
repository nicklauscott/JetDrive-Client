package com.niclauscott.jetdrive.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data class LoginScreen(val email: String? = null): NavKey
@Serializable data object RegisterScreen : NavKey
@Serializable data object LandingScreen : NavKey

