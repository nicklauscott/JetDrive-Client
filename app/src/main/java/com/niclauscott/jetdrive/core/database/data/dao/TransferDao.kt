package com.niclauscott.jetdrive.core.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niclauscott.jetdrive.core.database.data.entities.TransferStatus
import com.niclauscott.jetdrive.core.database.data.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.database.data.entities.upload.UploadStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TransferDao {

    // ========== DOWNLOADS ==========

    @Query("SELECT * FROM DownloadStatus WHERE downloadedBytes < fileSize ORDER BY queue_position ASC")
    fun getIncompleteDownloads(): Flow<List<DownloadStatus>>

    @Query("SELECT * FROM DownloadStatus WHERE status = 'PENDING' OR status = 'ACTIVE'")
    fun getActiveAndPendingDownloads(): Flow<List<DownloadStatus>>

    @Query("SELECT * FROM DownloadStatus WHERE id = :id")
    fun getDownloadStatus(id: UUID): DownloadStatus?

    // 3. Delete a download by ID
    @Query("DELETE FROM DownloadStatus WHERE id = :id")
    suspend fun deleteDownloadById(id: UUID)

    // 4. Update DownloadStatus (partial: status only)
    @Query("UPDATE DownloadStatus SET status = :status WHERE id = :id")
    suspend fun updateDownloadStatus(id: UUID, status: TransferStatus)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDownloadStatus(downloadStatus: DownloadStatus)

    @Query("SELECT MAX(queue_position) FROM DownloadStatus")
    suspend fun getDownloadMaxQueuePosition(): Int?




    // ========== UPLOADS ==========

    // 5. Get all incomplete uploads
    @Query("SELECT * FROM UploadStatus WHERE uploadedBytes < totalBytes ORDER BY queue_position ASC")
    fun getIncompleteUploads(): Flow<List<UploadStatus>>

    @Query("SELECT * FROM Uploadstatus WHERE status = 'PENDING' OR status = 'ACTIVE'")
    fun getActiveAndPendingUploads(): Flow<List<UploadStatus>>

    @Query("SELECT * FROM UploadStatus WHERE id = :id")
    suspend fun getUploadById(id: UUID): UploadStatus?

    // 6. Get all complete uploads
    @Query("SELECT * FROM UploadStatus WHERE uploadedBytes >= totalBytes")
    fun getAllCompleteUploads(): Flow<List<UploadStatus>>

    // 7. Delete an upload by ID
    @Query("DELETE FROM UploadStatus WHERE id = :id")
    suspend fun deleteUploadById(id: UUID)

    @Query("SELECT MAX(queue_position) FROM UploadStatus")
    suspend fun getUploadMaxQueuePosition(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUploadStatus(uploadStatus: UploadStatus)
}
