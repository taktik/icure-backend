package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.MedicalLocationDAO
import org.taktik.icure.asynclogic.AsyncICureSessionLogic
import org.taktik.icure.asynclogic.MedicalLocationLogic
import org.taktik.icure.entities.MedicalLocation
import org.taktik.icure.exceptions.DeletionException

@ExperimentalCoroutinesApi
@Service
class MedicalLocationLogicImpl(private val medicalLocationDAO: MedicalLocationDAO,
                               private val sessionLogic: AsyncICureSessionLogic) : GenericLogicImpl<MedicalLocation, MedicalLocationDAO>(sessionLogic), MedicalLocationLogic {

    override suspend fun createMedicalLocation(medicalLocation: MedicalLocation): MedicalLocation? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return medicalLocationDAO.create(dbInstanceUri, groupId, medicalLocation)
    }

    override fun deleteMedicalLocations(ids: List<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getMedicalLocation(medicalLocation: String): MedicalLocation? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return medicalLocationDAO.get(dbInstanceUri, groupId, medicalLocation)
    }

    override suspend fun modifyMedicalLocation(medicalLocation: MedicalLocation): MedicalLocation? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return medicalLocationDAO.save(dbInstanceUri, groupId, medicalLocation)
    }

    override fun findByPostCode(postCode: String): Flow<MedicalLocation> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(medicalLocationDAO.byPostCode(dbInstanceUri, groupId, postCode))
    }

    override fun getGenericDAO(): MedicalLocationDAO {
        return medicalLocationDAO
    }
}
