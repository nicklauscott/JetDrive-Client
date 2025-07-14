package com.niclauscott.jetdrive.features.home.ui.state

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

sealed interface HomeScreenUIEvent {
    data class CreateNewFolder(val folderName: String): HomeScreenUIEvent
    data class UploadFile(val uri: String): HomeScreenUIEvent
    data class OpenFileNode(val fileNode: FileNode): HomeScreenUIEvent
    data object OpenTransferScreen: HomeScreenUIEvent
}