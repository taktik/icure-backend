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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.ClassificationDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ClassificationLogic
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.validation.aspect.Check
import java.util.function.Consumer

/**
 * Created by dlm on 16-07-18
 */
@Service
class ClassificationLogicImpl(private val classificationDAO: ClassificationDAO,
                              private val uuidGenerator: UUIDGenerator,
                              private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Classification, ClassificationDAO>(sessionLogic), ClassificationLogic {

    override fun getGenericDAO(): ClassificationDAO {
        return classificationDAO
    }

    override suspend fun createClassification(classification: Classification): Classification? {
        try { // Fetching the hcParty
            val healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()
            // Setting Classification attributes
            classification.id = classification.id ?: uuidGenerator.newGUID().toString()
            classification.author = healthcarePartyId
            classification.responsible = healthcarePartyId
            return createEntities(setOf(classification)).firstOrNull()
        } catch (e: Exception) {
            log.error("createClassification: " + e.message)
            throw IllegalArgumentException("Invalid Classification", e)
        }
    }

    override suspend fun getClassification(classificationId: String): Classification? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return classificationDAO.getClassification(dbInstanceUri, groupId, classificationId)
    }

    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<Classification> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(classificationDAO.findByHCPartySecretPatientKeys(dbInstanceUri, groupId, hcPartyId, secretPatientKeys))
    }

    override fun deleteClassifications(ids: Set<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            log.error(e.message, e)
            flowOf()
        }
    }

    override suspend fun modifyClassification(classification: Classification): Classification {
        return try {
            classification.id?.let {
                getClassification(it)?.let { toEdit ->
                    toEdit.label = classification.label
                    updateEntities(setOf(toEdit))
                    getClassification(classification.id!!)
                }
            } ?: throw IllegalArgumentException("Non-existing Classification")
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Classification", e)
        }
    }

    override suspend fun addDelegation(classificationId: String, healthcarePartyId: String, delegation: Delegation): Classification? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val classification = getClassification(classificationId)
        return classification?.let {
            it.addDelegation(healthcarePartyId, delegation)
            classificationDAO.save(dbInstanceUri, groupId, it)
        }
    }

    override suspend fun addDelegations(classificationId: String, delegations: List<Delegation>): Classification? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val classification = getClassification(classificationId)
        return classification?.let {
            delegations.forEach(Consumer { d -> d.delegatedTo?.let { delegateTo -> it.addDelegation(delegateTo, d) } })
            return classificationDAO.save(dbInstanceUri, groupId, it)
        }
    }

    override fun getClassificationByIds(ids: List<String>): Flow<Classification> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(classificationDAO.getList(dbInstanceUri, groupId, ids))
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClassificationLogicImpl::class.java)
    }
}
