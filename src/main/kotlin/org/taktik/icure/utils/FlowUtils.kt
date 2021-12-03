/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.entities.base.StoredDocument
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.OutputStream
import java.nio.ByteBuffer

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

@ExperimentalCoroutinesApi
fun <T : Any> Flow<T>.injectReactorContext(): Flux<T> {
    return Mono.subscriberContext().flatMapMany { reactorCtx ->
        this.flowOn(reactorCtx.asCoroutineContext()).asFlux()
    }
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


suspend fun Flow<ByteBuffer>.writeTo(os: OutputStream) {
    this.collect { bb ->
        if (bb.hasArray() && bb.hasRemaining()) {
            os.write(bb.array(), bb.position() + bb.arrayOffset(), bb.remaining())
        } else {
            os.write(ByteArray(bb.remaining()).also { bb.get(it) })
        }
    }
}

