package org.taktik.icure.spring.asynccache

import com.hazelcast.core.ExecutionCallback
import com.hazelcast.core.IMap
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.cache.support.SimpleValueWrapper
import org.taktik.icure.utils.firstOrNull
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

    override suspend fun get(key: K): V? = suspendCoroutine { continuation ->
        map.getAsync(key).andThen(object : ExecutionCallback<V> {
            override fun onFailure(t: Throwable) = continuation.resumeWithException(t)
            override fun onResponse(response: V?) = continuation.resume(response)
        })
    }

    override fun clear() {
        map.clear()
    }

    override fun invalidate(): Boolean {
        clear()
        return false
    }

    override suspend fun evict(key: K?) = suspendCoroutine<Unit> { continuation ->
        map.removeAsync(key).andThen(object : ExecutionCallback<V> {
            override fun onFailure(t: Throwable) = continuation.resumeWithException(t)
            override fun onResponse(response: V?) = continuation.resume(Unit)
        })
    }

    override suspend fun put(key: K, value: V) = suspendCoroutine<Unit> { continuation ->
        map.putAsync(key, value).andThen(object : ExecutionCallback<V> {
            override fun onFailure(t: Throwable) = continuation.resumeWithException(t)
            override fun onResponse(response: V?) = continuation.resume(Unit)
        })
    }

    override fun getNativeCache(): IMap<K, V>? {
        return map
    }
}
