package com.niclauscott.jetdrive.features.file.domain.mapper

import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeTreeResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNodeTree

fun FileNodeTreeResponse.toFileNodeTree(): FileNodeTree {
    return FileNodeTree(
        parentId = parentId,
        updatedAt = updatedAt,
        children = children.map { it.toFileNode() }
    )
}