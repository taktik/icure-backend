package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.MedicalLocationDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MedicalLocationLogic
import org.taktik.icure.entities.MedicalLocation
import org.taktik.icure.exceptions.DeletionException

@ExperimentalCoroutinesApi
@Service
class MedicalLocationLogicImpl(private val medicalLocationDAO: MedicalLocationDAO,
                               private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<MedicalLocation, MedicalLocationDAO>(sessionLogic), MedicalLocationLogic {

    override suspend fun createMedicalLocation(medicalLocation: MedicalLocation) = fix(medicalLocation) { medicalLocation ->
        medicalLocationDAO.create(medicalLocation)
    }

    override fun deleteMedicalLocations(ids: List<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getMedicalLocation(medicalLocation: String): MedicalLocation? {
        return medicalLocationDAO.get(medicalLocation)
    }

    override suspend fun modifyMedicalLocation(medicalLocation: MedicalLocation)= fix(medicalLocation) { medicalLocation ->
        medicalLocationDAO.save(medicalLocation)
    }

    override fun findByPostCode(postCode: String): Flow<MedicalLocation> = flow {
        emitAll(medicalLocationDAO.byPostCode(postCode))
    }

    override fun getGenericDAO(): MedicalLocationDAO {
        return medicalLocationDAO
    }
}
