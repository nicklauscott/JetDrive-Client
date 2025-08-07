package com.niclauscott.jetdrive.features.landing.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.sync.domain.service.FileSyncService
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.ui.navigation.FileListScreen
import com.niclauscott.jetdrive.features.landing.ui.state.LandingScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LandingScreenViewModel(
    syncService: FileSyncService,
    val rootBackStack: NavBackStack
): ViewModel() {

    private val _state: MutableState<LandingScreenUiState> = mutableStateOf(LandingScreenUiState())
    val state: State<LandingScreenUiState> = _state

    private val _showBottomBar = MutableStateFlow(true)
    val showBottomBar: StateFlow<Boolean> = _showBottomBar

    val fileScreenBackStack = NavBackStack()

    init {
        syncService.start()
        fileScreenBackStack.add(FileListScreen(FileNode()))
    }

    fun hideBottomBars() {
        _showBottomBar.value = false
    }

    fun showBottomBars() {
        _showBottomBar.value = true
    }

    fun changeScreen(screenIndex: Int) {
        _state.value = state.value.copy(currentScreen = screenIndex)
    }

}