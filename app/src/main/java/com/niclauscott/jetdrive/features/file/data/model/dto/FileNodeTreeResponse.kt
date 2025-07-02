package com.niclauscott.jetdrive.features.file.data.model.dto

import com.niclauscott.jetdrive.core.model.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FileNodeTreeResponse(
    val parentId: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?,
    val children: List<FileNodeDTO>
)
