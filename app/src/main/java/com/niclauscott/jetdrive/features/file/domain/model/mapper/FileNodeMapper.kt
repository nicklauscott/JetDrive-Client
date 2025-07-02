package com.niclauscott.jetdrive.features.file.domain.model.mapper

import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeDTO
import com.niclauscott.jetdrive.features.file.domain.model.FileNode

fun FileNodeDTO.toFileNode(): FileNode {
    return FileNode(
        id = id,
        name = name,
        type = FileNode.toFileType(type),
        size = size,
        parentId = parentId,
        hasThumbnail = hasThumbnail,
        mimeType = mimeType,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}