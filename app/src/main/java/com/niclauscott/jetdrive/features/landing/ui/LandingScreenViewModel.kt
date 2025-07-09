package com.niclauscott.jetdrive.features.landing.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.ui.navigation.FileListScreen
import com.niclauscott.jetdrive.features.landing.ui.state.LandingScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LandingScreenViewModel: ViewModel() {

    private val _state: MutableState<LandingScreenUiState> = mutableStateOf(LandingScreenUiState())
    val state: State<LandingScreenUiState> = _state

    // Get latest data from database
    private val _activeFileOperation: MutableState<Boolean>
        get() = mutableStateOf(true)
    val activeFileOperation: State<Boolean> = _activeFileOperation

    // Get latest progress from database data
    private val _activeFileOperationProgress: MutableState<Float>
        get() = mutableFloatStateOf(0.4f)
    val activeFileOperationProgress: State<Float> = _activeFileOperationProgress

    private val _showBottomBar = MutableStateFlow(true)
    val showBottomBar: StateFlow<Boolean> = _showBottomBar

    private val _showFab = MutableStateFlow(true)
    val showFab: StateFlow<Boolean> = _showFab

    val homeScreenBackStack = NavBackStack()
    val fileScreenBackStack = NavBackStack()
    val profileScreenBackStack = NavBackStack()

    init {
        fileScreenBackStack.add(FileListScreen(FileNode()))
    }

    fun hideBottomBars() {
        _showBottomBar.value = false
    }

    fun showBottomBars() {
        _showBottomBar.value = true
    }

}