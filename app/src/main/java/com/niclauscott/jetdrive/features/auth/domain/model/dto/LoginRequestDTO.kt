package com.niclauscott.jetdrive.features.auth.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(val email: String, val password: String)