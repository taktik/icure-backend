package org.taktik.icure.testutils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.apache.http.HttpStatus
import org.taktik.couchdb.BulkUpdateResult
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.entity.Versionable
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.InternalDAO

// Not thread-safe
class FakeInternalDAO<T : Versionable<String>> : InternalDAO<T> {
	private val entitiesMap = mutableMapOf<String, EntitiesByVersion<T>>()

	override fun getEntities(): Flow<T> =
		entitiesMap.values.map { it.latest }.asFlow()

	override fun getEntityIds(): Flow<String> =
		entitiesMap.keys.toList().asFlow()

	override suspend fun get(id: String, vararg options: Option): T? =
		get(id, null, *options)

	override suspend fun get(id: String, rev: String?, vararg options: Option): T? {
		require(options.isEmpty()) { "Options are not supported by mock" }
		return if (rev != null) entitiesMap[id]?.let { it.all[rev] } else entitiesMap[id]?.latest
	}

	override fun getEntities(ids: Collection<String>): Flow<T> {
		val idsSet = ids.toSet()
		return entitiesMap.mapNotNull { if (it.value.latest.id in idsSet) it.value.latest else null }.asFlow()
	}

	override suspend fun save(entity: T): T {
		val entityWithRev = entity.withIdRev(null, entity.rev?.let { (it.toInt() + 1).toString() } ?: "0") as T
		val versions = entitiesMap.getOrPut(entity.id) { EntitiesByVersion(entityWithRev, mutableMapOf(entityWithRev.rev!! to entityWithRev)) }
		if (versions.latest != entityWithRev) {
			versions.latest = entityWithRev
			versions.all[entityWithRev.rev!!] = entityWithRev
		}
		return entityWithRev
	}

	override suspend fun update(entity: T): T =
		entitiesMap[entity.id]?.let { updateExisting(entity, it) }
			?: throw CouchDbException("Trying to update a non-existing entity", HttpStatus.SC_NOT_FOUND, "Fake internal dao")

	@Suppress("UNCHECKED_CAST")
	private fun addNew(entity: T) : T {
		require(entity.rev == null) { "To add a new entity is rev should be null" }
		return (entity.withIdRev(null, "0") as T).also {
			entitiesMap[entity.id] = EntitiesByVersion(it, mutableMapOf(it.rev!! to it))
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun updateExisting(entity: T, previousVersions: EntitiesByVersion<T>): T {
		if (entity.rev != previousVersions.latest.rev) throw CouchDbConflictException(
			"Rev does not match latest rev",
			HttpStatus.SC_CONFLICT,
			"Fake internal dao"
		)
		return (entity.withIdRev(null, (entity.rev!!.toInt() + 1).toString()) as T).also {
			previousVersions.latest = it
			previousVersions.all[it.rev!!] = it
		}
	}

	override fun save(entities: Flow<T>): Flow<DocIdentifier> = flow {
		entities.collect { save(it).also { updatedEntity -> emit(DocIdentifier(updatedEntity.id, updatedEntity.rev)) } }
	}

	override fun save(entities: List<T>): Flow<DocIdentifier> =
		save(entities.asFlow())

	override suspend fun purge(entity: T): DocIdentifier =
		entitiesMap.remove(entity.id)?.latest.let { DocIdentifier(it?.id, it?.rev) }

	override fun purge(entities: Flow<T>): Flow<BulkUpdateResult> = flow {
		entities.collect {
			val purgedInfo = purge(it)
			if (purgedInfo.id != null) {
				emit(BulkUpdateResult(it.id, purgedInfo.rev, true, null, null))
			} else {
				emit(BulkUpdateResult(it.id, purgedInfo.rev, false, "No entity with matching id", null))
			}
		}
	}

	override suspend fun remove(entity: T): DocIdentifier =
		purge(entity)

	override fun remove(entities: Flow<T>): Flow<BulkUpdateResult> =
		purge(entities)

	override suspend fun forceInitStandardDesignDocument(updateIfExists: Boolean) {
		// Do nothing
	}

	private data class EntitiesByVersion<T : Versionable<String>>(
		var latest: T,
		var all: MutableMap<String, T> = mutableMapOf(latest.rev!! to latest)
	)
}
