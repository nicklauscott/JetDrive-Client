package com.niclauscott.jetdrive.features.transfer.domain.repository

import com.niclauscott.jetdrive.core.database.domain.constant.Transfer
import com.niclauscott.jetdrive.core.database.domain.constant.TransferType
import com.niclauscott.jetdrive.core.database.domain.model.Download
import com.niclauscott.jetdrive.core.database.domain.model.Upload
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TransferRepository {
    suspend fun pauseAllTransfer()
    suspend fun startAllTransfer()
    suspend fun cancelAllTransfer()
    suspend fun cancelAllSpecificTransfer(type: TransferType)
    suspend fun pauseAllSpecificTransfer(type: TransferType)
    suspend fun startAllSpecificTransfer(type: TransferType)
    suspend fun toggleTransfer(transferId: UUID, type: TransferType)
    suspend fun cancelTransfer(transferId: UUID, type: TransferType)
    fun getAllIncompleteTransfer(): Flow<List<Transfer>>
    suspend fun updateUploadStatus(items: List<Upload>)
    suspend fun updateDownloadStatus(items: List<Download>)
}