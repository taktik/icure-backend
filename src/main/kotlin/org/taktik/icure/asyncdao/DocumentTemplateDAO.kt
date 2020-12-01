package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.DocumentTemplate
import java.net.URI

interface DocumentTemplateDAO: GenericDAO<DocumentTemplate> {
    fun findByUserGuid(userId: String, guid: String?): Flow<DocumentTemplate>

    fun findBySpecialtyGuid(healthcarePartyId: String, guid: String?): Flow<DocumentTemplate>

    fun findByTypeUserGuid(documentTypeCode: String, userId: String?, guid: String?): Flow<DocumentTemplate>

    suspend fun createDocumentTemplate(entity: DocumentTemplate): DocumentTemplate
}
