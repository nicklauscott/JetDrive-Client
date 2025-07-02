package com.niclauscott.jetdrive.features.auth.google.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequestDTO(val accessToken: String)
