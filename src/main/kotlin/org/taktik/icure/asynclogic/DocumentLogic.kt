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

package org.taktik.icure.asynclogic

import java.nio.ByteBuffer
import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentModificationLogic.DataAttachmentChange
import org.taktik.icure.entities.Document

interface DocumentLogic : EntityPersister<Document, String> {
	suspend fun createDocument(document: Document, ownerHealthcarePartyId: String): Document?

	suspend fun getDocument(documentId: String): Document?
	fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer>

	/**
	 * Modifies a document, without changing any information on data attachments.
	 * If the updatedDocument has data attachment ids information which is inconsistent with the current document
	 * the method can have two different behaviours, depending on the value of [strict]:
	 * - **strict**: throws an [IllegalArgumentException].
	 * - **lenient**: updates all other values, leaving data attachment information unchanged.
	 * This method still allows updating non-id attachment information such as utis.
	 * @param updatedDocument the new version of the document
	 * @param strict specifies whether to behave in a strict or lenient way.
	 * @param currentDocument the current document if already available, else null
	 * @return the updated document.
	 * @throws IllegalArgumentException if strict and the updated document data attachment information is inconsistent
	 * with the current document.
	 */
	suspend fun modifyDocument(updatedDocument: Document, strict: Boolean = true, currentDocument: Document? = null): Document?

	/**
	 * Updates the attachments for a document. For additional details check [DataAttachmentChange].
	 * @param currentDocument the document to update
	 * @param mainAttachmentChange specifies how to change the main attachment. If null the main attachment will be unchanged.
	 * @param secondaryAttachmentsChanges specifies how to change the secondary attachments. Only secondary attachments specified
	 * in this map will be changed, other attachments in the document will be ignored.
	 * @return the updated document.
	 */
	suspend fun updateAttachments(
		currentDocument: Document,
		mainAttachmentChange: DataAttachmentChange? = null,
		secondaryAttachmentsChanges: Map<String, DataAttachmentChange> = emptyMap()
	): Document?

	fun listDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>
	fun listDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>
	fun listDocumentsWithoutDelegation(limit: Int): Flow<Document>
	fun getDocuments(documentIds: List<String>): Flow<Document>
	fun modifyDocuments(documents: List<Document>): Flow<Document>

	fun solveConflicts(ids: List<String>?): Flow<Document>
	fun getGenericDAO(): DocumentDAO
	suspend fun getDocumentsByExternalUuid(documentId: String): List<Document>
}
