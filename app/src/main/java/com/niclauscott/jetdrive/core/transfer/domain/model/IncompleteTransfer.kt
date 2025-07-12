package com.niclauscott.jetdrive.core.transfer.domain.model

import com.niclauscott.jetdrive.core.database.data.entities.Transfer

data class IncompleteTransfer(
    val task: Transfer,
    val type: TransferType
) {
    enum class TransferType {
        UPLOAD, DOWNLOAD, BOTH, NONE
    }
}

