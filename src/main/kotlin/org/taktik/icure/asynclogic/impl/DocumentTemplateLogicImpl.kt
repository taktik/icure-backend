/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
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
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        documentTemplateDAO.createDocumentTemplate(dbInstanceUri, groupId, documentTemplate.owner?.let { documentTemplate } ?: documentTemplate.copy(owner = sessionLogic.getCurrentUserId()))
    }

    override suspend fun getDocumentTemplateById(documentTemplateId: String): DocumentTemplate? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return documentTemplateDAO.get(dbInstanceUri, groupId, documentTemplateId)
    }

    override fun getDocumentTemplatesBySpecialty(specialityCode: String): Flow<DocumentTemplate> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentTemplateDAO.findBySpecialtyGuid(dbInstanceUri, groupId, specialityCode, null))
    }

    override fun getDocumentTemplatesByDocumentType(documentTypeCode: String): Flow<DocumentTemplate> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentTemplateDAO.findByTypeUserGuid(dbInstanceUri, groupId, documentTypeCode, null, null))
    }

    override fun getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode: String, userId: String): Flow<DocumentTemplate> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentTemplateDAO.findByTypeUserGuid(dbInstanceUri, groupId, documentTypeCode, userId, null))
    }

    override fun getDocumentTemplatesByUser(userId: String): Flow<DocumentTemplate> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(documentTemplateDAO.findByUserGuid(dbInstanceUri, groupId, userId, null))
    }

    override suspend fun modifyDocumentTemplate(documentTemplate: DocumentTemplate) = fix(documentTemplate) { documentTemplate ->
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        documentTemplateDAO.save(dbInstanceUri, groupId, documentTemplate.owner?.let { documentTemplate } ?: documentTemplate.copy(owner = sessionLogic.getCurrentUserId()))
    }

    override fun getGenericDAO(): DocumentTemplateDAO {
        return documentTemplateDAO
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DocumentTemplateLogicImpl::class.java)
    }
}
