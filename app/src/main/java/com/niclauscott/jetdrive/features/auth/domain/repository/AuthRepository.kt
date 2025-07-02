package com.niclauscott.jetdrive.features.auth.domain.repository

import com.niclauscott.jetdrive.features.auth.domain.model.constant.AuthResponse
import com.niclauscott.jetdrive.features.auth.domain.model.dto.LoginRequestDTO
import com.niclauscott.jetdrive.features.auth.domain.model.dto.RegisterRequestDTO

interface AuthRepository {
    suspend fun login(loginRequestDTO: LoginRequestDTO): AuthResponse
    suspend fun register(registerRequestDTO: RegisterRequestDTO): AuthResponse
}