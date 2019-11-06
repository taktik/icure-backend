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

import org.apache.commons.lang3.ArrayUtils
import org.ektorp.AttachmentInputStream
import org.ektorp.BulkDeleteDocument
import org.ektorp.CouchDbConnector
import org.ektorp.DocumentNotFoundException
import org.ektorp.DocumentOperationResult
import org.ektorp.Options
import org.ektorp.UpdateConflictException
import org.ektorp.ViewQuery
import org.ektorp.support.DesignDocument
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.CouchDbICureRepositorySupport
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dao.impl.keymanagers.KeyManager
import org.taktik.icure.dao.impl.keymanagers.UniversallyUniquelyIdentifiableKeyManager
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.exceptions.BulkUpdateConflictException

import javax.persistence.PersistenceException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Collections
import java.util.HashSet
import java.util.Objects
import java.util.function.Function
import java.util.stream.Collectors

abstract class GenericDAOImpl<T : StoredDocument>(val entityClass: Class<T>, val db: CouchDbDispatcher, validGenerator: IDGenerator) : GenericDAO<T> {
    protected val keyManager: KeyManager<T, String>
    init {
        this.keyManager = UniversallyUniquelyIdentifiableKeyManager(idGenerator)
    }

    override fun contains(groupId:String, id: String): Boolean {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".contains: " + id)
        }
        return db.getClient(groupId).contains(id)
    }

    override fun hasAny(): Boolean {
        return designDocContainsAllView() && db.queryView(createQuery("all").limit(1)).size > 0
    }

    override fun getAll(): List<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAll")
        }
        val result = super.getAll()

        result.forEach(Consumer<T> { this.postLoad(it) })

        return result
    }

    override fun getAttachment(documentId: String, attachmentId: String): String {
        val attachmentInputStream = db.getAttachment(documentId, attachmentId)
        val reader = BufferedReader(InputStreamReader(attachmentInputStream))

        val sb = StringBuilder()
        reader.lines().forEach(Consumer<String> { sb.append(it) })
        return sb.toString()
    }

    override fun getAttachmentInputStream(documentId: String, attachmentId: String, rev: String?): AttachmentInputStream {
        return if (rev != null) db.getAttachment(documentId, attachmentId, rev) else db.getAttachment(documentId, attachmentId)
    }

    override fun createAttachment(documentId: String, rev: String, data: AttachmentInputStream): String {
        // return Document Revision
        return db.createAttachment(documentId, rev, data)
    }

    override fun deleteAttachment(documentId: String, rev: String, attachmentId: String): String {
        // return Document Revision
        return db.deleteAttachment(documentId, rev, attachmentId)
    }

    override fun get(id: String, vararg options: Option): T? {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }
        try {
            val result = super.get(id, asEktorpOptions(*options))

            postLoad(result)

            return result
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
        }

        return null
    }

    override fun get(id: String, rev: String): T? {
        try {
            val result = super.get(id, rev)

            postLoad(result)

            return result
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
        }

        return null
    }

    override operator fun get(id: String): T? {
        try {
            val result = super.get(id)

            postLoad(result)

            return result
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
        }

        return null
    }


    fun find(id: String, vararg options: Option): T? {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }

        val result = db.find(type, id, asEktorpOptions(*options))
        if (result != null) {
            postLoad(result)
        }
        return result
    }

    override fun getSet(ids: Collection<String>): Set<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        return HashSet(getList(ids))
    }

    override fun getList(ids: Collection<String>): List<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        val q = ViewQuery()
                .allDocs()
                .includeDocs(true)
                .keys(ids)
        q.isIgnoreNotFound = true
        val result = queryResults(q)

        result.forEach(Consumer<T> { this.postLoad(it) })

        return result
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

    override fun create(entity: T): T? {
        return save(true, entity)
    }

    override fun save(entity: T): T? {
        return save(null, entity)
    }

    protected fun save(newEntity: Boolean?, entity: T?): T? {
        var newEntity = newEntity
        if (entity != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".save: " + entity.id + ":" + entity.rev)
            }

            // Before save
            beforeSave(entity)

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

            if (!newEntity) {
                //saveRevHistory(entity, null);
            }

            // Save entity
            super.update(entity)

            // After save
            afterSave(entity)
        }

        return entity
    }

    protected fun beforeSave(entity: T) {}

    protected fun afterSave(entity: T) {}

    override fun remove(entity: T) {
        if (entity != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".remove: " + entity)
            }
            // Before remove
            beforeDelete(entity)
            // Delete
            super.remove(entity)
            // After remove
            afterDelete(entity)
        }
    }

    override fun unremove(entity: T) {
        if (entity != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".unremove: " + entity)
            }
            beforeUnDelete(entity)
            super.unremove(entity)
            afterUnDelete(entity)
        }
    }

    override fun purge(entity: T?) {
        if (entity != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".remove: " + entity)
            }
            // Before remove
            beforeDelete(entity)
            // Delete
            super.purge(entity)
            // After remove
            afterDelete(entity)
        }
    }


    override fun removeById(id: String?) {
        if (id != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".removeById: " + id)
            }
            // Get entity by id
            val entity = get(id)
            // Delete entity
            remove(entity!!)
        }
    }

    override fun unremoveById(id: String?) {
        if (id != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".unremoveById: " + id)
            }
            // Get entity by id
            val entity = get(id)
            // Delete entity
            unremove(entity!!)
        }
    }

    @Throws(PersistenceException::class)
    override fun purgeById(id: String?) {
        if (id != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".removeById: " + id)
            }
            // Get entity by id
            val entity = get(id)
            // Purge entity
            purge(entity)
        }
    }

    @Throws(PersistenceException::class)
    override fun remove(entities: Collection<T>) {
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            for (entity in entities) {
                beforeDelete(entity)
                entity.deletionDate = System.currentTimeMillis()
            }
            db.executeBulk(entities)
            for (entity in entities) {
                afterDelete(entity)
            }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }

    }

    @Throws(PersistenceException::class)
    override fun unremove(entities: Collection<T>) {
        if (log.isDebugEnabled) {
            log.debug("unremove $entities")
        }
        try {
            for (entity in entities) {
                beforeUnDelete(entity)
                entity.deletionDate = null
            }
            db.executeBulk(entities)
            for (entity in entities) {
                afterUnDelete(entity)
            }
        } catch (e: Exception) {
            throw PersistenceException("failed to unremove entities ", e)
        }

    }

    @Throws(PersistenceException::class)
    override fun purge(entities: Collection<T>) {
        if (log.isDebugEnabled) {
            log.debug("remove $entities")
        }
        try {
            for (entity in entities) {
                beforeDelete(entity)
            }
            db.executeBulk(entities.stream().map { BulkDeleteDocument.of(it) }.collect<List<BulkDeleteDocument>, Any>(Collectors.toList()))
            for (entity in entities) {
                afterDelete(entity)
            }
        } catch (e: Exception) {
            throw PersistenceException("failed to remove entities ", e)
        }

    }

    @Throws(PersistenceException::class)
    override fun purgeByIds(ids: Collection<String>) {
        purge(getList(ids))
    }

    @Throws(PersistenceException::class)
    override fun removeByIds(ids: Collection<String>) {
        remove(getList(ids))
    }

    @Throws(PersistenceException::class)
    override fun unremoveByIds(ids: Collection<String>) {
        unremove(getList(ids))
    }

    override fun <K : Collection<T>> create(entities: K): K? {
        return save(true, entities)
    }

    override fun <K : Collection<T>> save(entities: K): K? {
        return save(null, entities)
    }

    protected fun <K : Collection<T>> save(newEntity: Boolean?, entities: K?): List<T>? {
        var newEntity = newEntity
        if (entities != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".save: " + entities.stream().filter(Predicate<T> { Objects.nonNull(it) }).map { entity -> entity.id + ":" + entity.rev }.collect<String, *>(Collectors.joining(",")))
            }

            val updatedEntities = ArrayList<T>()

            // Before save
            for (entity in entities) {
                beforeSave(entity)

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

            //final Map<String,T> previousEntities = getList(updatedEntities.stream().map(T::getId).collect(Collectors.toList())).stream().collect(Collectors.toMap(T::getId,Function.<T>identity()));
            //updatedEntities.forEach((e)->saveRevHistory(e,previousEntities.get(e.getId())));

            // Save entity
            val orderedEntities = ArrayList(entities)
            val results = db.executeBulk(orderedEntities)

            val conflicts = ArrayList<org.taktik.icure.exceptions.UpdateConflictException>()
            for (r in results) {
                if (r.error != null && r.error == "conflict") {
                    conflicts.add(org.taktik.icure.exceptions.UpdateConflictException(orderedEntities.stream().filter { e -> e.id == r.id }.findAny().orElse(null)))
                }
            }
            if (conflicts.size > 0) {
                throw BulkUpdateConflictException(conflicts, orderedEntities)
            }

            orderedEntities.stream().filter { e -> e.rev != null }.forEach(Consumer<T> { this.afterSave(it) })
        }

        return entities
    }

    @Throws(PersistenceException::class)
    override fun visitAll(callback: Function<T, Boolean>) {
        log.debug("visitAll")
        try {
            val q = ViewQuery()
                    .allDocs()
                    .includeDocs(true)
            db.queryView(q, entityClass).stream().forEach { t -> callback.apply(refresh(t)) }
        } catch (e: Exception) {
            throw PersistenceException("Failed to fetch all entites", e)
        }

    }

    protected fun beforeDelete(entity: T) {}

    protected fun afterDelete(entity: T) {}

    protected fun beforeUnDelete(entity: T) {}

    protected fun afterUnDelete(entity: T) {}

    fun postLoad(entity: T) {
        doFetchRelationship(entity)
    }

    protected fun doFetchRelationship(`object`: T) {}

    override fun refreshIndex() {
        try {
            this.hasAny()
        } catch (ignored: Exception) {
        }

    }

    override fun getAllIds(): List<String> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAllIds")
        }
        return if (designDocContainsAllView()) {
            db.queryView(createQuery("all").includeDocs(false), String::class.java)
        } else ArrayList()

    }

    private fun asEktorpOptions(vararg options: Option): Options {
        val ektorpOptions = Options()
        for (option in options) {
            ektorpOptions.param(option.paramName(), "true")
        }
        return ektorpOptions
    }

    override fun initStandardDesignDocument(group: Group?) {
        if (group == null || db !is CouchDbICureConnector) {
            this.initStandardDesignDocument()
        } else {
            (if (group.servers != null && group.servers.size > 0) group.servers else listOf<String>(null as String?)).forEach { db -> initDesignDocInternal(group.id, db, 0, false) }
        }
    }

    override fun forceInitStandardDesignDocument(group: Group) {
        if (group.id == null || db !is CouchDbICureConnector) {
            this.forceInitStandardDesignDocument()
        } else {
            (if (group.servers != null && group.servers.size > 0) group.servers else listOf<String>(null as String?)).forEach { db -> initDesignDocInternal(group.id, db, 0, true) }
        }
    }

    private fun initDesignDocInternal(groupId: String, dbInstanceUrl: String, invocations: Int, forceUpdate: Boolean) {
        val cdb = if (db is CouchDbICureConnector) db.getCouchDbICureConnector(groupId, dbInstanceUrl, true) else db
        if (cdb is CouchDbICureConnector) {
            cdb.initSystemDesignDocument()
        }

        val designDoc: DesignDocument
        if (cdb.contains(stdDesignDocumentId)) {
            designDoc = getDesignDocumentFactory().getFromDatabase(cdb, stdDesignDocumentId)
        } else {
            designDoc = getDesignDocumentFactory().newDesignDocumentInstance()
            designDoc.id = stdDesignDocumentId
        }
        log.debug("Generating DesignDocument for {}", type)
        val generated = getDesignDocumentFactory().generateFrom(this)
        val changed = designDoc.mergeWith(generated, forceUpdate)
        if (log.isDebugEnabled) {
            debugDesignDoc(designDoc)
        }
        if (changed) {
            log.debug("DesignDocument changed or new. Updating database")
            try {
                cdb.update(designDoc)
            } catch (e: UpdateConflictException) {
                log.warn("Update conflict occurred when trying to update design document: {}", designDoc.id)
                if (invocations == 0) {
                    backOff()
                    log.info("retrying initStandardDesignDocument for design document: {}", designDoc.id)
                    initDesignDocInternal(groupId, dbInstanceUrl, 1, forceUpdate)
                }
            }

        } else {
            log.debug("DesignDocument was unchanged. Database was not updated.")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GenericDAOImpl<*>::class.java)
    }
}
