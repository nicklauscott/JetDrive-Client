package com.niclauscott.jetdrive.core.database.domain.model

import com.niclauscott.jetdrive.core.database.data.entities.TransferStatus
import java.util.UUID

data class Upload(
    val id: UUID,
    val uri: String,
    val fileName: String,
    val uploadedChunks: List<Int>,
    val totalBytes: Long,
    val uploadedBytes: Long,
    val chunkSize: Int,
    val status: TransferStatus,
    var queuePosition: Int,
    val speed: Double,
    val eta: Double
)
