package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.apache.commons.lang3.ArrayUtils
import org.apache.taglibs.standard.util.EscapeXML.emit
import org.ektorp.DocumentNotFoundException
import org.ektorp.ViewQuery
import org.ektorp.impl.NameConventions
import org.slf4j.LoggerFactory
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.queryView
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

    override fun getAll() = couchDbDispatcher.getClient(URI(couchDbProperties.url), null).queryView(ViewQuery()
            .designDocId(NameConventions.designDocName(entityClass))
            .viewName("all").includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()


    override fun getAllIds(): Flow<String> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAllIds")
        }
        return couchDbDispatcher.getClient(URI(couchDbProperties.url), null).queryView<String, String>(ViewQuery()
                .designDocId(NameConventions.designDocName(entityClass))
                .viewName("all").includeDocs(false)).map { it.id }.filterNotNull()
    }

    override suspend fun get(id: String, vararg options: Option): T? = get(id, null, *options)

    override suspend fun get(id: String, rev: String?, vararg options: Option): T? {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url), null)
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

    override suspend fun save(entity: T): T? {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url), null)
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
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url), null)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save flow of entities")
        }
        client.bulkUpdate(entities.toList(), entityClass).collect { emit(DocIdentifier(it.id, it.rev)) }
    }

    override suspend fun update(entity: T): T? {
        val client = couchDbDispatcher.getClient(URI(couchDbProperties.url), null)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
        }
        return client.update(entity, entityClass)
    }

    override fun list(ids: List<String>) =
            couchDbDispatcher.getClient(URI(couchDbProperties.url), null).queryView(ViewQuery()
                    .designDocId(NameConventions.designDocName(entityClass))
                    .viewName("all").keys(ids).includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()


}
