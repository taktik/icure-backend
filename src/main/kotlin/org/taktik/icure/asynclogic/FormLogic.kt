package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.FormDAO
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.embed.Delegation

interface FormLogic : EntityPersister<Form, String> {
    suspend fun getForm(id: String): Form?
    fun getForms(selectedIds: Collection<String>): Flow<Form>
    fun findByHCPartyPatient(hcPartyId: String, secretPatientKeys: List<String>, healthElementId: String?, planOfActionId: String?, formTemplateId: String?): Flow<Form>

    suspend fun addDelegation(formId: String, delegation: Delegation): Form?

    suspend fun createForm(form: Form): Form?
    fun deleteForms(ids: Set<String>): Flow<DocIdentifier>

    suspend fun modifyForm(form: Form): Form?
    fun findByHcPartyParentId(hcPartyId: String, formId: String): Flow<Form>

    suspend fun addDelegations(formId: String, delegations: List<Delegation>): Form?
    fun getGenericDAO(): FormDAO
    suspend fun solveConflicts()
    suspend fun getAllByExternalUuid(documentId: String): List<Form>
}
