package com.niclauscott.jetdrive.features.file.ui.screen.file_preview.state

import android.content.Context

sealed interface FilePreviewUiEvent  {
    data object GoBack: FilePreviewUiEvent
    data object Release: FilePreviewUiEvent
    data object ConfigurationChanged: FilePreviewUiEvent
    data class InitializePlayer(val context: Context): FilePreviewUiEvent
}