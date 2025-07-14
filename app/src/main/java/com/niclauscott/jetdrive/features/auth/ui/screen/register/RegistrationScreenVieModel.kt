package com.niclauscott.jetdrive.features.auth.ui.screen.register;

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.ui.navigation.Login
import com.niclauscott.jetdrive.features.auth.domain.model.constant.AuthResponse
import com.niclauscott.jetdrive.features.auth.domain.model.dto.RegisterRequestDTO
import com.niclauscott.jetdrive.features.auth.domain.repository.AuthRepository
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUIEffect
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUIEvent
import com.niclauscott.jetdrive.features.auth.ui.screen.register.state.RegisterScreenUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class RegistrationScreenVieModel(
    private val authRepository: AuthRepository,
    private val backStack: NavBackStack
): ViewModel()  {

    private val _state: MutableState<RegisterScreenUiState> = mutableStateOf(RegisterScreenUiState())
    val state: State<RegisterScreenUiState> = _state

    private val _effect: MutableSharedFlow<RegisterScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<RegisterScreenUIEffect> = _effect

    fun onEvent(event: RegisterScreenUIEvent) {
        when (event) {
            RegisterScreenUIEvent.Login -> {
                backStack.clear()
                backStack.add(Login(null))
            }
            is RegisterScreenUIEvent.RegisterScreen -> register(event.request)
        }
    }

    private fun register(request: RegisterRequestDTO) {
        viewModelScope.launch {
            _state.value = state.value.copy(isRegistering = true)
            val response = authRepository.register(request)
            if (response == AuthResponse.RegistrationSuccessful) {
                _effect.emit(RegisterScreenUIEffect.ShowSnackbar("Registration successful"))
                _state.value = state.value.copy(isRegistering = false, successful = true)
                backStack.clear()
                backStack.add(Login(request.email))
            } else if (response is AuthResponse.RegistrationFailure) {
                response.message?.let { _effect.emit(RegisterScreenUIEffect.ShowSnackbar(it)) }
                _state.value = state.value.copy(isRegistering = false, errorMessage = response.message)
            }
        }
    }

}
