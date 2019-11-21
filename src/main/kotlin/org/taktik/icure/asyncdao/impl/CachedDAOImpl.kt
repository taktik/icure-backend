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
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.Validate
import org.ektorp.UpdateConflictException
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.exceptions.BulkUpdateConflictException
import java.net.URI
import java.util.*
import java.util.function.Function
import javax.persistence.PersistenceException

abstract class CachedDAOImpl<T : StoredDocument>(clazz: Class<T>, couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator, cacheManager: CacheManager) : GenericDAOImpl<T>(clazz, couchDbDispatcher, idGenerator) {
    private val cache: Cache = cacheManager.getCache(entityClass.name)

    init {
        log.debug("Cache impl = {}", this.cache.nativeCache)
        Validate.notNull(cache, "No cache found for: $entityClass")
    }

    private fun getFullId(dbInstanceUrl: URI, groupId: String?, id: String): String {
        return dbInstanceUrl.toString() + (groupId ?: "FALLBACK") +":$id" // TODO SH correct? no need to use DigestUtils.sha256Hex? is groupId enough or do we need the full database name to handle -base etc.?
    }

    override fun getList(dbInstanceUrl: URI, groupId: String, ids: Collection<String>): Flow<T> {
        val missingKeys = mutableListOf<String>()
        val cachedKeys = mutableListOf<Pair<String, T>>()

        // Get cached values
        for (id in ids) {
            val fullId = getFullId(dbInstanceUrl, groupId, id)
            val value = cache.get(fullId)

            if (value != null) {
                val o = value.get() as T?
                if (o != null) {
                    log.trace("Cache HIT  = {}, {} - {}", fullId, o.id, o.rev)
                    cachedKeys.add(Pair(id, o))
                } else {
                    log.trace("Cache HIT  = {}, Null value", fullId)
                }
            } else {
                log.debug("Cache MISS = {}", fullId)
                missingKeys.add(id)
            }
        }


        if (missingKeys.isEmpty()) {
            return cachedKeys.map { it.second }.asFlow()
        } else {
            return flow {
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
    }

    override suspend fun get(dbInstanceUrl: URI, groupId: String, id: String, vararg options: Option): T? {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        val value = cache.get(fullId)
        if (value == null) {
            log.debug("Cache MISS = {}", fullId)
            val e = super.get(dbInstanceUrl, groupId, id, *options)
            if (e != null) {
                log.debug("Cache SAVE = {}, {} - {}", fullId, e.id, e.rev)
            } else {
                log.debug("Cache Save  = {}, Null value", fullId)
            }
            cache.put(fullId, e)
            return e
        } else {
            val o = value.get() as T?
            if (o != null) {
                log.trace("Cache HIT  = {}, {} - {}", fullId, o.id, o.rev)
            } else {
                log.trace("Cache HIT  = {}, Null value", fullId)
            }
            return o
        }
    }

    fun getFromCache(dbInstanceUrl: URI, groupId: String, id: String): T? {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        val value = cache.get(fullId)
        return if (value == null) {
            log.debug("Cache MISS = {}", fullId)
            null
        } else {
            val o = value.get() as T?
            if (o != null) {
                log.trace("Cache HIT  = {}, {} - {}", fullId, o.id, o.rev)
            } else {
                log.trace("Cache HIT  = {}, Null value", fullId)
            }
            o
        }
    }

    fun putInCache(dbInstanceUrl: URI, groupId: String, key: String, value: T?) {
        val fullId = getFullId(dbInstanceUrl, groupId, key)
        if (value != null) {
            log.debug("Cache SAVE = {}, {} - {}", fullId, value.id, value.rev)
        } else {
            log.debug("Cache SAVE = {}, null placeholder", fullId)
        }
        cache.put(fullId, value)
    }

    open fun evictFromCache(dbInstanceUrl: URI, groupId: String, entity: T) {
        val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))
        val fullId1 = getFullId(dbInstanceUrl, groupId, ALL_ENTITIES_CACHE_KEY)
        log.debug("Cache EVICT= {}", fullId)
        log.debug("Cache EVICT= {}", fullId1)
        cache.evict(fullId)
        cache.evict(fullId1)
    }

    fun evictFromCache(dbInstanceUrl: URI, groupId: String, id: String) {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        val fullId1 = getFullId(dbInstanceUrl, groupId, ALL_ENTITIES_CACHE_KEY)
        log.debug("Cache EVICT= {}", fullId)
        log.debug("Cache EVICT= {}", fullId1)
        cache.evict(fullId)
        cache.evict(fullId1)
    }

    fun evictFromCache(id: String) {
        log.debug("Cache EVICT= {}", id)
        // TODO SH this is wrong? should be the fullId?
        cache.evict(id)
    }

    protected fun getWrapperFromCache(dbInstanceUrl: URI, groupId: String, id: String): Cache.ValueWrapper? {
        val fullId = getFullId(dbInstanceUrl, groupId, id)
        val value = cache.get(fullId)
        if (value != null) {
            log.trace("Cache HIT  = {}, WRAPPER", fullId)
        } else {
            log.debug("Cache MISS = {}, WRAPPER", fullId)
        }

        return value
    }

    override fun getAll(dbInstanceUrl: URI, groupId: String): Flow<T> {
        val fullId = getFullId(dbInstanceUrl, groupId, ALL_ENTITIES_CACHE_KEY)

        val valueWrapper = cache.get(fullId)
        return if (valueWrapper == null) {
            log.debug("Cache MISS = {}", fullId)
            val allEntities = super.getAll(dbInstanceUrl, groupId)
            cache.put(fullId, allEntities)
            log.debug("Cache SAVE = {}", fullId)
            allEntities
        } else {
            log.trace("Cache HIT  = {}", fullId)

            valueWrapper.get() as Flow<T>
        }
    }

    override suspend fun save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entity: T?): T? {
        var entity = entity
        val fullId1 = getFullId(dbInstanceUrl, groupId, ALL_ENTITIES_CACHE_KEY)
        try {
            entity = super.save(dbInstanceUrl, groupId, newEntity, entity)
        } catch (e: UpdateConflictException) {
            val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))

            log.info("Cache EVICT= {}", fullId)
            log.info("Cache EVICT= {}", fullId1)

            cache.evict(fullId)
            cache.evict(fullId1)

            throw e
        }

        putInCache(dbInstanceUrl, groupId, keyManager.getKey(entity), entity)
        cache.evict(fullId1)
        log.debug("Cache EVICT= {}", fullId1)

        return entity
    }

    override suspend fun remove(dbInstanceUrl: URI, groupId: String, entity: T) {
        super.remove(dbInstanceUrl, groupId, entity)
        evictFromCache(dbInstanceUrl, groupId, entity)
    }

    override suspend fun unRemove(dbInstanceUrl: URI, groupId: String, entity: T) {
        super.unRemove(dbInstanceUrl, groupId, entity)
        evictFromCache(dbInstanceUrl, groupId, entity)
    }

    override suspend fun purge(dbInstanceUrl: URI, groupId: String, entity: T) {
        super.purge(dbInstanceUrl, groupId, entity)
        evictFromCache(dbInstanceUrl, groupId, entity)
    }

    override suspend fun remove(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) {
        super.remove(dbInstanceUrl, groupId, entities)
        for (entity in entities) {
            evictFromCache(dbInstanceUrl, groupId, entity)
        }
    }

    override suspend fun unRemove(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) {
        super.unRemove(dbInstanceUrl, groupId, entities)
        for (entity in entities) {
            evictFromCache(dbInstanceUrl, groupId, entity)
        }
    }

    override suspend fun purge(dbInstanceUrl: URI, groupId: String, entities: Collection<T>) {
        super.purge(dbInstanceUrl, groupId, entities)
        for (entity in entities) {
            evictFromCache(dbInstanceUrl, groupId, entity)
        }
    }

    override suspend fun <K : Collection<T>> save(dbInstanceUrl: URI, groupId: String, newEntity: Boolean?, entities: K?): List<T> {
        val fullId1 = getFullId(dbInstanceUrl, groupId, ALL_ENTITIES_CACHE_KEY)
        var savedEntities: List<T> = ArrayList()
        try {
            savedEntities = super.save(dbInstanceUrl, groupId, newEntity, savedEntities)
        } catch (e: UpdateConflictException) {
            for (entity in savedEntities) {
                val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))
                log.debug("Cache EVICT= {}", fullId)
                cache.evict(fullId)
            }

            log.debug("Cache EVICT= {}", fullId1)
            cache.evict(fullId1)

            throw e
        } catch (e: BulkUpdateConflictException) {
            for (entity in savedEntities) {
                val fullId = getFullId(dbInstanceUrl, groupId, keyManager.getKey(entity))
                log.debug("Cache EVICT= {}", fullId)
                cache.evict(fullId)
            }
            log.debug("Cache EVICT= {}", fullId1)
            cache.evict(fullId1)
            throw e
        }

        for (entity in savedEntities) {
            putInCache(dbInstanceUrl, groupId, keyManager.getKey(entity), entity)
        }
        cache.evict(fullId1)
        log.debug("Cache EVICT= {}", fullId1)

        return savedEntities
    }

    companion object {
        internal val ALL_ENTITIES_CACHE_KEY = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
        private val log = LoggerFactory.getLogger(javaClass)
    }
}
