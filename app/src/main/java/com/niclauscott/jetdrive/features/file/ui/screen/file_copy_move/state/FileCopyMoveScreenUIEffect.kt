package com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state

sealed interface FileCopyMoveScreenUIEffect {
    data class ShowSnackbar(val message: String): FileCopyMoveScreenUIEffect
}
