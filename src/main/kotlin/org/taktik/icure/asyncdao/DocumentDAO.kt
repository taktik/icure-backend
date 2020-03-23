package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.dao.Option
import org.taktik.icure.entities.Document
import java.net.URI
import java.nio.ByteBuffer
import java.util.ArrayList

interface DocumentDAO: GenericDAO<Document> {
    suspend fun beforeSave(dbInstanceUrl: URI, groupId: String, entity: Document)

    suspend fun afterSave(dbInstanceUrl: URI, groupId: String, entity: Document) : Document

    fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Document>

    fun findDocumentsByHCPartySecretMessageKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>

    fun findDocumentsWithNoDelegations(dbInstanceUrl: URI, groupId: String, limit: Int): Flow<Document>

    fun findDocumentsByDocumentTypeHCPartySecretMessageKeys(dbInstanceUrl: URI, groupId: String, documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>

    fun readAttachment(dbInstanceUrl: URI, groupId: String, documentId: String, attachmentId: String, rev: String?): Flow<ByteBuffer>
}
