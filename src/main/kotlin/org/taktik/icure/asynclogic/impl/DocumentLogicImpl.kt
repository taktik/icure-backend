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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.couchdb.entity.Option
import org.taktik.icure.entities.Document
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.utils.firstOrNull
import java.nio.ByteBuffer
import java.util.*

@ExperimentalCoroutinesApi
@Service
class DocumentLogicImpl(private val documentDAO: DocumentDAO, private val sessionLogic: AsyncSessionLogicImpl) : GenericLogicImpl<Document, DocumentDAO>(sessionLogic), DocumentLogic {

    override suspend fun createDocument(document: Document, ownerHealthcarePartyId: String) = fix(document) { document ->
        try {
            createEntities(setOf(document)).firstOrNull()
        } catch (e: Exception) {
            throw CreationException("Could not create document. ", e)
        }
    }

    override suspend fun get(documentId: String): Document? {
        return documentDAO.get(documentId)
    }

    override suspend fun getAllByExternalUuid(documentId: String): List<Document> {
        return documentDAO.getAllByExternalUuid(documentId)
    }

    override fun get(documentIds: List<String>): Flow<Document> = flow {
        emitAll(documentDAO.getList(documentIds))
    }

    override fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer> = flow {
        emitAll(documentDAO.getAttachment(documentId, attachmentId))
    }

    override fun readAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer> = flow {
        emitAll(documentDAO.readAttachment(documentId, attachmentId, null))
    }

    override suspend fun modifyDocument(document: Document) = fix(document) { document ->
        try {
            documentDAO.save(document)
        } catch (e: CouchDbException) {
            logger.warn("Documents of class {} with id {} and rev {} could not be merged", document.javaClass.simpleName, document.id, document.rev)
            throw IllegalStateException(e)
        }
    }

    override fun findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
        emitAll(documentDAO.findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, secretForeignKeys))
    }

    override fun findDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
        emitAll(documentDAO.findDocumentsByHCPartySecretMessageKeys(hcPartyId, secretForeignKeys))
    }

    override fun findWithoutDelegation(limit: Int): Flow<Document> = flow {
        emitAll(documentDAO.findDocumentsWithNoDelegations(limit))
    }

    override fun getDocuments(documentIds: List<String>): Flow<Document> = flow {
        emitAll(documentDAO.getList(documentIds))
    }

    override fun updateDocuments(documents: List<Document>): Flow<Document> = flow {
        emitAll(documentDAO.save(documents))
    }

    override suspend fun solveConflicts(ids: List<String>?) {
        val documentsInConflict = ids?.asFlow()?.map { documentDAO.get(it, Option.CONFLICTS) }
                ?: documentDAO.listConflicts().map { documentDAO.get(it.id, Option.CONFLICTS) }
        documentsInConflict.collect { doc ->
            if (doc != null && doc.conflicts != null) {
                val conflicted = doc.conflicts.mapNotNull { c -> documentDAO.get(doc.id, c) }
                conflicted.forEach { other -> doc.solveConflictsWith(other) }
                documentDAO.save(doc)
                conflicted.forEach { cp -> documentDAO.purge(cp) }
            }
        }
    }

    override fun getGenericDAO(): DocumentDAO {
        return documentDAO
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DocumentLogicImpl::class.java)
    }
}
