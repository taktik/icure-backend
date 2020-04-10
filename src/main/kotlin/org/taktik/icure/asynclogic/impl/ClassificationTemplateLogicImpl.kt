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
import kotlinx.coroutines.flow.flowOf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.ClassificationTemplateDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ClassificationTemplateLogic
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.validation.aspect.Check
import java.util.*
import java.util.function.Consumer

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

    override suspend fun createClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        try { // Fetching the hcParty
            val healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()
            // Setting Classification Template attributes
            classificationTemplate.id = classificationTemplate.id ?: uuidGenerator.newGUID().toString()
            classificationTemplate.author = healthcarePartyId
            classificationTemplate.responsible = healthcarePartyId
            return createEntities(setOf(classificationTemplate)).firstOrNull()
        } catch (e: Exception) {
            log.error("createClassificationTemplate: " + e.message)
            throw IllegalArgumentException("Invalid Classification Template", e)
        }
    }

    override suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return classificationTemplateDAO.getClassificationTemplate(dbInstanceUri, groupId, classificationTemplateId)
    }

    override fun deleteClassificationTemplates(ids: Set<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            log.error(e.message, e)
            flowOf()
        }
    }

    override suspend fun modifyClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate {
        return try {
            getClassificationTemplate(classificationTemplate.id)?.let { toEdit ->
                toEdit.label = classificationTemplate.label
                updateEntities(setOf(toEdit))
                getClassificationTemplate(classificationTemplate.id)
            } ?: throw IllegalArgumentException("Non-existing Classification Template")
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Classification Template", e)
        }
    }

    override suspend fun addDelegation(classificationTemplateId: String, healthcarePartyId: String, delegation: Delegation): ClassificationTemplate? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val classificationTemplate = getClassificationTemplate(classificationTemplateId)
        return classificationTemplate?.let {
            it.addDelegation(healthcarePartyId, delegation)
            classificationTemplateDAO.save(dbInstanceUri, groupId, classificationTemplate)
        }
    }

    override suspend fun addDelegations(classificationTemplateId: String, delegations: List<Delegation>): ClassificationTemplate? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val classificationTemplate = getClassificationTemplate(classificationTemplateId)
        return classificationTemplate?.let {
            delegations.forEach(Consumer { d: Delegation -> d.delegatedTo?.let { delegatedTo -> it.addDelegation(delegatedTo, d) } })
            return classificationTemplateDAO.save(dbInstanceUri, groupId, classificationTemplate)
        }
    }

    override fun getClassificationTemplateByIds(ids: List<String>): Flow<ClassificationTemplate> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(classificationTemplateDAO.getList(dbInstanceUri, groupId, ids))
    }

    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: ArrayList<String>): Flow<ClassificationTemplate> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(classificationTemplateDAO.findByHCPartySecretPatientKeys(dbInstanceUri, groupId, hcPartyId, secretPatientKeys))
    }

    override fun listClassificationTemplates(paginationOffset: PaginationOffset<String>) =flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(classificationTemplateDAO.listClassificationTemplates(dbInstanceUri, groupId, paginationOffset))
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClassificationTemplateLogicImpl::class.java)
    }
}
