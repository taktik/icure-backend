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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.apache.commons.lang3.ArrayUtils
import org.slf4j.LoggerFactory
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.dao.designDocName
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.exception.DocumentNotFoundException
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.support.StdDesignDocumentFactory
import org.taktik.couchdb.update
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.asyncdao.VersionnedDesignDocumentQueries
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI

@ExperimentalCoroutinesApi
@FlowPreview
open class InternalDAOImpl<T : StoredDocument>(override val entityClass: Class<T>, val couchDbProperties: CouchDbProperties, val couchDbDispatcher: CouchDbDispatcher, val idGenerator: IDGenerator) : InternalDAO<T>, VersionnedDesignDocumentQueries<T>(entityClass, couchDbProperties) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val client = couchDbDispatcher.getClient(URI(couchDbProperties.url))

    override fun getEntities() = couchDbDispatcher.getClient(URI(couchDbProperties.url)).queryView(ViewQuery()
            .designDocId(designDocName(entityClass))
            .viewName("all").includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()


    override fun getEntityIds(): Flow<String> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAllIds")
        }
        return couchDbDispatcher.getClient(URI(couchDbProperties.url)).queryView<String, String>(ViewQuery()
                .designDocId(designDocName(entityClass))
                .viewName("all").includeDocs(false)).map { it.id }.filterNotNull()
    }

    override suspend fun get(id: String, vararg options: Option): T? = get(id, null, *options)

    override suspend fun get(id: String, rev: String?, vararg options: Option): T? {
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

    override fun getEntities(ids: Collection<String>): Flow<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        return client.get(ids, entityClass)
    }

    override suspend fun save(entity: T): T? {
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
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save flow of entities")
        }
        client.bulkUpdate(entities.toList(), entityClass).collect { emit(DocIdentifier(it.id, it.rev)) }
    }

    override fun save(entities: List<T>): Flow<DocIdentifier> = flow {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save flow of entities")
        }
        client.bulkUpdate(entities, entityClass).collect { emit(DocIdentifier(it.id, it.rev)) }
    }

    override suspend fun update(entity: T): T? {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
        }
        return client.update(entity, entityClass)
    }

    override fun getEntities(ids: List<String>) =
            couchDbDispatcher.getClient(URI(couchDbProperties.url)).queryView(ViewQuery()
                    .designDocId(designDocName(entityClass))
                    .viewName("all").keys(ids).includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()

    override fun purge(entities: Flow<T>) = flow {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".purge flow of entities ")
        }
        emitAll(client.bulkDelete(entities.toList()))
    }

    override fun remove(entities: Flow<T>) = flow {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove flow of entities ")
        }
        emitAll(client.bulkUpdate(entities.map { it.withDeletionDate(System.currentTimeMillis()) as T }.toList(), entityClass))
    }

    override suspend fun forceInitStandardDesignDocument(updateIfExists: Boolean) {
        val baseId = designDocName(this.entityClass)
        val generated = StdDesignDocumentFactory().generateFrom(baseId, this)
        val fromDatabase = client.get(generated.id, DesignDocument::class.java)
                ?: client.get(baseId, DesignDocument::class.java)
        val (_, changed) = fromDatabase?.mergeWith(generated, true) ?: generated to true
        if (changed && updateIfExists) {
            client.update(generated)
        }
    }
}
