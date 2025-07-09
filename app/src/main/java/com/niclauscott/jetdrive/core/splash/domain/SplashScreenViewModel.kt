package com.niclauscott.jetdrive.core.splash.domain

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.niclauscott.jetdrive.core.splash.domain.model.constant.AuthValidationResponse
import com.niclauscott.jetdrive.core.splash.domain.repository.AuthValidationRepository
import com.niclauscott.jetdrive.core.ui.navigation.LandingScreen
import com.niclauscott.jetdrive.core.ui.navigation.LoginScreen
import com.niclauscott.jetdrive.features.landing.ui.LandingScreen
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val authRepository: AuthValidationRepository
): ViewModel() {

    private val _screen = mutableStateOf<NavKey?>(null)
    val screen: State<NavKey?> get() = _screen

    val validationComplete: Boolean
        get() = _screen.value != null

    init {
        _screen.value = LandingScreen // Remove later
        //_screen.value = LoginScreen(null) // Remove later
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