package org.taktik.icure.spring.asynccache

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class AsyncMapCacheManager() : AsyncCacheManager{

    private val caches: ConcurrentMap<String, Cache<Any, Any>> = ConcurrentHashMap()

    override fun <K, V> getCache(name: String): Cache<K, V> {
        var cache = caches[name] as Cache<K, V>?
        if (cache == null) {
            cache = MapCache(name, HashMap<K,V>())
            val currentCache = caches.putIfAbsent(name, (cache as Cache<Any, Any>))
            if (currentCache != null) {
                cache = currentCache as Cache<K, V>
            }
        }
        return cache
    }

}
