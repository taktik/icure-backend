package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asyncdao.DocumentTemplateDAO
import org.taktik.icure.entities.DocumentTemplate

interface DocumentTemplateLogic : EntityPersister<DocumentTemplate, String> {
    suspend fun createDocumentTemplate(entity: DocumentTemplate): DocumentTemplate

    suspend fun getDocumentTemplateById(documentTemplateId: String): DocumentTemplate?
    fun getDocumentTemplatesBySpecialty(specialityCode: String): Flow<DocumentTemplate>
    fun getDocumentTemplatesByDocumentType(documentTypeCode: String): Flow<DocumentTemplate>
    fun getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode: String, userId: String): Flow<DocumentTemplate>
    fun getDocumentTemplatesByUser(userId: String): Flow<DocumentTemplate>

    suspend fun modifyDocumentTemplate(documentTemplate: DocumentTemplate): DocumentTemplate?
    fun getGenericDAO(): DocumentTemplateDAO
}
