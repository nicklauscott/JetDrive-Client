package com.niclauscott.jetdrive.features.file.domain.model

import java.time.LocalDateTime

data class FileNodeTree(
    val parentId: String?,
    val updatedAt: LocalDateTime?,
    val children: List<FileNode>
)
