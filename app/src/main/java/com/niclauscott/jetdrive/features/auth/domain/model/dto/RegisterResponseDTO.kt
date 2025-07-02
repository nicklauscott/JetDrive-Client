package com.niclauscott.jetdrive.features.auth.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(val email: String, val firstName: String, val lastName: String)