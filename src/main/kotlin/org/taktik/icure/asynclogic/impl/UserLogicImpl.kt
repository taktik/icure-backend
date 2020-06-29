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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.singleOrNull
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.listeners.UserLogicListener
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.constants.Users
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.Property
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.exceptions.UserRegistrationException
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.firstOrNull
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.regex.Pattern

@Transactional
@Service
class UserLogicImpl(
        couchDbProperties: CouchDbProperties,
        roleDao: RoleDAO,
        sessionLogic: AsyncSessionLogic,
        private val userDAO: UserDAO,
        private val healthcarePartyLogic: HealthcarePartyLogic,
        private val propertyLogic: PropertyLogic,
        private val groupLogic: GroupLogic,
        private val passwordEncoder: PasswordEncoder,
        private val uuidGenerator: UUIDGenerator) : PrincipalLogicImpl<User>(roleDao, sessionLogic), UserLogic {

    private val dbInstanceUri = URI(couchDbProperties.url)
    private val listeners: MutableSet<UserLogicListener> = HashSet()

    override fun addListener(listener: UserLogicListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: UserLogicListener) {
        listeners.remove(listener)
    }

    override suspend fun getUser(id: String): User? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.getUserOnUserDb(dbInstanceUri, groupId, id, false).also { fillGroup(it, groupId) }
    }

    private suspend fun fillGroup(user: User, groupId: String? = null): User =
            user.copy(groupId = groupId ?: sessionLogic.getCurrentSessionContext().getGroupId())

    override suspend fun getUserByEmail(email: String): User? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.findByEmail(dbInstanceUri, groupId, email).singleOrNull()?.also { fillGroup(it, groupId) }
    }

    suspend fun getUserByEmail(groupId: String, email: String): User? {
        val group = getDestinationGroup(groupId)
        return group.id?.let {
            userDAO.findByEmail(URI.create(group.dbInstanceUrl()
                    ?: dbInstanceUri.toASCIIString()), it, email).singleOrNull()?.also { fillGroup(it, group.id) }
        }
    }

    override fun findByHcpartyId(hcpartyId: String): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(userDAO.findByHcpId(dbInstanceUri, groupId, hcpartyId).mapNotNull { v: User -> v.id })
    }

    override suspend fun newUser(type: Users.Type, status: Users.Status, email: String, createdDate: Instant): User {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.create(dbInstanceUri, groupId, User(
                id = uuidGenerator.newGUID().toString(),
                type = type,
                status = status,
                createdDate = createdDate,
                login = email,
                email = email
        )) ?: throw java.lang.IllegalStateException("Cannot create user")
    }

    override fun buildStandardUser(userName: String, password: String) = User(
            id = uuidGenerator.newGUID().toString(),
            type = Users.Type.database,
            status = Users.Status.ACTIVE,
            name = userName,
            login = userName,
            createdDate = Instant.now(),
            passwordHash = encodePassword(password),
            email = userName)

    override suspend fun deleteUser(userId: String) {
        val user = getUser(userId)
        user?.let { deleteUser(it) }
    }

    override suspend fun deleteUser(groupId: String, userId: String) {
        val group = getDestinationGroup(groupId)
        group.id.let {
            userDAO.remove(URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString()), it,
                    userDAO.getUserOnUserDb(URI.create(group.dbInstanceUrl()
                            ?: dbInstanceUri.toASCIIString()), it, userId, false)
            )
        }
    }

    override suspend fun undeleteUser(userId: String) {
        val user = getUser(userId)
        user?.let { undeleteUser(it) }
    }

    override fun getRoles(user: User): Flow<Role> {
        return getParents(user)
    }

    override fun getUsersByLogin(login: String): Flow<User> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(userDAO.findByUsername(dbInstanceUri, groupId, formatLogin(login)).map { fillGroup(it, groupId) })
    }

    override fun listUsersByLoginOnFallbackDb(login: String): Flow<User> =
            userDAO.findByUsernameOnFallback(dbInstanceUri, login)

    override fun listUsersByEmailOnFallbackDb(email: String): Flow<User> =
            userDAO.listByEmailOnFallbackDb(dbInstanceUri, email)

    override suspend fun getUserByLogin(login: String): User? { // Format login
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.findByUsername(dbInstanceUri, groupId, formatLogin(login)).firstOrNull()?.also { fillGroup(it, groupId) }
    }

    override suspend fun newUser(type: Users.Type, email: String, password: String?, healthcarePartyId: String): User? { // Format login
        val email = formatLogin(email)
        Validate.isTrue(isLoginValid(email), "Login is invalid")

        type.takeIf { it == Users.Type.database }
                .let { Validate.isTrue(isPasswordValid(password!!), "Password is invalid") }

        getUserByLogin(email)?.let { throw CreationException("User already exists") }
        val user =  setHealthcarePartyIdIfExists(healthcarePartyId, newUser(type, Users.Status.ACTIVE, email, Instant.now()))

        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return fix(password?.let { user.copy(passwordHash = encodePassword(password)) } ?: user) { userDAO.create(dbInstanceUri, groupId, it) }
    }

    private suspend fun setHealthcarePartyIdIfExists(healthcarePartyId: String?, user: User) : User {
        return healthcarePartyId?.let {
            val healthcareParty = healthcarePartyLogic.getHealthcareParty(it)
            if (healthcareParty != null) {
                user.copy(healthcarePartyId = healthcarePartyId)
            } else {
                Companion.log.error("newUser: healthcare party " + healthcarePartyId + "does not exist. But, user is created with Null healthcare party.")
                user
            }
        } ?: user
    }

    override suspend fun registerUser(user: User, password: String): User? {
        return if (propertyLogic.getSystemPropertyValue<Any?>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier) != null
                && propertyLogic.getSystemPropertyValue<Boolean>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier)!!) {

            if (!isLoginValid(user.login)) {
                throw UserRegistrationException("Login is invalid")
            }
            // Check password
            if (!isPasswordValid(password)) {
                throw UserRegistrationException("Password is invalid")
            }
            // Check login does not already exist
            if (user.login?.let { getUserByLogin(it) } != null) {
                throw UserRegistrationException("User login already exists")
            }

            // Save user
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            fix(user.copy(
                    passwordHash = encodePassword(password),
                    type = Users.Type.database,
                    status = Users.Status.ACTIVE,
                    createdDate = Instant.now()
            )) { userDAO.create(dbInstanceUri, groupId, it) }?.also {
                for (listener in listeners) {
                    listener.userRegistered(it)
                }
            }
        } else null
    }

    override suspend fun createUser(user: User): User? { // checking requirements
        if (user.login != null || user.email == null) {
            throw MissingRequirementsException("createUser: Requirements are not met. Email has to be set and the Login has to be null.")
        }
        return try { // check whether user exists
            val userByEmail = getUserByEmail(user.email)
            userByEmail?.let { throw CreationException("User already exists (" + user.email + ")") }
            fix(user.copy(
                    createdDate = Instant.now(),
                    login = user.email
            )) { createEntities(setOf(it)).firstOrNull() }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid User", e)
        }
    }

    override suspend fun createUser(groupId: String, user: User): User? {
        val group = getDestinationGroup(groupId)

        if (user.login != null || user.email == null) {
            throw MissingRequirementsException("createUser: Requirements are not met. Email has to be set and the Login has to be null.")
        }
        return try { // check whether user exists
            getUserByEmail(groupId, user.email)?.let { throw CreationException("User already exists (" + user.email + ")") }
            fix(user.copy(login = user.email)) { createEntities(group, setOf(it)).firstOrNull() }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid User", e)
        }
    }

    override fun createEntities(users: Collection<User>): Flow<User> = flow {
        val regex = Regex("^[0-9a-zA-Z]{64}$")
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        for (user in users) {

            fix(
                    if (user.passwordHash != null && !user.passwordHash.matches(regex)) {
                        user.copy(passwordHash = encodePassword(user.passwordHash))
                    } else user
            ) { userDAO.create(dbInstanceUri, groupId, user) }?.let { emit(it) }
        }
    }

    private fun createEntities(group: Group, users: Collection<User>): Flow<User> = flow {
        val regex = Regex("^[0-9a-zA-Z]{64}$")
        for (user in users) {
            fix(if (user.passwordHash != null && !user.passwordHash.matches(regex)) {
                user.copy(passwordHash = encodePassword(user.passwordHash))
            } else user) { user ->
                userDAO.create(URI.create(group.dbInstanceUrl()
                        ?: dbInstanceUri.toASCIIString()), group.id, user)?.also { emit(it) }
            }
        }
    }

    override suspend fun registerUser(email: String, password: String, healthcarePartyId: String, name: String): User? {
        if (propertyLogic.getSystemPropertyValue<Any?>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier) != null
                && propertyLogic.getSystemPropertyValue<Boolean>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier)!!) {

            val user = newUser(Users.Type.database, Users.Status.ACTIVE, formatLogin(email), Instant.now()).copy(
                    email = email,
                    name = name
            )
            return registerUser(setHealthcarePartyIdIfExists(healthcarePartyId, user), password)
        }
        return null
    }

    override suspend fun isUserActive(userId: String) = getUser(userId)?.let {
        if (it.status == null || it.status != Users.Status.ACTIVE) {
            false
        } else it.expirationDate == null || !it.expirationDate!!.isBefore(Instant.now())
    } ?: false

    override suspend fun checkPassword(password: String) =
            sessionLogic.getCurrentSessionContext().getUser().let { passwordEncoder.matches(password, it?.passwordHash) }

    override suspend fun verifyPasswordToken(userId: String, token: String): Boolean {
        getUser(userId)?.takeIf { it.passwordToken?.isNotEmpty() ?: false }
                ?.let {
                    if (it.passwordToken == token) {
                        return it.passwordTokenExpirationDate == null || it.passwordTokenExpirationDate!!.isAfter(Instant.now())
                    }
                }
        return false
    }

    override suspend fun verifyActivationToken(userId: String, token: String): Boolean {
        getUser(userId)?.let {
            if (it.activationToken != null && it.activationToken == token) {
                return it.activationTokenExpirationDate == null || it.activationTokenExpirationDate!!.isAfter(Instant.now())
            }
        }
        return false
    }

    override fun getExpiredUsers(fromExpirationDate: Instant, toExpirationDate: Instant): Flow<User> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(userDAO.getExpiredUsers(dbInstanceUri, groupId, fromExpirationDate, toExpirationDate))
    }

    private fun formatLogin(login: String) = login.trim { it <= ' ' }

    override suspend fun isLoginValid(login: String?): Boolean {
        return login?.takeIf { it.isNotEmpty() }?.let {
            val loginRegexp = propertyLogic.getSystemPropertyValue<String>(PropertyTypes.System.USER_LOGIN_REGEXP.identifier)
            return loginRegexp?.takeIf { it.isNotEmpty() }?.let { Pattern.matches(loginRegexp, login) } ?: true
        } ?: false
    }

    override suspend fun isPasswordValid(password: String): Boolean {
        return password.takeIf { it.isNotEmpty() }?.let {
            // Check for regular expression
            val passwordRegexp = propertyLogic.getSystemPropertyValue<String>(PropertyTypes.System.USER_PASSWORD_REGEXP.identifier)
            return passwordRegexp?.takeIf { it.isNotEmpty() }?.let { Pattern.matches(passwordRegexp, password) } ?: true
        } ?: false
    }

    override suspend fun modifyUser(modifiedUser: User): User? {
        // Save user
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return (userDAO.save(dbInstanceUri, groupId,
                if (modifiedUser.passwordHash != null && !modifiedUser.passwordHash.matches(Regex("^[0-9a-zA-Z]{64}$"))) {
                    modifiedUser.copy(passwordHash = encodePassword(modifiedUser.passwordHash))
                } else modifiedUser)?.let { fillGroup(it, groupId) }
        )
    }

    override suspend fun modifyUser(groupId: String, modifiedUser: User): User? {
        val group = getDestinationGroup(groupId)
        return userDAO.save(URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString()), group.id, if (modifiedUser.passwordHash != null && !modifiedUser.passwordHash.matches(Regex("^[0-9a-zA-Z]{64}$"))) {
            modifiedUser.copy(passwordHash = encodePassword(modifiedUser.passwordHash))
        } else modifiedUser)
    }

    override suspend fun getToken(group: Group, user: User, key: String): String {
        val uri = group.servers?.firstOrNull()?.let { URI(it) } ?: dbInstanceUri
        return user.applicationTokens[key]
                ?: (userDAO.getUserOnUserDb(uri, group.id, if (user.id.contains(':')) user.id.split(":")[1] else user.id, false).let {
                    userDAO.save(URI.create(group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString()), group.id, it.copy(applicationTokens = it.applicationTokens + (key to uuidGenerator.newGUID().toString()))) ?: throw IllegalStateException("Cannot create token for user")
                }.applicationTokens[key] ?: error("Cannot create token for user"))
    }


    override suspend fun deleteUser(user: User) {
        user.id.let { userId ->
            getUser(userId)?.let {
                val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
                userDAO.remove(dbInstanceUri, groupId, user)
            }
        }
    }

    override suspend fun undeleteUser(user: User) {
        user.id.let { userId ->
            getUser(userId)?.let {
                val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
                userDAO.unRemove(dbInstanceUri, groupId, it)
            }
        }
    }

    override suspend fun disableUser(userId: String) {
        getUser(userId)?.let {
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.save(dbInstanceUri, groupId, it.copy(status = Users.Status.DISABLED))
        }
    }

    override suspend fun enableUser(userId: String) {
        getUser(userId)?.let {
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.save(dbInstanceUri, groupId, it.copy(status = Users.Status.ACTIVE))
        }
    }

    override fun getProperties(userId: String): Flow<PropertyStub> = flow {
        emitAll(getProperties(userId, true, true, true))
    }

    override fun updateEntities(users: Collection<User>): Flow<User> = flow {
        emitAll(users.asFlow().mapNotNull { modifyUser(it) })
    }

    suspend fun deleteEntities(userIds: Collection<String>) { //TODO MB was override here
        for (userId in userIds) {
            deleteUser(userId)
        }
    }

    suspend fun undeleteEntities(userIds: Collection<String>) { //TODO MB was override here
        for (userId in userIds) {
            undeleteUser(userId)
        }
    }

    override fun getAllEntities(): Flow<User> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(userDAO.getAll(dbInstanceUri, groupId).onEach { }.map { fillGroup(it, groupId) })
    }

    override fun getAllEntityIds(): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(userDAO.getAllIds(dbInstanceUri, groupId))
    }

    override suspend fun hasEntities(): Boolean {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.hasAny(dbInstanceUri, groupId)
    }

    override suspend fun exists(id: String): Boolean {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.contains(dbInstanceUri, groupId, id)
    }

    override suspend fun getEntity(id: String): User? {
        return getUser(id)
    }

    override suspend fun checkUsersExpiration() {
        val toExpirationDate = Instant.now()
        val fromExpirationDate = toExpirationDate.minus(CHECK_USERS_EXPIRATION_TIME_RANGE)
        // Get users that expired in this time range
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.getExpiredUsers(dbInstanceUri, groupId, fromExpirationDate, toExpirationDate).map {
            for (listener in listeners) {
                listener.userExpired(it)
            }
        }.collect()
    }

    override suspend fun save(user: User): User? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.save(dbInstanceUri, groupId, user)
    }

    override fun listUsers(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(userDAO.listUsers(dbInstanceUri, groupId, paginationOffset))
    }

    override fun listUsers(groupId: String, paginationOffset: PaginationOffset<String>) = flow {
        val group = getDestinationGroup(groupId)
        emitAll(userDAO.listUsers(URI.create(group.dbInstanceUrl()
                ?: dbInstanceUri.toASCIIString()), group.id, paginationOffset))
    }

    override suspend fun setProperties(user: User, properties: List<PropertyStub>): User? {
        val properties = properties.fold(user.properties) { props, p ->
            val prop = user.properties.find { pp -> pp.type?.identifier == p.type?.identifier }
            prop?.let { props - it + it.copy(typedValue = p.typedValue) } ?: props + p
        }
        return modifyUser(user.copy(properties = properties))
    }

    override fun getUsers(ids: List<String>): Flow<User> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(userDAO.getList(dbInstanceUri, groupId, ids).map { fillGroup(it, groupId) })
    }

    override suspend fun getUserOnFallbackDb(userId: String): User? {
        return userDAO.getOnFallback(dbInstanceUri, userId, false)
    }

    override suspend fun createUserOnUserDb(user: User, groupId: String, dbInstanceUrl: URI): User? {
        if (user.login != null || user.email == null) {
            throw MissingRequirementsException("createUser: Requirements are not met. Email has to be set and the Login has to be null.")
        }
        try { // check whether user exists
            getUserByEmailOnUserDb(user.email, groupId, dbInstanceUrl)?.let { throw CreationException("User already exists (" + user.email + ")") }

            return fix(user.copy(createdDate = Instant.now(),
                    login = user.email)) { getGenericDAO().create(dbInstanceUrl, groupId, it) }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid User", e)
        }
    }

    override suspend fun getUserByEmailOnUserDb(email: String, groupId: String, dbInstanceUrl: URI): User? {
        return userDAO.findByEmail(dbInstanceUrl, groupId, email).singleOrNull()?.also { fillGroup(it, groupId) }
    }

    override suspend fun getUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User {
        return fillGroup(userDAO.getUserOnUserDb(dbInstanceUrl, groupId, userId, false), groupId)
    }


    override suspend fun findUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User? {
        return userDAO.findUserOnUserDb(dbInstanceUrl, groupId, userId, false)?.also { fillGroup(it, groupId) }
    }

    override suspend fun getPrincipal(userId: String) = getUser(userId)

    override fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserLogicImpl::class.java)
        private val pub = PropertyUtilsBean()
        private val CHECK_USERS_EXPIRATION_TIME_RANGE = Duration.ofDays(1)
    }

    override fun getGenericDAO(): GenericDAO<User> {
        return userDAO
    }

    protected suspend fun getDestinationGroup(groupId: String): Group {
        val groupUserId = sessionLogic.getCurrentSessionContext().getGroupIdUserId()
        val userGroupId = userDAO.getOnFallback(dbInstanceUri, groupUserId, false)?.groupId
                ?: throw IllegalAccessException("Invalid user, no group")
        val group = groupLogic.getGroup(groupId)

        if (group?.superGroup != userGroupId) {
            throw IllegalAccessException("You are not allowed to access this group database")
        }
        return group
    }


}
