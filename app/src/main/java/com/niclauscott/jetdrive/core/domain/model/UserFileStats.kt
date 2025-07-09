package com.niclauscott.jetdrive.core.domain.model

import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import java.time.LocalDateTime

data class UserFileStats (
    val totalStorageSize: Long = 0,
    val totalStorageUsed: Long = 0,
    val totalFile: Int = 0,
    val totalFolder: Int = 0,
    val averageFileSize: Double = 0.0,
    val largestFileSize: Double = 0.0,
    val smallestFileSize: Double = 0.0,
    val mostCommonMimeType: String? = null,
    val lastUpload: LocalDateTime? = null,
    val recentFiles: List<FileNode>? = null
)