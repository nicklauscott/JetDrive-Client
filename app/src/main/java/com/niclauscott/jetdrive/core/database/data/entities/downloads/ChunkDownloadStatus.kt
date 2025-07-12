package com.niclauscott.jetdrive.core.database.data.entities.downloads

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DownloadStatus::class,
            parentColumns = ["id"],
            childColumns = ["downloadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("downloadId")]
)
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

