package com.niclauscott.jetdrive.core.splash.domain

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.niclauscott.jetdrive.core.splash.domain.repository.AuthValidationRepository
import com.niclauscott.jetdrive.core.ui.navigation.Landing
import com.niclauscott.jetdrive.core.ui.navigation.Login

class SplashScreenViewModel(
    private val authRepository: AuthValidationRepository
): ViewModel() {

    private val _screen = mutableStateOf<NavKey?>(null)
    val screen: State<NavKey?> get() = _screen

    val validationComplete: Boolean
        get() = _screen.value != null

    init {
        //_screen.value = Landing // Remove later
        _screen.value = Login(null) // Remove later
        /* // Uncomment later
        viewModelScope.launch {
            val response = authRepository.validate()
            _screen.value = when (response) {
                AuthValidationResponse.NetworkFailed -> LoginScreen(null)
                AuthValidationResponse.ValidationFailed -> LoginScreen(null)
                AuthValidationResponse.ValidationSuccessful -> LandingScreen
            }
        }
        */
    }

}