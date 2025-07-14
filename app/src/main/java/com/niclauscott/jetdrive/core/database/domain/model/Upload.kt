package com.niclauscott.jetdrive.core.database.domain.model

import com.niclauscott.jetdrive.core.database.domain.constant.Transfer
import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
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
    val eta: Double,
): Transfer {
    override val type: TransferType = TransferType.UPLOAD
}
