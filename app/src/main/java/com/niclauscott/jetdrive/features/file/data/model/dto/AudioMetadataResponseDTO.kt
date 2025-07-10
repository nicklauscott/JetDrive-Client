package com.niclauscott.jetdrive.features.file.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AudioMetadataResponseDTO(
    val title: String? = null,
    val artist: String? = null,
    val genre: String? = null,
    val durationInSeconds: Int = 0,
    val base64CoverArt: String? = null
)