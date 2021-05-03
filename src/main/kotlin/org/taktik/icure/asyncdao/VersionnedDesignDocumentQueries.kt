package org.taktik.icure.asyncdao

import org.taktik.couchdb.Client
import org.taktik.couchdb.dao.designDocName
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.base.StoredDocument

open class VersionnedDesignDocumentQueries<T : StoredDocument>(protected open val entityClass: Class<T>) {

    protected var designDoc = designDocName(this.entityClass)

    protected suspend fun updateUsedDesignDocument(client: Client, mostRecentDesignDocument: String) {
        val baseId = designDocName(this.entityClass)
        val relatedDesignDocs = client.designDocumentsIds().filter { it.startsWith(baseId) }
        designDoc = when {
            relatedDesignDocs.contains(mostRecentDesignDocument) -> {
                mostRecentDesignDocument
            }
            relatedDesignDocs.contains(baseId) -> {
                baseId
            }
            else -> {
                relatedDesignDocs.first()
            }
        }
    }


    protected fun createQuery(viewName: String): ViewQuery = ViewQuery()
            .designDocId(designDoc)
            .viewName(viewName)


    protected fun <T> createQuery(viewName: String, entityClass: Class<T>): ViewQuery = ViewQuery()
            .designDocId(designDoc)
            .viewName(viewName)

    protected inline fun <reified T, P> pagedViewQuery(viewName: String, startKey: P?, endKey: P?, pagination: PaginationOffset<P>, descending: Boolean): ViewQuery {
        var viewQuery = createQuery(viewName)
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


    protected inline fun <reified T, P> pagedViewQueryOfIds(viewName: String, startKey: P?, endKey: P?, pagination: PaginationOffset<P>): ViewQuery {
        var viewQuery = createQuery(viewName)
                .startKey(startKey)
                .includeDocs(false)
                .reduce(false)
                .limit(pagination.limit)

        if (endKey != null) {
            viewQuery = viewQuery.endKey(endKey)
        }

        return viewQuery
    }

    protected fun <T, P> pagedViewQuery(viewName: String, entityClass: Class<T>, startKey: P?, endKey: P?, pagination: PaginationOffset<P>, descending: Boolean): ViewQuery {
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

    protected fun <T, P> pagedViewQueryOfIds(viewName: String, entityClass: Class<T>, startKey: P?, endKey: P?, pagination: PaginationOffset<P>): ViewQuery {
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

}
