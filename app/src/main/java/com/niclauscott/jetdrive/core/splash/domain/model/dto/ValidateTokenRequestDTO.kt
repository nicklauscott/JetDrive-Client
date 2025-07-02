package com.niclauscott.jetdrive.core.splash.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ValidateTokenRequestDTO(val access: String)