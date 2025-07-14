package com.niclauscott.jetdrive.core.database.domain.model

import com.niclauscott.jetdrive.core.database.domain.constant.Transfer
import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import java.util.UUID

data class Download(
    val id: UUID,
    val fileId: UUID,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val sizePerChunk: Long,
    val numberOfChunks: Int,
    val downloadedBytes: Long = 0,
    val status: TransferStatus,
    var queuePosition: Int,
    val speed: Double,
    val eta: Double
): Transfer {
    override val type: TransferType = TransferType.DOWNLOAD
}
