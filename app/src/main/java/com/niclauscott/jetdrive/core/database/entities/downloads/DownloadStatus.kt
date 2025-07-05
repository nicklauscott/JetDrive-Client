package com.niclauscott.jetdrive.core.database.entities.downloads

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niclauscott.jetdrive.core.database.entities.TransferStatus
import java.util.UUID

@Entity
data class DownloadStatus(
    @PrimaryKey
    val id: UUID,
    val fileName: String,
    val fileSize: Long,
    val sizePerChunk: Long,
    val numberOfChunks: Int,
    val status: TransferStatus
)