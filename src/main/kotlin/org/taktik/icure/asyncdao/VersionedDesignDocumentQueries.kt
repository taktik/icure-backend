package org.taktik.icure.asyncdao

import java.time.Duration
import java.util.concurrent.TimeUnit
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.taktik.couchdb.Client
import org.taktik.couchdb.dao.designDocName
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.entity.Indexer
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.queryView
import org.taktik.couchdb.support.StdDesignDocumentFactory
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.properties.CouchDbProperties

open class VersionedDesignDocumentQueries<T : StoredDocument>(protected open val entityClass: Class<T>, private val couchdDbProperties: CouchDbProperties) {

	private val designDocIdProvider = CacheBuilder.newBuilder()
		.maximumSize(100)
		.expireAfterAccess(couchdDbProperties.cachedDesignDocumentTtlMinutes, TimeUnit.MINUTES)
		.build(object : CacheLoader<Pair<Client, Class<T>>, Deferred<String>>() {
			@Throws(Exception::class)
			override fun load(key: Pair<Client, Class<T>>): Deferred<String> {
				return GlobalScope.async {
					val client = key.first
					val baseId = designDocName(key.second)
					val relatedDesignDocs = client.designDocumentsIds().filter { if (it.length == baseId.length) it.startsWith(baseId) else it.startsWith("${baseId}_") }
					val generatedDesignDocument = StdDesignDocumentFactory().generateFrom(baseId, this@VersionedDesignDocumentQueries)
					return@async if (relatedDesignDocs.size == 1) {
						relatedDesignDocs.first()
					} else if (relatedDesignDocs.contains(generatedDesignDocument.id) && isReadyDesignDoc(client, generatedDesignDocument.id)) {
						deleteStaleDesignDocuments(client, relatedDesignDocs.filter { it != generatedDesignDocument.id })
						generatedDesignDocument.id
					} else {
						relatedDesignDocs.filter { it != generatedDesignDocument.id }.first { isReadyDesignDoc(client, it) }
					}
				}
			}
		})

	private val viewsBeingIndexed = CacheBuilder.newBuilder()
		.maximumSize(1)
		.expireAfterAccess(10, TimeUnit.SECONDS)
		.build(object : CacheLoader<Client, Deferred<List<String>>>() {
			override fun load(client: Client): Deferred<List<String>> = GlobalScope.async {
				return@async client.activeTasks()
					.filterIsInstance(Indexer::class.java)
					.mapNotNull { it.design_document }
			}
		})

	private suspend fun deleteStaleDesignDocuments(client: Client, relatedDesignDocuments: List<String>) {
		client.bulkDelete(
			relatedDesignDocuments.mapNotNull { client.get(it, DesignDocument::class.java) }
		).collect()
	}

	private suspend fun isReadyDesignDoc(client: Client, designDocumentId: String): Boolean =
		viewsBeingIndexed.get(client).await().takeIf { it.contains(designDocumentId) }?.let { false }
			?: client.queryView<String, String>(ViewQuery().designDocId(designDocumentId).viewName("all").limit(1), Duration.ofMillis(couchdDbProperties.desingDocumentStatusCheckTimeoutMilliseconds))
			.map { true }
			.catch { emit(false) }
			.firstOrNull() ?: true

	private suspend fun designDocId(client: Client) = designDocIdProvider.get(Pair(client, this.entityClass)).await()

	protected suspend fun createQuery(client: Client, viewName: String): ViewQuery = ViewQuery()
		.designDocId(designDocId(client))
		.viewName(viewName)

	protected suspend fun <T> createQuery(client: Client, viewName: String, entityClass: Class<T>): ViewQuery = ViewQuery()
		.designDocId(designDocId(client))
		.viewName(viewName)

	protected suspend inline fun <reified T, P> pagedViewQuery(client: Client, viewName: String, startKey: P?, endKey: P?, pagination: PaginationOffset<P>, descending: Boolean): ViewQuery {
		var viewQuery = createQuery(client, viewName)
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

	protected suspend inline fun <reified T, P> pagedViewQueryOfIds(
		client: Client,
		viewName: String,
		startKey: P?,
		endKey: P?,
		pagination: PaginationOffset<P>
	): ViewQuery {
		var viewQuery = createQuery(client, viewName)
			.startKey(startKey)
			.includeDocs(false)
			.startDocId(pagination.startDocumentId)
			.reduce(false)
			.limit(pagination.limit)

		if (endKey != null) {
			viewQuery = viewQuery.endKey(endKey)
		}

		return viewQuery
	}

	protected suspend fun <T, P> pagedViewQuery(client: Client, viewName: String, entityClass: Class<T>, startKey: P?, endKey: P?, pagination: PaginationOffset<P>, descending: Boolean): ViewQuery {
		var viewQuery = createQuery(client, viewName, entityClass)
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

	protected suspend fun <T, P> pagedViewQueryOfIds(client: Client, viewName: String, entityClass: Class<T>, startKey: P?, endKey: P?, pagination: PaginationOffset<P>): ViewQuery {
		var viewQuery = createQuery(client, viewName, entityClass)
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
