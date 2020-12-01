package org.taktik.icure.spring.asynccache

import org.springframework.cache.Cache

interface Cache<K, V> {
    suspend fun getWrapper(key: K?): Cache.ValueWrapper?
    fun getName(): String
    suspend fun get(key: K): V?
    fun clear()
    fun invalidate(): Boolean
    suspend fun evict(key: K?)
    suspend fun put(key: K, value: V)
    fun getNativeCache(): Any?
}
