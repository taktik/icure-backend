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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.FormDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.embed.Delegation

@ExperimentalCoroutinesApi
@Service
class FormLogicImpl(
	private val formDAO: FormDAO,
	private val sessionLogic: AsyncSessionLogic,
	private val uuidGenerator: UUIDGenerator
) : GenericLogicImpl<Form, FormDAO>(sessionLogic), FormLogic {

	override suspend fun getForm(id: String): Form? {
		return formDAO.get(id)
	}

	override fun getForms(selectedIds: Collection<String>): Flow<Form> = flow {
		emitAll(formDAO.getEntities(selectedIds))
	}

	override suspend fun getAllByLogicalUuid(formUuid: String): List<Form> {
		return formDAO.getAllByLogicalUuid(formUuid)
	}

	override suspend fun getAllByUniqueId(lid: String): List<Form> {
		return formDAO.getAllByUniqueId(lid)
	}

	override fun listFormsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>, healthElementId: String?, planOfActionId: String?, formTemplateId: String?): Flow<Form> = flow {
		val forms = formDAO.listFormsByHcPartyPatient(hcPartyId, secretPatientKeys)
		val filteredForms = forms.filter { f ->
			(healthElementId == null || healthElementId == f.healthElementId) &&
				(planOfActionId == null || planOfActionId == f.planOfActionId) &&
				(formTemplateId == null || formTemplateId == f.formTemplateId)
		}
		emitAll(filteredForms)
	}

	override suspend fun addDelegation(formId: String, delegation: Delegation): Form? {
		val form = getForm(formId)
		return delegation.delegatedTo?.let { healthcarePartyId ->
			form?.let { c ->
				formDAO.save(
					c.copy(
						delegations = c.delegations + mapOf(
							healthcarePartyId to setOf(delegation)
						)
					)
				)
			}
		} ?: form
	}

	override suspend fun createForm(form: Form): Form? = fix(form) { form ->
		try { // Fetching the hcParty
			createEntities(setOf(form)).firstOrNull()
		} catch (e: Exception) {
			logger.error("createContact: " + e.message)
			throw IllegalArgumentException("Invalid form", e)
		}
	}

	override fun deleteForms(ids: Set<String>): Flow<DocIdentifier> {
		return try {
			deleteEntities(ids)
		} catch (e: Exception) {
			logger.error(e.message, e)
			return flowOf()
		}
	}

	override suspend fun modifyForm(form: Form) = fix(form) { form ->
		try {
			formDAO.save(if (form.created == null) form.copy(created = getForm(form.id)?.created) else form)
		} catch (e: CouchDbException) { //resolveConflict(form, e);
			logger.warn("Documents of class {} with id {} and rev {} could not be merged", form.javaClass.simpleName, form.id, form.rev)
			throw IllegalArgumentException("Invalid form", e)
		} catch (e: Exception) {
			throw IllegalArgumentException("Invalid form", e)
		}
	}

	override fun listByHcPartyAndParentId(hcPartyId: String, formId: String): Flow<Form> = flow {
		emitAll(formDAO.listFormsByHcPartyAndParentId(hcPartyId, formId))
	}

	override suspend fun addDelegations(formId: String, delegations: List<Delegation>): Form? {
		val form = getForm(formId)
		return form?.let {
			formDAO.save(
				it.copy(
					delegations = it.delegations +
						delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
				)
			)
		}
	}

	override fun getGenericDAO(): FormDAO {
		return formDAO
	}

	override fun solveConflicts(): Flow<Form> =
		formDAO.listConflicts().mapNotNull {
			formDAO.get(it.id, Option.CONFLICTS)?.let { form ->
				form.conflicts?.mapNotNull { conflictingRevision -> formDAO.get(form.id, conflictingRevision) }
					?.fold(form) { kept, conflict -> kept.merge(conflict).also { formDAO.purge(conflict) } }
					?.let { mergedForm -> formDAO.save(mergedForm) }
			}
		}

	companion object {
		private val logger = LoggerFactory.getLogger(FormLogicImpl::class.java)
	}
}
