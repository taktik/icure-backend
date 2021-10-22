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

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.PlaceDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.entities.Place
import org.taktik.icure.exceptions.DeletionException

@Service
class PlaceLogicImpl(private val placeDAO: PlaceDAO,
                     private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Place, PlaceDAO>(sessionLogic), PlaceLogic {

    override suspend fun createPlace(place: Place) = fix(place) { place ->
        placeDAO.create(place)
    }

    override fun deletePlace(ids: List<String>): Flow<DocIdentifier> {
        return try {
            deleteEntities(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getPlace(place: String): Place? {
        return placeDAO.get(place)
    }

    override suspend fun modifyPlace(place: Place) = fix(place) { place ->
        placeDAO.save(place)
    }

    override fun getGenericDAO(): PlaceDAO {
        return placeDAO
    }
}
