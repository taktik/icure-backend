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
import org.taktik.icure.entities.embed.Identifier

interface HealthcarePartyDAO : GenericDAO<HealthcareParty> {
	fun listHealthcarePartiesByNihii(nihii: String?): Flow<HealthcareParty>

	fun listHealthcarePartiesBySsin(ssin: String): Flow<HealthcareParty>

	fun listHealthcarePartiesBySpecialityAndPostcode(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>

	fun findHealthCareParties(pagination: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

	fun listHealthcarePartiesByName(name: String): Flow<HealthcareParty>

	fun findHealthcarePartiesBySsinOrNihii(searchValue: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

	fun findHealthcarePartiesByHcPartyNameContainsFuzzy(searchString: String?, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>

	fun listHealthcareParties(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

	@Deprecated(message = "A HCP may now have multiple AES Keys. Use getAesExchangeKeysForDelegate instead")
	suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

	suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, List<String>>

	fun listHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty>

	fun findHealthcarePartiesByIds(hcpIds: Flow<String>): Flow<ViewQueryResultEvent>
	fun listHealthcarePartyIdsByIdentifiers(hcpIdentifiers: List<Identifier>): Flow<String>
	fun listHealthcarePartyIdsByCode(codeType: String?, codeCode: String?): Flow<String>
	fun listHealthcarePartyIdsByTag(tagType: String?, tagCode: String?): Flow<String>
}
