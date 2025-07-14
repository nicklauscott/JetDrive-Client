package com.niclauscott.jetdrive.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data class Login(val email: String? = null): NavKey
@Serializable data object Register : NavKey
@Serializable data object Landing : NavKey

