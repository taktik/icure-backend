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
import org.taktik.icure.entities.Document
import java.nio.ByteBuffer

interface DocumentDAO: GenericDAO<Document> {
    fun listConflicts(): Flow<Document>

    fun listDocumentsByHcPartyAndSecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>

    fun listDocumentsWithNoDelegations(limit: Int): Flow<Document>

    fun listDocumentsByDocumentTypeHcPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>

    fun readAttachment(documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer>

    suspend fun listDocumentsByExternalUuid(externalUuid: String): List<Document>
}
