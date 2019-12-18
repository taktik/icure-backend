package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.PlaceDAO
import org.taktik.icure.entities.Place

interface PlaceLogic : EntityPersister<Place, String> {
    suspend fun createPlace(place: Place): Place?
    fun deletePlace(ids: List<String>): Flow<DocIdentifier>

    suspend fun getPlace(place: String): Place?

    suspend fun modifyPlace(place: Place): Place?
    fun getGenericDAO(): PlaceDAO
}
