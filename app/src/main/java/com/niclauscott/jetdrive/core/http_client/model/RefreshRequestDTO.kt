package com.niclauscott.jetdrive.core.http_client.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDTO(val refresh: String)
