package com.niclauscott.jetdrive.features.home.domain.repository

import com.niclauscott.jetdrive.core.domain.model.UserFileStats
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getStats(): FileResponse<UserFileStats>
    suspend fun createFolder(name: String): FileResponse<Unit>
    fun getAllActiveTransferProgress(): Flow<Float?>
    suspend fun upload(uri: String)
}