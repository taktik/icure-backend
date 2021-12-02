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

import kotlinx.coroutines.flow.*
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.listeners.UserLogicListener
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.constants.Users
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Role
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.security.AuthenticationToken
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.exceptions.UserRegistrationException
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.firstOrNull
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.regex.Pattern

@Service
class UserLogicImpl(
        couchDbProperties: CouchDbProperties,
        roleDao: RoleDAO,
        sessionLogic: AsyncSessionLogic,
        private val userDAO: UserDAO,
        private val healthcarePartyLogic: HealthcarePartyLogic,
        private val propertyLogic: PropertyLogic,
        private val passwordEncoder: PasswordEncoder,
        private val uuidGenerator: UUIDGenerator) : PrincipalLogicImpl<User>(roleDao, sessionLogic), UserLogic {

    private val dbInstanceUri = URI(couchDbProperties.url)
    private val listeners: MutableSet<UserLogicListener> = HashSet()
    private val passwordRegex = Regex("^(\\{.+?})?[0-9a-zA-Z]{64}$")

    override fun addListener(listener: UserLogicListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: UserLogicListener) {
        listeners.remove(listener)
    }

    override suspend fun getUser(id: String): User? {
        return userDAO.getUserOnUserDb(id, false)
    }

    override suspend fun getUserByEmail(email: String): User? {
        val findByEmail = userDAO.listUsersByEmail(email).toList()
        return findByEmail.firstOrNull()
    }

    suspend fun getUserByEmail(groupId: String, email: String): User? {
        return userDAO.listUsersByEmail(email).firstOrNull()
    }

    override fun findByHcpartyId(hcpartyId: String): Flow<String> = flow {
        emitAll(userDAO.listUsersByHcpId(hcpartyId).mapNotNull { v: User -> v.id })
    }

    override suspend fun newUser(type: Users.Type, status: Users.Status, email: String, createdDate: Instant): User {
        return userDAO.create(fix(User(
                id = uuidGenerator.newGUID().toString(),
                type = type,
                status = status,
                createdDate = createdDate,
                login = email,
                email = email
        ))) ?: throw java.lang.IllegalStateException("Cannot create user")
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
        userDAO.remove(userDAO.getUserOnUserDb(userId, false))
    }

    override suspend fun undeleteUser(userId: String) {
        val user = getUser(userId)
        user?.let { undeleteUser(it) }
    }

    override fun getRoles(user: User): Flow<Role> {
        return getParents(user)
    }

    override fun getUsersByLogin(login: String): Flow<User> = flow {
        emitAll(userDAO.listUsersByUsername(formatLogin(login)))
    }

    override fun listUsersByLoginOnFallbackDb(login: String): Flow<User> =
            userDAO.findByUsernameOnFallback(login)

    override fun listUsersByEmailOnFallbackDb(email: String): Flow<User> =
            userDAO.listByEmailOnFallbackDb(email)

    override suspend fun getUserByLogin(login: String): User? { // Format login
        return userDAO.listUsersByUsername(formatLogin(login)).firstOrNull()
    }

    override suspend fun newUser(type: Users.Type, email: String, password: String?, healthcarePartyId: String): User? { // Format login
        val email = formatLogin(email)
        Validate.isTrue(isLoginValid(email), "Login is invalid")

        type.takeIf { it == Users.Type.database }
                .let { Validate.isTrue(isPasswordValid(password!!), "Password is invalid") }

        getUserByLogin(email)?.let { throw CreationException("User already exists") }
        val user =  setHealthcarePartyIdIfExists(healthcarePartyId, newUser(type, Users.Status.ACTIVE, email, Instant.now()))

        return fix(password?.let { user.copy(passwordHash = encodePassword(password)) } ?: user) { userDAO.create(it) }
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
            fix(user.copy(
                    passwordHash = encodePassword(password),
                    type = Users.Type.database,
                    status = Users.Status.ACTIVE,
                    createdDate = Instant.now()
            )) { userDAO.create(it) }?.also {
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
                    login = user.email,
                    passwordHash = if (user.passwordHash != null && !user.passwordHash.matches(passwordRegex))
                        encodePassword(user.passwordHash) else user.passwordHash,
                    applicationTokens = emptyMap(),
                    authenticationTokens = encryptedAuthTokensFor(user)
            )) { createEntities(setOf(it)).firstOrNull() }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid User", e)
        }
    }

    private fun encryptedAuthTokensFor(user: User) : Map<String, AuthenticationToken> {
        return (user.authenticationTokens.map { (application, authToken) ->
            application to authToken.copy(
                    token = (if (!authToken.token.matches(passwordRegex)) encodePassword(authToken.token) else authToken.token)
            )
        } + user.applicationTokens.map { (application, rawToken) ->
            application to AuthenticationToken(token = encodePassword(rawToken), validity = AuthenticationToken.LONG_LIVING_TOKEN_VALIDITY)
        }).toMap()
    }

    override fun createEntities(users: Collection<User>): Flow<User> = flow {
        for (user in users) {
            fix(hashPasswordAndTokens(user)) { userDAO.create(user) }?.let { emit(it) }
        }
    }

    private suspend fun hashPasswordAndTokens(user: User) : User {
        return user.let { u ->
            if (u.passwordHash != null && u.passwordHash != "*" && !u.passwordHash.matches(passwordRegex)) {
                u.copy(passwordHash = encodePassword(u.passwordHash))
            } else u
        }.let { u ->
            if (!u.authenticationTokens.isNullOrEmpty()
                    && u.authenticationTokens.any { (_, authToken) -> !authToken.token.matches(passwordRegex)}) {
                u.copy(authenticationTokens = u.authenticationTokens.map { (application, authToken) ->
                    application to authToken.copy(
                            token = (if (!authToken.token.matches(passwordRegex)) encodePassword(authToken.token) else authToken.token)
                    )
                }.toMap())
            } else u
        }.let { u ->
            if (!u.applicationTokens.isNullOrEmpty()) {
                u.copy(applicationTokens = emptyMap(),
                        authenticationTokens = (u.authenticationTokens + u.applicationTokens.mapValues { (_, rawToken) ->
                            AuthenticationToken(token = encodePassword(rawToken), validity = AuthenticationToken.LONG_LIVING_TOKEN_VALIDITY)
                        })
                )
            } else u
        }.let { u ->
            if (u.rev != null) {
                val prev = getUser(user.id)
                u.copy(
                        applicationTokens = mapOf(),
                        authenticationTokens = (prev?.authenticationTokens ?: mapOf()) + u.authenticationTokens + (prev?.applicationTokens?.mapValues { (_, rawToken) ->
                            AuthenticationToken(token = encodePassword(rawToken), validity = AuthenticationToken.LONG_LIVING_TOKEN_VALIDITY)
                        } ?: mapOf()),
                        passwordHash = if (u.passwordHash == "*") prev?.passwordHash else u.passwordHash
                )
            } else u
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
        } else it.expirationDate == null || !it.expirationDate.isBefore(Instant.now())
    } ?: false

    override suspend fun checkPassword(password: String) =
            sessionLogic.getCurrentSessionContext().getUser().let { passwordEncoder.matches(password, it?.passwordHash) }

    override suspend fun verifyAuthenticationToken(userId: String, token: String): Boolean =
        getUser(userId)?.takeIf { it.authenticationTokens.isNotEmpty() }?.authenticationTokens?.any { (_, tok) ->
            !tok.isExpired() && passwordEncoder.matches(token, tok.token)
        } ?: false

    override fun getExpiredUsers(fromExpirationDate: Instant, toExpirationDate: Instant): Flow<User> = flow {
        emitAll(userDAO.getExpiredUsers(fromExpirationDate, toExpirationDate))
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

    override suspend fun modifyUser(modifiedUser: User) = fix(modifiedUser) { modifiedUser ->
        // Save user
        userDAO.save(hashPasswordAndTokens(modifiedUser))
    }

    override suspend fun getToken(user: User, key: String, tokenValidity: Long): String {
        val authenticationToken = uuidGenerator.newGUID().toString()

        userDAO.getUserOnUserDb(user.id, false).let {
            userDAO.save(
                    it.copy(authenticationTokens = it.authenticationTokens + (key to AuthenticationToken(encodePassword(authenticationToken), validity = tokenValidity)))
            ) ?: throw IllegalStateException("Cannot create token for user")
        }

        return authenticationToken
    }


    override suspend fun deleteUser(user: User) {
        user.id.let { userId ->
            getUser(userId)?.let {
                userDAO.remove(user)
            }
        }
    }

    override suspend fun undeleteUser(user: User) {
        user.id.let { userId ->
            getUser(userId)?.let {
                userDAO.unRemove(it)
            }
        }
    }

    override suspend fun disableUser(userId: String) {
        getUser(userId)?.let {
            userDAO.save(it.copy(status = Users.Status.DISABLED))
        }
    }

    override suspend fun enableUser(userId: String) {
        getUser(userId)?.let {
            userDAO.save(it.copy(status = Users.Status.ACTIVE))
        }
    }

    override fun getProperties(userId: String): Flow<PropertyStub> = flow {
        emitAll(getProperties(userId, true, true, true))
    }

    override fun modifyEntities(users: Collection<User>): Flow<User> = flow {
        emitAll(users.asFlow().mapNotNull { modifyUser(it) })
    }

    suspend fun undeleteEntities(userIds: Collection<String>) { //TODO MB was override here
        for (userId in userIds) {
            undeleteUser(userId)
        }
    }

    override fun getEntities(): Flow<User> = flow {
        emitAll(userDAO.getEntities())
    }

    override fun getEntitiesIds(): Flow<String> = flow {
        emitAll(userDAO.getEntityIds())
    }

    override suspend fun hasEntities(): Boolean {
        return userDAO.hasAny()
    }

    override suspend fun exists(id: String): Boolean {
        return userDAO.contains(id)
    }

    override suspend fun getEntity(id: String): User? {
        return getUser(id)
    }

    override suspend fun checkUsersExpiration() {
        val toExpirationDate = Instant.now()
        val fromExpirationDate = toExpirationDate.minus(CHECK_USERS_EXPIRATION_TIME_RANGE)
        // Get users that expired in this time range
        userDAO.getExpiredUsers(fromExpirationDate, toExpirationDate).map {
            for (listener in listeners) {
                listener.userExpired(it)
            }
        }.collect()
    }

    override suspend fun save(user: User): User? {
        return userDAO.save(hashPasswordAndTokens(user))
    }

    override fun listUsers(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        emitAll(userDAO.findUsers(paginationOffset))
    }

    override suspend fun setProperties(user: User, properties: List<PropertyStub>): User? {
        val properties = properties.fold(user.properties) { props, p ->
            val prop = user.properties.find { pp -> pp.type?.identifier == p.type?.identifier }
            prop?.let { props - it + it.copy(
                    type = if (it.type?.type != null) it.type else it.type?.copy(type = p.typedValue?.type),
                    typedValue = p.typedValue
            ) } ?: props + p
        }
        return modifyUser(user.copy(properties = properties))
    }

    override fun getUsers(ids: List<String>): Flow<User> = flow {
        emitAll(userDAO.getEntities(ids))
    }

    override suspend fun getUserOnFallbackDb(userId: String): User? {
        return userDAO.getOnFallback(userId, false)
    }

    override suspend fun createUserOnUserDb(user: User): User? {
        if (user.login != null || user.email == null) {
            throw MissingRequirementsException("createUser: Requirements are not met. Email has to be set and the Login has to be null.")
        }
        try { // check whether user exists
            getUserByEmailOnUserDb(user.email)?.let { throw CreationException("User already exists (" + user.email + ")") }

            return fix(user.copy(createdDate = Instant.now(),
                    login = user.email,
                    passwordHash = if (user.passwordHash != null && !user.passwordHash.matches(passwordRegex))
                        encodePassword(user.passwordHash) else user.passwordHash,
                    applicationTokens = emptyMap(),
                    authenticationTokens = encryptedAuthTokensFor(user))
            ) { getGenericDAO().create(it) }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid User", e)
        }
    }

    override suspend fun getUserByEmailOnUserDb(email: String): User? {
        return userDAO.listUsersByEmail(email).singleOrNull()
    }

    override suspend fun getUserOnUserDb(userId: String): User {
        return userDAO.getUserOnUserDb(userId, false)
    }


    override suspend fun findUserOnUserDb(userId: String): User? {
        return userDAO.findUserOnUserDb(userId, false)
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

}
