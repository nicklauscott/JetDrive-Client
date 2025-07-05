package com.niclauscott.jetdrive.core.database.entities.downloads

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class ChunkDownloadStatus(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val downloadId: UUID,
    val chunkIndex: Int,
    val expectedByte: Long,
    val downloadedBytes: Long
) {
    val isComplete: Boolean
        get() = downloadedBytes >= expectedByte
}

