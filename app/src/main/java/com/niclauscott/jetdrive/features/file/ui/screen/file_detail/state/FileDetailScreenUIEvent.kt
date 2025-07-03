package com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state

sealed interface FileDetailScreenUIEvent {
    data object GoBack: FileDetailScreenUIEvent
    data object Download: FileDetailScreenUIEvent
    data class Rename(val newName: String): FileDetailScreenUIEvent
    data object Move: FileDetailScreenUIEvent
    data object Copy: FileDetailScreenUIEvent
    data object Delete: FileDetailScreenUIEvent
}
