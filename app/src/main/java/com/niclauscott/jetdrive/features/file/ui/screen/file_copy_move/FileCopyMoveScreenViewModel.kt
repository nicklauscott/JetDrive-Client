package com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.sync.domain.service.FileEventManager
import com.niclauscott.jetdrive.core.sync.domain.service.FileSystemEvent
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIEffect
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_copy_move.state.FileCopyMoveScreenUIState
import com.niclauscott.jetdrive.features.file.ui.navigation.CopyMoveScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.Action
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FileCopyMoveScreenViewModel(
    private val fileNode: FileNode, private val folderId: String, title: String,
    private val action: Action,
    private val backStack: NavBackStack,
    private val landingScreenViewModel: LandingScreenViewModel,
    private val repository: FileRepository
): ViewModel() {
    private val syncFolderId = if (folderId == "-1") "root" else folderId

    private val _state: MutableState<FileCopyMoveScreenUIState> = mutableStateOf(FileCopyMoveScreenUIState(
        parentId = if (folderId == "-1") null else folderId,
        title = title, fileNode = fileNode
    ))
    val state: State<FileCopyMoveScreenUIState> = _state

    private val _effect: MutableSharedFlow<FileCopyMoveScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<FileCopyMoveScreenUIEffect> = _effect

    init {
        landingScreenViewModel.hideBottomBars()
        loadContents()

        repository.subScribe(syncFolderId) { files ->
            _state.value = state.value.copy(
                children = files.sortedByDescending { it.type.toString() }
                    .filter { it.type == FileNode.Companion.FileType.Folder }
            )
        }

        FileEventManager.event
            .filter { it isRelevantTo syncFolderId }
            .onEach { handleEvent(it, syncFolderId) }
            .launchIn(viewModelScope)
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
        }

    }

    private fun completeAction() {
        viewModelScope.launch {
            val response = if (action == Action.Move) {
                repository.moveFileNode(
                    fileId = fileNode.id,
                    newParentId = if (folderId == "-1") null else folderId
                )
            } else {
                repository.copyFileNode(
                    fileId = fileNode.id,
                    parentId = if (folderId == "-1") null else folderId
                )
            }
            if (response is FileResponse.Successful) {
                repository.invalidate(syncFolderId)
                repository.invalidate(fileNode.parentId ?: "root")
                FileEventManager.emit(FileSystemEvent.FileDelete(fileNode, fileNode.parentId))
                landingScreenViewModel.showBottomBars()
            } else if (response is FileResponse.Failure) {
                _effect.emit(FileCopyMoveScreenUIEffect.ShowSnackbar(response.message))
            }
            backStack.removeAll { it is CopyMoveScreen }
        }
    }

    private fun createNewFolder(folderName: String) {
        viewModelScope.launch {
            val response = repository.createFolder(
                name = folderName,
                parentId = state.value.parentId
            )

            if (response is FileResponse.Successful) {
                repository.invalidate(syncFolderId)
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

    private fun loadContents() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoadingFiles = true)
            val response = if (folderId == "-1") repository.getRootFiles()
            else repository.getChildren(folderId)

            if (response is FileResponse.Successful) {
                _state.value = state.value.copy(
                    isLoadingFiles = false,
                    children = response.data.sortedByDescending { it.type.toString() }
                        .filter { it.type == FileNode.Companion.FileType.Folder }
                )
            } else if (response is FileResponse.Failure) {
                _state.value = state.value.copy(
                    isLoadingFiles = false,
                    errorMessage = response.message
                )
            }
        }
    }

    private fun handleEvent(event: FileSystemEvent, folderId: String) {
        val rootFolder = if (folderId == "root") null else fileNode.id
        when (event) {
            is FileSystemEvent.FileCopied -> {
                if (event.newParentId == rootFolder)
                    _state.value = state.value.copy(children = state.value.children + event.fileNode)
            }
            is FileSystemEvent.FileCreated -> {
                if (event.parentId == rootFolder)
                    event.fileNode?.let { _state.value = state.value.copy(children = state.value.children + it) }

            }
            is FileSystemEvent.FileDelete -> {
                _state.value = state.value.copy(
                    children = state.value.children.filterNot { it.id == event.fileNode.id })
            }
            is FileSystemEvent.FileModified -> {
                _state.value = state.value.copy(
                    children = state.value.children.map {
                        if (it.id == event.fileNode.id) event.fileNode else it
                    }
                )
            }
            is FileSystemEvent.FileMoved -> {
                if (event.newParentId == rootFolder)
                    _state.value = state.value.copy(children = state.value.children + event.fileNode)
                if (event.oldParentId == rootFolder)
                    _state.value = state.value.copy(
                        children = state.value.children.filterNot { it.id == event.fileNode.id })
            }
        }
    }

    private infix fun FileSystemEvent.isRelevantTo(folderId: String): Boolean {
        val rootFolder = if (folderId == "root") null else fileNode.id
        return when(this) {
            is FileSystemEvent.FileCopied -> this.newParentId == rootFolder
            is FileSystemEvent.FileCreated -> this.parentId == rootFolder
            is FileSystemEvent.FileDelete -> this.parentId == rootFolder
            is FileSystemEvent.FileModified -> this.parentId == rootFolder
            is FileSystemEvent.FileMoved -> this.newParentId == rootFolder || this.oldParentId == rootFolder
        }
    }


}