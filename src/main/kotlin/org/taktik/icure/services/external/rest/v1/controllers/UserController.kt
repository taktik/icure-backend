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

package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Property
import org.taktik.icure.entities.User
import org.taktik.icure.security.database.DatabaseUserDetails
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto
import org.taktik.icure.services.external.rest.v1.dto.UserDto
import org.taktik.icure.services.external.rest.v1.dto.UserGroupDto
import org.taktik.icure.services.external.rest.v1.dto.UserPaginatedList
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList


/* Useful notes:
 * @RequestParam is required by default, but @ApiParam (which is useful to add a description)
 * is not required by default and overrides it, so we have to make sure they always match!
 * Nicknames are required so that operationId is e.g. 'modifyAccessLog' instead of 'modifyAccessLogUsingPUT' */

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/user")
@Api(tags = ["user"]) // otherwise would default to "user-controller"
class UserController(private val mapper: MapperFacade,
                     private val userLogic: UserLogic,
                     private val groupLogic: GroupLogic,
                     private val sessionLogic: AsyncSessionLogic) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val DEFAULT_LIMIT = 1000

    @ApiOperation(nickname = "getCurrentUser", value = "Get presently logged-in user.", notes = "Get current user.")
    @GetMapping(value = ["/current"])
    suspend fun getCurrentUser(): UserDto {
            val user = userLogic.getUser(sessionLogic.getCurrentUserId())
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting Current User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
            return mapper.map(user, UserDto::class.java)
    }

    @ApiOperation(nickname = "getCurrentSession", value = "Get Currently logged-in user session.", notes = "Get current user.")
    @GetMapping("/session", produces = ["text/plain"])
    fun getCurrentSession(): String? { // TODO MB nullable or exception ?
        return sessionLogic.getOrCreateSession()?.id
    }

    @ApiOperation(nickname = "getMatchingUsers", value = "Get presently logged-in user.", notes = "Get current user.")
    @GetMapping("/matches")
    suspend fun getMatchingUsers(): List<UserGroupDto> {
        return (sessionLogic.getCurrentSessionContext().getUserDetails() as DatabaseUserDetails).groupIdUserIdMatching.map { ug ->
            val split = ug.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split.size == 1) UserGroupDto(null, split[0], null) else UserGroupDto(split[0], split[1], groupLogic.getGroup(split[0])?.name)
        }
    }

    @ApiOperation(nickname = "listUsers", value = "List users with(out) pagination", notes = "Returns a list of users.")
    @GetMapping
    suspend fun listUsers(
            @ApiParam(value = "An user email") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "An user document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): UserPaginatedList {

        val realLimit = limit ?: DEFAULT_LIMIT // TODO SH MB: rather use defaultValue = DEFAULT_LIMIT everywhere?
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)
        val allUsers = userLogic.listUsers(paginationOffset)
        return UserPaginatedList(allUsers.paginatedList<User, UserDto>(mapper, realLimit))
    }

    @ApiOperation(nickname = "listUsersInGroup", value = "List users with(out) pagination", notes = "Returns a list of users.")
    @GetMapping("/{groupId}")
    suspend fun listUsersInGroup(
            @PathVariable groupId: String,
            @ApiParam(value = "An user login") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "An user document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): UserPaginatedList {

        val realLimit = limit ?: DEFAULT_LIMIT // TODO SH MB: rather use defaultValue = DEFAULT_LIMIT everywhere?
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)
        val allUsers = userLogic.listUsers(groupId, paginationOffset)
        return UserPaginatedList(allUsers.paginatedList<User, UserDto>(mapper, realLimit))
    }

    @ApiOperation(nickname = "createUser", value = "Create a user", notes = "Create a user. HealthcareParty ID should be set. Email has to be set and the Login has to be null. On server-side, Email will be used for Login.")
    @PostMapping
    suspend fun createUser(@RequestBody userDto: UserDto): UserDto {
        //Sanitize group
        userDto.groupId = null

        val user = try {
            userLogic.createUser(mapper.map(userDto, User::class.java))
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User creation failed.")

        return mapper.map(user, UserDto::class.java)
    }

    @ApiOperation(nickname = "getUser", value = "Get a user by his ID", notes = "General information about the user")
    @GetMapping("/{userId}")
    suspend fun getUser(@PathVariable userId: String): UserDto {
        val user = userLogic.getUser(userId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
        return mapper.map(user, UserDto::class.java)
    }

    @ApiOperation(nickname = "getUserByEmail", value = "Get a user by his Email/Login", notes = "General information about the user")
    @GetMapping("/byEmail/{email}")
    suspend fun getUserByEmail(@PathVariable email: String): UserDto {
        val user = userLogic.getUserByEmail(email)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
        return mapper.map(user, UserDto::class.java)
    }

    @ApiOperation(nickname = "findByHcpartyId", value = "Get the list of users by healthcare party id")
    @GetMapping("/byHealthcarePartyId/{id}")
    fun findByHcpartyId(@PathVariable id: String) = userLogic.findByHcpartyId(id).injectReactorContext()

    @ApiOperation(nickname = "deleteUser", value = "Delete a User based on his/her ID.", notes = "Delete a User based on his/her ID. The return value is an array containing the ID of deleted user.")
    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String) = try {
        userLogic.deleteByIds(setOf(userId)).injectReactorContext()
    } catch (e: Exception) {
        logger.warn(e.message, e)
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }


    @ApiOperation(nickname = "modifyUser", value = "Modify a user.", notes = "No particular return value. It's just a message.")
    @PutMapping
    suspend fun modifyUser(@RequestBody userDto: UserDto): UserDto {
        //Sanitize group
        userDto.groupId = null

        userLogic.modifyUser(mapper.map(userDto, User::class.java))
        val modifiedUser = userLogic.getUser(userDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User modification failed.")

        return mapper.map(modifiedUser, UserDto::class.java)
    }

    @ApiOperation(nickname = "assignHealthcareParty", value = "Assign a healthcare party ID to current user", notes = "UserDto gets returned.")
    @PutMapping("/current/hcparty/{healthcarePartyId}")
    suspend fun assignHealthcareParty(@PathVariable healthcarePartyId: String): UserDto {
        val modifiedUser = userLogic.getUser(sessionLogic.getCurrentUserId())
        modifiedUser?.let {
            modifiedUser.healthcarePartyId = healthcarePartyId
            userLogic.save(modifiedUser)

            return mapper.map(modifiedUser, UserDto::class.java)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Assigning healthcare party ID to the current user failed.").also { logger.error(it.message) }
    }

    @ApiOperation(nickname = "modifyProperties", value = "Modify a User property", notes = "Modify a User properties based on his/her ID. The return value is the modified user.")
    @PutMapping("/{userId}/properties")
    suspend fun modifyProperties(@PathVariable userId: String, @RequestBody properties: List<PropertyDto>?): UserDto {
        val user = userLogic.getUser(userId)
        user?.let {
            val modifiedUser = userLogic.setProperties(user, properties?.map { p -> mapper.map(p, Property::class.java) }
                    ?: listOf())
            if (modifiedUser == null) {
                logger.error("Modify a User property failed.")
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modify a User property failed.")
            }
            return mapper.map(modifiedUser, UserDto::class.java)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modify a User property failed.")

    }

    @GetMapping("/checkPassword")
    suspend fun checkPassword(@RequestHeader("password") password: String)  = userLogic.checkPassword(password)
}
