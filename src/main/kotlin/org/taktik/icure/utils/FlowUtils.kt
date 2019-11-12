package org.taktik.icure.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.taktik.icure.entities.base.StoredDocument
import java.util.*

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
