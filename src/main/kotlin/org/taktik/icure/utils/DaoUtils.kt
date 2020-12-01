package org.taktik.icure.utils

import org.ektorp.impl.NameConventions
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.icure.db.PaginationOffset
import java.net.URI

inline fun <reified E> PaginationOffset<List<E>>.toComplexKeyPaginationOffset(): PaginationOffset<ComplexKey> =
        PaginationOffset(this.startKey?.toComplexKey(), this.startDocumentId, this.offset, this.limit)

inline fun <reified E> List<E>.toComplexKey(): ComplexKey = ComplexKey.of(*this.toTypedArray())

fun getFullId(dbInstanceUrl: URI, id: String) = "${dbInstanceUrl}:$id"

/**
 * Creates a ViewQuery pre-configured with correct dbPath, design document id and view name.
 * @param viewName
 * @return
 */
inline fun<reified T> createQuery(viewName: String): ViewQuery = ViewQuery()
        .designDocId(NameConventions.designDocName(T::class.java))
        .viewName(viewName)

fun<T> createQuery(viewName: String, entityClass: Class<T>): ViewQuery = ViewQuery()
        .designDocId(NameConventions.designDocName(entityClass))
        .viewName(viewName)

inline fun<reified T, P> pagedViewQuery(viewName: String, startKey: P?, endKey: P?, pagination: PaginationOffset<P>, descending: Boolean): ViewQuery {
    var viewQuery = createQuery<T>(viewName)
            .startKey(pagination.startKey ?: startKey) // NB: pagination.startKey is preferred when present
            .includeDocs(true)
            .reduce(false)
            .startDocId(pagination.startDocumentId)
            .limit(pagination.limit)
            .descending(descending)

    if (endKey != null) {
        viewQuery = viewQuery.endKey(endKey)
    }

    return viewQuery
}

inline fun<reified T, P> pagedViewQueryOfIds(viewName: String, startKey: P?, endKey: P?, pagination: PaginationOffset<P>): ViewQuery {
    var viewQuery = createQuery<T>(viewName)
            .startKey(startKey)
            .includeDocs(false)
            .reduce(false)
            .limit(pagination.limit)

    if (endKey != null) {
        viewQuery = viewQuery.endKey(endKey)
    }

    return viewQuery
}

inline fun<T, P> pagedViewQuery(viewName: String, entityClass: Class<T>, startKey: P?, endKey: P?, pagination: PaginationOffset<P>, descending: Boolean): ViewQuery {
    var viewQuery = createQuery(viewName, entityClass)
            .startKey(startKey) // NB: pagination.startKey is ignored, but should always be null or the same as startKey
            .includeDocs(true)
            .reduce(false)
            .startDocId(pagination.startDocumentId)
            .limit(pagination.limit)
            .descending(descending)

    if (endKey != null) {
        viewQuery = viewQuery.endKey(endKey)
    }

    return viewQuery
}

inline fun<T, P> pagedViewQueryOfIds(viewName: String, entityClass: Class<T>, startKey: P?, endKey: P?, pagination: PaginationOffset<P>): ViewQuery {
    var viewQuery = createQuery(viewName, entityClass)
            .startKey(startKey)
            .includeDocs(false)
            .reduce(false)
            .limit(pagination.limit)

    if (endKey != null) {
        viewQuery = viewQuery.endKey(endKey)
    }

    return viewQuery
}
