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
import org.taktik.couchdb.*
import org.taktik.couchdb.dao.designDocName
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.entity.View
import org.taktik.couchdb.exception.DocumentNotFoundException
import org.taktik.couchdb.exception.UpdateConflictException
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.support.StdDesignDocumentFactory
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.VersionedDesignDocumentQueries
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.exceptions.BulkUpdateConflictException
import org.taktik.icure.exceptions.PersistenceException
import org.taktik.icure.properties.CouchDbProperties
import java.net.URI
import java.nio.ByteBuffer

@FlowPreview
@ExperimentalCoroutinesApi
abstract class GenericDAOImpl<T : StoredDocument>(couchDbProperties: CouchDbProperties,
                                                  protected override val entityClass: Class<T>, protected val couchDbDispatcher: CouchDbDispatcher, protected val idGenerator: IDGenerator
) : GenericDAO<T>, VersionedDesignDocumentQueries<T>(entityClass, couchDbProperties) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    protected val dbInstanceUrl = URI(couchDbProperties.url)

    override suspend fun contains(id: String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".contains: " + id)
        }
        return client.get(id, entityClass) != null
    }

    override suspend fun hasAny(): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return designDocContainsAllView(dbInstanceUrl) && client.queryView<String, String>(createQuery(client,"all").limit(1)).count() > 0
    }

    private suspend fun designDocContainsAllView(dbInstanceUrl: URI): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.get<DesignDocument>(designDocName(entityClass))?.views?.containsKey("all")
                ?: false
    }

    override fun getEntityIds(limit: Int?): Flow<String> = flow {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAllIds")
        }
        if (designDocContainsAllView(dbInstanceUrl)) {
            val client = couchDbDispatcher.getClient(dbInstanceUrl)
            client.queryView<String, String>(if (limit != null) createQuery(client,"all", entityClass).limit(limit) else createQuery(client,"all", entityClass)).onEach { emit(it.id) }.collect()
        }
    }

    override fun getEntities(): Flow<T> = flow {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAll")
        }
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
            client.queryView(createQuery(client, "all").includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()
        )
    }

    override fun getAttachment(documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.getAttachment(documentId, attachmentId, rev))
    }

    override suspend fun createAttachment(documentId: String, attachmentId: String, rev: String, contentType: String, data: Flow<ByteBuffer>): String {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.createAttachment(documentId, attachmentId, rev, contentType, data)
    }

    override suspend fun deleteAttachment(documentId: String, rev: String, attachmentId: String): String {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.deleteAttachment(documentId, attachmentId, rev)
    }

    override suspend fun get(id: String, vararg options: Option): T? = get(id, null, *options)

    override suspend fun get(id: String, rev: String?, vararg options: Option): T? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }
        try {
            return rev?.let { client.get(id, it, entityClass, *options) }
                    ?: client.get(id, entityClass, *options)?.let { postLoad(it) }
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
        }

        return null
    }

    override fun getEntities(ids: Collection<String>): Flow<T> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        emitAll(client.get(ids, entityClass).map { this@GenericDAOImpl.postLoad(it) })
    }

    override fun getEntities(ids: Flow<String>): Flow<T> = flow  {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        emitAll(client.get(ids, entityClass).map { this@GenericDAOImpl.postLoad(it) })
    }

    override suspend fun create(entity: T): T? {
        return save(true, entity)
    }

    override suspend fun save(entity: T): T? {
        return save(null, entity)
    }

    protected open suspend fun save(newEntity: Boolean?, entity: T): T? {
        var newEntity = newEntity
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
        }

        return beforeSave(entity).let { entity ->
            if (newEntity ?: entity.rev == null) {
                client.create(entity, entityClass)
            } else {
                client.update(entity, entityClass)
                //saveRevHistory(entity, null);
            }.let { afterSave(it) }
        }
    }

    protected open suspend fun beforeSave(entity: T): T {
        return entity
    }

    protected open suspend fun afterSave(entity: T): T {
        return entity
    }

    protected open suspend fun beforeDelete(entity: T): T {
        return entity
    }

    protected open suspend fun afterDelete(entity: T): T {
        return entity
    }

    protected open suspend fun beforeUnDelete(entity: T): T {
        return entity
    }

    protected open suspend fun afterUnDelete(entity: T): T {
        return entity
    }

    protected open suspend fun postLoad(entity: T): T {
        return entity
    }

    override suspend fun remove(entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        val deleted = client.update(beforeDelete(entity).withDeletionDate(deletionDate = System.currentTimeMillis()) as T, entityClass).let {
            afterDelete(it)
        }

        return DocIdentifier(deleted.id, deleted.rev)
    }

    override suspend fun unRemove(entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        return beforeUnDelete(entity).let {
            val undeleted = client.update(it.withDeletionDate(null) as T, entityClass).let {
                afterUnDelete(it)
            }

            DocIdentifier(undeleted.id, undeleted.rev)
        }
    }

    override suspend fun purge(entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Delete
        val purged = client.delete(beforeDelete(entity))
        // After remove
        afterDelete(entity)
        return purged
    }

    override fun remove(entities: Collection<T>) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            val bulkUpdateResults = client.bulkUpdate(entities.map {
                beforeDelete(it).let {
                    it.withDeletionDate(System.currentTimeMillis()) as T
                }
            }, entityClass).onEach { r ->
                entities.firstOrNull { e -> r.id == e.id }?.let { afterDelete(it) }
            }
            emitAll(bulkUpdateResults.map { DocIdentifier(it.id, it.rev) })
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    override fun unRemove(entities: Collection<T>) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            val bulkUpdateResults = client.bulkUpdate(entities.map {
                beforeUnDelete(it).let {
                    it.withDeletionDate(null) as T
                }
            }, entityClass).onEach { r ->
                entities.firstOrNull { e -> r.id == e.id }?.let { afterUnDelete(it) }
            }
            emitAll(bulkUpdateResults.map { DocIdentifier(it.id, it.rev) })
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    // This function is not reactive, but it doesn't seem to be used at all anyway...
    override suspend fun purge(entities: Collection<T>) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            val bulkDeleteResults = client.bulkDelete(entities.map { beforeDelete(it) }).toList()
            for (entity in entities) {
                afterDelete(entity)
            }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    override fun <K : Collection<T>> create(entities: K): Flow<T> {
        return save(true, entities)
    }

    override fun <K : Collection<T>> save(entities: K): Flow<T> {
        return save(false, entities)
    }

    // TODO SH later: make sure this is correct
    protected open fun <K : Collection<T>> save(newEntity: Boolean?, entities: K): Flow<T> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save: " + entities.mapNotNull { entity -> entity.id + ":" + entity.rev })
        }

        // Save entity
        val orderedEntities = ArrayList(entities.map { beforeSave(it) })
        val updatedEntities = orderedEntities.filter { !(newEntity ?: it.rev == null) }

        val results = client.bulkUpdate(orderedEntities, entityClass).toList()

        val conflicts = results.filter { it.error == "conflict" }.map { r -> UpdateConflictException(r.id, r.rev ?: "unknown") }.toList()
        if (conflicts.isNotEmpty()) {
            throw BulkUpdateConflictException(conflicts, orderedEntities)
        }
        emitAll(results.asFlow().mapNotNull { r ->
            (updatedEntities.firstOrNull { u -> r.id == u.id }
                    ?: entities.firstOrNull { u -> r.id == u.id })?.let { e -> r.rev?.let { e.withIdRev(rev = it) as T } ?: e }
        }.map { afterSave(it) })
    }

    override suspend fun forceInitStandardDesignDocument(updateIfExists: Boolean, useVersioning: Boolean) {
        forceInitStandardDesignDocument(couchDbDispatcher.getClient(dbInstanceUrl), updateIfExists, useVersioning)
    }

    override suspend fun forceInitStandardDesignDocument(client: Client, updateIfExists: Boolean, useVersioning: Boolean) {
        val designDocId = designDocName(this.entityClass)
        val generated = StdDesignDocumentFactory().generateFrom(designDocId, this)

        val fromDatabase = client.get(generated.id, DesignDocument::class.java) ?: client.get(designDocId, DesignDocument::class.java)

        val (merged, changed) = fromDatabase?.mergeWith(generated, true) ?: generated to true
        if (changed && (updateIfExists || fromDatabase == null)) {
            client.update(fromDatabase?.let { if (it.id == generated.id) merged.copy(rev = it.rev) else merged } ?: merged)
        }
    }

    override suspend fun initSystemDocumentIfAbsent(dbInstanceUrl: URI) {
        initSystemDocumentIfAbsent(couchDbDispatcher.getClient(dbInstanceUrl))
    }

    override suspend fun initSystemDocumentIfAbsent(client: Client) {
        val designDocId = designDocName("_System")
        val designDocument = client.get(designDocId, DesignDocument::class.java)
        if (designDocument == null) {
            client.update(DesignDocument(designDocId, views = mapOf("revs" to View("function (doc) { emit(doc.java_type, doc._rev); }"))))
        }
    }
}
