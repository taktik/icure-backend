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

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.entities.Document
import java.nio.ByteBuffer

interface DocumentLogic : EntityPersister<Document, String> {
    suspend fun createDocument(document: Document, ownerHealthcarePartyId: String): Document?

    suspend fun getDocument(documentId: String): Document?
    fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer>

    suspend fun modifyDocument(document: Document): Document?
    fun listDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>
    fun listDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>
    fun listDocumentsWithoutDelegation(limit: Int): Flow<Document>
    fun getDocuments(documentIds: List<String>): Flow<Document>
    fun modifyDocuments(documents: List<Document>): Flow<Document>

    fun solveConflicts(ids: List<String>?): Flow<Document>
    fun getGenericDAO(): DocumentDAO
    suspend fun getDocumentsByExternalUuid(documentId: String): List<Document>
}
