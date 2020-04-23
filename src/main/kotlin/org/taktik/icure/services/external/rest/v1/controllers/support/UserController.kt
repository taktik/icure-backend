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

package org.taktik.icure.services.external.rest.v1.controllers.support

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.apache.commons.lang3.text.StrSubstitutor
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dao.replicator.GroupDBUrl
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Property
import org.taktik.icure.entities.User
import org.taktik.icure.properties.TwilioProperties
import org.taktik.icure.security.database.DatabaseUserDetails
import org.taktik.icure.services.external.rest.v1.dto.*
import org.taktik.icure.utils.distinctById
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import java.io.IOException
import java.util.*
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response


/* Useful notes:
 * @RequestParam is required by default, but @ApiParam (which is useful to add a description)
 * is not required by default and overrides it, so we have to make sure they always match!
 * Nicknames are required so that operationId is e.g. 'modifyAccessLog' instead of 'modifyAccessLogUsingPUT' */

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/user")
@Tag(name = "user") // otherwise would default to "user-controller"
class UserController(private val mapper: MapperFacade,
                     private val userLogic: UserLogic,
                     private val groupLogic: GroupLogic,
                     private val sessionLogic: AsyncSessionLogic,
                     private val twilioProperties: TwilioProperties) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val idGenerator = UUIDGenerator()
    private val DEFAULT_LIMIT = 1000

    @Operation(summary = "Get presently logged-in user.", description = "Get current user.")
    @GetMapping(value = ["/current"])
    fun getCurrentUser() = mono {
            val user = userLogic.getUser(sessionLogic.getCurrentUserId())
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting Current User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
            mapper.map(user, UserDto::class.java)
    }

    @Operation(summary = "Get Currently logged-in user session.", description = "Get current user.")
    @GetMapping("/session", produces = ["text/plain"])
    fun getCurrentSession(): String? { // TODO MB nullable or exception ?
        return sessionLogic.getOrCreateSession()?.id
    }

    @Operation(summary = "Get presently logged-in user.", description = "Get current user.")
    @GetMapping("/matches")
    fun getMatchingUsers() = mono {
        (sessionLogic.getCurrentSessionContext().getUserDetails() as DatabaseUserDetails).groupIdUserIdMatching.map { ug ->
            val split = ug.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split.size == 1) UserGroupDto(null, split[0], null) else UserGroupDto(split[0], split[1], groupLogic.getGroup(split[0])?.name)
        }
    }

    @Operation(summary = "List users with(out) pagination", description = "Returns a list of users.")
    @GetMapping
    fun listUsers(
            @Parameter(description = "An user email") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "An user document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT // TODO SH MB: rather use defaultValue = DEFAULT_LIMIT everywhere?
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)
        val allUsers = userLogic.listUsers(paginationOffset)

        PaginatedList(allUsers.paginatedList<User, UserDto>(mapper, realLimit))
    }

    @Operation(summary = "List users with(out) pagination", description = "Returns a list of users.")
    @GetMapping("/inGroup/{groupId}")
    fun listUsersInGroup(
            @PathVariable groupId: String,
            @Parameter(description = "An user login") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "An user document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT // TODO SH MB: rather use defaultValue = DEFAULT_LIMIT everywhere?
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)
        val allUsers = userLogic.listUsers(groupId, paginationOffset)
        PaginatedList(allUsers.paginatedList<User, UserDto>(mapper, realLimit))
    }

    @Operation(summary = "Create a user", description = "Create a user. HealthcareParty ID should be set. Email has to be set and the Login has to be null. On server-side, Email will be used for Login.")
    @PostMapping
    fun createUser(@RequestBody userDto: UserDto) = mono {
        //Sanitize group
        userDto.groupId = null

        val user = try {
            userLogic.createUser(mapper.map(userDto, User::class.java))
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User creation failed.")

        mapper.map(user, UserDto::class.java)
    }

    @Operation(summary = "Create a user", description = "Create a user. HealthcareParty ID should be set. Email has to be set and the Login has to be null. On server-side, Email will be used for Login.")
    @PostMapping("/inGroup/{groupId}")
    fun createUserInGroup(
            @PathVariable groupId: String,
            @RequestBody userDto: UserDto) = mono {
        //Sanitize group
        userDto.groupId = null

        val user = try {
            userLogic.createUser(groupId, mapper.map(userDto, User::class.java))
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User creation failed.")

        mapper.map(user, UserDto::class.java)
    }

    @Operation(summary = "Modify a user.", description = "No particular return value. It's just a message.")
    @PutMapping("/inGroup/{groupId}")
    fun modifyUserInGroup(
            @PathVariable groupId: String,
            @RequestBody userDto: UserDto) = mono {
        //Sanitize group
        userDto.groupId = null

        userLogic.modifyUser(groupId, mapper.map(userDto, User::class.java))
        val modifiedUser = userLogic.getUser(userDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User modification failed.")

        mapper.map(modifiedUser, UserDto::class.java)
    }

    @Operation(summary = "Delete a User based on his/her ID.", description = "Delete a User based on his/her ID. The return value is an array containing the ID of deleted user.")
    @DeleteMapping("/inGroup/{groupId}/{userId}")
    fun deleteUserInGroup(
            @PathVariable groupId: String,
            @PathVariable userId: String) = mono {
        try {
            userLogic.deleteUser(groupId, userId)
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }


    @Operation(summary = "Get a user by his ID", description = "General information about the user")
    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String) = mono {
        val user = userLogic.getUser(userId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
        mapper.map(user, UserDto::class.java)
    }

    @Operation(summary = "Get a user by his Email/Login", description = "General information about the user")
    @GetMapping("/byEmail/{email}")
    fun getUserByEmail(@PathVariable email: String) = mono {
        val user = userLogic.getUserByEmail(email)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
        mapper.map(user, UserDto::class.java)
    }

    @Operation(summary = "Get the list of users by healthcare party id")
    @GetMapping("/byHealthcarePartyId/{id}")
    fun findByHcpartyId(@PathVariable id: String) = userLogic.findByHcpartyId(id).injectReactorContext()

    @Operation(summary = "Delete a User based on his/her ID.", description = "Delete a User based on his/her ID. The return value is an array containing the ID of deleted user.")
    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String) = mono {
        try {
            userLogic.deleteByIds(setOf(userId)).firstOrNull()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }


    @Operation(summary = "Modify a user.", description = "No particular return value. It's just a message.")
    @PutMapping
    fun modifyUser(@RequestBody userDto: UserDto) = mono {
        //Sanitize group
        userDto.groupId = null

        userLogic.modifyUser(mapper.map(userDto, User::class.java))
        val modifiedUser = userLogic.getUser(userDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User modification failed.")

        mapper.map(modifiedUser, UserDto::class.java)
    }

    @Operation(summary = "Assign a healthcare party ID to current user", description = "UserDto gets returned.")
    @PutMapping("/current/hcparty/{healthcarePartyId}")
    fun assignHealthcareParty(@PathVariable healthcarePartyId: String) = mono {
        val modifiedUser = userLogic.getUser(sessionLogic.getCurrentUserId())
        modifiedUser?.let {
            modifiedUser.healthcarePartyId = healthcarePartyId
            userLogic.save(modifiedUser)

            mapper.map(modifiedUser, UserDto::class.java)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Assigning healthcare party ID to the current user failed.").also { logger.error(it.message) }
    }

    @Operation(summary = "Modify a User property", description = "Modify a User properties based on his/her ID. The return value is the modified user.")
    @PutMapping("/{userId}/properties")
    fun modifyProperties(@PathVariable userId: String, @RequestBody properties: List<PropertyDto>?) = mono {
        val user = userLogic.getUser(userId)
        user?.let {
            val modifiedUser = userLogic.setProperties(user, properties?.map { p -> mapper.map(p, Property::class.java) }
                    ?: listOf())
            if (modifiedUser == null) {
                logger.error("Modify a User property failed.")
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modify a User property failed.")
            }
            mapper.map(modifiedUser, UserDto::class.java)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modify a User property failed.")

    }

    @GetMapping("/checkPassword")
    fun checkPassword(@RequestHeader("password") password: String)  = mono {
        userLogic.checkPassword(password)
    }

    @GetMapping("/encodePassword")
    fun encodePassword(@RequestHeader("password") password: String)  = mono {
        userLogic.encodePassword(password)
    }

    @Operation(summary = "Send a forgotten email message to an user")
    @PostMapping("/forgottenPassword/{email}")
    fun forgottenPassword(@Parameter(description = "the email of the user ") @PathVariable email: String, @RequestBody template: EmailTemplateDto) = mono {
        flow {
            emitAll(userLogic.listUsersByEmailOnFallbackDb(email))
            emitAll(userLogic.listUsersByLoginOnFallbackDb(email))
        }.distinctById().collect { user ->
            val groupId = user.id.split(":")[0]
            val userId = user.id.split(":")[1]
            groupLogic.getGroup(groupId)?.let { group ->
                try {
                    val applicationToken = userLogic.getToken(group, user, "passwordRecovery")
                    val variables: MutableMap<String, String?> = HashMap()
                    variables["id"] = userId
                    variables["email"] = user.email ?: user.login
                    variables["token"] = applicationToken
                    val mail = Mail(
                            Email(twilioProperties.sendgridfrom),
                            StrSubstitutor(variables, "{{", "}}").replace(template.subject),
                            Email(email),
                            Content("text/plain", StrSubstitutor(variables, "{{", "}}").replace(template.body))
                    )
                    val sg = SendGrid(twilioProperties.sendgridapikey)
                    val request = Request()
                    try {
                        request.method = Method.POST
                        request.endpoint = "mail/send"
                        request.body = mail.build()
                        val response = sg.api(request)
                        logger.info("Sendgrid status code ${response.statusCode}")
                    } catch (ex: IOException) {
                        logger.error("Error while sending forgotten password email", ex)
                    }

                } catch(e:Exception) {
                    logger.warn("Skipping ${user.id} during password recovery")
                }
            }
        }
        true
    }


}
