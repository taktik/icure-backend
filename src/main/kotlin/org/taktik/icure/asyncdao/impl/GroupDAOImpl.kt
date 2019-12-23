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
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.GroupDAO
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.Group
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.getFullId
import java.net.URI

@ExperimentalCoroutinesApi
@FlowPreview
@Repository("groupDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Group' && !doc.deleted) emit( null, doc._id )}")
class GroupDAOImpl(val couchDbProperties: CouchDbProperties, @Qualifier("configCouchDbDispatcher") val couchDbDispatcher: CouchDbDispatcher, val idGenerator: IDGenerator, @Qualifier("entitiesCacheManager") final val cacheManager: CacheManager) : GroupDAO {
    private val cache: Cache = cacheManager.getCache(Group::class.java.name)
            ?: throw UnsupportedOperationException("No cache found for: ${Group::class.java.name}")
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
                val o = value.get() as Group?
                if (o != null) {
                    log.trace("Cache HIT  = {}, {} - {}", fullId, o.id, o.rev)
                    emit(o)
                } else {
                    log.trace("Cache HIT  = {}, Null value", fullId)
                }
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
        if (log.isDebugEnabled) {
            log.debug(Group::class.java.simpleName + ".get: " + id + " [" + ArrayUtils.toString(options) + "]")
        }
        return try {
            return rev?.let{ client.get(id, Group::class.java, *options) } ?: client.get(id, Group::class.java, *options)
        } catch (e: DocumentNotFoundException) {
            log.warn("Document not found", e)
            null
        }
    }

    override suspend fun save(group: Group): Group? {
        val dbInstanceUri = URI(couchDbProperties.url)
        val client = couchDbDispatcher.getClient(dbInstanceUri, null)
        if (log.isDebugEnabled) {
            log.debug(Group::class.java.simpleName + ".save: " + group.id + ":" + group.rev)
        }
        return when {
            group.id == null -> {
                group.id = idGenerator.newGUID().toString()
                client.create(group, Group::class.java)
            }
            group.rev == null -> {
                client.create(group, Group::class.java)
            }
            else -> {
                client.update(group, Group::class.java)
            }
        }
    }

    internal fun getListInternal(dbInstanceUri: URI, ids: List<String>) =
            couchDbDispatcher.getClient(dbInstanceUri, null).queryView(ViewQuery()
                    .designDocId(NameConventions.designDocName(Group::class.java))
                    .viewName("all").keys(ids).includeDocs(true), String::class.java, String::class.java, Group::class.java).map { (it as? ViewRowWithDoc<*, *, Group?>)?.doc }.filterNotNull()
}
