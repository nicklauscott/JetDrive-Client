package com.niclauscott.jetdrive.features.file.data.repository

import android.util.Log
import com.niclauscott.jetdrive.core.cache.InMemoryCache
import com.niclauscott.jetdrive.core.cache.model.CachedEntry
import com.niclauscott.jetdrive.core.http_client.model.RefreshRequestDTO
import com.niclauscott.jetdrive.core.model.dto.ErrorMessageDTO
import com.niclauscott.jetdrive.core.util.TAG
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeCopyRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeCreateRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeMoveRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeRenameRequestDTO
import com.niclauscott.jetdrive.features.file.data.model.dto.FileNodeTreeResponse
import com.niclauscott.jetdrive.features.file.domain.constant.FileResponse
import com.niclauscott.jetdrive.features.file.domain.model.FileNode
import com.niclauscott.jetdrive.features.file.domain.model.FileNodeTree
import com.niclauscott.jetdrive.features.file.domain.model.mapper.toFileNode
import com.niclauscott.jetdrive.features.file.domain.repository.FileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import java.time.LocalDateTime

class FileRepositoryImpl(
    private val baseUrl: String,
    private val client: HttpClient,
    private val cache: InMemoryCache<String, CachedEntry>
): FileRepository {
    override suspend fun getRootFiles(useCache: Boolean): FileResponse<List<FileNode>> {
        return try {
            val now = System.currentTimeMillis()
            val cached = getCachedRootFiles()

            if (useCache) {
                if (cached.isNotEmpty() && now - cached.first().cachedAt <= 2 * 60 * 1000) {
                    return FileResponse.Successful(cached.map { it.data })
                }
            }

            val response = client.request("$baseUrl/files") {
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
            fileNodes.forEach {
                cache.put(it.id, CachedEntry(
                it, cachedAt = now,
                    updatedAt = result.updatedAt ?: LocalDateTime.now()))
            }

            FileResponse.Successful(fileNodes)
        } catch (ex: ConnectTimeoutException) {
            FileResponse.Failure("Connection timeout. Try again")
        } catch (ex: Exception) {
            FileResponse.Failure("Newtwork. Check your error. Check your internet connection")
        }
    }

    override suspend fun search(searchQuery: String): FileResponse<List<FileNode>> {
        return try {
            val response = client.request("$baseUrl/search/$searchQuery") {
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
        } catch (ex: ConnectTimeoutException) {
            FileResponse.Failure("Connection timeout. Try again")
        } catch (ex: Exception) {
            FileResponse.Failure("Newtwork. Check your error. Check your internet connection")
        }
    }

    override suspend fun getChildren(
        parentID: String,
        ifUpdatedSince: LocalDateTime?,
        useCache: Boolean
    ): FileResponse<List<FileNode>> {
        return try {
            val now = System.currentTimeMillis()

            if (useCache) {
                val cached = getCachedChildren(parentID)
                if (cached.isNotEmpty() && cached.any { now - it.cachedAt <= 2 * 60 * 1000 }) {
                    return FileResponse.Successful(cached.map { it.data })
                }
            }

            val response = client.request("$baseUrl/files/$parentID/children") {
                method = HttpMethod.Get
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (ifUpdatedSince != null) {
                    parameters { append("ifUpdatedSince", ifUpdatedSince.toString()) }
                }
            }

            if (response.status != HttpStatusCode.OK) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeTreeResponse>()

            val fileNodes = result.children.map { it.toFileNode() }
            fileNodes.forEach {
                cache.put(it.id, CachedEntry(
                    it, cachedAt = now,
                    updatedAt = result.updatedAt ?: LocalDateTime.now()))
            }

            FileResponse.Successful(fileNodes)
        } catch (ex: ConnectTimeoutException) {
            FileResponse.Failure("Connection timeout. Try again")
        } catch (ex: Exception) {
            FileResponse.Failure("Newtwork. Check your error. Check your internet connection")
        }
    }

    override suspend fun createFolder(name: String, parentId: String?): FileResponse<FileNode> {
       return try {
           val now = System.currentTimeMillis()

           val response = client.request("$baseUrl/files/create") {
               method = HttpMethod.Post
               headers {
                   append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                   append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
               }
               setBody(FileNodeCreateRequestDTO(name = name, parentId = parentId))
           }

           if (response.status != HttpStatusCode.Created) {
               val error = response.body<ErrorMessageDTO>()
               return FileResponse.Failure(error.message)
           }

           val result = response.body<FileNodeDTO>()
           val fileNode = result.toFileNode()

           cache.put(fileNode.id, CachedEntry(
               fileNode, cachedAt = now,
               updatedAt = LocalDateTime.now())
           )

           FileResponse.Successful(fileNode)
       } catch (ex: ConnectTimeoutException) {
           FileResponse.Failure("Connection timeout. Try again")
       } catch (ex: Exception) {
           FileResponse.Failure("Newtwork. Check your error. internet connection")
       }
    }

    override suspend fun renameFileNode(fileId: String, newName: String): FileResponse<Unit>  {
        return try {
            val response = client.request("$baseUrl/files/rename") {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(FileNodeRenameRequestDTO(fileId, newName))
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            cache.remove(fileId)
            FileResponse.Successful(Unit)
        } catch (ex: ConnectTimeoutException) {
            FileResponse.Failure("Connection timeout. Try again")
        } catch (ex: Exception) {
            FileResponse.Failure("Newtwork. Check your error. internet connection")
        }
    }

    override suspend fun copyFileNode(fileId: String, parentId: String?): FileResponse<FileNode>  {
        return try {
            val now = System.currentTimeMillis()

            val response = client.request("$baseUrl/files/copy") {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(FileNodeCopyRequestDTO(fileId, parentId))
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeDTO>()
            val fileNode = result.toFileNode()

            cache.put(fileNode.id, CachedEntry(
                fileNode, cachedAt = now,
                updatedAt = LocalDateTime.now())
            )

            FileResponse.Successful(fileNode)
        } catch (ex: ConnectTimeoutException) {
            FileResponse.Failure("Connection timeout. Try again")
        } catch (ex: Exception) {
            FileResponse.Failure("Newtwork. Check your error. internet connection")
        }
    }

    override suspend fun moveFileNode(fileId: String, newParentId: String?): FileResponse<FileNode>  {
        return try {
            val now = System.currentTimeMillis()

            val response = client.request("$baseUrl/files/move") {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(FileNodeMoveRequestDTO(fileId, newParentId))
            }

            if (response.status != HttpStatusCode.Accepted) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            val result = response.body<FileNodeDTO>()
            val fileNode = result.toFileNode()

            cache.put(fileNode.id, CachedEntry(
                fileNode, cachedAt = now,
                updatedAt = LocalDateTime.now())
            )

            FileResponse.Successful(fileNode)
        } catch (ex: ConnectTimeoutException) {
            FileResponse.Failure("Connection timeout. Try again")
        } catch (ex: Exception) {
            FileResponse.Failure("Newtwork. Check your error. internet connection")
        }
    }

    override suspend fun deleteFileNode(fileId: String): FileResponse<Unit>  {
        return try {
            val response = client.request("$baseUrl/files/delete/$fileId") {
                method = HttpMethod.Delete
            }

            if (response.status != HttpStatusCode.NoContent) {
                val error = response.body<ErrorMessageDTO>()
                return FileResponse.Failure(error.message)
            }

            cache.remove(fileId)

            FileResponse.Successful(Unit)
        } catch (ex: ConnectTimeoutException) {
            FileResponse.Failure("Connection timeout. Try again")
        } catch (ex: Exception) {
            FileResponse.Failure("Newtwork. Check your error. internet connection")
        }
    }

    private fun getCachedRootFiles(): List<CachedEntry> {
        return  cache.snapShot()
            .values.filter { it.data.parentId == null }
    }

    private fun getCachedChildren(parentID: String): List<CachedEntry> {
        return cache.snapShot()
            .values.filter { it.data.parentId == parentID }
    }
}