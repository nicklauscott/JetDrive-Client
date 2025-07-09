package com.niclauscott.jetdrive.features.profile.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDTO(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val picUrl: String?,
)
