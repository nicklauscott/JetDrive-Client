package com.niclauscott.jetdrive.features.auth.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDTO(
    val email: String, val password: String,
    val firstName: String, val lastName: String
)