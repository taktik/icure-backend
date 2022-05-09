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
import org.taktik.icure.asyncdao.FrontEndMigrationDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.FrontEndMigrationLogic
import org.taktik.icure.entities.FrontEndMigration
import org.taktik.icure.exceptions.DeletionException

@ExperimentalCoroutinesApi
@Service
class FrontEndMigrationLogicImpl(
	private val frontEndMigrationDAO: FrontEndMigrationDAO,
	private val sessionLogic: AsyncSessionLogic
) : GenericLogicImpl<FrontEndMigration, FrontEndMigrationDAO>(sessionLogic), FrontEndMigrationLogic {

	override suspend fun createFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? {
		return frontEndMigrationDAO.create(frontEndMigration)
	}

	override suspend fun deleteFrontEndMigration(frontEndMigrationId: String): DocIdentifier? {
		return try {
			deleteEntities(setOf(frontEndMigrationId)).firstOrNull()
		} catch (e: Exception) {
			throw DeletionException(e.message, e)
		}
	}

	override suspend fun getFrontEndMigration(frontEndMigrationId: String): FrontEndMigration? {
		return frontEndMigrationDAO.get(frontEndMigrationId)
	}

	override fun getFrontEndMigrationByUserIdName(userId: String, name: String?): Flow<FrontEndMigration> = flow {
		emitAll(frontEndMigrationDAO.getFrontEndMigrationsByUserIdAndName(userId, name))
	}

	override suspend fun modifyFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration? {
		return frontEndMigrationDAO.save(frontEndMigration)
	}

	override fun getGenericDAO(): FrontEndMigrationDAO {
		return frontEndMigrationDAO
	}
}
