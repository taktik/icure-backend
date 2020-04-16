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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.ektorp.UpdateConflictException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.dao.Option
import org.taktik.icure.entities.Document
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.validation.aspect.Check
import java.nio.ByteBuffer
import java.util.*

@ExperimentalCoroutinesApi
@Service
class DocumentLogicImpl(private val documentDAO: DocumentDAO, private val sessionLogic: AsyncSessionLogicImpl) : GenericLogicImpl<Document, DocumentDAO>(sessionLogic), DocumentLogic {

    override suspend fun createDocument(document: Document, ownerHealthcarePartyId: String): Document? {
        // Fill audit details
        document.author = ownerHealthcarePartyId
        document.responsible = ownerHealthcarePartyId
        return try {
            createEntities(setOf(document)).firstOrNull()
        } catch (e: Exception) {
            throw CreationException("Could not create document. ", e)
        }
    }

    override suspend fun get(documentId: String): Document? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return documentDAO.get(dbInstanceUri, groupId, documentId)
    }

    override fun get(documentIds: List<String>): Flow<Document> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.getList(dbInstanceUri, groupId, documentIds))
    }

    override fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.getAttachment(dbInstanceUri, groupId, documentId, attachmentId))
    }

    override fun readAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.readAttachment(dbInstanceUri, groupId, documentId, attachmentId, null))
    }

    override suspend fun modifyDocument(document: Document) {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        try {
            documentDAO.save(dbInstanceUri, groupId, document)
        } catch (e: UpdateConflictException) {
            logger.warn("Documents of class {} with id {} and rev {} could not be merged", document.javaClass.simpleName, document.id, document.rev)
            throw IllegalStateException(e)
        }
    }

    override fun findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.findDocumentsByDocumentTypeHCPartySecretMessageKeys(dbInstanceUri, groupId, documentTypeCode, hcPartyId, secretForeignKeys))
    }

    override fun findDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.findDocumentsByHCPartySecretMessageKeys(dbInstanceUri, groupId, hcPartyId, secretForeignKeys))
    }

    override fun findWithoutDelegation(limit: Int): Flow<Document> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.findDocumentsWithNoDelegations(dbInstanceUri, groupId, limit))
    }

    override fun getDocuments(documentIds: List<String>): Flow<Document> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.getList(dbInstanceUri, groupId, documentIds))
    }

    override fun updateDocuments(documents: List<Document>): Flow<Document> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentDAO.save(dbInstanceUri, groupId, documents))
    }

    override suspend fun solveConflicts(ids: List<String>?) {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()

        val documentsInConflict = ids?.asFlow()?.map { documentDAO.get(dbInstanceUri, groupId, it, Option.CONFLICTS) }
                ?: documentDAO.listConflicts(dbInstanceUri, groupId).map { documentDAO.get(dbInstanceUri, groupId, it.id, Option.CONFLICTS) }
        documentsInConflict.collect { doc ->
            if (doc != null && doc.conflicts != null) {
                val conflicted = doc.conflicts.mapNotNull { c -> documentDAO.get(dbInstanceUri, groupId, doc.id, c) }
                conflicted.forEach { other -> doc.solveConflictsWith(other) }
                documentDAO.save(dbInstanceUri, groupId, doc)
                conflicted.forEach { cp -> documentDAO.purge(dbInstanceUri, groupId, cp) }
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
