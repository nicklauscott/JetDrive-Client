package com.niclauscott.jetdrive.features.file.ui.screen.copy_move.state

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

data class FileCopyMoveScreenUIState(
    val isLoadingFiles: Boolean? = null,
    val errorMessage: String? = null,
    val fileNode: FileNode,
    val parentId: String? = null,
    val title: String = "Drive",
    val children: List<FileNode> = emptyList(),
)
