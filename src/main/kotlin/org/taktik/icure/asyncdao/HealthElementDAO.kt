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
import java.net.URI

interface HealthElementDAO: GenericDAO<HealthElement> {
    fun findByPatient(patientId: String): Flow<HealthElement>

    fun findByPatientAndCodes(patientId: String, codes: Set<Code>): Flow<HealthElement>

    fun findByHCPartyAndCodes(healthCarePartyId: String, codeType: String, codeNumber: String): Flow<String>

    fun findByHCPartyAndTags(healthCarePartyId: String, tagType: String, tagCode: String): Flow<String>

    fun findByHCPartyAndStatus(healthCarePartyId: String, status: Int?): Flow<String>

    suspend fun findHealthElementByPlanOfActionId(planOfActionId: String): HealthElement?

    suspend fun getHealthElement(healthElementId: String): HealthElement?

    fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement>

    fun listConflicts(): Flow<HealthElement>
}
