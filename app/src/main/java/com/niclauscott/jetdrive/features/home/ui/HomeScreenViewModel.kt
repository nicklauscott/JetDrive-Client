package com.niclauscott.jetdrive.features.home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.sync.domain.service.FileEventManager
import com.niclauscott.jetdrive.core.sync.domain.service.FileSystemEvent
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.ui.navigation.PreviewScreen
import com.niclauscott.jetdrive.features.home.domain.repository.HomeRepository
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUIEffect
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUIEvent
import com.niclauscott.jetdrive.features.home.ui.state.HomeScreenUiState
import com.niclauscott.jetdrive.features.landing.ui.navigation.Transfer
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@FlowPreview
class HomeScreenViewModel(
    private val backStack: NavBackStack,
    private val repository: HomeRepository
): ViewModel() {

    private val _state: MutableState<HomeScreenUiState> = mutableStateOf(HomeScreenUiState())
    val state: State<HomeScreenUiState> = _state

    private val _effect: MutableSharedFlow<HomeScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<HomeScreenUIEffect> = _effect

    val activeTransfer: StateFlow<Float?> = repository
        .getAllActiveTransferProgress()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0f
        )

    init {
        loadData()

        FileEventManager.event
            .debounce(500)
            .onEach { updateData() }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: HomeScreenUIEvent) {
        when (event) {
            is HomeScreenUIEvent.CreateNewFolder -> createNewFolder(event.folderName)
            is HomeScreenUIEvent.UploadFile -> uploadFile(event.uri)
            is HomeScreenUIEvent.OpenFileNode ->  backStack.add(PreviewScreen(event.fileNode))
            HomeScreenUIEvent.OpenTransferScreen -> { backStack.add(Transfer) }
        }
    }

    private fun uploadFile(uri: String) {
        viewModelScope.launch {
            repository.upload(uri)
            FileEventManager.emit(FileSystemEvent.FileCreated(null, null))
            repository.invalidate("root")
        }
    }

    private fun createNewFolder(folderName: String) {
        viewModelScope.launch {
            val response = repository.createFolder(name = folderName)
            if (response is FileResponse.Successful) {
                FileEventManager.emit(FileSystemEvent.FileCreated(response.data, null))
                repository.invalidate("root")
            } else if (response is FileResponse.Failure) {
                _effect.emit(HomeScreenUIEffect.ShowSnackBar(response.message))
            }
        }
    }

    private fun loadData() {
        _state.value = state.value.copy(isLoading = true)
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

    private suspend fun updateData() {
        val response = repository.getStats()
        if (response is FileResponse.Successful) {
            _state.value = state.value.copy(isLoading = false, data = response.data)
        }
    }

}