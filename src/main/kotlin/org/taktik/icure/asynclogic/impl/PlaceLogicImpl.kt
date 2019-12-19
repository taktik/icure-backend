package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.PlaceDAO
import org.taktik.icure.entities.Place
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PlaceLogic

@Service
class PlaceLogicImpl(private val placeDAO: PlaceDAO,
                     private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Place, PlaceDAO>(sessionLogic), PlaceLogic {

    override suspend fun createPlace(place: Place): Place? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return placeDAO.create(dbInstanceUri, groupId, place)
    }

    override fun deletePlace(ids: List<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getPlace(place: String): Place? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return placeDAO.get(dbInstanceUri, groupId, place)
    }

    override suspend fun modifyPlace(place: Place): Place? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return placeDAO.save(dbInstanceUri, groupId, place)
    }

    override fun getGenericDAO(): PlaceDAO {
        return placeDAO
    }
}
