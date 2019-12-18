package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.MedicalLocation

interface MedicalLocationLogic : EntityPersister<MedicalLocation, String> {
    suspend fun createMedicalLocation(medicalLocation: MedicalLocation): MedicalLocation?
    fun deleteMedicalLocations(ids: List<String>): Flow<DocIdentifier>

    suspend fun getMedicalLocation(medicalLocation: String): MedicalLocation?

    suspend fun modifyMedicalLocation(medicalLocation: MedicalLocation): MedicalLocation?
    fun findByPostCode(postCode: String): Flow<MedicalLocation>
}
