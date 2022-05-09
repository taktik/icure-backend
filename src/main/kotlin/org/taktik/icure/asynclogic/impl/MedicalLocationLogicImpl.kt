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
class MedicalLocationLogicImpl(
	private val medicalLocationDAO: MedicalLocationDAO,
	private val sessionLogic: AsyncSessionLogic
) : GenericLogicImpl<MedicalLocation, MedicalLocationDAO>(sessionLogic), MedicalLocationLogic {

	override suspend fun createMedicalLocation(medicalLocation: MedicalLocation) = fix(medicalLocation) { medicalLocation ->
		medicalLocationDAO.create(medicalLocation)
	}

	override fun deleteMedicalLocations(ids: List<String>): Flow<DocIdentifier> {
		return try {
			deleteEntities(ids)
		} catch (e: Exception) {
			throw DeletionException(e.message, e)
		}
	}

	override suspend fun getMedicalLocation(medicalLocation: String): MedicalLocation? {
		return medicalLocationDAO.get(medicalLocation)
	}

	override suspend fun modifyMedicalLocation(medicalLocation: MedicalLocation) = fix(medicalLocation) { medicalLocation ->
		medicalLocationDAO.save(medicalLocation)
	}

	override fun findMedicalLocationByPostCode(postCode: String): Flow<MedicalLocation> = flow {
		emitAll(medicalLocationDAO.byPostCode(postCode))
	}

	override fun getGenericDAO(): MedicalLocationDAO {
		return medicalLocationDAO
	}
}
