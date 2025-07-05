package com.niclauscott.jetdrive.core.database.entities.upload

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niclauscott.jetdrive.core.database.entities.TransferStatus
import java.util.UUID

@Entity
data class UploadStatus(
    @PrimaryKey
    val id: UUID,
    val filePath: String,
    val uploadedChunks: List<Int>,
    val totalBytes: Long,
    val uploadedBytes: Long,
    val chunkSize: Int,
    val status: TransferStatus,
)