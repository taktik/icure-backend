package org.taktik.icure.testutils

import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.apache.http.HttpStatus
import org.taktik.couchdb.BulkUpdateResult
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.entity.Versionable
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.InternalDAO

class FakeInternalDAO<T : Versionable<String>> : InternalDAO<T> {
	private val latestEntitiesMap = ConcurrentHashMap<String, T>()
	private val versionedEntitiesMap = ConcurrentHashMap<String, ConcurrentHashMap<String, T>>()

	override fun getEntities(): Flow<T> =
		latestEntitiesMap.values.asFlow()

	override fun getEntityIds(): Flow<String> =
		latestEntitiesMap.keys.toList().asFlow()

	override suspend fun get(id: String, vararg options: Option): T? =
		get(id, null, *options)

	override suspend fun get(id: String, rev: String?, vararg options: Option): T? {
		require(options.isEmpty()) { "Options are not supported by mock" }
		return if (rev == null) latestEntitiesMap[id] else versionedEntitiesMap[id]?.get(rev)
	}

	override fun getEntities(ids: Collection<String>): Flow<T> {
		val idsSet = ids.toSet()
		return latestEntitiesMap.toList().asFlow().filter { it.first in idsSet }.map { it.second }
	}

	override suspend fun save(entity: T): T {
		@Suppress("UNCHECKED_CAST")
		val entityWithNewRev = entity.withIdRev(null, entity.rev?.let { (it.toInt() + 1).toString() } ?: "0") as T
		val newValue = latestEntitiesMap.compute(entityWithNewRev.id) { _, prev ->
			if (prev?.rev == entity.rev) entityWithNewRev else prev
		}
		return if (newValue == entityWithNewRev) {
			versionedEntitiesMap.compute(entityWithNewRev.id) { _, prev -> prev ?: ConcurrentHashMap() }!!
				.put(entityWithNewRev.rev!!, entityWithNewRev)
			newValue
		} else throw CouchDbConflictException("Current rev is ${newValue?.rev}", HttpStatus.SC_CONFLICT, "Rev conflict")
	}

	override suspend fun update(entity: T): T =
		if (latestEntitiesMap.containsKey(entity.id)) {
			save(entity)
		} else throw CouchDbException("Trying to update a non-existing entity", HttpStatus.SC_NOT_FOUND, "Fake internal dao")

	override fun save(entities: Flow<T>): Flow<DocIdentifier> = flow {
		entities.collect { save(it).also { updatedEntity -> emit(DocIdentifier(updatedEntity.id, updatedEntity.rev)) } }
	}

	override fun save(entities: List<T>): Flow<DocIdentifier> =
		save(entities.asFlow())

	override suspend fun purge(entity: T): DocIdentifier =
		latestEntitiesMap.remove(entity.id).let { DocIdentifier(it?.id, it?.rev) }.also {
			versionedEntitiesMap.remove(entity.id)
		}

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
}
