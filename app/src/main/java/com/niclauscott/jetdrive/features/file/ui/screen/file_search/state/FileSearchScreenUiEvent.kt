package com.niclauscott.jetdrive.features.file.ui.screen.file_search.state

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

sealed interface FileSearchScreenUiEvent {
    data object GoBack: FileSearchScreenUiEvent
    data class Search(val query: String): FileSearchScreenUiEvent
    data class OpenFileNode(val fileNode: FileNode): FileSearchScreenUiEvent
}
