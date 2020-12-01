package org.taktik.icure.spring.asynccache

import org.springframework.cache.support.SimpleValueWrapper

class MapCache<K, V>(private val name: String, private val map: HashMap<K, V>) : Cache<K, V> {

    override suspend fun getWrapper(key: K?): org.springframework.cache.Cache.ValueWrapper? {
        return key?.let {
            val value: V? = get(key)
            value?.let { SimpleValueWrapper(value) }
        }
    }

    override suspend fun get(key: K): V? = map[key]

    override fun clear() {
        map.clear()
    }

    override fun invalidate(): Boolean {
        clear()
        return false
    }

    override suspend fun evict(key: K?) {
        map.remove(key)
    }

    override suspend fun put(key: K, value: V) {
        map[key] = value
    }

    override fun getNativeCache(): Any? {
        return map
    }

    override fun getName(): String {
        return name
    }
}
