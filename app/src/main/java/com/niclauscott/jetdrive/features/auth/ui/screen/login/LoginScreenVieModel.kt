package com.niclauscott.jetdrive.features.auth.ui.screen.login

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import com.niclauscott.jetdrive.core.ui.navigation.Landing
import com.niclauscott.jetdrive.core.ui.navigation.Register
import com.niclauscott.jetdrive.features.auth.domain.model.constant.AuthResponse
import com.niclauscott.jetdrive.features.auth.domain.model.dto.LoginRequestDTO
import com.niclauscott.jetdrive.features.auth.domain.repository.AuthRepository
import com.niclauscott.jetdrive.features.auth.domain.repository.OAuthClient
import com.niclauscott.jetdrive.features.auth.ui.screen.login.state.LoginScreenUIEffect
import com.niclauscott.jetdrive.features.auth.ui.screen.login.state.LoginScreenUIEvent
import com.niclauscott.jetdrive.features.auth.ui.screen.login.state.LoginScreenUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LoginScreenVieModel(
    private val authRepository: AuthRepository,
    private val oAuthClient: OAuthClient,
    private val backStack: NavBackStack,
): ViewModel()  {

    private val _state: MutableState<LoginScreenUiState> = mutableStateOf(LoginScreenUiState())
    val state: State<LoginScreenUiState> = _state

    private val _effect: MutableSharedFlow<LoginScreenUIEffect> = MutableSharedFlow()
    val effect: SharedFlow<LoginScreenUIEffect> = _effect

    fun onEvent(event: LoginScreenUIEvent) {
        when (event) {
            is LoginScreenUIEvent.GoogleLoginScreen -> googleLogin(event.context)
            is LoginScreenUIEvent.LoginScreen -> login(event.email, event.password)
            LoginScreenUIEvent.Register -> backStack.add(Register)
        }
    }

    private fun googleLogin(context: Context) {
        viewModelScope.launch {
            val response = oAuthClient.login(context) {
                _state.value = state.value.copy(isLoginIn = true)
            }

            if (response is AuthResponse.LoginFailed) {
                _state.value = state.value.copy(
                    isLoginIn = false,
                    errorMessage = response.message
                )
                response.message?.let { _effect.emit(LoginScreenUIEffect.ShowSnackbar(it)) }
                return@launch
            }

            if (response == AuthResponse.LoginSuccessful) {
                backStack.clear()
                backStack.add(Landing)
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoginIn = true)
            val response = authRepository.login(LoginRequestDTO(email, password))
            if (response == AuthResponse.LoginSuccessful) {
                _state.value = state.value.copy(isLoginIn = false)
                backStack.clear()
                backStack.add(Landing)
            } else if (response is AuthResponse.LoginFailed) {
                _state.value = state.value.copy(isLoginIn = false, errorMessage = response.message)
                response.message?.let { _effect.emit(LoginScreenUIEffect.ShowSnackbar(it)) }
            }
        }
    }

}
