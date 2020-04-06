package org.taktik.icure.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import ma.glasnost.orika.MapperFacade
import org.taktik.couchdb.TotalCount
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowNoDoc
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.Serializable
import java.util.*
import kotlin.collections.HashSet


fun <T> Flow<T>.distinct(): Flow<T> = flow {
    val previous = HashSet<T>()
    collect { value: T ->
        if (!previous.contains(value)) {
            previous.add(value)
            emit(value)
        }
    }
}

fun <T : Identifiable<*>> Flow<T>.distinctById(): Flow<T> = flow {
    val previous = HashSet<Any>()
    collect { value: T ->
        if (!previous.contains(value.id)) {
            value.id?.let { previous.add(it) }
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

@ExperimentalCoroutinesApi
fun <T : Any> Flow<T>.injectReactorContext(): Flux<T> {
    return Mono.subscriberContext().flatMapMany { reactorCtx ->
        this.flowOn(reactorCtx.asCoroutineContext()).asFlux()
    }
}

@Suppress("UNCHECKED_CAST")
// TODO SH MB: handle offsets
suspend inline fun <U: Identifiable<String>, reified T: Serializable> Flow<ViewQueryResultEvent>.paginatedList(mapper: MapperFacade, realLimit: Int, predicate: Predicate? = null): PaginatedList<T> {
    val result = PaginatedList<T>(realLimit)
    var viewRowCount = 0
    var lastProcessedViewRow: ViewRowWithDoc<*, *, *>? = null
    var lastProcessedViewRowNoDoc: ViewRowNoDoc<*, *>? = null
    val resultRows = mutableListOf<T>()
    this.mapNotNull { viewQueryResultEvent ->
        when (viewQueryResultEvent) {
            is TotalCount -> {
                result.totalSize = viewQueryResultEvent.total
                null
            }
            is ViewRowWithDoc<*, *, *> -> {
                when {
                    viewRowCount == realLimit -> {
                        result.nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id) // TODO SH MB: startKey was a List<String> before, now it is a String, ok?
                        viewRowCount++
                        lastProcessedViewRow?.doc as? U
                    }
                    viewRowCount < realLimit -> {
                        val previous = lastProcessedViewRow
                        lastProcessedViewRow = viewQueryResultEvent
                        viewRowCount++
                        previous?.doc as? U // if this is the first one, the Mono will be empty, so it will be ignored by flatMap
                    }
                    else -> { // we have more elements than expected, just ignore them
                        viewRowCount++
                        null
                    }
                }?.takeUnless { predicate?.apply(it) == false }
            }
            is ViewRowNoDoc<*, *> -> {
                when{
                    viewRowCount == realLimit -> {
                        result.nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id)
                        viewRowCount++
                        lastProcessedViewRowNoDoc?.id
                    }
                    viewRowCount < realLimit -> {
                        val previous = lastProcessedViewRowNoDoc
                        lastProcessedViewRowNoDoc = viewQueryResultEvent
                        viewRowCount++
                        previous?.id // if this is the first one, the Mono will be empty, so it will be ignored by flatMap
                    }
                    else -> { // we have more elements than expected, just ignore them
                        viewRowCount++
                        null
                    }
                }
            }
            else -> {
                null
            }
        }
    }.map {
        mapper.map(it, T::class.java)
    }.toCollection(resultRows)

    if(resultRows.size < realLimit){
        ((lastProcessedViewRow?.doc as? U) ?: lastProcessedViewRowNoDoc?.id)?.let { resultRows.add(mapper.map(it, T::class.java)) }
    }
    result.rows = resultRows
    return result
}

suspend inline fun <reified T: Serializable> Flow<ViewQueryResultEvent>.paginatedList(realLimit: Int): PaginatedList<T> {
    val result = PaginatedList<T>(realLimit)
    var viewRowCount = 0
    var lastProcessedViewRow: ViewRowWithDoc<*, *, *>? = null
    val resultRows = mutableListOf<T>()
    this.mapNotNull { viewQueryResultEvent ->
        when (viewQueryResultEvent) {
            is TotalCount -> {
                result.totalSize = viewQueryResultEvent.total
                null
            }
            is ViewRowWithDoc<*, *, *> -> {
                when {
                    viewRowCount == realLimit -> {
                        result.nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id)
                        viewRowCount++
                        lastProcessedViewRow?.doc as? T
                    }
                    viewRowCount < realLimit -> {
                        val previous = lastProcessedViewRow
                        lastProcessedViewRow = viewQueryResultEvent
                        viewRowCount++
                        previous?.doc as? T
                    }
                    else -> { // we have more elements than expected, just ignore them
                        viewRowCount++
                        null
                    }
                }
            }
            else -> {
                null
            }
        }
    }.toCollection(resultRows)
    if(resultRows.size < realLimit){
        (lastProcessedViewRow?.doc as? T)?.let {
            resultRows.add(it)
        }
    }
    result.rows = resultRows
    return result
}

@ExperimentalCoroutinesApi
fun <T> Flow<T>.bufferedChunks(min: Int, max: Int): Flow<List<T>> = channelFlow<List<T>> {
    require(min >= 1 && max >= 1 && max >= min) {
        "Min and max chunk sizes should be greater than 0, and max >= min"
    }
    val buffer = ArrayList<T>(max)
    collect {
        buffer += it
        if(buffer.size >= max) {
            send(buffer.toList())
            buffer.clear()
        } else if (min <= buffer.size) {
            val offered = offer(buffer.toList())
            if (offered) {
                buffer.clear()
            }
        }
    }
    if (buffer.size > 0) send(buffer.toList())
}.buffer(1)
