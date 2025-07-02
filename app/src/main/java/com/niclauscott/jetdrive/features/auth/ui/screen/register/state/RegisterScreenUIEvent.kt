package com.niclauscott.jetdrive.features.auth.ui.screen.register.state

import com.niclauscott.jetdrive.features.auth.domain.model.dto.RegisterRequestDTO

sealed interface RegisterScreenUIEvent {
    data object Login: RegisterScreenUIEvent
    data class RegisterScreen(val request: RegisterRequestDTO): RegisterScreenUIEvent
}