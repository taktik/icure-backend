package org.taktik.icure.spring.asynccache

import com.hazelcast.core.HazelcastInstance
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class AsyncHazelCastCacheManager(private val hazelcastInstance: HazelcastInstance) : AsyncCacheManager{

    private val caches: ConcurrentMap<String, Cache<Any, Any>> = ConcurrentHashMap()

    override fun <K, V> getCache(name: String): Cache<K, V>? {
        var cache = caches[name] as? Cache<K, V>
        if (cache == null) {
            val map = hazelcastInstance.getMap<K, V>(name)
            cache = HazelcastCache(map)
            val currentCache = caches.putIfAbsent(name, (cache as Cache<Any, Any>))
            if (currentCache != null) {
                cache = currentCache as? Cache<K, V>
            }
        }
        return cache
    }

}
