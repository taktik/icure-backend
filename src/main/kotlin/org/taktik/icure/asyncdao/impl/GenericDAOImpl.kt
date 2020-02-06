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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.apache.commons.lang3.ArrayUtils
import org.ektorp.DocumentNotFoundException
import org.ektorp.ViewQuery
import org.ektorp.impl.NameConventions
import org.slf4j.LoggerFactory
import org.taktik.couchdb.*
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.keymanagers.UniversallyUniquelyIdentifiableKeyManager
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.exceptions.BulkUpdateConflictException
import org.taktik.icure.exceptions.PersistenceException
import org.taktik.icure.exceptions.UpdateConflictException
import org.taktik.icure.utils.createQuery
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
abstract class GenericDAOImpl<T : StoredDocument>(protected val entityClass: Class<T>, protected val couchDbDispatcher: CouchDbDispatcher, protected val idGenerator: IDGenerator) : GenericDAO<T> {
    protected val keyManager = UniversallyUniquelyIdentifiableKeyManager<T>(idGenerator)
    private val log = LoggerFactory.getLogger(this.javaClass)

    override suspend fun contains(dbInstanceUrl:URI, groupId:String, id: String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".contains: " + id)
        }
        return client.get(id, entityClass) != null
    }

    override suspend fun hasAny(dbInstanceUrl:URI, groupId:String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return designDocContainsAllView(dbInstanceUrl, groupId) && client.queryView<String, String>(createQuery("all", entityClass).limit(1)).count() > 0
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
            client.queryView<String, String>(createQuery("all", entityClass)).onEach { emit(it.id) }.collect()
        }
    }

    override fun getAll(dbInstanceUrl:URI, groupId:String): Flow<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAll")
        }
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryView(createQuery("all", entityClass).includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()
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

    override fun getList(dbInstanceUrl:URI, groupId:String, ids: Flow<String>): Flow<T> {
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

    protected open suspend fun save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entity: T): T? {
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


    override suspend fun remove(dbInstanceUrl:URI, groupId:String, entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        beforeDelete(dbInstanceUrl, groupId, entity)
        // Mark soft deleted
        entity.deletionDate = System.currentTimeMillis()
        val deleted = client.update(entity, entityClass)
        // After remove
        afterDelete(dbInstanceUrl, groupId, entity)

        return DocIdentifier(deleted.id, deleted.rev)
    }

    override suspend fun unRemove(dbInstanceUrl:URI, groupId:String, entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        beforeUnDelete(dbInstanceUrl, groupId, entity)
        // Mark soft deleted
        entity.deletionDate = null
        val undeleted = client.update(entity, entityClass)
        // After remove
        afterUnDelete(dbInstanceUrl, groupId, entity)

        return DocIdentifier(undeleted.id, undeleted.rev)
    }

    override suspend fun purge(dbInstanceUrl:URI, groupId:String, entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        beforeDelete(dbInstanceUrl, groupId, entity)
        // Delete
        val purged = client.delete(entity)
        // After remove
        afterDelete(dbInstanceUrl, groupId, entity)

        return purged
    }

    override fun remove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>): Flow<DocIdentifier> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            for (entity in entities) {
                beforeDelete(dbInstanceUrl, groupId, entity)
                entity.deletionDate = System.currentTimeMillis()
            }
            val bulkUpdateResults = client.bulkUpdate(entities, entityClass).onEach { r ->
                entities.firstOrNull { e -> r.id == e.id }?.let { afterDelete(dbInstanceUrl, groupId, it) }
            }
            return bulkUpdateResults.map { DocIdentifier(it.id, it.rev) }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    override fun unRemove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>): Flow<DocIdentifier> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            for (entity in entities) {
                beforeUnDelete(dbInstanceUrl, groupId, entity)
                entity.deletionDate = null
            }
            val bulkUpdateResults = client.bulkUpdate(entities, entityClass).onEach { r ->
                entities.firstOrNull { e -> r.id == e.id }?.let { afterUnDelete(dbInstanceUrl, groupId, it) }
            }
            return bulkUpdateResults.map { DocIdentifier(it.id, it.rev) }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    // This function is not reactive, but it doesn't seem to be used at all anyway...
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

    override suspend fun <K : Collection<T>> create(dbInstanceUrl:URI, groupId:String, entities: K): Flow<T> {
        return save(dbInstanceUrl, groupId, true, entities)
    }

    override suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId:String, entities: K): Flow<T> {
        return save(dbInstanceUrl, groupId, false, entities)
    }

    // TODO SH later: make sure this is correct
    protected open suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entities: K): Flow<T> = flow {
        var newEntity = newEntity
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

        val conflicts = results.filter { it.error == "conflict" }.map { r -> UpdateConflictException(orderedEntities.firstOrNull { e -> e.id == r.id }) }.toList()
        if (conflicts.isNotEmpty()) {
            throw BulkUpdateConflictException(conflicts, orderedEntities)
        }
        emitAll(results.asFlow().mapNotNull { r -> updatedEntities.firstOrNull { u -> r.id == u.id } ?: entities.firstOrNull { u -> r.id == u.id } }.onEach { afterSave(dbInstanceUrl, groupId, it) })
    }
}
