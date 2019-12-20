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
package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.listeners.UserLogicListener
import org.taktik.icure.constants.Users
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Property
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Permission
import java.net.URI
import java.time.Instant

interface UserLogic : EntityPersister<User, String>, PrincipalLogic<User> {

    fun getProperties(userId: String): Flow<Property>
    suspend fun modifyProperties(userId: String, newProperties: Set<Property>)
    suspend fun newUser(type: Users.Type, login: String, password: String, healthcarePartyId: String): User?
    suspend fun registerUser(user: User, password: String): User?
    suspend fun createUser(user: User): User?
    suspend fun registerUser(email: String, password: String, healthcarePartyId: String, name: String): User?
    suspend fun isLoginValid(login: String): Boolean
    suspend fun isPasswordValid(password: String): Boolean
    suspend fun modifyUser(modifiedUser: User): User?
    suspend fun modifyUserAttributes(userId: String, attributesValues: Map<String, Any>)
    suspend fun enableUser(userId: String)
    suspend fun disableUser(userId: String)
    fun encodePassword(password: String): String
    suspend fun isUserActive(userId: String): Boolean
    suspend fun checkPassword(password: String): Boolean
    suspend fun verifyPasswordToken(userId: String, token: String): Boolean
    suspend fun verifyActivationToken(userId: String, token: String): Boolean
    suspend fun usePasswordToken(userId: String, token: String, newPassword: String): Boolean
    suspend fun useActivationToken(userId: String, token: String): Boolean
    suspend fun checkUsersExpiration()
    fun getExpiredUsers(fromExpirationDate: Instant, toExpirationDate: Instant): Flow<User>
    suspend fun acceptUserTermsOfUse(userId: String)
    fun addListener(listener: UserLogicListener)
    fun removeListener(listener: UserLogicListener)
    suspend fun addPermissions(userId: String, permissions: Set<Permission>)
    suspend fun modifyPermissions(userId: String, permissions: Set<Permission>)
    suspend fun modifyRoles(userId: String, roles: Set<Role>)
    suspend fun getUser(id: String): User?
    fun getUsersByLogin(login: String): Flow<User>
    suspend fun getUserByLogin(login: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun newUser(type: Users.Type, status: Users.Status, login: String, createdDate: Instant): User?
    suspend fun deleteUser(user: User)
    suspend fun undeleteUser(user: User)
    fun buildStandardUser(userName: String, password: String): User
    fun getBootstrapUser(): User
    suspend fun deleteUser(id: String)
    suspend fun undeleteUser(id: String)
    fun getRoles(user: User): Flow<Role>
    suspend fun save(user: User)
    suspend fun userLogged(user: User)
    fun listUsers(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    suspend fun setProperties(user: User, properties: List<Property>): User?
    fun getUsers(ids: List<String>): Flow<User>
    suspend fun getUserOnFallbackDb(userId: String): User?
    suspend fun getUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User?
    suspend fun findUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User?
    fun getUsersByPartialIdOnFallbackDb(id: String): Flow<User>
    fun findUsersByLoginOnFallbackDb(username: String): Flow<User>
    fun findByHcpartyId(hcpartyId: String): Flow<String>
}
