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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.InsuranceDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.entities.Insurance
import org.taktik.icure.exceptions.DeletionException

@ExperimentalCoroutinesApi
@Service
class InsuranceLogicImpl(private val insuranceDAO: InsuranceDAO,
                         private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Insurance, InsuranceDAO>(sessionLogic), InsuranceLogic {
    override suspend fun createInsurance(insurance: Insurance) = fix(insurance) { insurance ->
        insuranceDAO.create(insurance)
    }

    override suspend fun deleteInsurance(insuranceId: String): DocIdentifier? {
        return try {
            deleteEntities(listOf(insuranceId)).firstOrNull()
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getInsurance(insuranceId: String): Insurance? {
        return insuranceDAO.get(insuranceId)
    }

    override fun listInsurancesByCode(code: String): Flow<Insurance> = flow {
        emitAll(insuranceDAO.listInsurancesByCode(code))
    }

    override fun listInsurancesByName(name: String): Flow<Insurance> = flow {
        emitAll(insuranceDAO.listInsurancesByName(name))
    }

    override suspend fun modifyInsurance(insurance: Insurance) = fix(insurance) { insurance ->
        insuranceDAO.save(insurance)
    }

    override fun getInsurances(ids: Set<String>): Flow<Insurance> = flow {
        emitAll(insuranceDAO.getEntities(ids))
    }

    override fun getGenericDAO(): InsuranceDAO {
        return insuranceDAO
    }
}
