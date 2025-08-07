package com.niclauscott.jetdrive.core.splash.domain

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.niclauscott.jetdrive.core.splash.domain.model.constant.AuthValidationResponse
import com.niclauscott.jetdrive.core.splash.domain.repository.AuthValidationRepository
import com.niclauscott.jetdrive.core.ui.navigation.Landing
import com.niclauscott.jetdrive.core.ui.navigation.Login
import com.niclauscott.jetdrive.features.file.ui.screen.file_list.state.FileScreenUIEffect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel(private val authRepository: AuthValidationRepository): ViewModel() {

    private val _screen = mutableStateOf<NavKey?>(null)
    val screen: State<NavKey?> get() = _screen

    private val _effect: MutableSharedFlow<SplashScreenEffect> = MutableSharedFlow()
    val effect: SharedFlow<SplashScreenEffect> = _effect

    val validationComplete: Boolean
        get() = _screen.value != null

    init {
        //_screen.value = Landing // Remove later
        //_screen.value = Login(null) // Remove later
        ///* // Uncomment later
        viewModelScope.launch {
            val response = authRepository.validate()
            _screen.value = when (response) {
                is AuthValidationResponse.NetworkFailed -> {
                    _effect.emit(SplashScreenEffect.ShowSnackbar(response.message))
                    null
                }
                AuthValidationResponse.ValidationFailed -> Login(null)
                AuthValidationResponse.ValidationSuccessful -> Landing
            }
        }
        //*/
    }

    sealed interface SplashScreenEffect {
        data class ShowSnackbar(val message: String): SplashScreenEffect
    }

}