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

package org.taktik.icure.asyncdao

import java.net.URI
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User

interface UserDAO : GenericDAO<User> {
	fun getExpiredUsers(fromExpirationInstant: Instant, toExpirationInstant: Instant): Flow<User>
	fun listUserIdsByNameEmailPhone(searchString: String): Flow<String>
	fun listUsersByUsername(searchString: String): Flow<User>
	fun listUsersByEmail(email: String): Flow<User>
	fun listUsersByPhone(phone: String): Flow<User>
	fun findUsers(pagination: PaginationOffset<String>, skipPatients: Boolean = false, extendedLimit: Int): Flow<ViewQueryResultEvent>
	fun listUsersByHcpId(hcPartyId: String): Flow<User>
	fun listUsersByPatientId(patientId: String): Flow<User>
	suspend fun getUserOnUserDb(userId: String, bypassCache: Boolean): User
	suspend fun findUserOnUserDb(userId: String, bypassCache: Boolean): User?
	fun getUsersOnDb(dbInstanceUrl: URI): Flow<User>
	suspend fun evictFromCache(userIds: Flow<String>)
	fun findUsersByIds(userIds: Flow<String>): Flow<ViewQueryResultEvent>
	fun findUsersByNameEmailPhone(searchString: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
}
