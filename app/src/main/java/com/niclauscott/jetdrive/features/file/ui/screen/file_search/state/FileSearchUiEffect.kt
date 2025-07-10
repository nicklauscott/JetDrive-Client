package com.niclauscott.jetdrive.features.file.ui.screen.file_search.state

import java.io.File

sealed interface FileSearchUiEffect  {
    data class ShowSnackBar(val message: String): FileSearchUiEffect
    data class PreviewFile(val mimeType: String, val file: File): FileSearchUiEffect
}