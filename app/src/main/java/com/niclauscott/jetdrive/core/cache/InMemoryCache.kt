package com.niclauscott.jetdrive.core.cache

import android.util.LruCache

class InMemoryCache<K, V>(maxSize: Int) {
    private val cache = LruCache<K, V>(maxSize)

    fun get(key: K): V? = cache.get(key)

    fun put(key: K, value: V) = cache.put(key, value)

    fun remove(key: K) = cache.remove(key)

    fun clear() = cache.evictAll()

    fun snapShot()  = cache.snapshot()

    fun size() = cache.size()
}