package com.niclauscott.jetdrive.features.file.domain.repository

import com.niclauscott.jetdrive.features.file.domain.constant.FileProgress
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.AudioMetadata
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FilePreviewRepository {
    suspend fun getFileUri(fileId: String): FileResponse<String>
    suspend fun getAudioMetadata(fileId: String): FileResponse<AudioMetadata>
    fun downloadToCacheFile(url: String): Flow<FileProgress<File>>
}