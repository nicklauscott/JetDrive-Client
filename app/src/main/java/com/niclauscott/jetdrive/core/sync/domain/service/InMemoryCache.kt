package com.niclauscott.jetdrive.core.sync.domain.service

import com.niclauscott.jetdrive.features.file.domain.model.FileNode

class InMemoryCache {
    private data class  CacheEntry(
        val data: List<FileNode>,
        val timeStamp: Long
    )

    private val cache = mutableMapOf<String, CacheEntry>()

    fun get(folderId: String): List<FileNode>? {
        val entry = cache[folderId] ?: return null
        val ttl = 2 * 60 * 1000
        return if (System.currentTimeMillis() - entry.timeStamp < ttl) {
            entry.data
        } else {
            cache.remove(folderId)
            null
        }
    }

    fun set(folderId: String, items: List<FileNode>) {
        cache[folderId] = CacheEntry(items, System.currentTimeMillis())
    }

    fun invalidate(folderId: String) {
        cache.remove(folderId)
    }
}