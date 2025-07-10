package com.niclauscott.jetdrive.core.domain.mapper

import com.niclauscott.jetdrive.core.domain.dto.UserFileStatsResponseDTO
import com.niclauscott.jetdrive.features.file.domain.mapper.toFileNode
import com.niclauscott.jetdrive.core.domain.model.UserFileStats

fun UserFileStatsResponseDTO.toUserFileStats(): UserFileStats =
    UserFileStats(
        totalStorageSize, totalStorageUsed, totalFile, totalFolder, averageFileSize,
        largestFileSize, smallestFileSize, mostCommonMimeType, lastUpload, recentFiles?.map { it.toFileNode() }
    )