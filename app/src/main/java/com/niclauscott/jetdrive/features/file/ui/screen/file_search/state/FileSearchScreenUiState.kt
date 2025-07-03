package com.niclauscott.jetdrive.features.file.ui.screen.file_search.state

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

data class FileSearchScreenUiState(
    val isSearching: Boolean = false,
    val error: String? = null,
    val fileNodes: List<FileNode> = emptyList()
)
