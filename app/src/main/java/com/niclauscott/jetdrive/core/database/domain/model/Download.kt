package com.niclauscott.jetdrive.core.database.domain.model

import com.niclauscott.jetdrive.core.database.data.entities.TransferStatus
import java.util.UUID

data class Download(
    val id: UUID,
    val fileId: UUID,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val sizePerChunk: Long,
    val numberOfChunks: Int,
    val status: TransferStatus,
    var queuePosition: Int,
    val speed: Double,
    val eta: Double
)
