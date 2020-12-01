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

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.FrontEndMigrationDAO
import org.taktik.icure.entities.FrontEndMigration

interface FrontEndMigrationLogic : EntityPersister<FrontEndMigration, String> {
    suspend fun createFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration?
    suspend fun deleteFrontEndMigration(frontEndMigrationId: String): DocIdentifier?

    suspend fun getFrontEndMigration(frontEndMigrationId: String): FrontEndMigration?
    fun getFrontEndMigrationByUserIdName(userId: String, name: String?): Flow<FrontEndMigration>

    suspend fun modifyFrontEndMigration(frontEndMigration: FrontEndMigration): FrontEndMigration?
    fun getGenericDAO(): FrontEndMigrationDAO
}
