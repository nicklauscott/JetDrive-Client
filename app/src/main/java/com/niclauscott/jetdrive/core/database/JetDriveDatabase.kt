package com.niclauscott.jetdrive.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.niclauscott.jetdrive.core.database.converter.Converters
import com.niclauscott.jetdrive.core.database.dao.TransferDao
import com.niclauscott.jetdrive.core.database.entities.downloads.ChunkDownloadStatus
import com.niclauscott.jetdrive.core.database.entities.downloads.DownloadStatus
import com.niclauscott.jetdrive.core.database.entities.upload.UploadStatus

@Database(
    entities = [ChunkDownloadStatus::class, DownloadStatus::class, UploadStatus::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class JetDriveDatabase: RoomDatabase() {

    abstract fun transferDao(): TransferDao

}