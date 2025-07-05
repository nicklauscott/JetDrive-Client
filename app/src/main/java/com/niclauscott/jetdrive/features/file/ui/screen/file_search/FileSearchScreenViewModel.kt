package com.niclauscott.jetdrive.features.file.ui.screen.file_search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import com.niclauscott.jetdrive.features.file.ui.navigation.FileListScreen
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchScreenUiEvent
import com.niclauscott.jetdrive.features.file.ui.screen.file_search.state.FileSearchScreenUiState
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import kotlinx.coroutines.launch

class FileSearchScreenViewModel(
    private val backStack: NavBackStack,
    private val landingScreenViewModel: LandingScreenViewModel,
    private val fileRepository: FileRepository
): ViewModel() {

    private val _state: MutableState<FileSearchScreenUiState> = mutableStateOf(FileSearchScreenUiState())
    val state: State<FileSearchScreenUiState> = _state

    init {
        landingScreenViewModel.hideBottomBars()
    }

    fun onEvent(event: FileSearchScreenUiEvent) {
        when (event) {
            FileSearchScreenUiEvent.GoBack -> {
                landingScreenViewModel.showBottomBars()
                backStack.removeAt(backStack.lastIndex)
            }
            is FileSearchScreenUiEvent.OpenFileNode -> backStack.add(FileListScreen(event.fileNode))
            is FileSearchScreenUiEvent.Search -> search(event.query)
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

}