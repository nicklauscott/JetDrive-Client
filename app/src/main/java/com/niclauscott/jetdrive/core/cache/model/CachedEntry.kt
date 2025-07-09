package com.niclauscott.jetdrive.core.cache.model

import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import java.time.LocalDateTime

data class CachedEntry(
    val data: FileNode,
    val cachedAt: Long,
    val updatedAt: LocalDateTime
)

data class HomeCachedEntry(
    val data: UserFileStats,
    val cachedAt: Long,
    val updatedAt: LocalDateTime
)