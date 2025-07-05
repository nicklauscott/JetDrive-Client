package com.niclauscott.jetdrive.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.niclauscott.jetdrive.core.database.entities.TransferStatus
import com.niclauscott.jetdrive.core.database.entities.downloads.DownloadStatusWithChunks
import com.niclauscott.jetdrive.core.database.entities.upload.UploadStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TransferDao {

    // ========== DOWNLOADS ==========

    // 1. Get all incomplete downloads
    @Transaction
    @Query("""
        SELECT * FROM DownloadStatus 
        WHERE id IN (
            SELECT downloadId FROM ChunkDownloadStatus 
            GROUP BY downloadId 
            HAVING COUNT(*) < (SELECT numberOfChunks FROM DownloadStatus WHERE id = downloadId)
        )
    """)
    fun getAllIncompleteDownloads(): Flow<List<DownloadStatusWithChunks>>

    // 2. Get all complete downloads
    @Transaction
    @Query("""
        SELECT * FROM DownloadStatus 
        WHERE id IN (
            SELECT downloadId FROM ChunkDownloadStatus 
            GROUP BY downloadId 
            HAVING COUNT(*) >= (SELECT numberOfChunks FROM DownloadStatus WHERE id = downloadId)
        )
    """)
    fun getAllCompleteDownloads(): Flow<List<DownloadStatusWithChunks>>

    // 3. Delete a download by ID
    @Query("DELETE FROM DownloadStatus WHERE id = :id")
    suspend fun deleteDownloadById(id: UUID)

    // 4. Update DownloadStatus (partial: status only)
    @Query("UPDATE DownloadStatus SET status = :status WHERE id = :id")
    suspend fun updateDownloadStatus(id: UUID, status: TransferStatus)



    // ========== UPLOADS ==========

    // 5. Get all incomplete uploads
    @Query("SELECT * FROM UploadStatus WHERE uploadedBytes < totalBytes")
    fun getAllIncompleteUploads(): Flow<List<UploadStatus>>

    // 6. Get all complete uploads
    @Query("SELECT * FROM UploadStatus WHERE uploadedBytes >= totalBytes")
    fun getAllCompleteUploads(): Flow<List<UploadStatus>>

    // 7. Delete an upload by ID
    @Query("DELETE FROM UploadStatus WHERE id = :id")
    suspend fun deleteUploadById(id: UUID)

    // 8. Update UploadStatus (partial: status, uploadedBytes, uploadedChunks)
    @Query("""
        UPDATE UploadStatus 
        SET status = :status, 
            uploadedBytes = :uploadedBytes, 
            uploadedChunks = :uploadedChunks 
        WHERE id = :id
    """)
    suspend fun updateUploadStatus(
        id: UUID,
        status: TransferStatus,
        uploadedBytes: Long,
        uploadedChunks: List<Int>
    )
}
