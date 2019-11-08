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
import org.ektorp.*
import org.ektorp.impl.NameConventions
import org.ektorp.support.DesignDocument
import org.slf4j.LoggerFactory
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.get
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.keymanagers.UniversallyUniquelyIdentifiableKeyManager
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.exceptions.BulkUpdateConflictException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import javax.persistence.PersistenceException

abstract class GenericDAOImpl<T : StoredDocument>(protected val entityClass: Class<T>, protected val couchDbDispatcher: CouchDbDispatcher, protected val idGenerator: IDGenerator) : GenericDAO<T> {
    private val keyManager = UniversallyUniquelyIdentifiableKeyManager<T>(idGenerator)

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

    override fun getAll(dbInstanceUrl:URI, groupId:String): Flow<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".getAll")
        }
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryView(createQuery("all").includeDocs(true), String::class.java, String::class.java, entityClass).map { (it as? ViewRowWithDoc<*, *, T?>)?.doc }.filterNotNull()
    }

    override suspend fun getAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, attachmentId: String): String {
        val attachmentInputStream = db.getAttachment(documentId, attachmentId)
        val reader = BufferedReader(InputStreamReader(attachmentInputStream))

        val sb = StringBuilder()
        reader.lines().forEach(Consumer<String> { sb.append(it) })
        return sb.toString()
    }

    override suspend fun getAttachmentInputStream(dbInstanceUrl:URI, groupId:String, documentId: String, attachmentId: String, rev: String?): AttachmentInputStream {
        return if (rev != null) db.getAttachment(documentId, attachmentId, rev) else db.getAttachment(documentId, attachmentId)
    }

    override suspend fun createAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, rev: String, data: AttachmentInputStream): String {
        // return Document Revision
        return db.createAttachment(documentId, rev, data)
    }

    override suspend fun deleteAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, rev: String, attachmentId: String): String {
        // return Document Revision
        return db.deleteAttachment(documentId, rev, attachmentId)
    }

    override suspend fun get(dbInstanceUrl:URI, groupId:String, id: String, vararg options: Option): T? {
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

    override suspend fun get(dbInstanceUrl:URI, groupId:String, id: String, rev: String): T? {
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


    fun find(dbInstanceUrl:URI, groupId:String, id: String, vararg options: Option): T? {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }

        val result = db.find(type, id, asEktorpOptions(*options))
        if (result != null) {
            postLoad(result)
        }
        return result
    }

    override suspend fun getSet(dbInstanceUrl:URI, groupId:String, ids: Collection<String>): Set<T> {
        if (log.isDebugEnabled) {
            log.debug(entityClass.simpleName + ".get: " + ids)
        }
        return HashSet(getList(ids))
    }

    override suspend fun getList(dbInstanceUrl:URI, groupId:String, ids: Collection<String>): List<T> {
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

    override suspend fun newInstance(): T {
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
        return save(true, entity)
    }

    override suspend fun save(dbInstanceUrl:URI, groupId:String, entity: T): T? {
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

    protected fun beforeSave(dbInstanceUrl:URI, groupId:String, entity: T) {}

    protected fun afterSave(dbInstanceUrl:URI, groupId:String, entity: T) {}

    override suspend fun remove(dbInstanceUrl:URI, groupId:String, entity: T) {
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

    override suspend fun unremove(dbInstanceUrl:URI, groupId:String, entity: T) {
        if (entity != null) {
            if (log.isDebugEnabled) {
                log.debug(entityClass.simpleName + ".unremove: " + entity)
            }
            beforeUnDelete(entity)
            super.unremove(entity)
            afterUnDelete(entity)
        }
    }

    override suspend fun purge(dbInstanceUrl:URI, groupId:String, entity: T?) {
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


    override suspend fun removeById(dbInstanceUrl:URI, groupId:String, id: String?) {
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

    override suspend fun unremoveById(dbInstanceUrl:URI, groupId:String, id: String?) {
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
    override suspend fun purgeById(dbInstanceUrl:URI, groupId:String, id: String?) {
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
    override suspend fun remove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>) {
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
    override suspend fun unremove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>) {
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
    override suspend fun purge(dbInstanceUrl:URI, groupId:String, entities: Collection<T>) {
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
    override suspend fun purgeByIds(dbInstanceUrl:URI, groupId:String, ids: Collection<String>) {
        purge(getList(ids))
    }

    @Throws(PersistenceException::class)
    override suspend fun removeByIds(dbInstanceUrl:URI, groupId:String, ids: Collection<String>) {
        remove(getList(ids))
    }

    @Throws(PersistenceException::class)
    override suspend fun unremoveByIds(dbInstanceUrl:URI, groupId:String, ids: Collection<String>) {
        unremove(getList(ids))
    }

    override suspend fun <K : Collection<T>> create(dbInstanceUrl:URI, groupId:String, entities: K): K? {
        return save(true, entities)
    }

    override suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId:String, entities: K): K {
        return save(null, entities)
    }

    protected fun <K : Collection<T>> save(dbInstanceUrl:URI, groupId:String, newEntity: Boolean?, entities: K?): List<T>? {
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
    override suspend fun visitAll(callback: Function<T, Boolean>) {
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

    override suspend fun refreshIndex() {
        try {
            this.hasAny()
        } catch (ignored: Exception) {
        }

    }

    override suspend fun getAllIds(): List<String> {
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

    override suspend fun initStandardDesignDocument(group: Group?) {
        if (group == null || db !is CouchDbICureConnector) {
            this.initStandardDesignDocument()
        } else {
            (if (group.servers != null && group.servers.size > 0) group.servers else listOf<String>(null as String?)).forEach { db -> initDesignDocInternal(group.id, db, 0, false) }
        }
    }

    override suspend fun forceInitStandardDesignDocument(group: Group) {
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
