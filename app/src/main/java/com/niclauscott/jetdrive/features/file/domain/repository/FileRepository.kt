package com.niclauscott.jetdrive.features.file.domain.repository

import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface FileRepository {
    suspend fun getRootFiles(useCache: Boolean): FileResponse<List<FileNode>>
    suspend fun search(searchQuery: String): FileResponse<List<FileNode>>
    suspend fun getChildren(
        parentId: String, ifUpdatedSince: LocalDateTime?, useCache: Boolean
    ): FileResponse<List<FileNode>>
    suspend fun createFolder(name: String, parentId: String?): FileResponse<FileNode>
    suspend fun renameFileNode(fileId: String, newName: String): FileResponse<Unit>
    suspend fun copyFileNode(fileId: String, parentId: String?): FileResponse<FileNode>
    suspend fun moveFileNode(fileId: String, newParentId: String?): FileResponse<FileNode>
    suspend fun deleteFileNode(fileId: String): FileResponse<Unit>
    fun getAllActiveTransferProgress(): Flow<Float?>
    suspend fun upload(uri: String)
    suspend fun download(fileNode: FileNode)
}