package org.taktik.icure.spring.asynccache

import com.hazelcast.map.IMap
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.cache.support.SimpleValueWrapper
import reactor.core.publisher.Mono

class HazelcastCache<K, V>(private val map: IMap<K, V>) : Cache<K, V> {

    override fun getName(): String {
        return map.name
    }

    override suspend fun getWrapper(key: K?): org.springframework.cache.Cache.ValueWrapper? {
        return key?.let {
            val value: V? = get(key)
            value?.let { SimpleValueWrapper(value) }
        }
    }

    override suspend fun get(key: K): V? {
        return Mono.fromCompletionStage(map.getAsync(key)).awaitFirstOrNull()
    }

    override fun clear() {
        map.clear()
    }

    override fun invalidate(): Boolean {
        clear()
        return false
    }

    override fun evict(key: K) {
        key?.let { map.removeAsync(key) }
    }

    override fun put(key: K, value: V) {
        key?.let { map.putAsync(key, value) }
    }

    override fun getNativeCache(): IMap<K, V>? {
        return map
    }
}
