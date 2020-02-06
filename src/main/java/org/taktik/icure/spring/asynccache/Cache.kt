package org.taktik.icure.spring.asynccache

import com.hazelcast.map.IMap
import org.springframework.cache.Cache

interface Cache<K, V> {
    suspend fun getWrapper(key: K?): Cache.ValueWrapper?
    fun getName(): String
    suspend fun get(key: K): V?
    fun clear()
    fun invalidate(): Boolean
    fun evict(key: K)
    fun put(key: K, value: V)
    fun getNativeCache(): IMap<K, V>?
}
