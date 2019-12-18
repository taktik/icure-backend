/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.InsuranceDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.entities.Insurance
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.utils.firstOrNull

@ExperimentalCoroutinesApi
@Service
class InsuranceLogicImpl(private val insuranceDAO: InsuranceDAO,
                         private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Insurance, InsuranceDAO>(sessionLogic), InsuranceLogic {
    override suspend fun createInsurance(insurance: Insurance): Insurance? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return insuranceDAO.create(dbInstanceUri, groupId, insurance)
    }

    override suspend fun deleteInsurance(insuranceId: String): DocIdentifier? {
        return try {
            deleteByIds(listOf(insuranceId)).firstOrNull()
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getInsurance(insuranceId: String): Insurance? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return insuranceDAO.get(dbInstanceUri, groupId, insuranceId)
    }

    override fun listInsurancesByCode(code: String): Flow<Insurance> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(insuranceDAO.listByCode(dbInstanceUri, groupId, code))
    }

    override fun listInsurancesByName(name: String): Flow<Insurance> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(insuranceDAO.listByName(dbInstanceUri, groupId, name))
    }

    override suspend fun modifyInsurance(insurance: Insurance): Insurance? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return insuranceDAO.save(dbInstanceUri, groupId, insurance)
    }

    override fun getInsurances(ids: Set<String>): Flow<Insurance> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(insuranceDAO.getList(dbInstanceUri, groupId, ids))
    }

    override fun getGenericDAO(): InsuranceDAO {
        return insuranceDAO
    }
}
