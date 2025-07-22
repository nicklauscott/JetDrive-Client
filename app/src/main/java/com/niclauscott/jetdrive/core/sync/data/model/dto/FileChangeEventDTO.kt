package com.niclauscott.jetdrive.core.sync.data.model.dto

import com.niclauscott.jetdrive.core.domain.util.LocalDateTimeSerializer
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

@Serializable
data class FileChangeEventDTO(
    val fileId: String,
    val parentId: String?,
    val oldParentId: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val timeStamp: LocalDateTime,
    private val snapShotJson: String,
    val eventType: ChangeType
) {
    val fileNode = Json.decodeFromString<FileNodeDTO>(snapShotJson)
}

enum class ChangeType {
    CREATED, MODIFIED, DELETED, MOVED, COPIED
}

