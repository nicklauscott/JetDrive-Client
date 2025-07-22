package com.niclauscott.jetdrive.core.sync.domain.model

import com.niclauscott.jetdrive.core.sync.data.model.dto.ChangeType
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import java.time.LocalDateTime

data class FileChangeEvent(
    val fileId: String,
    val parentId: String?,
    val oldParentId: String?,
    val timeStamp: LocalDateTime,
    val fileNode: FileNode,
    val eventType: ChangeType
)