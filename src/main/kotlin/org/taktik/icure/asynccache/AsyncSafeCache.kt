/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.asynccache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.cache.Cache

class AsyncSafeCache<K:Any, V>(val cache: Cache) {
    interface AsyncValueProvider<K, V> {
        suspend fun getValue(key: K): V?
    }

    val mutex = Mutex()
    val name: String
        get() = cache.name

    suspend fun clear() = mutex.withLock {
        cache.clear()
    }

    suspend fun evict(key: K) = mutex.withLock {
        cache.evict(key)
    }

    suspend fun get(key: K, valueProvider: AsyncValueProvider<K, V>) =
            (cache.get(key)?.get() ?: mutex.withLock {
                cache.get(key)?.get() ?: valueProvider.getValue(key)?.also { cache.put(key, it) }
            }) as V?

    suspend fun getIfPresent(key: K) = mutex.withLock {
        cache.get(key)?.get() as V?
    }

    suspend fun put(key: K, value: V) = mutex.withLock {
        cache.put(key, value)
    }
}
