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

import java.nio.ByteBuffer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.entities.Document
import org.taktik.icure.exceptions.CreationException

@ExperimentalCoroutinesApi
@Service
class DocumentLogicImpl(
	private val documentDAO: DocumentDAO,
	sessionLogic: AsyncSessionLogicImpl
) : GenericLogicImpl<Document, DocumentDAO>(sessionLogic), DocumentLogic {

	override suspend fun createDocument(document: Document, ownerHealthcarePartyId: String) = fix(document) { document ->
		try {
			createEntities(setOf(document)).firstOrNull()
		} catch (e: Exception) {
			throw CreationException("Could not create document. ", e)
		}
	}

	override suspend fun getDocument(documentId: String): Document? {
		return documentDAO.get(documentId)
	}

	override suspend fun getDocumentsByExternalUuid(documentId: String): List<Document> {
		return documentDAO.listDocumentsByExternalUuid(documentId)
	}

	override fun getDocuments(documentIds: List<String>) = documentDAO.getEntities(documentIds)

	override fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer> = flow {
		emitAll(documentDAO.readAttachment(documentId, attachmentId, null))
	}

	override suspend fun modifyDocument(updatedDocument: Document, strict: Boolean, currentDocument: Document?): Document? {
		TODO("Not yet implemented")
	/* OLD
	override suspend fun modifyDocument(document: Document) = fix(document) { document ->
		try {
			documentDAO.save(document)
		} catch (e: CouchDbException) {
			logger.warn("Documents of class {} with id {} and rev {} could not be merged", document.javaClass.simpleName, document.id, document.rev)
			throw IllegalStateException(e)
		}
	}
	 */
	}

	override suspend fun updateAttachments(currentDocument: Document, mainAttachmentChange: DocumentLogic.DataAttachmentChange?, secondaryAttachmentsChanges: Map<String, DocumentLogic.DataAttachmentChange>): Document? {
		TODO("Not yet implemented")
	}

	override fun listDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
		emitAll(documentDAO.listDocumentsByDocumentTypeHcPartySecretMessageKeys(documentTypeCode, hcPartyId, secretForeignKeys))
	}

	override fun listDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document> = flow {
		emitAll(documentDAO.listDocumentsByHcPartyAndSecretMessageKeys(hcPartyId, secretForeignKeys))
	}

	override fun listDocumentsWithoutDelegation(limit: Int): Flow<Document> = flow {
		emitAll(documentDAO.listDocumentsWithNoDelegations(limit))
	}

	override fun modifyDocuments(documents: List<Document>): Flow<Document> = flow {
		emitAll(documentDAO.save(documents))
	}

	override fun solveConflicts(ids: List<String>?): Flow<Document> {
		val documentsInConflict = ids?.asFlow()?.mapNotNull { documentDAO.get(it, Option.CONFLICTS) }
			?: documentDAO.listConflicts().mapNotNull { documentDAO.get(it.id, Option.CONFLICTS) }
		return documentsInConflict.mapNotNull {
			documentDAO.get(it.id, Option.CONFLICTS)?.let { document ->
				document.conflicts?.mapNotNull { conflictingRevision -> documentDAO.get(document.id, conflictingRevision) }
					?.fold(document) { kept, conflict -> kept.merge(conflict).also { documentDAO.purge(conflict) } }
					?.let { mergedDocument -> documentDAO.save(mergedDocument) }
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
