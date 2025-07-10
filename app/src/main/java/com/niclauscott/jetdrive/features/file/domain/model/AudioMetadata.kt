package com.niclauscott.jetdrive.features.file.domain.model

data class AudioMetadata(
    val title: String? = null,
    val artist: String? = null,
    val genre: String? = null,
    val durationInSeconds: Int = 0,
    val base64CoverArt: String? = null
)