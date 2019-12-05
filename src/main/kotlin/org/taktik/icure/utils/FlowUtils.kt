package org.taktik.icure.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import org.taktik.icure.entities.base.StoredDocument
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

fun <T> Flow<T>.distinct(): Flow<T> = flow {
    val previous = HashSet<T>()
    collect { value: T ->
        if (!previous.contains(value)) {
            previous.add(value)
            emit(value)
        }
    }
}

fun <T : StoredDocument> Flow<T>.distinctById(): Flow<T> = flow {
    val previous = TreeSet<T>(compareBy { it.id })
    collect { value: T ->
        if (!previous.contains(value)) {
            previous.add(value)
            emit(value)
        }
    }
}

fun <T : StoredDocument> Flow<T>.subsequentDistinctById(): Flow<T> = flow {
    val previousId = ""
    var first = true
    collect { value: T ->
        if (first || value.id != previousId) {
            emit(value)
        }
        first = false
    }
}

/**
 * The terminal operator that returns the first element emitted by the flow and then cancels flow's collection.
 * Throws [NoSuchElementException] if the flow was empty.
 */
suspend fun <T> Flow<T>.firstOrNull(): T? {
    var result: T? = null
    try {
        collect { value ->
            result = value
            throw AbortFlowException()
        }
    } catch (e: AbortFlowException) {
        // Do nothing
    }

    return result
}

private class AbortFlowException : CancellationException("Flow was aborted, no more elements needed") {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

fun <T> Flow<T>.reEmit(): Flow<T> {
    return flow {
        collect {
            emit(it)
        }
    }
}

@ExperimentalCoroutinesApi
fun <T : Any> Flow<T>.injectReactorContext(): Flux<T> {
    return Mono.subscriberContext().flatMapMany { reactorCtx ->
        flowOn(reactorCtx.asCoroutineContext()).asFlux()
    }
}
