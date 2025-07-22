package com.niclauscott.jetdrive.core.sync.domain.maper

import com.niclauscott.jetdrive.core.sync.data.model.dto.FileChangeEventDTO
import com.niclauscott.jetdrive.core.sync.domain.model.FileChangeEvent
import com.niclauscott.jetdrive.features.file.domain.mapper.toFileNode

fun FileChangeEventDTO.toModel(): FileChangeEvent =
    FileChangeEvent(
        fileId = fileId,
        parentId = parentId,
        oldParentId = oldParentId,
        timeStamp = timeStamp,
        fileNode = fileNode.toFileNode(),
        eventType = eventType
    )