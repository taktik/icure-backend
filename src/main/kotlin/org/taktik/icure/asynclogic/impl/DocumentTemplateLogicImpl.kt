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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.DocumentTemplateDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentTemplateLogic
import org.taktik.icure.entities.DocumentTemplate


@ExperimentalCoroutinesApi
@Service
class DocumentTemplateLogicImpl(private val documentTemplateDAO: DocumentTemplateDAO,
                                private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<DocumentTemplate, DocumentTemplateDAO>(sessionLogic), DocumentTemplateLogic {

    override fun createEntities(entities: Collection<DocumentTemplate>): Flow<DocumentTemplate> = flow {
        emitAll(super.createEntities(
                entities.map {dt ->
                    fix(dt) { e ->
                        e.owner?.let { e } ?: e.copy(owner = sessionLogic.getCurrentUserId())
                    }
                }))
    }

    override suspend fun createDocumentTemplate(documentTemplate: DocumentTemplate) = fix(documentTemplate) { documentTemplate ->
        documentTemplateDAO.createDocumentTemplate(documentTemplate.owner?.let { documentTemplate } ?: documentTemplate.copy(owner = sessionLogic.getCurrentUserId()))
    }

    override suspend fun getDocumentTemplate(documentTemplateId: String): DocumentTemplate? {
        return documentTemplateDAO.get(documentTemplateId)
    }

    override fun getDocumentTemplatesBySpecialty(specialityCode: String): Flow<DocumentTemplate> = flow {
        emitAll(documentTemplateDAO.findBySpecialtyGuid(specialityCode, null))
    }

    override fun getDocumentTemplatesByDocumentType(documentTypeCode: String): Flow<DocumentTemplate> = flow {
        emitAll(documentTemplateDAO.findByTypeUserGuid(documentTypeCode, null, null))
    }

    override fun getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode: String, userId: String): Flow<DocumentTemplate> = flow {
        emitAll(documentTemplateDAO.findByTypeUserGuid(documentTypeCode, userId, null))
    }

    override fun getDocumentTemplatesByUser(userId: String): Flow<DocumentTemplate> = flow {
        emitAll(documentTemplateDAO.findByUserGuid(userId, null))
    }

    override suspend fun modifyDocumentTemplate(documentTemplate: DocumentTemplate) = fix(documentTemplate) { documentTemplate ->
        documentTemplateDAO.save(documentTemplate.owner?.let { documentTemplate } ?: documentTemplate.copy(owner = sessionLogic.getCurrentUserId()))
    }

    override fun getGenericDAO(): DocumentTemplateDAO {
        return documentTemplateDAO
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DocumentTemplateLogicImpl::class.java)
    }
}
