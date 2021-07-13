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
import org.taktik.icure.asyncdao.HealthElementDAO
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation

interface HealthElementLogic : EntityPersister<HealthElement, String> {
    fun getGenericDAO(): HealthElementDAO

    suspend fun createHealthElement(healthElement: HealthElement): HealthElement?

    suspend fun getHealthElement(healthElementId: String): HealthElement?
    fun getHealthElements(healthElementIds: List<String>): Flow<HealthElement>
    fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement>

    suspend fun findLatestByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): List<HealthElement>
    fun findByHCPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String): Flow<String>
    fun findByHCPartyAndTags(hcPartyId: String, tagType: String, tagCode: String): Flow<String>
    fun findByHCPartyAndStatus(hcPartyId: String, status: Int): Flow<String>
    fun deleteHealthElements(ids: Set<String>): Flow<DocIdentifier>

    suspend fun modifyHealthElement(healthElement: HealthElement): HealthElement?

    suspend fun addDelegation(healthElementId: String, delegation: Delegation): HealthElement?

    suspend fun addDelegations(healthElementId: String, delegations: List<Delegation>): HealthElement?

    fun solveConflicts(): Flow<HealthElement>

    fun filter(filter: FilterChain<HealthElement>): Flow<HealthElement>
}
