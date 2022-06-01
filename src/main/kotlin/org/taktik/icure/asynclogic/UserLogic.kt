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

import java.time.Instant
import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.listeners.UserLogicListener
import org.taktik.icure.constants.Users
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.PropertyStub

interface UserLogic : EntityPersister<User, String>, PrincipalLogic<User> {

	fun addListener(listener: UserLogicListener)
	fun buildStandardUser(userName: String, password: String): User
	fun encodePassword(password: String): String
	fun findByHcpartyId(hcpartyId: String): Flow<String>
	fun findByNameEmailPhone(searchString: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
	fun getExpiredUsers(fromExpirationDate: Instant, toExpirationDate: Instant): Flow<User>
	fun getProperties(userId: String): Flow<PropertyStub>
	fun getRoles(user: User): Flow<Role>
	fun getUsers(ids: List<String>): Flow<User>
	fun getUsersByLogin(login: String): Flow<User>
	fun listUserIdsByNameEmailPhone(searchString: String): Flow<String>
	fun listUsers(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
	fun removeListener(listener: UserLogicListener)
	suspend fun checkPassword(password: String): Boolean
	suspend fun checkUsersExpiration()
	suspend fun createUser(user: User): User?
	suspend fun createUserOnUserDb(user: User): User?
	suspend fun deleteUser(groupId: String, userId: String)
	suspend fun deleteUser(id: String)
	suspend fun deleteUser(user: User)
	suspend fun disableUser(userId: String)
	suspend fun enableUser(userId: String)
	suspend fun findUserOnUserDb(userId: String): User?
	suspend fun getUser(id: String): User?
	suspend fun getUserByEmail(email: String): User?
	suspend fun getUserByEmailOnUserDb(email: String): User?
	suspend fun getUserByLogin(login: String): User?
	suspend fun getUserOnUserDb(userId: String): User?
	suspend fun isLoginValid(login: String?): Boolean
	suspend fun isPasswordValid(password: String): Boolean
	suspend fun isUserActive(userId: String): Boolean
	suspend fun modifyUser(modifiedUser: User): User?
	suspend fun newUser(type: Users.Type, login: String, password: String?, healthcarePartyId: String): User?
	suspend fun newUser(type: Users.Type, status: Users.Status, login: String, createdDate: Instant): User?
	suspend fun registerUser(email: String, password: String, healthcarePartyId: String, name: String): User?
	suspend fun registerUser(user: User, password: String): User?
	suspend fun save(user: User): User?
	suspend fun setProperties(user: User, properties: List<PropertyStub>): User?
	suspend fun undeleteUser(id: String)
	suspend fun undeleteUser(user: User)
	suspend fun verifyAuthenticationToken(userId: String, token: String): Boolean

	/**
	 * @param tokenValidity Token validity time in seconds. By default, token will be valid during one hour
	 */
	suspend fun getToken(user: User, key: String, tokenValidity: Long = 3600): String
	fun filterUsers(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<User>): Flow<ViewQueryResultEvent>
	suspend fun getUserByPhone(phone: String): User?
}
