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
