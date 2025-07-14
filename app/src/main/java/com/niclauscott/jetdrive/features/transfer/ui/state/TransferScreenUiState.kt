package com.niclauscott.jetdrive.features.transfer.ui.state

import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import com.niclauscott.jetdrive.core.database.domain.model.Download
import com.niclauscott.jetdrive.core.database.domain.model.Upload

data class TransferScreenUiState(
    val isUploadTasksLoading: Boolean = false,
    val uploadTasks: List<Upload> = emptyList(),

    val isDownloadTasksLoading: Boolean = false,
    val downloadTasks: List<Download> = emptyList()
) {
    val isUploadsPaused: Boolean
        get() = uploadTasks.all { it.status == TransferStatus.PAUSED }

    val isDownloadsPaused: Boolean
        get() = uploadTasks.all { it.status == TransferStatus.PAUSED }

    val isAllTransferPaused: Boolean
        get() = isDownloadsPaused && isUploadsPaused
}