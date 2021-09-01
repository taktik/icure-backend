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

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.Client
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.id.Identifiable
import java.net.URI
import java.nio.ByteBuffer


interface GenericDAO<T : Identifiable<String>> : LookupDAO<T> {
    fun getAttachment(documentId: String, attachmentId: String, rev: String? = null): Flow<ByteBuffer>
    suspend fun createAttachment(documentId: String, attachmentId: String, rev: String, contentType: String, data: Flow<ByteBuffer>): String
    suspend fun deleteAttachment(documentId: String, rev: String, attachmentId: String): String

    fun <K : Collection<T>> create(entities: K): Flow<T>
    fun <K : Collection<T>> save(entities: K): Flow<T>

    suspend fun contains(id: String): Boolean
    suspend fun hasAny(): Boolean

    fun getEntities(): Flow<T>
    fun getEntityIds(limit: Int? = null): Flow<String>
    fun getEntities(ids: Collection<String>): Flow<T>
    fun getEntities(ids: Flow<String>): Flow<T>

    suspend fun remove(entity: T): DocIdentifier
    fun remove(entities: Collection<T>): Flow<DocIdentifier>
    suspend fun purge(entity: T): DocIdentifier
    suspend fun purge(entities: Collection<T>)
    fun unRemove(entities: Collection<T>): Flow<DocIdentifier>
    suspend fun unRemove(entity: T): DocIdentifier
    suspend fun forceInitStandardDesignDocument(updateIfExists: Boolean = true, useVersioning: Boolean = true)
    suspend fun forceInitStandardDesignDocument(client: Client, updateIfExists: Boolean = true, useVersioning: Boolean = true)
    suspend fun initSystemDocumentIfAbsent(dbInstanceUrl: URI)
    suspend fun initSystemDocumentIfAbsent(client: Client)
}
