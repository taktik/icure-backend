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

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.FormDAO
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.embed.Delegation

interface FormLogic : EntityPersister<Form, String> {
    suspend fun getForm(id: String): Form?
    fun getForms(selectedIds: Collection<String>): Flow<Form>
    fun listFormsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>, healthElementId: String?, planOfActionId: String?, formTemplateId: String?): Flow<Form>

    suspend fun addDelegation(formId: String, delegation: Delegation): Form?

    suspend fun createForm(form: Form): Form?
    fun deleteForms(ids: Set<String>): Flow<DocIdentifier>

    suspend fun modifyForm(form: Form): Form?
    fun listByHcPartyAndParentId(hcPartyId: String, formId: String): Flow<Form>

    suspend fun addDelegations(formId: String, delegations: List<Delegation>): Form?
    fun getGenericDAO(): FormDAO
    fun solveConflicts(): Flow<Form>
    suspend fun getFormsByExternalUuid(documentId: String): List<Form>
}
