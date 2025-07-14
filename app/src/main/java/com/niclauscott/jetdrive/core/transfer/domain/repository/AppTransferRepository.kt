package com.niclauscott.jetdrive.core.transfer.domain.repository

import com.niclauscott.jetdrive.core.database.data.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import com.niclauscott.jetdrive.core.transfer.domain.model.IncompleteTransfer
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AppTransferRepository {
    fun getAllActiveTransferProgress(): Flow<Float?>
    fun getAllIncompleteTransfer(): Flow<List<IncompleteTransfer>>
    suspend fun getUploadStatusById(id: UUID): UploadStatus?
    suspend fun getDownloadStatusById(id: UUID): DownloadStatus?
    suspend fun saveUploadStatus(uploadStatus: UploadStatus): UploadStatus
    suspend fun saveDownloadStatus(downloadStatus: DownloadStatus): DownloadStatus
    suspend fun deleteUploadStatusById(id: UUID)
}