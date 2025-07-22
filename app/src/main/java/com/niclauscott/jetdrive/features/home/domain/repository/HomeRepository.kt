package com.niclauscott.jetdrive.features.home.domain.repository

import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getStats(): FileResponse<UserFileStats>
    suspend fun createFolder(name: String): FileResponse<FileNode>
    fun getAllActiveTransferProgress(): Flow<Float?>
    suspend fun upload(uri: String)
    fun invalidate(folderId: String)
}