package com.niclauscott.jetdrive.core.sync.data.repository

import com.niclauscott.jetdrive.core.domain.dto.ErrorMessageDTO
import com.niclauscott.jetdrive.core.domain.util.getNetworkErrorMessage
import com.niclauscott.jetdrive.core.sync.domain.service.InMemoryCache
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeTreeResponse
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.mapper.toFileNode
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FileSyncRepository(
    baseUrl: String,
    private val client: HttpClient,
    private val cache: InMemoryCache,
) {
    private val fileUrl = "$baseUrl/files"

    private val listeners = mutableMapOf<String, MutableList<(List<FileNode>) -> Unit>>()

    suspend fun getFolder(folderId: String?): FileResponse<List<FileNode>> {
        if (folderId == null) {
            val cachedRootFiles = cache.get("root")
            if (cachedRootFiles != null) {
                return FileResponse.Successful(cachedRootFiles)
            } else {
                val remoteRootFiles = fetchAndCacheRootFiles()
                if (remoteRootFiles is FileResponse.Successful) {
                    cache.set("root", remoteRootFiles.data)
                }
                return remoteRootFiles
            }
        }

        val cacheChildren = cache.get(folderId)
        if (cacheChildren != null) {
            return FileResponse.Successful(cacheChildren)
        }

        val remoteChildren = fetchAndCacheChildren(folderId)
        if (remoteChildren is FileResponse.Successful) {
            cache.set(folderId, remoteChildren.data)
        }
        return remoteChildren
    }

    private suspend fun fetchAndCacheRootFiles(): FileResponse<List<FileNode>> {
        return try {
            val response = client.request(fileUrl) {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            }

            if (response.status != HttpStatusCode.OK) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeTreeResponse>()
            val fileNodes = result.children.map { it.toFileNode() }
            FileResponse.Successful(fileNodes)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    private suspend fun fetchAndCacheChildren(parentId: String?): FileResponse<List<FileNode>> {
        return try {
            val response = client.request("$fileUrl/$parentId/children") {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            }

            if (response.status != HttpStatusCode.OK) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeTreeResponse>()
            val fileNodes = result.children.map { it.toFileNode() }
            FileResponse.Successful(fileNodes)
        } catch (ex: Throwable) {
            FileResponse.Failure(getNetworkErrorMessage(ex))
        }
    }

    fun subScribe(folderId: String, onUpdate: (List<FileNode>) -> Unit) {
        listeners.getOrPut(folderId) { mutableListOf() }.add(onUpdate)
    }

    fun invalidate(folderId: String) {
        cache.invalidate(folderId)
        CoroutineScope(Dispatchers.IO).launch {
            val fresh = if (folderId == "root") fetchAndCacheRootFiles()
            else fetchAndCacheChildren(folderId)
            if (fresh is FileResponse.Successful) {
                listeners[folderId]?.forEach { it(fresh.data) }
            }
        }
    }
}