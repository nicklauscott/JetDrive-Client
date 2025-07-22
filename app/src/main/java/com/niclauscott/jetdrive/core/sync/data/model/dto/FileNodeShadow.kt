package com.niclauscott.jetdrive.core.sync.data.model.dto

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

data class FileNodeShadow(
    val fileNode: FileNode,
    val status: SyncStatus
)

enum class SyncStatus {
    SYNCED, PENDING_CREATE, PENDING_UPDATE, PENDING_DELETE, FAILED
}
