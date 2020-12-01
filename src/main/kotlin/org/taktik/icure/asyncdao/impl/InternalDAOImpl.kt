package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.ArrayUtils
import org.ektorp.impl.NameConventions
import org.ektorp.support.StdDesignDocumentFactory
import org.slf4j.LoggerFactory
import org.taktik.couchdb.BulkUpdateResult
import org.taktik.couchdb.DesignDocument
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.View
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.exception.DocumentNotFoundException
import org.taktik.couchdb.queryView
import org.taktik.couchdb.update
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI

@ExperimentalCoroutinesApi
@FlowPreview
open class InternalDAOImpl<T : StoredDocument>(val entityClass: Class<T>, val couchDbProperties: CouchDbProperties, val couchDbDispatcher: CouchDbDispatcher, val idGenerator: IDGenerator) : InternalDAO<T> {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getAll() = couchDbDispatcher.getClient(URI(couchDbProperties.url)).queryView(ViewQuery()
            .designDocId(NameConventions.designDocName(entityClass))
            .viewName("all").includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()


    override fun getAllIds(): Flow<String> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAllIds")
        }
        return couchDbDispatcher.getClient(URI(couchDbProperties.url)).queryView<String, String>(ViewQuery()
                .designDocId(NameConventions.designDocName(entityClass))
                .viewName("all").includeDocs(false)).map { it.id }.filterNotNull()
    }

    override suspend fun get(id: String, vararg options: Option): T? = get(id, null, *options)

    override suspend fun get(id: String, rev: String?, vararg options: Option): T? {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }
        return try {
            return rev?.let{ client.get(id, entityClass, *options) } ?: client.get(id, entityClass, *options)
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
            null
        }
    }

    override fun getList(ids: Collection<String>): Flow<T> {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        return client.get(ids, entityClass)
    }

    override suspend fun save(entity: T): T? {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
        }
        return when {
            entity.rev == null -> {
                client.create(entity, entityClass)
            }
            else -> {
                client.update(entity, entityClass)
            }
        }
    }

    override fun save(entities: Flow<T>): Flow<DocIdentifier> = flow {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save flow of entities")
        }
        client.bulkUpdate(entities.toList(), entityClass).collect { emit(DocIdentifier(it.id, it.rev)) }
    }

    override fun save(entities: List<T>): Flow<DocIdentifier> = flow {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save flow of entities")
        }
        client.bulkUpdate(entities, entityClass).collect { emit(DocIdentifier(it.id, it.rev)) }
    }

    override suspend fun update(entity: T): T? {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
        }
        return client.update(entity, entityClass)
    }

    override fun list(ids: List<String>) =
            couchDbDispatcher.getClient(URI(couchDbProperties.url)).queryView(ViewQuery()
                    .designDocId(NameConventions.designDocName(entityClass))
                    .viewName("all").keys(ids).includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()

    override suspend fun purge(entities: Flow<T>): Flow<BulkUpdateResult> {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".purge flow of entities ")
        }
        return client.bulkDelete(entities.toList())
    }

    override suspend fun remove(entities: Flow<T>): Flow<BulkUpdateResult> {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove flow of entities ")
        }
        return client.bulkUpdate(entities.map { it.withDeletionDate(System.currentTimeMillis()) as T }.toList(), entityClass)
    }

    override suspend fun forceInitStandardDesignDocument(updateIfExists: Boolean) {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))
        val designDocId = NameConventions.designDocName(this.entityClass)
        val fromDatabase = client.get(designDocId, DesignDocument::class.java)?.let {
            org.ektorp.support.DesignDocument(it.id).apply {
                revision = it.rev
                views = it.views.mapValues { org.ektorp.support.DesignDocument.View().apply {
                    map= it.value?.map; reduce= it.value?.reduce
                } }
                updates = it.updateHandlers
                lists = it.lists
                shows = it.shows
            }
        }
        val generated = StdDesignDocumentFactory().generateFrom(this)
        val changed: Boolean = fromDatabase?.mergeWith(generated, true) ?: true
        if (changed && (updateIfExists || fromDatabase == null)) {
            client.update((fromDatabase ?: generated).let {
                DesignDocument(
                        id = designDocId,
                        rev = it.revision,
                        views = it.views.mapValues { View(map= it.value.map, reduce= it.value.reduce) },
                        updateHandlers = it.updates,
                        lists = it.lists,
                        shows = it.shows)
            })
        }
    }

}
