package com.niclauscott.jetdrive.features.file.ui.screen.file_list

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.CopyMoveViewModelRefreshRegistry
import com.niclauscott.jetdrive.features.file.ui.navigation.CopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.navigation.DetailScreen
import com.niclauscott.jetdrive.features.file.ui.navigation.FileListScreen
import com.niclauscott.jetdrive.features.file.ui.navigation.SearchScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileListViewModelRefreshRegistry
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUiState
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortOrder
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.SortType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class FileScreenViewModel(
    private val fileNode: FileNode,
    private val backStack: NavBackStack,
    private val fileRepository: FileRepository
): ViewModel() {

    private val _state: MutableState<FileScreenUiState> = mutableStateOf(FileScreenUiState(
        parentId = if (fileNode.id == "-1") null else fileNode.id, title = fileNode.name
    ))
    val state: State<FileScreenUiState> = _state

    private val _effect: MutableSharedFlow<FileScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<FileScreenUIEffect> = _effect

    init { loadContent() }

    fun onEvent(event: FileScreenUIEvent) {
        when (event) {
            is FileScreenUIEvent.Copy -> {
                backStack.add(
                    CopyMoveScreen(
                    fileNode = event.fileNode,
                    folderId = "-1",
                    folderName = "Choose folder",
                    action = Action.Copy
                )
                )
            }
            is FileScreenUIEvent.Delete -> delete(event.fileId)
            is FileScreenUIEvent.Download -> TODO()
            is FileScreenUIEvent.FileDetails -> {
                backStack.add(DetailScreen(event.fileNode))
            }
            FileScreenUIEvent.GoBack -> {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            }
            is FileScreenUIEvent.Move -> {
                backStack.add(
                    CopyMoveScreen(
                    fileNode = event.fileNode,
                    folderId = "-1",
                    folderName = "Choose folder",
                    action = Action.Move
                )
                )
            }
            is FileScreenUIEvent.OpenFileNode -> {
                if (FileListViewModelRefreshRegistry.shouldRefresh(folderId = fileNode.id)) {
                    FileListViewModelRefreshRegistry.markForRefresh(event.fileNode.id)
                }

                if (event.fileNode.type == FileNode.Companion.FileType.Folder) {
                    backStack.add(FileListScreen(event.fileNode))
                } else {
                    // Preview file
                }
            }
            is FileScreenUIEvent.Rename -> rename(event.fileId, event.newName)
            is FileScreenUIEvent.Sort -> sort(event.sortType, event.sortOrder)
            FileScreenUIEvent.RefreshData -> onAppear()
            is FileScreenUIEvent.CreateNewFile -> {}
            is FileScreenUIEvent.CreateNewFolder -> createNewFolder(event.folderName)
            FileScreenUIEvent.Search -> backStack.add(SearchScreen)
        }
    }

    private fun createNewFolder(folderName: String) {
        viewModelScope.launch {
            val response = fileRepository.createFolder(
                name = folderName,
                parentId = state.value.parentId
            )
            if (response is FileResponse.Successful) {
                CopyMoveViewModelRefreshRegistry.markForRefresh(state.value.parentId ?: "-1")
                loadContent(useCache = false)
            } else if (response is FileResponse.Failure) {
                _effect.emit(FileScreenUIEffect.ShowSnackBar(response.message))
            }
        }
    }

    private fun delete(fileId: String) {
        viewModelScope.launch {
            val response = fileRepository.deleteFileNode(fileId = fileId)
            if (response is FileResponse.Successful) {
                loadContent(false)
                FileListViewModelRefreshRegistry.markForRefresh(fileNode.parentId ?: "-1")
            } else if (response is FileResponse.Failure) {
                _effect.emit(FileScreenUIEffect.ShowSnackBar(response.message))
            }
        }
    }

    private fun rename(fileId: String, newName: String) {
        viewModelScope.launch {
            val response = fileRepository.renameFileNode(fileId = fileId, newName = newName)
            if (response is FileResponse.Successful) {
                loadContent(false)
                FileListViewModelRefreshRegistry.markForRefresh(fileNode.parentId ?: "-1")
            } else if (response is FileResponse.Failure){
                _effect.emit(FileScreenUIEffect.ShowSnackBar(response.message))
            }
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
        if (FileListViewModelRefreshRegistry.shouldRefresh(folderId = fileNode.id)) {
            FileListViewModelRefreshRegistry.markForRefresh(fileNode.parentId ?: "-1")
            FileListViewModelRefreshRegistry.clear(folderId = fileNode.id)
            loadContent(useCache = false)
        }
    }

    private fun loadContent(useCache: Boolean = true) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoadingFiles = true)
            val response = if (fileNode.id == "-1") fileRepository.getRootFiles(useCache)
            else fileRepository.getChildren(fileNode.id, null, useCache)

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