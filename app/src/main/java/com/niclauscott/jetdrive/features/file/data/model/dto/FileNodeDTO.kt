package com.niclauscott.jetdrive.features.file.data.model.dto

import com.niclauscott.jetdrive.core.domain.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FileNodeDTO(
    val id: String,
    val name: String,
    val type: String,
    val size: Long,
    val parentId: String?,
    val hasThumbnail: Boolean,
    val mimeType: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime
)
