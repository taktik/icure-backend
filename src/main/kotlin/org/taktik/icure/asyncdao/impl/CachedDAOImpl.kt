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

import java.util.Objects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.getFullId

@FlowPreview
@ExperimentalCoroutinesApi
abstract class CachedDAOImpl<T : StoredDocument>(
	clazz: Class<T>,
	couchDbProperties: CouchDbProperties,
	couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	asyncCacheManager: AsyncCacheManager
) : GenericDAOImpl<T>(couchDbProperties, clazz, couchDbDispatcher, idGenerator) {
	private val cache = asyncCacheManager.getCache<String, T>(entityClass.name)
	private val log = LoggerFactory.getLogger(javaClass)

	init {
		log.debug("Cache impl = {}", this.cache.getNativeCache())
	}

	override fun getEntities(ids: Flow<String>) = flow<T> {
		val batch = mutableListOf<String>()
		ids.collect { id ->
			val fullId = getFullId(dbInstanceUrl, id)
			val value = cache.get(fullId)
			if (value != null) {
				if (batch.isNotEmpty()) {
					super.getEntities(batch).collect {
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
			super.getEntities(batch).collect {
				emit(it)
			}
		}
	}

	override fun getEntities(ids: Collection<String>) = flow {
		val missingKeys = mutableListOf<String>()
		val cachedKeys = mutableListOf<Pair<String, T>>()

		// Get cached values
		for (id in ids) {
			val fullId = getFullId(dbInstanceUrl, id)
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
			val entities = super.getEntities(missingKeys).filter { Objects.nonNull(it) }
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
				val fullId = getFullId(dbInstanceUrl, e.id)
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

	override suspend fun get(id: String, vararg options: Option): T? {
		val fullId = getFullId(dbInstanceUrl, id)
		val value = cache.get(fullId)
		return if (value == null) {
			log.debug("Cache MISS = {}", fullId)
			val e = super.get(id, *options)
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

	suspend fun getFromCache(id: String): T? {
		val fullId = getFullId(dbInstanceUrl, id)
		val value = cache.get(fullId)
		return if (value == null) {
			log.debug("Cache MISS = {}", fullId)
			null
		} else {
			log.trace("Cache HIT  = {}, {} - {}", fullId, value.id, value.rev)
			value
		}
	}

	suspend fun putInCache(key: String, value: T?) {
		val fullId = getFullId(dbInstanceUrl, key)
		if (value != null) {
			log.debug("Cache SAVE = {}, {} - {}", fullId, value.id, value.rev)
		} else {
			log.debug("Cache EVICT= {}", fullId)
		}
		value?.let { cache.put(fullId, value) } ?: cache.evict(fullId)
	}

	open suspend fun evictFromCache(entity: T) {
		val fullId = getFullId(dbInstanceUrl, entity.id)
		log.debug("Cache EVICT= {}", fullId)
		cache.evict(fullId)
	}

	suspend fun evictFromCache(id: String) {
		val fullId = getFullId(dbInstanceUrl, id)
		log.debug("Cache EVICT= {}", fullId)
		cache.evict(fullId)
	}

	protected suspend fun getWrapperFromCache(id: String): Cache.ValueWrapper? {
		val fullId = getFullId(dbInstanceUrl, id)
		val value = cache.getWrapper(fullId)
		if (value != null) {
			log.trace("Cache HIT  = {}, WRAPPER", fullId)
		} else {
			log.debug("Cache MISS = {}, WRAPPER", fullId)
		}

		return value
	}

	override fun getEntities(): Flow<T> =
		getEntities(getEntityIds())

	override suspend fun save(newEntity: Boolean?, entity: T): T? {
		try {
			return super.save(newEntity, entity)?.also {
				putInCache(it.id, it)
			}
		} catch (e: CouchDbException) {
			val fullId = getFullId(dbInstanceUrl, entity.id)
			log.info("Cache EVICT= {}", fullId)
			cache.evict(fullId)
			throw e
		}
	}

	override suspend fun remove(entity: T): DocIdentifier {
		val deleted = super.remove(entity)
		evictFromCache(entity)
		return deleted
	}

	override suspend fun unRemove(entity: T): DocIdentifier {
		return super.unRemove(entity).also { evictFromCache(entity) }
	}

	override suspend fun purge(entity: T): DocIdentifier {
		val purged = super.purge(entity)
		evictFromCache(entity)
		return purged
	}

	override fun remove(entities: Collection<T>): Flow<DocIdentifier> {
		return super.remove(entities).onEach {
			it.id?.let { id -> evictFromCache(id) }
		}
	}

	override fun unRemove(entities: Collection<T>): Flow<DocIdentifier> {
		return super.unRemove(entities).onEach {
			it.id?.let { id -> evictFromCache(id) }
		}
	}

	override suspend fun purge(entities: Collection<T>) {
		super.purge(entities)
		for (entity in entities) {
			evictFromCache(entity)
		}
	}

	override fun <K : Collection<T>> save(newEntity: Boolean?, entities: K) = flow {
		val savedEntities = try {
			super.save(newEntity, entities)
		} catch (e: CouchDbException) {
			for (entity in entities) {
				val fullId = getFullId(dbInstanceUrl, entity.id)
				log.debug("Cache EVICT= {}", fullId)
				cache.evict(fullId)
			}
			throw e
		}

		emitAll(
			savedEntities.map { entity ->
				putInCache(entity.id, entity)
				entity
			}
		)
	}
}
