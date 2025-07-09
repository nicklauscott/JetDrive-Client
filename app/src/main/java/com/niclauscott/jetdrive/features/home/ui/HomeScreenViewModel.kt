package com.niclauscott.jetdrive.features.home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileListViewModelRefreshRegistry
import com.niclauscott.jetdrive.features.home.domain.repository.HomeRepository
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUIEffect
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUIEvent
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val repository: HomeRepository
): ViewModel() {

    private val _state: MutableState<HomeScreenUiState> = mutableStateOf(HomeScreenUiState())
    val state: State<HomeScreenUiState> = _state

    private val _effect: MutableSharedFlow<HomeScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<HomeScreenUIEffect> = _effect

    val activeTransfer: StateFlow<Float> = repository
        .getAllActiveTransferProgress()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0f
        )

    init { loadData() }

    fun onEvent(event: HomeScreenUIEvent) {
        when (event) {
            is HomeScreenUIEvent.CreateNewFolder -> createNewFolder(event.folderName)
            HomeScreenUIEvent.UploadFile -> TODO()
        }
    }

    private fun createNewFolder(folderName: String) {
        viewModelScope.launch {
            val response = repository.createFolder(name = folderName)
            if (response is FileResponse.Successful) {
                FileListViewModelRefreshRegistry.markForRefresh("-1")
                loadData()
            } else if (response is FileResponse.Failure) {
                _effect.emit(HomeScreenUIEffect.ShowSnackBar(response.message))
            }
        }
    }

    private fun loadData() {
        _state.value = state.value.copy(isLoading = false)
        viewModelScope.launch {
            val response = repository.getStats()
            if (response is FileResponse.Failure) {
                _state.value = state.value.copy(isLoading = false, error = response.message)
                return@launch
            }

            if (response is FileResponse.Successful) {
                _state.value = state.value.copy(isLoading = false, data = response.data)
            }
        }
    }

}