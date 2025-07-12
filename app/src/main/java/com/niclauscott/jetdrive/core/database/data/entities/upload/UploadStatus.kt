package com.niclauscott.jetdrive.core.database.data.entities.upload

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niclauscott.jetdrive.core.database.data.entities.Transfer
import com.niclauscott.jetdrive.core.database.data.entities.TransferStatus
import java.util.UUID

@Entity
data class UploadStatus(
    @PrimaryKey
    val id: UUID,

    val uri: String,
    val fileName: String,
    val totalBytes: Long,
    val parentId: String? = null,

    val uploadId: UUID? = null,
    val chunkSize: Int = -1,

    val uploadedChunks: List<Int> = emptyList(),
    val uploadedBytes: Long = 0L,

    val status: TransferStatus = TransferStatus.PENDING,
    @ColumnInfo(name = "queue_position") val queuePosition: Int,
    val speed: Double = 0.0,
    val eta: Double = 0.0
): Transfer {
    override val transferId: UUID
        get() = id
    override val transferName: String
        get() = fileName
    override val transferQueuePosition: Int
        get() = queuePosition
}