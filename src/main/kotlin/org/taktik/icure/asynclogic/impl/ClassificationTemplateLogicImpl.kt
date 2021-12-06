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
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.ClassificationTemplateDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ClassificationTemplateLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.embed.Delegation

/**
 * Created by dlm on 16-07-18
 */
@ExperimentalCoroutinesApi
@Service
class ClassificationTemplateLogicImpl(private val classificationTemplateDAO: ClassificationTemplateDAO,
                                      private val uuidGenerator: UUIDGenerator,
                                      private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<ClassificationTemplate, ClassificationTemplateDAO>(sessionLogic), ClassificationTemplateLogic {

    override fun getGenericDAO(): ClassificationTemplateDAO {
        return classificationTemplateDAO
    }

    override suspend fun createClassificationTemplate(classificationTemplate: ClassificationTemplate) = fix(classificationTemplate) { classificationTemplate ->
        try { // Fetching the hcParty
            val userId = sessionLogic.getCurrentUserId()
            val healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()
            // Setting Classification Template attributes
            createEntities(setOf(classificationTemplate.copy(
                    author = userId, responsible = healthcarePartyId
            ))).firstOrNull()
        } catch (e: Exception) {
            log.error("createClassificationTemplate: " + e.message)
            throw IllegalArgumentException("Invalid Classification Template", e)
        }
    }

    override suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate? {
        return classificationTemplateDAO.getClassificationTemplate(classificationTemplateId)
    }

    override fun deleteClassificationTemplates(ids: Set<String>): Flow<DocIdentifier> {
        return try {
            deleteEntities(ids)
        } catch (e: Exception) {
            log.error(e.message, e)
            flowOf()
        }
    }

    override suspend fun modifyClassificationTemplate(classificationTemplate: ClassificationTemplate) = fix(classificationTemplate) { classificationTemplate ->
        try {
            getClassificationTemplate(classificationTemplate.id)?.let { toEdit ->
                modifyEntities(setOf(toEdit.copy(label = classificationTemplate.label))).firstOrNull()
            } ?: throw IllegalArgumentException("Non-existing Classification Template")
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Classification Template", e)
        }
    }

    override suspend fun addDelegation(classificationTemplateId: String, healthcarePartyId: String, delegation: Delegation): ClassificationTemplate? {
        val classificationTemplate = getClassificationTemplate(classificationTemplateId)
        return classificationTemplate?.let {
            classificationTemplateDAO.save(it.copy(delegations = it.delegations + mapOf(
                    healthcarePartyId to setOf(delegation)
            )))
        }
    }

    override suspend fun addDelegations(classificationTemplateId: String, delegations: List<Delegation>): ClassificationTemplate? {
        val classificationTemplate = getClassificationTemplate(classificationTemplateId)
        return classificationTemplate?.let {
            return classificationTemplateDAO.save(it.copy(
                    delegations = it.delegations +
                            delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
            ))
        }
    }

    override fun getClassificationTemplates(ids: List<String>): Flow<ClassificationTemplate> = flow {
        emitAll(classificationTemplateDAO.getEntities(ids))
    }

    override fun listClasificationsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: ArrayList<String>): Flow<ClassificationTemplate> = flow {
        emitAll(classificationTemplateDAO.listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys))
    }

    override fun listClassificationTemplates(paginationOffset: PaginationOffset<String>) =flow<ViewQueryResultEvent> {
        emitAll(classificationTemplateDAO.findClassificationTemplates(paginationOffset))
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClassificationTemplateLogicImpl::class.java)
    }
}
