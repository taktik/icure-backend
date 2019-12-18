package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.Insurance

interface InsuranceLogic : EntityPersister<Insurance, String> {
    suspend fun createInsurance(insurance: Insurance): Insurance?
    suspend fun deleteInsurance(insuranceId: String): DocIdentifier?

    suspend fun getInsurance(insuranceId: String): Insurance?
    fun listInsurancesByCode(code: String): Flow<Insurance>
    fun listInsurancesByName(name: String): Flow<Insurance>

    suspend fun modifyInsurance(insurance: Insurance): Insurance?
    fun getInsurances(ids: Set<String>): Flow<Insurance>
}
