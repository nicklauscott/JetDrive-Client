package com.niclauscott.jetdrive.features.landing.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object HomeScreen : NavKey
@Serializable data object FileScreen : NavKey
@Serializable data object ProfileScreen : NavKey
@Serializable data object DownloadUploadScreen : NavKey

