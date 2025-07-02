package com.niclauscott.jetdrive.features.file.ui.screen.copy_move.state

import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action

sealed interface FileCopyMoveScreenUIEvent {
    data class OpenFolderNode(
        val fileNode: FileNode, val folderId: String,
        val folderName: String, val action: Action
    ): FileCopyMoveScreenUIEvent
    data class CreateNewFolder(val folderName: String): FileCopyMoveScreenUIEvent
    data object Complete: FileCopyMoveScreenUIEvent
    data object GoBack: FileCopyMoveScreenUIEvent
    data object Cancel: FileCopyMoveScreenUIEvent
    data object RefreshData: FileCopyMoveScreenUIEvent
}
