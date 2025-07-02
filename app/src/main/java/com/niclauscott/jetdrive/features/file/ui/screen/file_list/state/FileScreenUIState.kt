package com.niclauscott.jetdrive.features.file.ui.screen.file_list.state

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

data class FileScreenUiState(
    val isLoadingFiles: Boolean? = null,
    val errorMessage: String? = null,

    val parentId: String? = null,
    val title: String = "Drive",
    val children: List<FileNode> = emptyList(),

    val sortType: SortType = SortType.Name,
    val sortOrder: SortOrder = SortOrder.ASC
)
