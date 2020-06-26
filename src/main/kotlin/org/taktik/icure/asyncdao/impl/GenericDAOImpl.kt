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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.apache.commons.lang3.ArrayUtils
import org.ektorp.DocumentNotFoundException
import org.ektorp.impl.NameConventions
import org.ektorp.support.StdDesignDocumentFactory
import org.slf4j.LoggerFactory
import org.taktik.couchdb.Client
import org.taktik.couchdb.DesignDocument
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.View
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.get
import org.taktik.couchdb.queryView
import org.taktik.couchdb.update
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
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
    private val log = LoggerFactory.getLogger(this.javaClass)

    override suspend fun contains(dbInstanceUrl: URI, groupId: String, id: String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".contains: " + id)
        }
        return client.get(id, entityClass) != null
    }

    override suspend fun hasAny(dbInstanceUrl: URI, groupId: String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return designDocContainsAllView(dbInstanceUrl, groupId) && client.queryView<String, String>(createQuery("all", entityClass).limit(1)).count() > 0
    }

    private suspend fun designDocContainsAllView(dbInstanceUrl: URI, groupId: String): Boolean {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.get<org.taktik.couchdb.DesignDocument>(NameConventions.designDocName(entityClass))?.views?.containsKey("all")
                ?: false
    }

    override fun getAllIds(dbInstanceUrl: URI, groupId: String): Flow<String> = flow {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAllIds")
        }
        if (designDocContainsAllView(dbInstanceUrl, groupId)) {
            val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
            client.queryView<String, String>(createQuery("all", entityClass)).onEach { emit(it.id) }.collect()
        }
    }

    override fun getAll(dbInstanceUrl: URI, groupId: String): Flow<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAll")
        }
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryView(createQuery("all", entityClass).includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()
    }

    override fun getAttachment(dbInstanceUrl: URI, groupId: String, documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.getAttachment(documentId, attachmentId, rev)
    }

    override suspend fun createAttachment(dbInstanceUrl: URI, groupId: String, documentId: String, attachmentId: String, rev: String, contentType: String, data: Flow<ByteBuffer>): String {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.createAttachment(documentId, attachmentId, rev, contentType, data)
    }

    override suspend fun deleteAttachment(dbInstanceUrl: URI, groupId: String, documentId: String, rev: String, attachmentId: String): String {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.deleteAttachment(documentId, attachmentId, rev)
    }

    override suspend fun get(dbInstanceUrl: URI, groupId: String, id: String, vararg options: Option): T? = get(dbInstanceUrl, groupId, id, null, *options)

    override suspend fun get(dbInstanceUrl: URI, groupId: String, id: String, rev: String?, vararg options: Option): T? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }
        try {
            return rev?.let { client.get(id, entityClass, *options) }
                    ?: client.get(id, entityClass, *options)?.let { postLoad(dbInstanceUrl, groupId, it) }
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
        }

        return null
    }

    override fun getList(dbInstanceUrl: URI, groupId: String, ids: Collection<String>): Flow<T> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        return client.get(ids, entityClass).map { this.postLoad(dbInstanceUrl, groupId, it) }
    }

    override fun getList(dbInstanceUrl: URI, groupId: String, ids: Flow<String>): Flow<T> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        return client.get(ids, entityClass).map { this.postLoad(dbInstanceUrl, groupId, it) }
    }

    override suspend fun create(dbInstanceUrl: URI, groupId: String, entity: T): T? {
        return save(dbInstanceUrl, groupId, true, entity)
    }

    override suspend fun save(dbInstanceUrl: URI, groupId: String, entity: T): T? {
        return save(dbInstanceUrl, groupId, null, entity)
    }

    protected open suspend fun save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entity: T): T? {
        var newEntity = newEntity
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
        }

        beforeSave(dbInstanceUrl, groupId, entity)

        if (newEntity == null) {
            newEntity = entity.rev == null
        }

        return if (newEntity) {
            client.create(entity, entityClass)
        } else {
            client.update(entity, entityClass)
            //saveRevHistory(entity, null);
        }.let { afterSave(dbInstanceUrl, groupId, it) }
    }

    protected open suspend fun beforeSave(dbInstanceUrl: URI, groupId: String, entity: T): T {
        return entity
    }

    protected open suspend fun afterSave(dbInstanceUrl: URI, groupId: String, entity: T): T {
        return entity
    }

    protected open suspend fun beforeDelete(dbInstanceUrl: URI, groupId: String, entity: T): T {
        return entity
    }

    protected open suspend fun afterDelete(dbInstanceUrl: URI, groupId: String, entity: T): T {
        return entity
    }

    protected open suspend fun beforeUnDelete(dbInstanceUrl: URI, groupId: String, entity: T): T {
        return entity
    }

    protected open suspend fun afterUnDelete(dbInstanceUrl: URI, groupId: String, entity: T): T {
        return entity
    }

    protected open suspend fun postLoad(dbInstanceUrl: URI, groupId: String, entity: T): T {
        return entity
    }

    override suspend fun remove(dbInstanceUrl: URI, groupId: String, entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        beforeDelete(dbInstanceUrl, groupId, entity).withDeletionDate(deletionDate = System.currentTimeMillis())
        val deleted = client.update(entity, entityClass)
        // After remove
        afterDelete(dbInstanceUrl, groupId, entity)

        return DocIdentifier(deleted.id, deleted.rev)
    }

    override suspend fun unRemove(dbInstanceUrl: URI, groupId: String, entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        return beforeUnDelete(dbInstanceUrl, groupId, entity).let {
            val undeleted = client.update(it.withDeletionDate(null) as T, entityClass)
            // After remove
            afterUnDelete(dbInstanceUrl, groupId, undeleted)
            DocIdentifier(undeleted.id, undeleted.rev)
        }
    }

    override suspend fun purge(dbInstanceUrl: URI, groupId: String, entity: T): DocIdentifier {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".remove: " + entity)
        }
        // Before remove
        return beforeDelete(dbInstanceUrl, groupId, entity).let {
            // Delete
            val purged = client.delete(it)
            // After remove
            afterDelete(dbInstanceUrl, groupId, entity)
            purged
        }
    }

    override fun remove(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            val bulkUpdateResults = client.bulkUpdate(entities.map {
                beforeDelete(dbInstanceUrl, groupId, it).let {
                    it.withDeletionDate(System.currentTimeMillis()) as T
                }
            }, entityClass).onEach { r ->
                entities.firstOrNull { e -> r.id == e.id }?.let { afterDelete(dbInstanceUrl, groupId, it) }
            }
            emitAll(bulkUpdateResults.map { DocIdentifier(it.id, it.rev) })
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    override fun unRemove(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            val bulkUpdateResults = client.bulkUpdate(entities.map {
                beforeUnDelete(dbInstanceUrl, groupId, it).let {
                    it.withDeletionDate(null) as T
                }
            }, entityClass).onEach { r ->
                entities.firstOrNull { e -> r.id == e.id }?.let { afterUnDelete(dbInstanceUrl, groupId, it) }
            }
            emitAll(bulkUpdateResults.map { DocIdentifier(it.id, it.rev) })
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }
    }

    // This function is not reactive, but it doesn't seem to be used at all anyway...
    override suspend fun purge(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) {
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

    override suspend fun <K : Collection<T>> create(dbInstanceUrl: URI, groupId: String, entities: K): Flow<T> {
        return save(dbInstanceUrl, groupId, true, entities)
    }

    override suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId: String, entities: K): Flow<T> {
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

            newEntity = newEntity ?: (entity.rev == null)

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
        emitAll(results.asFlow().mapNotNull { r ->
            (updatedEntities.firstOrNull { u -> r.id == u.id }
                    ?: entities.firstOrNull { u -> r.id == u.id })?.let { e -> r.rev?.let { e.withIdRev(rev = it) as T } ?: e }
        }.map { afterSave(dbInstanceUrl, groupId, it) })
    }

    override suspend fun forceInitStandardDesignDocument(dbInstanceUrl: URI, groupId: String, updateIfExists: Boolean) {
        forceInitStandardDesignDocument(couchDbDispatcher.getClient(dbInstanceUrl, groupId), updateIfExists)
    }

    override suspend fun forceInitStandardDesignDocument(client: Client, updateIfExists: Boolean) {
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

    override suspend fun initSystemDocumentIfAbsent(dbInstanceUrl: URI, groupId: String) {
        initSystemDocumentIfAbsent(couchDbDispatcher.getClient(dbInstanceUrl, groupId))
    }


    override suspend fun initSystemDocumentIfAbsent(client: Client) {
        val designDocId = NameConventions.designDocName("_System")
        val designDocument = client.get(designDocId, DesignDocument::class.java)
        if (designDocument == null) {
            client.update(
                    (org.ektorp.support.DesignDocument(designDocId)
                            .apply { addView("revs", org.ektorp.support.DesignDocument.View("function (doc) { emit(doc.java_type, doc._rev); }")) })
                            .let {
                                DesignDocument(
                                        id = designDocId,
                                        rev = it.revision,
                                        views = it.views.mapValues { View(map= it.value.map, reduce= it.value.reduce) },
                                        updateHandlers = it.updates,
                                        lists = it.lists,
                                        shows = it.shows)
                            }
            )
        }
    }
}
