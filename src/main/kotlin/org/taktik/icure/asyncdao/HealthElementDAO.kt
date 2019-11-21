package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.base.Code
import java.net.URI

interface HealthElementDAO {
    fun findByPatient(dbInstanceUrl: URI, groupId: String, patientId: String): Flow<HealthElement>

    fun findByPatientAndCodes(dbInstanceUrl: URI, groupId: String, patientId: String, codes: Set<Code>): Flow<HealthElement>

    fun findByHCPartyAndCodes(dbInstanceUrl: URI, groupId: String, healthCarePartyId: String, codeType: String, codeNumber: String): Flow<String?>

    fun findByHCPartyAndTags(dbInstanceUrl: URI, groupId: String, healthCarePartyId: String, tagType: String, tagCode: String): Flow<String?>

    fun findByHCPartyAndStatus(dbInstanceUrl: URI, groupId: String, healthCarePartyId: String, status: Int?): Flow<String?>

    suspend fun findHealthElementByPlanOfActionId(dbInstanceUrl: URI, groupId: String, planOfActionId: String): HealthElement?

    suspend fun getHealthElement(dbInstanceUrl: URI, groupId: String, healthElementId: String): HealthElement?

    fun findByHCPartySecretPatientKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<HealthElement>

    fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<HealthElement>
}
