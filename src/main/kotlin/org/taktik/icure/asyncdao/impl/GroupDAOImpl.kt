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
import org.ektorp.support.View
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier

import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Group
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.getFullId
import java.net.URI

@ExperimentalCoroutinesApi
@FlowPreview
@Repository("groupDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Group' && !doc.deleted) emit( null, doc._id )}")
class GroupDAOImpl(val couchDbProperties: CouchDbProperties, @Qualifier("configCouchDbDispatcher") val couchDbDispatcher: CouchDbDispatcher, val idGenerator: IDGenerator, @Qualifier("asyncCacheManager") final val AsyncCacheManager: AsyncCacheManager) : GroupDAO {
    private val cache = AsyncCacheManager.getCache<String, Group>(Group::class.java.name)
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getList(ids: Flow<String>) = flow<Group> {
        val dbInstanceUri = URI(couchDbProperties.url)
        val batch = mutableListOf<String>()
        ids.collect { id ->
            val fullId = getFullId(dbInstanceUri, null, id)
            val value = cache.get(fullId)
            if (value != null) {
                if (batch.isNotEmpty()) {
                    getListInternal(dbInstanceUri, batch).collect {
                        emit(it)
                    }
                    batch.clear()
                }
                log.trace("Cache HIT  = {}, {} - {}", fullId, value.id, value.rev)
                emit(value)
            } else {
                batch.add(id)
            }
        }

        if (batch.isNotEmpty()) {
            getListInternal(dbInstanceUri, batch).collect {
                emit(it)
            }
        }
    }

    override fun getAll() = getList(getAllIds())

    override fun getAllIds(): Flow<String> {
        val dbInstanceUri = URI(couchDbProperties.url)

        if (log.isDebugEnabled) {
            log.debug(Group::class.java.simpleName + ".getAllIds")
        }
        return couchDbDispatcher.getClient(dbInstanceUri, null).queryView<String, String>(ViewQuery()
                .designDocId(NameConventions.designDocName(Group::class.java))
                .viewName("all").includeDocs(false)).map { it.id }.filterNotNull()
    }

    override suspend fun get(id: String, vararg options: Option): Group? = get(id, null, *options)

    override suspend fun get(id: String, rev: String?, vararg options: Option): Group? {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)

        if (rev == null) {
            val fullId = getFullId(dbInstanceUri, null, id)
            val value = cache.get(fullId)
            if (value != null) {
                log.trace("Cache HIT  = {}, {} - {}", fullId, value.id, value.rev)
                return value
            }
            if (log.isDebugEnabled) {
                log.debug(Group::class.java.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
            }
            return try {
                client.get(id, Group::class.java, *options)?.also { cache.put(fullId, it) }
            } catch (e: DocumentNotFoundException) {
                log.warn("Document not found", e)
                null
            }
        } else {
            if (log.isDebugEnabled) {
                log.debug(Group::class.java.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
            }
            return try {
                client.get(id, rev, Group::class.java, *options)
            } catch (e: DocumentNotFoundException) {
                log.warn("Document not found", e)
                null
            }
        }
    }

    override suspend fun save(group: Group): Group? {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)
        if (log.isDebugEnabled) {
            log.debug(Group::class.java.simpleName + ".save: " + group.id + ":" + group.rev)
        }
        val fullId = group.id?.let { getFullId(dbInstanceUri, null, it) }
        cache.evict(fullId)
        return when {
            group.id == null -> {
                group.id = idGenerator.newGUID().toString()
                client.create(group, Group::class.java).also { fullId?.let { it1 -> cache.put(it1, it) } }
            }
            group.rev == null -> {
                client.create(group, Group::class.java).also { fullId?.let { it1 -> cache.put(it1, it) } }
            }
            else -> {
                client.update(group, Group::class.java).also { fullId?.let { it1 -> cache.put(it1, it) } }
            }
        }
    }

    internal fun getListInternal(dbInstanceUri: URI, ids: List<String>) =
            couchDbDispatcher.getClient(dbInstanceUri, null).get(ids, Group::class.java).filterNotNull()
}
