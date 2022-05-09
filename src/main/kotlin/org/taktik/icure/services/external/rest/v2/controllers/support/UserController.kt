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

package org.taktik.icure.services.external.rest.v2.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.rest.v2.dto.PropertyStubDto
import org.taktik.icure.services.external.rest.v2.dto.UserDto
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v2.mapper.UserV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.base.PropertyStubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterChainV2Mapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext

/* Useful notes:
 * @RequestParam is required by default, but @ApiParam (which is useful to add a description)
 * is not required by default and overrides it, so we have to make sure they always match!
 * Nicknames are required so that operationId is e.g. 'modifyAccessLog' instead of 'modifyAccessLogUsingPUT' */

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@RestController("userControllerV2")
@RequestMapping("/rest/v2/user")
@Tag(name = "user") // otherwise would default to "user-controller"
class UserController(
	private val filters: Filters,
	private val userLogic: UserLogic,
	private val sessionLogic: AsyncSessionLogic,
	private val userV2Mapper: UserV2Mapper,
	private val propertyStubV2Mapper: PropertyStubV2Mapper,
	private val filterChainV2Mapper: FilterChainV2Mapper,
) {
	private val logger = LoggerFactory.getLogger(javaClass)
	private val DEFAULT_LIMIT = 1000
	private val userToUserDto = { it: User -> userV2Mapper.map(it) }

	@Operation(summary = "Get presently logged-in user.", description = "Get current user.")
	@GetMapping(value = ["/current"])
	fun getCurrentUser() = mono {
		val user = userLogic.getUser(sessionLogic.getCurrentUserId())
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting Current User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
		userV2Mapper.map(user)
	}

	@Operation(summary = "Get Currently logged-in user session.", description = "Get current user.")
	@GetMapping("/session", produces = ["text/plain"])
	fun getCurrentSession(): String? { // TODO MB nullable or exception ?
		return sessionLogic.getOrCreateSession()?.id
	}

	@Operation(summary = "List users with(out) pagination", description = "Returns a list of users.")
	@GetMapping
	fun listUsersBy(
		@Parameter(description = "An user email") @RequestParam(required = false) startKey: String?,
		@Parameter(description = "An user document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@Parameter(description = "Filter out patient users") @RequestParam(required = false) skipPatients: Boolean?
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT // TODO SH MB: rather use defaultValue = DEFAULT_LIMIT everywhere?
		val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)
		val allUsers = userLogic.listUsers(paginationOffset, skipPatients ?: true)

		allUsers.paginatedList<User, UserDto>(userToUserDto, realLimit)
	}

	@Operation(summary = "Create a user", description = "Create a user. HealthcareParty ID should be set. Email has to be set and the Login has to be null. On server-side, Email will be used for Login.")
	@PostMapping
	fun createUser(@RequestBody userDto: UserDto) = mono {
		val user = try {
			userLogic.createUser(userV2Mapper.map(userDto.copy(groupId = null)))
		} catch (e: Exception) {
			logger.warn(e.message, e)
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		} ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User creation failed.")

		userV2Mapper.map(user)
	}

	@Operation(summary = "Get a user by his ID", description = "General information about the user")
	@GetMapping("/{userId}")
	fun getUser(@PathVariable userId: String) = mono {
		val user = userLogic.getUser(userId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
		userV2Mapper.map(user)
	}

	@Operation(summary = "Get a user by his Email/Login", description = "General information about the user")
	@GetMapping("/byEmail/{email}")
	fun getUserByEmail(@PathVariable email: String) = mono {
		val user = userLogic.getUserByEmail(email)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.")
		userV2Mapper.map(user)
	}

	@Operation(summary = "Get the list of users by healthcare party id")
	@GetMapping("/byHealthcarePartyId/{id}")
	fun findByHcpartyId(@PathVariable id: String) = mono {
		userLogic.findByHcpartyId(id).toList()
	}

	@Operation(summary = "Delete a User based on his/her ID.", description = "Delete a User based on his/her ID. The return value is an array containing the ID of deleted user.")
	@DeleteMapping("/{userId}")
	fun deleteUser(@PathVariable userId: String) = mono {
		try {
			userLogic.deleteEntities(setOf(userId)).firstOrNull()
		} catch (e: Exception) {
			logger.warn(e.message, e)
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
		}
	}

	@Operation(summary = "Modify a user.", description = "No particular return value. It's just a message.")
	@PutMapping
	fun modifyUser(@RequestBody userDto: UserDto) = mono {
		//Sanitize group
		userLogic.modifyUser(userV2Mapper.map(userDto.copy(groupId = null)))
		val modifiedUser = userLogic.getUser(userDto.id)
			?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User modification failed.")

		userV2Mapper.map(modifiedUser)
	}

	@Operation(summary = "Assign a healthcare party ID to current user", description = "UserDto gets returned.")
	@PutMapping("/current/hcparty/{healthcarePartyId}")
	fun assignHealthcareParty(@PathVariable healthcarePartyId: String) = mono {
		val modifiedUser = userLogic.getUser(sessionLogic.getCurrentUserId())
		modifiedUser?.let {
			userLogic.save(modifiedUser.copy(healthcarePartyId = healthcarePartyId))

			userV2Mapper.map(modifiedUser)
		} ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Assigning healthcare party ID to the current user failed.").also { logger.error(it.message) }
	}

	@Operation(summary = "Modify a User property", description = "Modify a User properties based on his/her ID. The return value is the modified user.")
	@PutMapping("/{userId}/properties")
	fun modifyProperties(@PathVariable userId: String, @RequestBody properties: List<PropertyStubDto>?) = mono {
		val user = userLogic.getUser(userId)
		user?.let {
			val modifiedUser = userLogic.setProperties(
				user,
				properties?.map { p -> propertyStubV2Mapper.map(p) }
					?: listOf()
			)
			if (modifiedUser == null) {
				logger.error("Modify a User property failed.")
				throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modify a User property failed.")
			}
			userV2Mapper.map(modifiedUser)
		} ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modify a User property failed.")
	}

	@GetMapping("/checkPassword")
	fun checkPassword(@RequestHeader("password") password: String) = mono {
		userLogic.checkPassword(password)
	}

	@GetMapping("/encodePassword")
	fun encodePassword(@RequestHeader("password") password: String) = mono {
		userLogic.encodePassword(password)
	}

	@Operation(summary = "Request a new temporary token for authentication")
	@PostMapping("/token/{userId}/{key}")
	fun getToken(@PathVariable userId: String, @Parameter(description = "The token key. Only one instance of a token with a defined key can exist at the same time") @PathVariable key: String, @Parameter(description = "The token validity in seconds", required = false) @RequestParam(required = false) tokenValidity: Long?) = mono {
		userLogic.getUser(userId)?.let {
			userLogic.getToken(it, key, tokenValidity ?: 3600)
		} ?: throw IllegalStateException("Invalid User")
	}

	@Operation(summary = "Check token validity")
	@GetMapping("/token/{userId}")
	fun checkTokenValidity(@PathVariable userId: String, @RequestHeader token: String) = mono {
		userLogic.verifyAuthenticationToken(userId, token)
	}

	@Operation(summary = "Filter users for the current user (HcParty)", description = "Returns a list of users along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
	@PostMapping("/filter")
	fun filterUsersBy(
		@Parameter(description = "A User document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@RequestBody filterChain: FilterChain<User>
	) = mono {
		val realLimit = limit ?: DEFAULT_LIMIT
		val paginationOffset = PaginationOffset(null, startDocumentId, null, realLimit + 1)
		val users = userLogic.filterUsers(paginationOffset, filterChainV2Mapper.map(filterChain))

		users.paginatedList(userToUserDto, realLimit)
	}

	@Operation(summary = "Get ids of healthcare party matching the provided filter for the current user (HcParty) ")
	@PostMapping("/match")
	fun matchUsersBy(@RequestBody filter: AbstractFilterDto<User>) = filters.resolve(filter).injectReactorContext()
}
