package com.niclauscott.jetdrive.core.domain.dto

import com.niclauscott.jetdrive.core.domain.util.LocalDateTimeSerializer
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeDTO
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
class UserFileStatsResponseDTO (
    val totalStorageSize: Long = 0,
    val totalStorageUsed: Long = 0,
    val totalFile: Int = 0,
    val totalFolder: Int = 0,
    val averageFileSize: Double = 0.0,
    val largestFileSize: Double = 0.0,
    val smallestFileSize: Double = 0.0,
    val mostCommonMimeType: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastUpload: LocalDateTime? = null,
    val recentFiles: List<FileNodeDTO>? = null
)