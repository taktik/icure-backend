package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.entities.Document
import java.nio.ByteBuffer
import java.util.*

interface DocumentLogic : EntityPersister<Document, String> {
    suspend fun createDocument(document: Document, ownerHealthcarePartyId: String): Document?

    suspend fun get(documentId: String): Document?
    fun get(documentIds: List<String>): Flow<Document>
    fun getAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer>
    fun readAttachment(documentId: String, attachmentId: String): Flow<ByteBuffer>

    suspend fun modifyDocument(document: Document): Document?
    fun findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>
    fun findDocumentsByHCPartySecretMessageKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<Document>
    fun findWithoutDelegation(limit: Int): Flow<Document>
    fun getDocuments(documentIds: List<String>): Flow<Document>
    fun updateDocuments(documents: List<Document>): Flow<Document>

    suspend fun solveConflicts(ids: List<String>?)
    fun getGenericDAO(): DocumentDAO
    suspend fun getAllByExternalUuid(documentId: String): List<Document>
}
