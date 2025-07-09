package com.niclauscott.jetdrive.features.file.ui.screen.file_detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state.FileDetailScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state.FileDetailScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_detail.state.FileDetailScreenUIState
import com.niclauscott.jetdrive.features.file.ui.navigation.CopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileListViewModelRefreshRegistry
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class FileDetailScreenViewModel(
    private val fileNode: FileNode,
    private val backStack: NavBackStack,
    private val landingScreenViewModel: LandingScreenViewModel,
    private val fileRepository: FileRepository
): ViewModel() {

    private val _state: MutableState<FileDetailScreenUIState> =
        mutableStateOf(FileDetailScreenUIState(fileNode = fileNode))
    val state: State<FileDetailScreenUIState> = _state

    private val _effect: MutableSharedFlow<FileDetailScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<FileDetailScreenUIEffect> = _effect

    init { landingScreenViewModel.hideBottomBars() }

    fun onEvent(event: FileDetailScreenUIEvent) {
        when (event) {
            is FileDetailScreenUIEvent.Copy -> {
                backStack.add(
                    CopyMoveScreen(
                    fileNode = fileNode,
                    folderId = "-1",
                    folderName = "Choose folder",
                    action = Action.Copy
                )
                )
            }
            is FileDetailScreenUIEvent.Delete -> delete(fileNode.id)
            is FileDetailScreenUIEvent.Download -> TODO()
            FileDetailScreenUIEvent.GoBack -> {
                backStack.removeAt(backStack.lastIndex)
                landingScreenViewModel.showBottomBars()
            }
            is FileDetailScreenUIEvent.Move -> {
                backStack.add(
                    CopyMoveScreen(
                    fileNode = fileNode,
                    folderId = "-1",
                    folderName = "Choose folder",
                    action = Action.Move
                )
                )
            }
            is FileDetailScreenUIEvent.Rename -> rename(fileNode.id, event.newName)
        }
    }

    private fun delete(fileId: String) {
        viewModelScope.launch {
            val response = fileRepository.deleteFileNode(fileId = fileId)
            if (response is FileResponse.Successful) {
                FileListViewModelRefreshRegistry.markForRefresh(fileNode.parentId ?: "-1")
                backStack.removeAt(backStack.lastIndex)
                landingScreenViewModel.showBottomBars()
            } else if (response is FileResponse.Failure) {
                _effect.emit(FileDetailScreenUIEffect.ShowSnackBar(response.message))
            }
        }
    }

    private fun rename(fileId: String, newName: String) {
        viewModelScope.launch {
            val response = fileRepository.renameFileNode(fileId = fileId, newName = newName)
            if (response is FileResponse.Successful) {
                _state.value = state.value.copy(fileNode = state.value.fileNode.copy(name = newName))
                FileListViewModelRefreshRegistry.markForRefresh(fileNode.parentId ?: "-1")
            } else if (response is FileResponse.Failure){
                _effect.emit(FileDetailScreenUIEffect.ShowSnackBar(response.message))
            }
        }
    }
}