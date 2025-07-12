package com.niclauscott.jetdrive.core.database.data.entities.downloads

import androidx.room.Embedded
import androidx.room.Relation
import com.niclauscott.jetdrive.core.database.data.entities.Transfer

data class DownloadStatusWithChunks(
    @Embedded val downloadStatus: DownloadStatus,

    @Relation(
        parentColumn = "id",
        entityColumn = "downloadId"
    )
    val chunks: List<ChunkDownloadStatus>
) {
    val isComplete: Boolean
        get() = chunks.size >= downloadStatus.numberOfChunks

    val downloadedBytes: Long
        get() = chunks.sumOf { it.downloadedBytes }
}
