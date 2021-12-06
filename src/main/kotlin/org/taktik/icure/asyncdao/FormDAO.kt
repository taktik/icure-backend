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

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Form
import java.net.URI

interface FormDAO: GenericDAO<Form> {
    fun listFormsByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Form>

    fun listFormsByHcPartyAndParentId(hcPartyId: String, formId: String): Flow<Form>

    fun findForms(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>

    fun listConflicts(): Flow<Form>

    suspend fun getAllByLogicalUuid(formUuid: String): List<Form>

    suspend fun getAllByUniqueId(externalUuid: String): List<Form>
}
