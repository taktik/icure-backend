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

import com.google.common.collect.Sets
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.EntityPersister
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.constants.TypedValuesType
import org.taktik.icure.constants.Users
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.*
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.entities.embed.TypedValue
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.exceptions.UserRegistrationException
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.PropertyLogic
import org.taktik.icure.logic.listeners.UserLogicListener
import org.taktik.icure.utils.firstOrNull
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.regex.Pattern

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
    suspend fun getUserOnFallbackDb(userId: String): User
    suspend fun getUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User?
    suspend fun findUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User
    fun getUsersByPartialIdOnFallbackDb(id: String): Flow<User>
    fun findUsersByLoginOnFallbackDb(username: String): Flow<User>
    fun findByHcpartyId(hcpartyId: String): Flow<String>
}

@Transactional
@Service
class UserLogicImpl(
        private val userDAO: UserDAO,
        roleDao: RoleDAO,
        private val healthcarePartyLogic: HealthcarePartyLogic,
        private val propertyLogic: PropertyLogic,
        private val sessionLogic: AsyncSessionLogic,
        private val passwordEncoder: PasswordEncoder,
        private val uuidGenerator: UUIDGenerator) : PrincipalLogicImpl<User>(roleDao, sessionLogic), UserLogic {

    private val listeners: MutableSet<UserLogicListener> = HashSet()

    override fun addListener(listener: UserLogicListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: UserLogicListener) {
        listeners.remove(listener)
    }

    override suspend fun getUser(id: String): User? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.getUsersByPartialIdOnFallback(dbInstanceUri, groupId, id).first().also { fillGroup(it) }
    }

    private suspend fun fillGroup(user: User): User =
            user.also { it.groupId = sessionLogic.getCurrentSessionContext().getGroupId() }

    override suspend fun getUserByEmail(email: String): User? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.findByEmail(dbInstanceUri, groupId, email).singleOrNull()?.also { fillGroup(it) }
                ?: throw IllegalStateException("Two users with same email $email")
    }

    override fun findByHcpartyId(hcpartyId: String): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.findByHcpId(dbInstanceUri, groupId, hcpartyId)
                .map { v: User -> v.id }
                .collect { emit(it) }
    }

    override suspend fun newUser(type: Users.Type, status: Users.Status, email: String, createdDate: Instant): User? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val user = User()
        initUser(type, status, createdDate, user)
        user.login = email
        user.email = email
        fillDefaultProperties(user)
        return userDAO.create(dbInstanceUri, groupId, user)
    }

    private fun initUser(type: Users.Type, status: Users.Status, createdDate: Instant, user: User) {
        if (user.id == null) {
            user.id = uuidGenerator.newGUID().toString()
        }
        user.type = type
        user.status = status
        user.createdDate = createdDate
    }

    private fun fillDefaultProperties(user: User) {
        user.properties.add(
                Property(PropertyType(TypedValuesType.JSON, "org.taktik.icure.datafilters"),
                        TypedValue(TypedValuesType.JSON, "{\"label\":{\"en\":\"Lab results\"},\"tags\":[{\"CD-ITEM\":\"labresult\"}]}")))
        user.properties.add(
                Property(PropertyType(TypedValuesType.JSON, "org.taktik.icure.preferred.forms"),
                        TypedValue(TypedValuesType.JSON, "{\"org.taktik.icure.form.standard.medicalhistory\":\"FFFFFFFF-FFFF-FFFF-FFFF-DOSSMED00000\",\"org.taktik.icure.form.standard.consultation\":\"FFFFFFFF-FFFF-FFFF-FFFF-CONSULTATION\"}")))
        user.properties.add(
                Property(PropertyType(TypedValuesType.JSON, "org.taktik.icure.tarification.favorites"),
                        TypedValue(TypedValuesType.JSON, "{}")))
    }

    override fun buildStandardUser(userName: String, password: String): User {
        val user = User()
        user.type = Users.Type.database
        user.status = Users.Status.ACTIVE
        user.name = userName
        user.login = userName
        user.createdDate = Instant.now()
        user.passwordHash = encodePassword(password)
        user.email = userName
        fillDefaultProperties(user)
        return user
    }

    override fun getBootstrapUser(): User {
        val user = buildStandardUser("bootstrap", "bootstrap")
        user.id = "bootstrap"
        return user
    }

    override suspend fun deleteUser(userId: String) {
        val user = getUser(userId)
        user?.let { deleteUser(it) }
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
        userDAO.findByUsername(dbInstanceUri, groupId, formatLogin(login)).map { fillGroup(it) }.collect { emit(it) }
    }

    override suspend fun getUserByLogin(login: String): User? { // Format login
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.findByUsername(dbInstanceUri, groupId, formatLogin(login)).firstOrNull()?.also { fillGroup(it) }
    }

    override suspend fun newUser(type: Users.Type, email: String, password: String, healthcarePartyId: String): User? { // Format login
        val email = formatLogin(email)
        Validate.isTrue(isLoginValid(email), "Login is invalid")

        type.takeIf { it == Users.Type.database }
                .let { Validate.isTrue(isPasswordValid(password!!), "Password is invalid") }

        var user = getUserByLogin(email)
        user?.let { throw CreationException("User already exists") }

        // Create user
        user = newUser(type!!, Users.Status.ACTIVE, email, Instant.now()) // !! validated above
        setHealthcarePartyIdIfExists(healthcarePartyId, user!!) // !! validated above

        // Set password if any
        password.let { user.passwordHash = encodePassword(password) }

        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.create(dbInstanceUri, groupId, user)
    }

    private fun setHealthcarePartyIdIfExists(healthcarePartyId: String?, user: User) {
        healthcarePartyId?.let {
            healthcarePartyLogic.getHealthcareParty(it)?.let {
                user.healthcarePartyId = healthcarePartyId
            }
                    ?: Companion.log.error("newUser: healthcare party " + healthcarePartyId + "does not exist. But, user is created with Null healthcare party.")
        }
    }

    override suspend fun registerUser(user: User, password: String): User? {
        if (propertyLogic.getSystemPropertyValue<Any?>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier) != null
                && propertyLogic.getSystemPropertyValue<Boolean>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier)!!) {

            if (!isLoginValid(user.login)) {
                throw UserRegistrationException("Login is invalid")
            }
            // Check password
            if (!isPasswordValid(password)) {
                throw UserRegistrationException("Password is invalid")
            }
            // Check login does not already exist
            if (getUserByLogin(user.login) != null) {
                throw UserRegistrationException("User login already exists")
            }
            initUser(Users.Type.database, Users.Status.ACTIVE, Instant.now(), user)
            user.passwordHash = encodePassword(password)
            fillDefaultProperties(user)
            // Save user
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.create(dbInstanceUri, groupId, user)
            // Notify listeners
            for (listener in listeners) {
                listener.userRegistered(user)
            }
            return user
        }
        return null
    }

    override suspend fun createUser(user: User): User? { // checking requirements
        if (user.login != null || user.email == null) {
            throw MissingRequirementsException("createUser: Requirements are not met. Email has to be set and the Login has to be null.")
        }
        try { // check whether user exists
            val userByEmail = getUserByEmail(user.email)
            userByEmail?.let { throw CreationException("User already exists (" + user.email + ")") }
            user.id = uuidGenerator.newGUID().toString()
            user.createdDate = Instant.now()
            user.login = user.email
            return createEntities(setOf(user)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid User", e)
        }
    }
    // TODO MB
    // previously was
    // suspend fun createEntities(@Check entities: Collection<E>, createdEntities: MutableCollection<E>): Boolean
    override fun createEntities(users: Collection<User>): Flow<User> = flow {
        val regex = Regex.fromLiteral("^[0-9a-zA-Z]{64}$")
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        for (user in users) {
            fillDefaultProperties(user)
            if (user.passwordHash != null && !user.passwordHash.matches(regex)) {
                user.passwordHash = encodePassword(user.passwordHash)
            }
            userDAO.create(dbInstanceUri, groupId, user)?.also { emit(it) }
        }
    }

    override suspend fun registerUser(email: String, password: String, healthcarePartyId: String, name: String): User? {
        if (propertyLogic.getSystemPropertyValue<Any?>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier) != null
                && propertyLogic.getSystemPropertyValue<Boolean>(PropertyTypes.System.USER_REGISTRATION_ENABLED.identifier)!!) {

            val user = newUser(Users.Type.database, Users.Status.ACTIVE, formatLogin(email), Instant.now())
            return user?.let {
                it.email = email
                it.name = name
                setHealthcarePartyIdIfExists(healthcarePartyId, it)
                registerUser(it, password)
            }
        }
        return null
    }

    override suspend fun isUserActive(userId: String) = getUser(userId)?.let {
        if (it.status == null || it.status != Users.Status.ACTIVE) {
            false
        } else it.expirationDate == null || !it.expirationDate.isBefore(Instant.now())
    } ?: false

    override suspend fun checkPassword(password: String) =
            sessionLogic.getCurrentSessionContext().getUser().let { passwordEncoder.matches(password, it.passwordHash) }

    override suspend fun verifyPasswordToken(userId: String, token: String): Boolean {
        getUser(userId)?.takeIf { it.passwordToken?.isNotEmpty() ?: false }
                ?.let {
                    if (it.passwordToken == token) {
                        return it.passwordTokenExpirationDate == null || it.passwordTokenExpirationDate.isAfter(Instant.now())
                    }
                }
        return false
    }

    override suspend fun verifyActivationToken(userId: String, token: String): Boolean {
        getUser(userId)?.let {
            if (it.activationToken != null && it.activationToken == token) {
                return it.activationTokenExpirationDate == null || it.activationTokenExpirationDate.isAfter(Instant.now())
            }
        }
        return false
    }

    override suspend fun usePasswordToken(userId: String, token: String, newPassword: String): Boolean { // Validate token
        if (verifyPasswordToken(userId, token)) { // Validate new password
            if (isPasswordValid(newPassword)) { // Get user
                getUser(userId)?.let {
                    it.passwordHash = encodePassword(newPassword)
                    // Remove passwordToken and passwordTokenExpirationDate
                    it.passwordToken = null
                    it.passwordTokenExpirationDate = null
                    for (listener in listeners) {
                        listener.userResetPassword(it)
                    }
                    return true
                }
            }
        }
        return false
    }

    override suspend fun useActivationToken(userId: String, token: String): Boolean { // Validate token
        if (verifyActivationToken(userId, token)) { // Get user
            getUser(userId)?.let {
                // Set user ACTIVE
                it.status = Users.Status.ACTIVE
                // Remove expirationDate
                it.expirationDate = null
                // Remove activationToken and activationTokenExpirationDate
                it.activationToken = null
                it.activationTokenExpirationDate = null
                // Notify listeners
                for (listener in listeners) {
                    listener.userActivated(it)
                }
                return true
            }
        }
        return false
    }

    override fun getExpiredUsers(fromExpirationDate: Instant, toExpirationDate: Instant): Flow<User> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.getExpiredUsers(dbInstanceUri, groupId, fromExpirationDate, toExpirationDate).collect { emit(it) }
    }

    override suspend fun acceptUserTermsOfUse(userId: String) {
        getUser(userId)?.also { it.termsOfUseDate = Instant.now() }
    }

    private fun formatLogin(login: String) = login.trim { it <= ' ' }

    override suspend fun isLoginValid(login: String): Boolean {
        return login.takeIf { it.isNotEmpty() }?.let {
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
        if (modifiedUser.passwordHash != null && !modifiedUser.passwordHash.matches(Regex.fromLiteral("^[0-9a-zA-Z]{64}$"))) {
            modifiedUser.passwordHash = encodePassword(modifiedUser.passwordHash)
        }
        // Save user
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.save(dbInstanceUri, groupId, modifiedUser)
        return getUser(modifiedUser.id)
    }

    override suspend fun addPermissions(userId: String, permissions: Set<Permission>) {
        getUser(userId)?.let {
            it.permissions.addAll(permissions)
            // Modify user
            modifyUser(it)
        }
    }

    override suspend fun modifyPermissions(userId: String, permissions: Set<Permission>) {
        getUser(userId)?.let {
            it.permissions = permissions
            // Modify user
            modifyUser(it)
        }
    }

    override suspend fun modifyRoles(userId: String, roles: Set<Role>) {
        getUser(userId)?.let {
            it.roles = roles.map { it.id }.toSet()
            // Modify user
            modifyUser(it)
        }
    }

    override suspend fun modifyUserAttributes(userId: String, attributesValues: Map<String, Any>) {
        getUser(userId)?.let {
            for (attribute in attributesValues.keys) {
                try {
                    pub.setProperty(it, attribute, attributesValues[attribute])
                } catch (e: Exception) {
                    Companion.log.error("Exception", e)
                }
            }
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.save(dbInstanceUri, groupId, it)
        }
    }

    override suspend fun deleteUser(user: User) {
        getUser(user.id)?.let {
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.remove(dbInstanceUri, groupId, user)
        }
    }

    override suspend fun undeleteUser(user: User) {
        getUser(user.id)?.let {
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.unRemove(dbInstanceUri, groupId, it)
        }
    }

    override suspend fun disableUser(userId: String) {
        getUser(userId)?.let {
            it.status = Users.Status.DISABLED
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.save(dbInstanceUri, groupId, it)
        }
    }

    override suspend fun enableUser(userId: String) {
        getUser(userId)?.let {
            it.status = Users.Status.ACTIVE
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.save(dbInstanceUri, groupId, it)
        }
    }

    override fun getProperties(userId: String): Flow<Property> = flow {
        getProperties(userId, true, true, true).collect { emit(it) }
    }

    override suspend fun modifyProperties(userId: String, propertiesToModify: Set<Property>) {
        getUser(userId)?.let { user ->
            val existingProperties = user.properties
            if (existingProperties == null) {
                user.properties = propertiesToModify
            } else {
                val newProperties: MutableSet<Property> = Sets.newHashSet(propertiesToModify)
                for (existingProp in existingProperties) {
                    if (propertiesToModify.stream().noneMatch { candidateProperty: Property -> existingProp.type == candidateProperty.type }) {
                        newProperties.add(existingProp)
                    }
                }
                user.properties = newProperties
            }
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.save(dbInstanceUri, groupId, user)
        }
    }


    override fun updateEntities(users: Collection<User>): Flow<User> = flow {
        users.asFlow().mapNotNull { modifyUser(it) }.map { fillGroup(it) }.collect { emit(it) }
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
        userDAO.getAll(dbInstanceUri, groupId).onEach {  }.map { fillGroup(it) }.collect { emit(it) }
    }

    override fun getAllEntityIds(): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.getAllIds(dbInstanceUri, groupId).collect { emit(it) }
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
        return getUser(id)?.let { fillGroup(it) }
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

    override suspend fun save(user: User) {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.save(dbInstanceUri, groupId, user)
    }

    override suspend fun userLogged(user: User) {
        getUser(user.id)?.let {
            // Set new last login date
            it.lastLoginDate = Instant.now()
            // Notify user logic listeners
            for (listener in listeners) {
                listener.userLogged(it)
            }
            // Save any changes made to the user
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            userDAO.save(dbInstanceUri, groupId, user)
        }
    }

    override fun listUsers(pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.listUsers(dbInstanceUri, groupId, pagination).collect { emit(it) }
    }

    override suspend fun setProperties(user: User, properties: List<Property>): User? {
        for (p in properties) {
            val prop = user.properties.stream().filter { pp: Property -> pp.type.identifier == p.type.identifier }.findAny()
            if (!prop.isPresent) {
                user.properties.add(Property(PropertyType(p.type.type, p.type.identifier), TypedValue(p.type.type, p.getValue<Any>())))
            } else {
                prop.orElseThrow { IllegalStateException() }.typedValue = TypedValue(p.type.type, p.getValue<Any>())
            }
        }
        return modifyUser(user)
    }

    override fun getUsers(ids: List<String>): Flow<User> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.getList(dbInstanceUri, groupId, ids).map { fillGroup(it) }.collect { emit(it) }
    }

    override suspend fun getUserOnFallbackDb(userId: String): User {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return userDAO.getOnFallback(dbInstanceUri, groupId, userId, false)
    }

    override suspend fun getUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User {
        return fillGroup(userDAO.getUserOnUserDb(dbInstanceUrl, groupId, userId, false))
    }

    override suspend fun findUserOnUserDb(userId: String, groupId: String, dbInstanceUrl: URI): User {
        return fillGroup(userDAO.findUserOnUserDb(dbInstanceUrl, groupId, userId, false))
    }

    override fun getUsersByPartialIdOnFallbackDb(id: String): Flow<User> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.getUsersByPartialIdOnFallback(dbInstanceUri, groupId, id).collect { emit(it) }
    }

    override fun findUsersByLoginOnFallbackDb(login: String): Flow<User> = flow {
        // Format login
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        userDAO.findByUsernameOnFallback(dbInstanceUri, groupId, formatLogin(login)).collect { emit(it) }
    }
    override suspend fun getPrincipal(userId: String): User? {
        return if (userId == "bootstrap") getBootstrapUser() else getUser(userId)
    }

    override fun encodePassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserLogicImpl::class.java)
        private val pub = PropertyUtilsBean()
        private val CHECK_USERS_EXPIRATION_TIME_RANGE = Duration.ofDays(1)
    }

    override fun deleteByIds(identifiers: Collection<String>): Flow<DocIdentifier> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val entities = userDAO.getList(dbInstanceUri, groupId, identifiers).toList()
        userDAO.remove(dbInstanceUri, groupId, entities).collect { emit(it) }
    }

    override fun undeleteByIds(identifiers: Collection<String>): Flow<DocIdentifier> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val entities = userDAO.getList(dbInstanceUri, groupId, identifiers).toList()
        userDAO.unRemove(dbInstanceUri, groupId, entities).collect { emit(it) }
    }
}
