package com.niclauscott.jetdrive.features.file.ui.screen.file_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.util.TAG
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.CopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.DetailScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.component.FileListScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileListViewModelRefreshRegistry
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUiState
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortOrder
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortType
import kotlinx.coroutines.launch

class FileScreenViewModel(
    private val fileId: String,
    title: String,
    private val backStack: NavBackStack,
    private val fileRepository: FileRepository
): ViewModel() {

    private val _state: MutableState<FileScreenUiState> = mutableStateOf(FileScreenUiState(
        parentId = if (fileId == "-1") null else fileId,
        title = title
    ))
    val state: State<FileScreenUiState> = _state

    init { loadContent() }

    fun onEvent(event: FileScreenUIEvent) {
        Log.e(TAG("FileScreenViewModel"), "onEvent event: $event")
        when (event) {
            is FileScreenUIEvent.Copy -> {
                backStack.add(CopyMoveScreen(
                    fileNode = event.fileNode,
                    folderId = "-1",
                    folderName = "Choose folder",
                    action = Action.Copy
                ))
            }
            is FileScreenUIEvent.Delete -> TODO()
            is FileScreenUIEvent.Download -> TODO()
            is FileScreenUIEvent.FileDetails -> {
                backStack.add(DetailScreen(event.fileNode))
            }
            FileScreenUIEvent.GoBack -> {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            }
            is FileScreenUIEvent.Move -> {
                backStack.add(CopyMoveScreen(
                    fileNode = event.fileNode,
                    folderId = "-1",
                    folderName = "Choose folder",
                    action = Action.Move
                ))
            }
            is FileScreenUIEvent.OpenFileNode -> backStack.add(FileListScreen(event.fileId, title = event.title))
            is FileScreenUIEvent.Rename -> TODO()
            is FileScreenUIEvent.Sort -> sort(event.sortType, event.sortOrder)
            FileScreenUIEvent.RefreshData -> onAppear()
        }
    }

    private fun sort(sortType: SortType, sortOrder: SortOrder) {
        _state.value = state.value.copy(sortType = sortType, sortOrder = sortOrder)
        viewModelScope.launch {
            _state.value = state.value.copy(
                children = if (sortOrder == SortOrder.ASC) sortAsc(sortType = sortType)
                    else sortDesc(sortType = sortType)
            )
        }
    }

    private fun sortDesc(sortType: SortType): List<FileNode> {
        return state.value.children.sortedByDescending {
            when (sortType) {
                SortType.Name -> it.name
                SortType.Date -> it.createdDate
                SortType.Type -> it.type.toString()
                SortType.Size -> it.fileSize
            }
        }
    }

    private fun sortAsc(sortType: SortType): List<FileNode> {
        return state.value.children.sortedBy {
            when (sortType) {
                SortType.Name -> it.name
                SortType.Date -> it.createdDate
                SortType.Type -> it.type.toString()
                SortType.Size -> it.fileSize
            }
        }
    }

    private fun onAppear() {
        if (FileListViewModelRefreshRegistry.shouldRefresh(folderId = fileId)) {
            FileListViewModelRefreshRegistry.clear(folderId = fileId)
            loadContent(useCache = false)
        }
    }

    private fun loadContent(useCache: Boolean = true) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoadingFiles = true)

            val response = if (fileId == "-1") fileRepository.getRootFiles()
            else fileRepository.getChildren(fileId, null, useCache)

            if (response is FileResponse.Successful) {
                _state.value = state.value.copy(
                    isLoadingFiles = false,
                    children = response.data.sortedByDescending { it.type.toString() }
                )
            } else if (response is FileResponse.Failure) {
                _state.value = state.value.copy(
                    isLoadingFiles = false,
                    errorMessage = response.message
                )
            }
        }
    }
}