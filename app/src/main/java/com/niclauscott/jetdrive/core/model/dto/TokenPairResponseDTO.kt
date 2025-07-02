package com.niclauscott.jetdrive.core.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenPairResponseDTO(
    val access: String,
    val refresh: String,
)
