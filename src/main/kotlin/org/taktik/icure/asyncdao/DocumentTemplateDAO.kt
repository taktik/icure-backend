package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.entities.DocumentTemplate
import java.net.URI

interface DocumentTemplateDAO: GenericDAO<DocumentTemplate> {
    fun findByUserGuid(dbInstanceUrl: URI, groupId: String, userId: String, guid: String): Flow<DocumentTemplate>

    fun findBySpecialtyGuid(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, guid: String?): Flow<DocumentTemplate>

    fun findByTypeUserGuid(dbInstanceUrl: URI, groupId: String, documentTypeCode: String, userId: String?, guid: String?): Flow<DocumentTemplate>

    fun evictFromCache(entity: DocumentTemplate)
    suspend fun createDocumentTemplate(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate): DocumentTemplate

    suspend fun beforeSave(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate)

    suspend fun afterSave(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate)
    suspend fun postLoad(dbInstanceUrl: URI, groupId: String, entity: DocumentTemplate?)
}
