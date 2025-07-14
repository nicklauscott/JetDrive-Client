package com.niclauscott.jetdrive.core.transfer.data.repository

import com.niclauscott.jetdrive.core.database.data.dao.TransferDao
import com.niclauscott.jetdrive.core.database.domain.constant.TransferStatus
import com.niclauscott.jetdrive.core.database.data.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import com.niclauscott.jetdrive.core.transfer.domain.model.IncompleteTransfer
import com.niclauscott.jetdrive.core.transfer.domain.repository.AppTransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class AppAppTransferRepositoryImpl(private val dao: TransferDao): AppTransferRepository {

    override fun getAllActiveTransferProgress(): Flow<Float?> {
        return combine(
            dao.getActiveAndPendingDownloads(),
            dao.getActiveAndPendingUploads()
        ) { downloads, uploads ->
            if (downloads.isEmpty() && uploads.isEmpty()) {
                null
            } else {
                val totalDownloadBytes = downloads.sumOf { it.fileSize }
                val totalDownloadedBytes = downloads.sumOf { it.downloadedBytes }

                val totalUploadBytes = uploads.sumOf { it.totalBytes }
                val totalUploadedBytes = uploads.sumOf { it.uploadedBytes }

                val totalBytes = totalUploadBytes + totalDownloadBytes
                val transferredBytes = totalUploadedBytes + totalDownloadedBytes

                if (totalBytes == 0L) 0f else transferredBytes.toFloat() / totalBytes
            }
        }
    }

    override fun getAllIncompleteTransfer(): Flow<List<IncompleteTransfer>> {
        return combine(dao.getActiveAndPendingDownloads(), dao.getActiveAndPendingUploads()) { downloads, uploads ->
            val activeDownloads = downloads.map { IncompleteTransfer(it, IncompleteTransfer.TransferType.DOWNLOAD) }

            val activeUploads = uploads.map { IncompleteTransfer(it, IncompleteTransfer.TransferType.UPLOAD) }
            activeUploads + activeDownloads
        }
    }

    override suspend fun getUploadStatusById(id: UUID): UploadStatus? {
        return dao.getUploadById(id)
    }

    override suspend fun getDownloadStatusById(id: UUID): DownloadStatus? {
        return dao.getDownloadStatus(id)
    }

    override suspend fun saveUploadStatus(uploadStatus: UploadStatus): UploadStatus {
        dao.saveUploadStatus(uploadStatus)
        return dao.getUploadById(uploadStatus.id) ?: uploadStatus
    }

    override suspend fun saveDownloadStatus(downloadStatus: DownloadStatus): DownloadStatus {
        dao.saveDownloadStatus(downloadStatus)
        return dao.getDownloadStatus(downloadStatus.id)  ?: downloadStatus
    }

    override suspend fun deleteUploadStatusById(id: UUID) {
        dao.deleteUploadById(id)
    }
}