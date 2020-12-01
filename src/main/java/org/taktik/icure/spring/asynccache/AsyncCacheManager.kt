package org.taktik.icure.spring.asynccache

interface AsyncCacheManager {
    fun <K, V> getCache(name: String): Cache<K, V>
}
