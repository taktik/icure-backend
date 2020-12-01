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
import org.taktik.icure.entities.HealthcareParty
import java.net.URI

interface HealthcarePartyDAO: GenericDAO<HealthcareParty> {
    fun findByNihii(nihii: String?): Flow<HealthcareParty>

    fun findBySsin(ssin: String): Flow<HealthcareParty>

    fun findBySpecialityPostcode(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>

    fun listHealthCareParties(pagination: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findByName(name: String): Flow<HealthcareParty>

    fun findBySsinOrNihii(searchValue: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findByHcPartyNameContainsFuzzy(searchString: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

    fun findHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

    suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

    fun findByParentId(parentId: String): Flow<HealthcareParty>
}
