package org.taktik.icure.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.TotalCount
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asynclogic.impl.AsyncSessionLogic
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.services.external.rest.v1.dto.IcureDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.services.external.rest.v1.dto.StoredDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.Serializable
import java.util.*
import java.net.URI


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

@ExperimentalCoroutinesApi
fun <T : Any> Flow<T>.injectReactorContext(): Flux<T> {
    return Mono.subscriberContext().flatMapMany { reactorCtx ->
        this.flowOn(reactorCtx.asCoroutineContext()).asFlux()
    }
}

suspend inline fun <U : StoredDocument, reified T : StoredDto> Flow<ViewQueryResultEvent>.paginatedList(mapper: MapperFacade, realLimit: Int): PaginatedList<T> {
    val result = PaginatedList<T>(realLimit)
    var viewRowCount = 0
    var lastProcessedViewRow: ViewRowWithDoc<*, *, *>? = null
    result.rows = this.mapNotNull { viewQueryResultEvent ->
        when (viewQueryResultEvent) {
            is TotalCount -> {
                result.totalSize = viewQueryResultEvent.total
                null
            }
            is ViewRowWithDoc<*, *, *> -> {
                // TODO SH can't a doc be null? e.g. if we get by ids and one id doesn't exist? then we should emit null, but flatMap doesn't support it...
                if (viewRowCount == realLimit) {
                    result.nextKeyPair = PaginatedDocumentKeyIdPair(viewQueryResultEvent.key, viewQueryResultEvent.id) // TODO SH startKey was a List<String>, ok with a String?
                    viewRowCount++
                    lastProcessedViewRow?.doc as? U?
                } else if (viewRowCount < realLimit) {
                    val previous = lastProcessedViewRow
                    lastProcessedViewRow = viewQueryResultEvent
                    viewRowCount++
                    previous?.doc as? U? // if this is the first one, the Mono will be empty, so it will be ignored by flatMap
                } else { // we have more elements than expected, just ignore them
                    viewRowCount++
                    null
                }
            }
            else -> {
                null
            }
        }
    }.map {
        mapper.map(it, T::class.java)
    }.toList()
    return result
}

fun <T> Flow<T>.handleErrors(message: String): Flow<T> = flow {
    try {
        collect { value -> emit(value) }
    } catch (e: Throwable) {
        throw IllegalStateException(message)
    }
}
