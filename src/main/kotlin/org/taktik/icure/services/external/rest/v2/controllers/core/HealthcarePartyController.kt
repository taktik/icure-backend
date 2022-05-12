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

package org.taktik.icure.services.external.rest.v2.controllers.core

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.exceptions.DocumentNotFoundException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v2.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.PublicKeyDto
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v2.mapper.HealthcarePartyV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterChainV2Mapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@RestController("healthcarePartyControllerV2")
@RequestMapping("/rest/v2/hcparty")
@Tag(name = "hcparty")
class HealthcarePartyController(
	private val filters: Filters,
	private val healthcarePartyLogic: HealthcarePartyLogic,
	private val sessionLogic: AsyncSessionLogic,
	private val healthcarePartyV2Mapper: HealthcarePartyV2Mapper,
	private val filterChainV2Mapper: FilterChainV2Mapper,
) {
	private val logger: Logger = LoggerFactory.getLogger(javaClass)
	private val DEFAULT_LIMIT = 1000
	private val healthcarePartyToHealthcarePartyDto = { it: HealthcareParty -> healthcarePartyV2Mapper.map(it) }

	@Operation(summary = "Get the current healthcare party if logged in.", description = "General information about the current healthcare Party")
	@GetMapping("/current")
	fun getCurrentHealthcareParty() = mono {
		val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the current healthcare party. Probable reasons: no healthcare party is logged in, or server error. Please try again or read the server log.")
		healthcarePartyV2Mapper.map(healthcareParty)
	}

	@Operation(summary = "List healthcare parties with(out) pagination", description = "Returns a list of healthcare parties.")
	@GetMapping
	fun findHealthcarePartiesBy(
		@Parameter(description = "A healthcare party Last name") @RequestParam(required = false) startKey: String?,
		@Parameter(description = "A healthcare party document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT
		val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)

		healthcarePartyLogic.findHealthcarePartiesBy(paginationOffset, desc)
			.paginatedList<HealthcareParty, HealthcarePartyDto>(healthcarePartyToHealthcarePartyDto, realLimit)
	}

	@Operation(summary = "Find healthcare parties by name with(out) pagination", description = "Returns a list of healthcare parties.")
	@GetMapping("/byName")
	fun findHealthcarePartiesByName(
		@Parameter(description = "The Last name search value") @RequestParam(required = false) name: String?,
		@Parameter(description = "A healthcare party Last name") @RequestParam(required = false) startKey: String?,
		@Parameter(description = "A healthcare party document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT
		val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)
		if (name == null || name.isEmpty()) {
			healthcarePartyLogic.findHealthcarePartiesBy(paginationOffset, desc)
		} else {
			healthcarePartyLogic.findHealthcarePartiesBy(name, paginationOffset, desc)
		}.paginatedList<HealthcareParty, HealthcarePartyDto>(healthcarePartyToHealthcarePartyDto, realLimit)
	}

	@Operation(summary = "Find healthcare parties by nihii or ssin with(out) pagination", description = "Returns a list of healthcare parties.")
	@GetMapping("/byNihiiOrSsin/{searchValue}")
	fun findHealthcarePartiesBySsinOrNihii(
		@PathVariable searchValue: String,
		@Parameter(description = "A healthcare party Last name") @RequestParam(required = false) startKey: String?,
		@Parameter(description = "A healthcare party document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean
	) = mono {

		val realLimit = limit ?: DEFAULT_LIMIT
		val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1)

		healthcarePartyLogic.findHealthcarePartiesBySsinOrNihii(searchValue, paginationOffset, desc)
			.paginatedList<HealthcareParty, HealthcarePartyDto>(healthcarePartyToHealthcarePartyDto, realLimit)
	}

	@Operation(summary = "Find healthcare parties by name with(out) pagination", description = "Returns a list of healthcare parties.")
	@GetMapping("/byNameStrict/{name}")
	fun listHealthcarePartiesByName(
		@Parameter(description = "The Last name search value")
		@PathVariable name: String
	) =
		healthcarePartyLogic.listHealthcarePartiesByName(name)
			.map { healthcarePartyV2Mapper.map(it) }
			.injectReactorContext()

	@Operation(summary = "Find healthcare parties by name with(out) pagination", description = "Returns a list of healthcare parties.")
	@GetMapping("/bySpecialityAndPostCode/{type}/{spec}/{firstCode}/to/{lastCode}")
	fun findHealthcarePartiesBySpecialityAndPostCode(
		@Parameter(description = "The type of the HCP (persphysician)") @PathVariable type: String,
		@Parameter(description = "The speciality of the HCP") @PathVariable spec: String,
		@Parameter(description = "The first postCode for the HCP") @PathVariable firstCode: String,
		@Parameter(description = "The last postCode for the HCP") @PathVariable lastCode: String,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int
	) = mono {
		healthcarePartyLogic.listHealthcarePartiesBy(type, spec, firstCode, lastCode).paginatedList<HealthcareParty, HealthcarePartyDto>(healthcarePartyToHealthcarePartyDto, limit)
	}

	@Operation(summary = "Create a healthcare party", description = "One of Name or Last name+First name, Nihii, and Public key are required.")
	@PostMapping
	fun createHealthcareParty(@RequestBody h: HealthcarePartyDto) = mono {
		val hcParty = try {
			healthcarePartyLogic.createHealthcareParty(healthcarePartyV2Mapper.map(h))
		} catch (e: MissingRequirementsException) {
			logger.warn(e.message, e)
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}

		val succeed = hcParty != null
		if (succeed) {
			hcParty?.let { healthcarePartyV2Mapper.map(it) }
		} else {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Healthcare party creation failed.")
		}
	}

	@Operation(
		summary = "Get the HcParty encrypted AES keys indexed by owner.",
		description = "(key, value) of the map is as follows: (ID of the owner of the encrypted AES key, encrypted AES keys)"
	)
	@GetMapping("/{healthcarePartyId}/aesExchangeKeys")
	fun getAesExchangeKeysForDelegate(@PathVariable healthcarePartyId: String) = mono {
		healthcarePartyLogic.getAesExchangeKeysForDelegate(healthcarePartyId)
	}

	@Operation(summary = "Get a healthcareParty by his ID", description = "General information about the healthcare Party")
	@GetMapping("/{healthcarePartyId}")
	fun getHealthcareParty(@PathVariable healthcarePartyId: String) = mono {
		val healthcareParty = healthcarePartyLogic.getHealthcareParty(healthcarePartyId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the healthcare party. Probable reasons: no such party exists, or server error. Please try again or read the server log.")
		healthcarePartyV2Mapper.map(healthcareParty)
	}

	@Operation(summary = "Get healthcareParties by their IDs", description = "General information about the healthcare Party")
	@PostMapping("/byIds")
	fun getHealthcareParties(@RequestBody healthcarePartyIds: ListOfIdsDto) =
		healthcarePartyIds.ids.takeIf { it.isNotEmpty() }
			?.let { ids ->
				try {
					healthcarePartyLogic
						.getHealthcareParties(ids)
						.map { healthcarePartyV2Mapper.map(it) }
						.injectReactorContext()
				} catch (e: java.lang.Exception) {
					throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
				}
			}
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }

	@Operation(summary = "Find children of an healthcare parties", description = "Return a list of children hcp.")
	@GetMapping("/{parentId}/children")
	fun listHealthcarePartiesByParentId(@PathVariable parentId: String) =
		healthcarePartyLogic.getHealthcarePartiesByParentId(parentId)
			.map { healthcarePartyV2Mapper.map(it) }
			.injectReactorContext()

	@Operation(summary = "Get public key of a healthcare party", description = "Returns the public key of a healthcare party in Hex")
	@GetMapping("/{healthcarePartyId}/publicKey")
	fun getPublicKey(@PathVariable healthcarePartyId: String) = mono {
		val publicKey = try {
			healthcarePartyLogic.getPublicKey(healthcarePartyId)
		} catch (e: DocumentNotFoundException) {
			logger.warn(e.message, e)
			throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
		} ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No public key is found.")

		PublicKeyDto(healthcarePartyId, publicKey)
	}

	@Operation(summary = "Delete healthcare parties", description = "Deleting healthcareParties. Response is an array containing the id of deleted healthcare parties.")
	@PostMapping("/delete/batch")
	fun deleteHealthcareParties(@RequestBody healthcarePartyIds: ListOfIdsDto): Flux<DocIdentifier> {
		return healthcarePartyIds.ids.takeIf { it.isNotEmpty() }
			?.let { ids ->
				try {
					healthcarePartyLogic.deleteHealthcareParties(ids).injectReactorContext()
				} catch (e: java.lang.Exception) {
					throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
				}
			}
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
	}

	@Operation(summary = "Modify a Healthcare Party.", description = "No particular return value. It's just a message.")
	@PutMapping
	fun modifyHealthcareParty(@RequestBody healthcarePartyDto: HealthcarePartyDto) = mono {
		try {
			healthcarePartyLogic.modifyHealthcareParty(healthcarePartyV2Mapper.map(healthcarePartyDto))?.let {
				healthcarePartyV2Mapper.map(it)
			}
		} catch (e: MissingRequirementsException) {
			logger.warn(e.message, e)
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}
	}

	@Operation(summary = "Get ids of healthcare party matching the provided filter for the current user (HcParty) ")
	@PostMapping("/match")
	fun matchHealthcarePartiesBy(@RequestBody filter: AbstractFilterDto<HealthcareParty>) = filters.resolve(filter).injectReactorContext()

	@Operation(summary = "Filter healthcare parties for the current user (HcParty)", description = "Returns a list of healthcare party along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
	@PostMapping("/filter")
	fun filterHealthPartiesBy(
		@Parameter(description = "A HealthcareParty document ID") @RequestParam(required = false) startDocumentId: String?,
		@Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
		@RequestBody filterChain: FilterChain<HealthcareParty>
	) = mono {
		val realLimit = limit ?: DEFAULT_LIMIT
		val paginationOffset = PaginationOffset(null, startDocumentId, null, realLimit + 1)
		val healthcareParties = healthcarePartyLogic.filterHealthcareParties(paginationOffset, filterChainV2Mapper.map(filterChain))

		healthcareParties.paginatedList(healthcarePartyToHealthcarePartyDto, realLimit)
	}
}
