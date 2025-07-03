package com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state

sealed interface FileDetailScreenUIEffect {
    data class ShowSnackBar(val message: String): FileDetailScreenUIEffect
}
