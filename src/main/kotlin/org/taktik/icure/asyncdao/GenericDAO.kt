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

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.AttachmentInputStream
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.dao.LookupDAO
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.base.Identifiable
import java.net.URI

import javax.persistence.PersistenceException
import java.util.function.Function

interface GenericDAO<T : Identifiable<String>> : LookupDAO<T> {

    fun getAll(dbInstanceUrl:URI, groupId:String): Flow<T>

    fun getAllIds(dbInstanceUrl:URI, groupId:String): Flow<String>

    suspend fun getAttachment(documentId: String, attachmentId: String): String

    suspend fun getAttachmentInputStream(documentId: String, attachmentId: String, rev: String): AttachmentInputStream

    /**
     * *
     * @param documentId document id
     * @param rev document revision
     * @param data AttachmentInputStream
     * @return
     */
    suspend fun createAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, rev: String, data: AttachmentInputStream): String

    suspend fun deleteAttachment(dbInstanceUrl:URI, groupId:String, documentId: String, rev: String, attachmentId: String): String

    fun getList(dbInstanceUrl:URI, groupId:String, ids: Collection<String>): Flow<T>

    @Throws(PersistenceException::class)
    suspend fun <K : Collection<T>> create(dbInstanceUrl: URI, groupId:String, entities: K): K

    @Throws(PersistenceException::class)
    suspend fun <K : Collection<T>> save(dbInstanceUrl:URI, groupId:String, entities: K): K

    @Throws(PersistenceException::class)
    suspend fun remove(dbInstanceUrl:URI, groupId:String, entity: T)

    @Throws(PersistenceException::class)
    suspend fun unremove(dbInstanceUrl:URI, groupId:String, entity: T)

    @Throws(PersistenceException::class)
    suspend fun purge(dbInstanceUrl:URI, groupId:String, entity: T)

    @Throws(PersistenceException::class)
    suspend fun removeById(dbInstanceUrl:URI, groupId:String, id: String)

    @Throws(PersistenceException::class)
    suspend fun unremoveById(dbInstanceUrl:URI, groupId:String, id: String)

    @Throws(PersistenceException::class)
    suspend fun purgeById(dbInstanceUrl:URI, groupId:String, id: String)

    @Throws(PersistenceException::class)
    suspend fun remove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>)

    @Throws(PersistenceException::class)
    suspend fun unremove(dbInstanceUrl:URI, groupId:String, entities: Collection<T>)

    @Throws(PersistenceException::class)
    suspend fun purge(dbInstanceUrl:URI, groupId:String, entities: Collection<T>)

    @Throws(PersistenceException::class)
    suspend fun removeByIds(dbInstanceUrl:URI, groupId:String, ids: Collection<String>)

    @Throws(PersistenceException::class)
    suspend fun unremoveByIds(dbInstanceUrl:URI, groupId:String, ids: Collection<String>)

    @Throws(PersistenceException::class)
    suspend fun purgeByIds(dbInstanceUrl:URI, groupId:String, ids: Collection<String>)

    suspend fun contains(dbInstanceUrl:URI, groupId:String, id: String): Boolean

    suspend fun hasAny(dbInstanceUrl:URI, groupId:String): Boolean

    suspend fun refreshIndex(dbInstanceUrl:URI, groupId:String)

    suspend fun initStandardDesignDocument(dbInstanceUrl:URI, groupId:String)

    suspend fun initStandardDesignDocument(dbInstanceUrl:URI, groupId:String, group: Group)

    suspend fun forceInitStandardDesignDocument(dbInstanceUrl:URI, groupId:String, group: Group)
}
