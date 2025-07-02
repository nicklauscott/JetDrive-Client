package com.niclauscott.jetdrive.features.file.ui.screen.file_list.state

sealed interface FileScreenUIEffect {
    data class ShowSnackBar(val message: String): FileScreenUIEffect
}
