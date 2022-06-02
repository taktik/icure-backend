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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.ClassificationDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ClassificationLogic
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.embed.Delegation

/**
 * Created by dlm on 16-07-18
 */
@Service
class ClassificationLogicImpl(
	private val classificationDAO: ClassificationDAO,
	private val uuidGenerator: UUIDGenerator,
	private val sessionLogic: AsyncSessionLogic
) : GenericLogicImpl<Classification, ClassificationDAO>(sessionLogic), ClassificationLogic {

	override fun getGenericDAO(): ClassificationDAO {
		return classificationDAO
	}

	override suspend fun createClassification(classification: Classification) = fix(classification) { classification ->
		try { // Fetching the hcParty
			val userId = sessionLogic.getCurrentUserId()
			val healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()
			createEntities(
				setOf(
					classification.copy(
						author = userId,
						responsible = healthcarePartyId
					)
				)
			).firstOrNull()
		} catch (e: Exception) {
			log.error("createClassification: " + e.message)
			throw IllegalArgumentException("Invalid Classification", e)
		}
	}

	override suspend fun getClassification(classificationId: String): Classification? {
		return classificationDAO.getClassification(classificationId)
	}

	override fun listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<Classification> = flow {
		emitAll(classificationDAO.listClassificationsByHCPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys))
	}

	override fun deleteClassifications(ids: Set<String>): Flow<DocIdentifier> {
		return try {
			deleteEntities(ids)
		} catch (e: Exception) {
			log.error(e.message, e)
			flowOf()
		}
	}

	override suspend fun modifyClassification(classification: Classification) = fix(classification) { classification ->
		try {
			classification.id.let {
				getClassification(it)?.let { toEdit ->
					modifyEntities(setOf(toEdit.copy(label = classification.label))).firstOrNull()
				}
			} ?: throw IllegalArgumentException("Non-existing Classification")
		} catch (e: Exception) {
			throw IllegalArgumentException("Invalid Classification", e)
		}
	}

	override suspend fun addDelegation(classificationId: String, healthcarePartyId: String, delegation: Delegation): Classification? {
		val classification = getClassification(classificationId)
		return classification?.let {
			classificationDAO.save(
				it.copy(
					delegations = it.delegations + mapOf(
						healthcarePartyId to setOf(delegation)
					)
				)
			)
		}
	}

	override suspend fun addDelegations(classificationId: String, delegations: List<Delegation>): Classification? {
		val classification = getClassification(classificationId)
		return classification?.let {
			return classificationDAO.save(
				it.copy(
					delegations = it.delegations +
						delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
				)
			)
		}
	}

	override fun getClassifications(ids: List<String>): Flow<Classification> = flow {
		emitAll(classificationDAO.getEntities(ids))
	}

	companion object {
		private val log = LoggerFactory.getLogger(ClassificationLogicImpl::class.java)
	}
}
