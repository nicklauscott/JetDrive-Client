package com.niclauscott.jetdrive.core.database.data.entities.downloads

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niclauscott.jetdrive.core.database.data.entities.Transfer
import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import java.util.UUID

@Entity
data class DownloadStatus(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    val fileId: UUID,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,

    val sizePerChunk: Long = -1,
    val numberOfChunks: Int = -1,
    val downloadedChunks: List<Int> = emptyList(),
    val downloadedBytes: Long = 0,
    val status: TransferStatus = TransferStatus.PENDING,

    @ColumnInfo(name = "queue_position")
    val queuePosition: Int,
    val speed: Double = 0.0,
    val eta: Double = 0.0
): Transfer {

    val isComplete: Boolean
        get() = downloadedBytes >= fileSize && downloadedChunks.size >= numberOfChunks

    override val transferId: UUID
        get() = id
    override val transferName: String
        get() = fileName
    override val transferQueuePosition: Int
        get() = queuePosition
}

