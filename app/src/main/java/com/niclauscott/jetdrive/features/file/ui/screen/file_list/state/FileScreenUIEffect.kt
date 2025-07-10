package com.niclauscott.jetdrive.features.file.ui.screen.file_list.state

import java.io.File

sealed interface FileScreenUIEffect {
    data class ShowSnackBar(val message: String): FileScreenUIEffect
    data class PreviewFile(val mimeType: String, val file: File): FileScreenUIEffect
}
