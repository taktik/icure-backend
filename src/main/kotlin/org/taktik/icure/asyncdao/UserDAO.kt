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

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import java.net.URI
import java.time.Instant

interface UserDAO : GenericDAO<User>{
	fun getExpiredUsers(fromExpirationInstant: Instant, toExpirationInstant: Instant): Flow<User>
	fun findByUsername(searchString: String): Flow<User>
    fun findByEmail(searchString: String): Flow<User>
    fun listByEmailOnFallbackDb(email: String): Flow<User>
    fun listUsers(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    suspend fun getOnFallback(userId: String, bypassCache: Boolean): User?
    fun getUsersByPartialIdOnFallback(id: String): Flow<User>
    fun findByHcpId(hcPartyId: String): Flow<User>
    fun findByUsernameOnFallback(login: String): Flow<User>
    suspend fun getUserOnUserDb(userId: String, bypassCache: Boolean): User
    suspend fun findUserOnUserDb(userId: String, bypassCache: Boolean): User?
    fun getUsersOnDb(dbInstanceUrl: URI): Flow<User>
    suspend fun evictFromCache(userIds: Flow<String>)
    suspend fun saveOnFallback(user: User): User
    suspend fun save(newEntity: Boolean?, entity: User): User?
}
