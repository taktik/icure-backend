package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.BulkUpdateResult
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.dao.Option
import org.taktik.icure.entities.base.Identifiable

interface InternalDAO<T : Identifiable<String>> {

    fun getAll(): Flow<T>
    fun getAllIds(): Flow<String>
    suspend fun get(id: String, vararg options: Option): T?
    suspend fun get(id: String, rev: String?, vararg options: Option): T?
    fun getList(ids: Collection<String>): Flow<T>
    suspend fun save(entity: T): T?
    fun save(entities: Flow<T>): Flow<DocIdentifier>
    suspend fun update(entity: T): T?
    fun list(ids: List<String>): Flow<T>

    fun save(entities: List<T>): Flow<DocIdentifier>
    suspend fun purge(entities: Flow<T>): Flow<BulkUpdateResult>
    suspend fun remove(entities: Flow<T>): Flow<BulkUpdateResult>

    suspend fun forceInitStandardDesignDocument(updateIfExists: Boolean)
}
