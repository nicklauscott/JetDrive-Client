package com.niclauscott.jetdrive.core.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenPairResponseDTO(
    val access: String,
    val refresh: String,
)
