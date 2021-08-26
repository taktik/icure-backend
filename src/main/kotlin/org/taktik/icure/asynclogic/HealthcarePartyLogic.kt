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
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.HealthcarePartyDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthcareParty
import java.net.URI

interface HealthcarePartyLogic : EntityPersister<HealthcareParty, String> {
    fun getGenericDAO(): HealthcarePartyDAO

    suspend fun getHealthcareParty(id: String): HealthcareParty?
    fun listHealthcarePartiesBy(searchString: String, offset: Int, limit: Int): Flow<HealthcareParty>

    suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

    suspend fun modifyHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty?
    fun deleteHealthcareParties(healthcarePartyIds: List<String>): Flow<DocIdentifier>

    suspend fun createHealthcareParty(healthcareParty: HealthcareParty): HealthcareParty?

    suspend fun modifyHcPartyKeys(healthcarePartyId: String, newHcPartyKeys: Map<String, Array<String>>): Map<String, Array<String>>
    fun findHealthcarePartiesBy(offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>
    fun findHealthcarePartiesBy(fuzzyName: String, offset: PaginationOffset<String>, desc: Boolean?): Flow<ViewQueryResultEvent>
    fun listHealthcarePartiesByNihii(nihii: String): Flow<HealthcareParty>
    fun listHealthcarePartiesBySsin(ssin: String): Flow<HealthcareParty>
    fun listHealthcarePartiesByName(name: String): Flow<HealthcareParty>

    suspend fun getPublicKey(healthcarePartyId: String): String?
    fun listHealthcarePartiesBy(type: String, spec: String, firstCode: String, lastCode: String): Flow<ViewQueryResultEvent>
    fun getHealthcareParties(ids: List<String>): Flow<HealthcareParty>
    fun findHealthcarePartiesBySsinOrNihii(searchValue: String, paginationOffset: PaginationOffset<String>, desc: Boolean): Flow<ViewQueryResultEvent>
    fun getHealthcarePartiesByParentId(parentId: String): Flow<HealthcareParty>
    suspend fun getHcpHierarchyIds(sender: HealthcareParty): HashSet<String>

    suspend fun createHealthcarePartyOnUserDb(healthcareParty: HealthcareParty, HealthcareParty: URI): HealthcareParty?
    fun getHealthcareParties(groupId: String, ids: List<String>?): Flow<HealthcareParty>
    fun deleteHealthcareParties(groupId: String, healthcarePartyIds: List<String>): Flow<DocIdentifier>
    suspend fun createHealthcareParty(groupId: String, healthcareParty: HealthcareParty): HealthcareParty?
    suspend fun modifyHealthcareParty(groupId: String, healthcareParty: HealthcareParty): HealthcareParty?
}
