/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.*
import org.apache.commons.lang3.ArrayUtils
import org.ektorp.DocumentNotFoundException
import org.ektorp.ViewQuery
import org.ektorp.impl.NameConventions
import org.slf4j.LoggerFactory
import org.taktik.couchdb.Client
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.get
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.keymanagers.UniversallyUniquelyIdentifiableKeyManager
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.exceptions.BulkUpdateConflictException
import org.taktik.icure.exceptions.PersistenceException
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

abstract class GenericDAOImpl<T : StoredDocument>(protected val entityClass: Class<T>, protected val couchDbDispatcher: CouchDbDispatcher, protected val idGenerator: IDGenerator) : GenericDAO<T> {
    protected val keyManager = UniversallyUniquelyIdentifiableKeyManager<T>(idGenerator)
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Creates a ViewQuery pre-configured with correct dbPath, design document id and view name.
     * @param viewName
     * @return
     */
    protected fun createQuery(viewName: String): ViewQuery = ViewQuery()
            .designDocId(NameConventions.designDocName(entityClass))
            .viewName(viewName)

    override suspend fun contains(dbInstanceUrl:URI, groupId:String, id: String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".contains: " + id)
        }
        return client.get(id, entityClass) != null
    }

    override suspend fun hasAny(dbInstanceUrl:URI, groupId:String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return designDocContainsAllView(dbInstanceUrl, groupId) && client.queryView<String, String>(createQuery("all").limit(1)).count() > 0
    }

    private suspend fun designDocContainsAllView(dbInstanceUrl:URI, groupId:String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.get<org.taktik.couchdb.DesignDocument>(NameConventions.designDocName(entityClass))?.views?.containsKey("all") ?: false
    }

    override fun getAllIds(dbInstanceUrl:URI, groupId:String): Flow<String> = flow {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAllIds")
        }
        if (designDocContainsAllView(dbInstanceUrl, groupId)) {
            val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
            client.queryView<String, String>(createQuery("all")).onEach { emit(it.id) }.collect()
        }
    }

    override fun getAll(dbInstanceUrl:URI, groupId:String): Flow<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAll")
        }
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryView(createQuery("all").includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()
    }

    override fun getAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.getAttachment(documentId, attachmentId, rev)
    }

    override suspend fun createAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, attachmentId: String, rev: String, contentType: String, data: Flow<ByteBuffer>): String {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.createAttachment(documentId, attachmentId, rev, contentType, data)
    }

    override suspend fun deleteAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, rev: String, attachmentId: String): String {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.deleteAttachment(documentId, attachmentId, rev)
    }

    override suspend fun get(dbInstanceUrl:URI, groupId:String, id: String, vararg options: Option): T? = get(dbInstanceUrl, groupId, id, null, *options)

    override suspend fun get(dbInstanceUrl:URI, groupId:String, id: String, rev: String?, vararg options: Option): T? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }
        try {
            return rev?.let{ client.get(id, entityClass, *options) } ?: client.get(id, entityClass, *options) ?.apply { postLoad(dbInstanceUrl, groupId, this) }
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
        }

        return null
    }

    override fun getList(dbInstanceUrl:URI, groupId:String, ids: Collection<String>): Flow<T> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        return client.get(ids, entityClass).map { this.postLoad(dbInstanceUrl, groupId, it); it }
    }

    override fun newInstance(): T {
        // Instantiate new entity
        val entity: T
        try {
            entity = entityClass.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException("Could not instantiate entity of class " + entityClass.name, e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Could not instantiate entity of class " + entityClass.name, e)
        }

        // Set new key
        keyManager.setNewKey(entity, entityClass.simpleName)

        return entity
    }

    override suspend fun create(dbInstanceUrl:URI, groupId:String, entity: T): T? {
        return save(dbInstanceUrl, groupId, true, entity)
    }

    override suspend fun save(dbInstanceUrl:URI, groupId:String, entity: T): T? {
        return save(dbInstanceUrl, groupId, null, entity)
    }

    protected open suspend fun save(dbInstanceUrl:URI, groupId:String, newEntity: Boolean?, entity: T?): T? {
        if (entity != null) {
            var newEntity = newEntity
            val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
            }

            beforeSave(dbInstanceUrl, groupId, entity)

            // Check if key is missing and if this is a new entity
            val missingKey = entity.id == null

            // Add new key if missing
            if (missingKey) {
                keyManager.setNewKey(entity, entityClass.simpleName)
                newEntity = true
            } else {
                if (newEntity == null) {
                    newEntity = entity.rev == null
                }
            }

            if (newEntity) {
                client.create(entity, entityClass)
            } else {
                client.update(entity, entityClass)
                //saveRevHistory(entity, null);
            }
            afterSave(dbInstanceUrl, groupId, entity)
        }

        return entity
    }

    protected open suspend fun beforeSave(dbInstanceUrl:URI, groupId:String, entity: T) {}

    protected open suspend fun afterSave(dbInstanceUrl:URI, groupId:String, entity: T) {}

    protected fun beforeDelete(dbInstanceUrl:URI, groupId:String, entity: T) {}

    protected fun afterDelete(dbInstanceUrl:URI, groupId:String, entity: T) {}

    protected fun beforeUnDelete(dbInstanceUrl:URI, groupId:String, entity: T) {}

    protected fun afterUnDelete(dbInstanceUrl:URI, groupId:String, entity: T) {}

    open suspend fun postLoad(dbInstanceUrl:URI, groupId:String, entity: T?) {
        doFetchRelationship(dbInstanceUrl, groupId, entity)
    }

    protected fun doFetchRelationship(dbInstanceUrl:URI, groupId:String, entity: T?) {}


    override suspend fun remove(dbInstanceUrl:URI, groupId:String, entity: T) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        beforeDelete(dbInstanceUrl, groupId, entity)
        // Mark soft deleted
        entity.deletionDate = System.currentTimeMillis()
        client.update(entity, entityClass)
        // After remove
        afterDelete(dbInstanceUrl, groupId, entity)
    }

    override suspend fun unRemove(dbInstanceUrl:URI, groupId:String, entity: T) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        beforeUnDelete(dbInstanceUrl, groupId, entity)
        // Mark soft deleted
        entity.deletionDate = null
        client.update(entity, entityClass)
        // After remove
        afterUnDelete(dbInstanceUrl, groupId, entity)
    }

    override suspend fun purge(dbInstanceUrl:URI, groupId:String, entity: T) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        beforeDelete(dbInstanceUrl, groupId, entity)
        // Delete
        client.delete(entity)
        // After remove
        afterDelete(dbInstanceUrl, groupId, entity)
    }

    override suspend fun remove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            for (entity in entities) {
                beforeDelete(dbInstanceUrl, groupId, entity)
                entity.deletionDate = System.currentTimeMillis()
            }
            val bulkUpdateResults = client.bulkUpdate(entities, entityClass).toList()
            for (entity in entities) {
                afterDelete(dbInstanceUrl, groupId, entity)
            }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    override suspend fun unRemove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            for (entity in entities) {
                beforeUnDelete(dbInstanceUrl, groupId, entity)
                entity.deletionDate = null
            }
            val bulkUpdateResults = client.bulkUpdate(entities, entityClass).toList()
            for (entity in entities) {
                afterUnDelete(dbInstanceUrl, groupId, entity)
            }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    override suspend fun purge(dbInstanceUrl:URI, groupId:String, entities: Collection<T>) {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            for (entity in entities) {
                beforeDelete(dbInstanceUrl, groupId, entity)
            }
            val bulkDeleteResults = client.bulkDelete(entities).toList()
            for (entity in entities) {
                afterDelete(dbInstanceUrl, groupId, entity)
            }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    override suspend fun <K : Collection<T>> create(dbInstanceUrl:URI, groupId:String, entities: K): List<T> {
        return save(dbInstanceUrl, groupId, true, entities)
    }

    override suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId:String, entities: K): List<T> {
        return save(dbInstanceUrl, groupId, false, entities)
    }

    override fun<P> pagedViewQuery(viewName: String, startKey: P?, endKey: P?, pagination: PaginationOffset<P>, descending: Boolean): ViewQuery {
        val DEFAULT_LIMIT = 1000
        val limit = if (pagination.limit != null) pagination.limit else DEFAULT_LIMIT

        var viewQuery = createQuery(viewName)
                .startKey(startKey)
                .includeDocs(true)
                .reduce(false)
                .startDocId(pagination.startDocumentId)
                .limit(limit)
                .descending(descending)

        if (endKey != null) {
            viewQuery = viewQuery.endKey(endKey)
        }

        return viewQuery
    }

    override fun<P> pagedViewQueryOfIds(client: Client, viewName: String, startKey: P?, endKey: P?, pagination: PaginationOffset<P>): ViewQuery {
        val DEFAULT_LIMIT = 1000
        val limit = if (pagination.limit != null) pagination.limit else DEFAULT_LIMIT

        var viewQuery = createQuery(viewName)
                .startKey(startKey)
                .includeDocs(false)
                .reduce(false)
                .limit(limit)

        if (endKey != null) {
            viewQuery = viewQuery.endKey(endKey)
        }

        return viewQuery
    }

    protected open suspend fun <K : Collection<T>> save(dbInstanceUrl:URI, groupId:String, newEntity: Boolean?, entities: K?): List<T> {
        var newEntity = newEntity
        if (entities != null) {
            val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".save: " + entities.mapNotNull { entity -> entity.id + ":" + entity.rev })
            }

            val updatedEntities = ArrayList<T>()

            // Before save
            for (entity in entities) {
                beforeSave(dbInstanceUrl, groupId, entity)

                // Check if key is missing and if this is a new entity
                val missingKey = entity.id == null
                newEntity = newEntity ?: (missingKey || entity.rev == null)

                // Add new key if missing
                if (missingKey) {
                    keyManager.setNewKey(entity, entityClass.simpleName)
                }

                if (!newEntity) {
                    updatedEntities.add(entity)
                }
            }

            // Save entity
            val orderedEntities = ArrayList(entities)
            val results = client.bulkUpdate(orderedEntities, entityClass).toList()
            val conflicts = ArrayList<org.taktik.icure.exceptions.UpdateConflictException>()
            for (r in results) {
                if (r.error != null && r.error == "conflict") {
                    conflicts.add(org.taktik.icure.exceptions.UpdateConflictException(orderedEntities.stream().filter { e -> e.id == r.id }.findAny().orElse(null)))
                }
            }
            if (conflicts.size > 0) {
                throw BulkUpdateConflictException(conflicts, orderedEntities)
            }

            orderedEntities.filter { e -> e.rev != null }.forEach { this.afterSave(dbInstanceUrl, groupId, it) }
            return updatedEntities
        }

        return listOf()
    }
}
