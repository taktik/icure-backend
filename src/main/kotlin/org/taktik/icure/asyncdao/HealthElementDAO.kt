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
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Identifier

interface HealthElementDAO: GenericDAO<HealthElement> {
    fun listHealthElementsByHcParty(hcPartyId: String): Flow<String>

    fun listHealthElementsByHcPartyAndCodes(hcPartyId: String, codeType: String, codeNumber: String): Flow<String>

    fun listHealthElementsByHcPartyAndTags(hcPartyId: String, tagType: String, tagCode: String): Flow<String>

    fun listHealthElementsByHcPartyAndStatus(hcPartyId: String, status: Int?): Flow<String>

    fun listHealthElementsIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>): Flow<String>

    suspend fun getHealthElementByPlanOfActionId(planOfActionId: String): HealthElement?

    suspend fun getHealthElement(healthElementId: String): HealthElement?

    fun listHealthElementsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement>

    fun listConflicts(): Flow<HealthElement>
}
