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
import ma.glasnost.orika.MapperFacade
import org.ektorp.UpdateConflictException
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.exceptions.BulkUpdateConflictException
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.getFullId
import java.net.URI
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
abstract class CachedDAOImpl<T : StoredDocument>(clazz: Class<T>, couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, AsyncCacheManager: AsyncCacheManager, mapper: MapperFacade) : GenericDAOImpl<T>(clazz, couchDbDispatcher, idGenerator, mapper) {
    private val cache = AsyncCacheManager.getCache<String, T>(entityClass.name)
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.debug("Cache impl = {}", this.cache.getNativeCache())
    }

    override fun getList(dbInstanceUrl: URI, groupId: String, ids: Flow<String>) = flow<T> {
        val batch = mutableListOf<String>()
        ids.collect {id ->
            val fullId = getFullId(dbInstanceUrl, groupId, id)
            val value = cache.get(fullId)
            if (value != null) {
                if (batch.isNotEmpty()) {
                    super.getList(dbInstanceUrl, groupId, batch).collect {
                        emit(it)
                    }
                    batch.clear()
                }
                log.trace("Cache HIT  = {}, {} - {}", fullId, value.id, value.rev)
                emit(value)
            } else {
                log.trace("Cache HIT  = {}, Null value", fullId)
                batch.add(id)
            }
        }

        if (batch.isNotEmpty()) {
            super.getList(dbInstanceUrl, groupId, batch).collect {
                emit(it)
            }
        }
    }

    override fun getList(dbInstanceUrl: URI, groupId: String, ids: Collection<String>) = flow {
        val missingKeys = mutableListOf<String>()
        val cachedKeys = mutableListOf<Pair<String, T>>()

        // Get cached values
        for (id in ids) {
            val fullId = getFullId(dbInstanceUrl, groupId, id)
            val value = cache.get(fullId)
            value?.let {
                log.trace("Cache HIT  = {}, {} - {}", fullId, value.id, value.rev)
                cachedKeys.add(Pair(id, value))
            } ?: log.debug("Cache MISS = {}", fullId).also { missingKeys.add(id) }

        }

        if (missingKeys.isEmpty()) {
            emitAll(cachedKeys.map { it.second }.asFlow())
        } else {
                // Get missing values from storage
                val entities = super.getList(dbInstanceUrl, groupId, missingKeys).filter { Objects.nonNull(it) }
                // Interleave missing and cached values to preserve original ordering
                var currentIndex = 0 // index of current element in [ids]
                var currentCachedIndex = 0 // index of current element in [cachedKeys]
                entities.collect { e ->
                    // if [e] doesn't match current element, it means it's cached
                    // The 2 first conditions should never match, but will avoid IndexOutOfBoundExceptions in case of odd data.
                    while (currentIndex < ids.size && currentCachedIndex < cachedKeys.size && e.id != ids.elementAt(currentIndex)) {
                        emit(cachedKeys[currentCachedIndex].second)
                        currentIndex++
                        currentCachedIndex++
                    }
                    // We are finally on [e], cache and emit it, and process next flow element
                    val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(e))
                    log.debug("Cache SAVE = {}, {} - {}", fullId, e.id, e.rev)
                    cache.put(fullId, e)

                    emit(e)
                    currentIndex++
                }
                while (currentCachedIndex < cachedKeys.size) {
                    emit(cachedKeys[currentCachedIndex].second)
                    currentCachedIndex++
                    currentIndex++
                }
        }
    }

    override suspend fun get(dbInstanceUrl: URI, groupId: String, id: String, vararg options: Option): T? {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        val value = cache.get(fullId)
        return if (value == null) {
            log.debug("Cache MISS = {}", fullId)
            val e = super.get(dbInstanceUrl, groupId, id, *options)
            if (e != null) {
                log.debug("Cache SAVE = {}, {} - {}", fullId, e.id, e.rev)
            } else {
                log.debug("Cache Save  = {}, Null value", fullId)
            }
            e?.let { cache.put(fullId, e) }
            log.trace("Cache HIT  = {}, Null value", fullId)
            e
        } else {
            log.trace("Cache HIT  = {}, {} - {}", fullId, value.id, value.rev)
            value
        }
    }

    suspend fun getFromCache(dbInstanceUrl: URI, groupId: String?, id: String): T? {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        val value = cache.get(fullId)
        return if (value == null) {
            log.debug("Cache MISS = {}", fullId)
            null
        } else {
            log.trace("Cache HIT  = {}, {} - {}", fullId, value.id, value.rev)
            value
        }
    }

    suspend fun putInCache(dbInstanceUrl: URI, groupId: String?, key: String, value: T?) {
        val fullId = getFullId(dbInstanceUrl, groupId, key)
        if (value != null) {
            log.debug("Cache SAVE = {}, {} - {}", fullId, value.id, value.rev)
        } else {
            log.debug("Cache SAVE = {}, null placeholder", fullId)
        }
        value?.let { cache.put(fullId, value) }
    }

    open suspend fun evictFromCache(dbInstanceUrl: URI, groupId: String, entity: T) {
        val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))
        log.debug("Cache EVICT= {}", fullId)
        cache.evict(fullId)
    }

    suspend fun evictFromCache(dbInstanceUrl: URI, groupId: String?, id: String) {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        log.debug("Cache EVICT= {}", fullId)
        cache.evict(fullId)
    }

    protected suspend fun getWrapperFromCache(dbInstanceUrl: URI, groupId: String?, id: String): Cache.ValueWrapper? {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        val value = cache.getWrapper(fullId)
        if (value != null) {
            log.trace("Cache HIT  = {}, WRAPPER", fullId)
        } else {
            log.debug("Cache MISS = {}, WRAPPER", fullId)
        }

        return value
    }

    override fun getAll(dbInstanceUrl: URI, groupId: String): Flow<T> =
            getList(dbInstanceUrl, groupId, getAllIds(dbInstanceUrl, groupId))

    override suspend fun save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entity: T): T? {
        var savedEntity: T? = entity
        try {
            savedEntity = super.save(dbInstanceUrl, groupId, newEntity, entity) // TODO MB : the saved entity should have the rev
        } catch (e: UpdateConflictException) {
            val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))
            log.info("Cache EVICT= {}", fullId)
            cache.evict(fullId)
            throw e
        }
        val updatedEntity = get(dbInstanceUrl, groupId, savedEntity!!.id)
       putInCache(dbInstanceUrl, groupId, keyManager.getKey(savedEntity), updatedEntity)
        return entity
    }

    override suspend fun remove(dbInstanceUrl: URI, groupId: String, entity: T): DocIdentifier {
        val deleted = super.remove(dbInstanceUrl, groupId, entity)
        evictFromCache(dbInstanceUrl, groupId, entity)
        return deleted
    }

    override suspend fun unRemove(dbInstanceUrl: URI, groupId: String, entity: T): DocIdentifier {
        return super.unRemove(dbInstanceUrl, groupId, entity).also { evictFromCache(dbInstanceUrl, groupId, entity) }
    }

    override suspend fun purge(dbInstanceUrl: URI, groupId: String, entity: T): DocIdentifier {
        val purged = super.purge(dbInstanceUrl, groupId, entity)
        evictFromCache(dbInstanceUrl, groupId, entity)
        return purged
    }

    override fun remove(dbInstanceUrl: URI, groupId: String, entities: Collection<T>): Flow<DocIdentifier> {
        return super.remove(dbInstanceUrl, groupId, entities).onEach {
            evictFromCache(dbInstanceUrl, groupId, it.id)
        }
    }

    override fun unRemove(dbInstanceUrl: URI, groupId: String, entities: Collection<T>): Flow<DocIdentifier> {
        return super.unRemove(dbInstanceUrl, groupId, entities).onEach {
            evictFromCache(dbInstanceUrl, groupId, it.id)
        }
    }

    override suspend fun purge(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) {
        super.purge(dbInstanceUrl, groupId, entities)
        for (entity in entities) {
            evictFromCache(dbInstanceUrl, groupId, entity)
        }
    }

    override suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entities: K): Flow<T> {
        val savedEntities = try {
            super.save(dbInstanceUrl, groupId, newEntity, entities)
        } catch (e: UpdateConflictException) {
            for (entity in entities) {
                val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))
                log.debug("Cache EVICT= {}", fullId)
                cache.evict(fullId)
            }
            throw e
        } catch (e: BulkUpdateConflictException) {
            for (entity in entities) {
                val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))
                log.debug("Cache EVICT= {}", fullId)
                cache.evict(fullId)
            }
            throw e
        }

        return savedEntities.onEach { entity ->
            putInCache(dbInstanceUrl, groupId, keyManager.getKey(entity), entity)
        }
    }

}
