package com.niclauscott.jetdrive.features.file.data.model.dto

import com.niclauscott.jetdrive.core.domain.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FileUrlResponseDTO(
    val url: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val expiresAt: LocalDateTime
)
