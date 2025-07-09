package com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIState
import com.niclauscott.jetdrive.features.file.ui.navigation.CopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileListViewModelRefreshRegistry
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class FileCopyMoveScreenViewModel(
    private val fileNode: FileNode, private val folderId: String, title: String,
    private val action: Action,
    private val backStack: NavBackStack,
    private val landingScreenViewModel: LandingScreenViewModel,
    private val fileRepository: FileRepository
): ViewModel() {

    private val _state: MutableState<FileCopyMoveScreenUIState> = mutableStateOf(FileCopyMoveScreenUIState(
        parentId = if (folderId == "-1") null else folderId,
        title = title, fileNode = fileNode
    ))
    val state: State<FileCopyMoveScreenUIState> = _state

    private val _effect: MutableSharedFlow<FileCopyMoveScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<FileCopyMoveScreenUIEffect> = _effect

    init {
        loadContent()
        landingScreenViewModel.hideBottomBars()
    }

    fun onEvent(event: FileCopyMoveScreenUIEvent) {
        when(event) {
            FileCopyMoveScreenUIEvent.Complete -> completeAction()
            is FileCopyMoveScreenUIEvent.CreateNewFolder -> createNewFolder(event.folderName)
            FileCopyMoveScreenUIEvent.GoBack -> {
                backStack.removeAt(backStack.lastIndex)
            }
            is FileCopyMoveScreenUIEvent.OpenFolderNode -> backStack.add(
                CopyMoveScreen(
                    fileNode = event.fileNode, folderId = event.folderId,
                    folderName = event.folderName, action = event.action
                )
            )
            FileCopyMoveScreenUIEvent.Cancel -> backStack.removeAll { it is CopyMoveScreen }

            FileCopyMoveScreenUIEvent.RefreshData -> onAppear()
        }

    }

    private fun completeAction() {
        viewModelScope.launch {

            val response = if (action == Action.Move) {
                fileRepository.moveFileNode(
                    fileId = fileNode.id,
                    newParentId = if (folderId == "-1") null else folderId
                )
            } else {
                fileRepository.copyFileNode(
                    fileId = fileNode.id,
                    parentId = if (folderId == "-1") null else folderId
                )
            }

            if (response is FileResponse.Successful) {
                FileListViewModelRefreshRegistry.markForRefresh(folderId) // new folder
                // old folder
                FileListViewModelRefreshRegistry.markForRefresh(fileNode.parentId ?: "-1")
                backStack.removeAll { it is CopyMoveScreen }
                landingScreenViewModel.showBottomBars()
            } else if (response is FileResponse.Failure) {
                _effect.emit(FileCopyMoveScreenUIEffect.ShowSnackbar(response.message))
            }
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
                backStack.add(
                    CopyMoveScreen(
                        fileNode = fileNode, folderId = response.data.id,
                        folderName = response.data.name, action = action
                    )
                )
            } else if (response is FileResponse.Failure) {
                _effect.emit(FileCopyMoveScreenUIEffect.ShowSnackbar(response.message))
            }
        }
    }

    private fun onAppear() {
        if (CopyMoveViewModelRefreshRegistry.shouldRefresh(folderId = folderId)) {
            CopyMoveViewModelRefreshRegistry.clear(folderId = folderId)
            loadContent()
        }
    }

    private fun loadContent() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoadingFiles = true)

            val response = if (folderId == "-1") fileRepository.getRootFiles(false)
            else fileRepository.getChildren(folderId, null, true)

            if (response is FileResponse.Successful) {
                _state.value = state.value.copy(
                    isLoadingFiles = false,
                    children = response.data.filter { it.type == FileNode.Companion.FileType.Folder }
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