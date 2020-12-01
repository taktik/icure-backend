package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.entities.Document
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

interface DocumentDAO: GenericDAO<Document> {
    fun listConflicts(): Flow<Document>

    fun findDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>

    fun findDocumentsWithNoDelegations(limit: Int): Flow<Document>

    fun findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>

    fun readAttachment(documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer>

    suspend fun getAllByExternalUuid(externalUuid: String): List<Document>
}
