package com.niclauscott.jetdrive.features.file.ui.screen.file_search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.features.file.domain.constant.FileProgress
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FilePreviewRepository
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.domain.util.shouldOpenFile
import com.niclauscott.jetdrive.features.file.ui.navigation.FileListScreen
import com.niclauscott.jetdrive.features.file.ui.navigation.PreviewScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchScreenUiEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchScreenUiState
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchUiEffect
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class FileSearchScreenViewModel(
    private val backStack: NavBackStack,
    private val landingScreenViewModel: LandingScreenViewModel,
    private val fileRepository: FileRepository,
    private val preview: FilePreviewRepository
): ViewModel() {

    private var downloadJob: Job? = null

    private val _state: MutableState<FileSearchScreenUiState> = mutableStateOf(FileSearchScreenUiState())
    val state: State<FileSearchScreenUiState> = _state

    private val _effect: MutableSharedFlow<FileSearchUiEffect> = MutableSharedFlow()
    val effect: SharedFlow<FileSearchUiEffect> = _effect

    private val _downloadProgress = MutableStateFlow<FileProgress<File>>(FileProgress.Idle)
    val downloadProgress: StateFlow<FileProgress<File>> = _downloadProgress
    var mimeType = ""

    init {
        landingScreenViewModel.hideBottomBars()
    }

    fun onEvent(event: FileSearchScreenUiEvent) {
        when (event) {
            FileSearchScreenUiEvent.GoBack -> {
                landingScreenViewModel.showBottomBars()
                backStack.removeAt(backStack.lastIndex)
            }
            is FileSearchScreenUiEvent.OpenFileNode -> {
                _downloadProgress.update { FileProgress.Idle }
                if (event.fileNode.type == FileNode.Companion.FileType.Folder) {
                    backStack.add(FileListScreen(event.fileNode))
                    return
                }

                if (event.fileNode.type == FileNode.Companion.FileType.Folder) {
                    backStack.add(FileListScreen(event.fileNode))
                } else {
                    val shouldOpenFile = shouldOpenFile(event.fileNode)
                    if (shouldOpenFile == true) {
                        backStack.add(PreviewScreen(event.fileNode))
                        return
                    }

                    if (shouldOpenFile == false) {
                        getFileUriAndPreview(event.fileNode)
                    }
                }
            }
            is FileSearchScreenUiEvent.Search -> search(event.query)
            FileSearchScreenUiEvent.CancelDownload -> {
                downloadJob?.cancel()
                _downloadProgress.value = FileProgress.Idle
            }
        }
    }

    private fun search(query: String) {
        _state.value = state.value.copy(isSearching = true)
        viewModelScope.launch {
            val response = fileRepository.search(searchQuery = query)
            if (response is FileResponse.Successful) {
                _state.value = state.value.copy(isSearching = false, fileNodes = response.data)
            } else if (response is
                 FileResponse.Failure) {
                _state.value = state.value.copy(isSearching = false, error = response.message)
            }
        }
    }

    private fun getFileUriAndPreview(fileNode: FileNode) {
        viewModelScope.launch {
            val response = preview.getFileUri(fileNode.id)

            if (response is FileResponse.Failure) {
                _effect.emit(FileSearchUiEffect.ShowSnackBar(response.message))
                return@launch
            }

            if (response is FileResponse.Successful) {
                mimeType = fileNode.mimeType ?: ""
                startDownload(response.data)
            }
        }
    }

    private fun startDownload(url: String) {
        _downloadProgress.update { FileProgress.Loading(null) }
        downloadJob = viewModelScope.launch {
            preview.downloadToCacheFile(url)
                .collect { progress ->
                    _downloadProgress.value = progress
                }
        }
    }

}