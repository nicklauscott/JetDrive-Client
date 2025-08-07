package com.niclauscott.jetdrive.features.profile.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.sync.domain.service.FileEventManager
import com.niclauscott.jetdrive.core.ui.navigation.Login
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUIEvent
import com.niclauscott.jetdrive.features.landing.ui.LandingScreenViewModel
import com.niclauscott.jetdrive.features.profile.domain.constant.ProfileResponse
import com.niclauscott.jetdrive.features.profile.domain.repository.ProfileRepository
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUIEffect
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUiEvent
import com.niclauscott.jetdrive.features.profile.ui.state.ProfileScreenUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@FlowPreview
class ProfileScreenViewModel(
    private val repository: ProfileRepository,
    private val landingScreenViewModel: LandingScreenViewModel
): ViewModel() {

    private val _state: MutableState<ProfileScreenUiState> = mutableStateOf(ProfileScreenUiState())
    val state: State<ProfileScreenUiState> = _state

    private val _effect: MutableSharedFlow<ProfileScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<ProfileScreenUIEffect> = _effect

    init {
        loadFileStats()
        loadUserData()

        FileEventManager.event
            .debounce(500)
            .onEach { updateData() }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ProfileScreenUiEvent) {
        when (event) {
            is ProfileScreenUiEvent.EditProfileName -> updateProfile(event.firstName, event.lastName)
            is ProfileScreenUiEvent.UploadProfilePicture -> uploadProfilePicture(event.uri)
            ProfileScreenUiEvent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            repository.logout()
            landingScreenViewModel.rootBackStack.clear()
            landingScreenViewModel.rootBackStack.add(Login(state.value.profileData?.email))
            landingScreenViewModel.changeScreen(0)
        }
    }

    private fun uploadProfilePicture(uri: String) {
        _state.value = state.value.copy(isProfilePictureUpdating = true)
        viewModelScope.launch {
            val response = repository.uploadProfilePhoto(uri)
            if (response is ProfileResponse.Failure) {
                _state.value = state.value.copy(isProfilePictureUpdating = false)
                _effect.emit(ProfileScreenUIEffect.ShowSnackBar(response.message))
                return@launch
            }

            if (response is ProfileResponse.Successful) {
                _state.value = state.value.copy(isProfilePictureUpdating = false, profileData = response.data)
            }
        }
    }

    private suspend fun updateData() {
        val response = repository.getFileStats()
        if (response is ProfileResponse.Successful) {
            _state.value = state.value.copy(statsData = response.data)
        }
    }

    private fun loadUserData() {
        _state.value = state.value.copy(isProfileLoading = true)
        viewModelScope.launch {
            val response = repository.getProfile()
            if (response is ProfileResponse.Failure) {
                _state.value = state.value.copy(isProfileLoading = false, profileError = response.message)
                return@launch
            }

            if (response is ProfileResponse.Successful) {
                _state.value = state.value.copy(isProfileLoading = false, profileData = response.data)
            }
        }
    }

    private fun loadFileStats() {
        _state.value = state.value.copy(isStatsLoading = true)
        viewModelScope.launch {
            val response = repository.getFileStats()
            if (response is ProfileResponse.Failure) {
                _state.value = state.value.copy(isStatsLoading = false, statsError = response.message)
                return@launch
            }

            if (response is ProfileResponse.Successful) {
                _state.value = state.value.copy(isStatsLoading = false, statsData = response.data)
            }
        }
    }

    private fun updateProfile(firstName: String, lastName: String) {
        _state.value = state.value.copy(isProfileUpdating = true)
        viewModelScope.launch {
            val response = repository.updateProfileName(firstName, lastName)

            if (response is ProfileResponse.Failure) {
                _state.value = state.value.copy(isProfileUpdating = false, profileError = response.message)
                _effect.emit(ProfileScreenUIEffect.ShowSnackBar(response.message))
                return@launch
            }

            if (response is ProfileResponse.Successful) {
                _state.value = state.value.copy(isProfileUpdating = false, profileData = response.data)
            }
        }
    }

}