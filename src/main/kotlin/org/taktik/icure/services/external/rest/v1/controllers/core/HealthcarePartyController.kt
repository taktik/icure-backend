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

package org.taktik.icure.services.external.rest.v1.controllers.core

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.exceptions.DocumentNotFoundException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.PublicKeyDto
import org.taktik.icure.services.external.rest.v1.mapper.HealthcarePartyMapper
import org.taktik.icure.services.external.rest.v1.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/hcparty")
@Tag(name = "hcparty")
class HealthcarePartyController(private val userLogic: UserLogic,
                                private val healthcarePartyLogic: HealthcarePartyLogic,
                                private val sessionLogic: AsyncSessionLogic,
                                private val healthcarePartyMapper: HealthcarePartyMapper
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val DEFAULT_LIMIT = 1000
    private val healthcarePartyToHealthcarePartyDto = { it: HealthcareParty -> healthcarePartyMapper.map(it) }

    @Operation(summary = "Get the current healthcare party if logged in.", description = "General information about the current healthcare Party")
    @GetMapping("/current")
    fun getCurrentHealthcareParty() = mono {
        val healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId())
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the current healthcare party. Probable reasons: no healthcare party is logged in, or server error. Please try again or read the server log.")
        healthcarePartyMapper.map(healthcareParty)
    }

    @Operation(summary = "List healthcare parties with(out) pagination", description = "Returns a list of healthcare parties.")
    @GetMapping
    fun listHealthcareParties(
            @Parameter(description = "A healthcare party Last name") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A healthcare party document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        healthcarePartyLogic.findHealthcarePartiesBy(paginationOffset, desc)
                .paginatedList<HealthcareParty, HealthcarePartyDto>(healthcarePartyToHealthcarePartyDto, realLimit)
    }

    @Operation(summary = "Find healthcare parties by name with(out) pagination", description = "Returns a list of healthcare parties.")
    @GetMapping("/byName")
    fun findByName(
            @Parameter(description = "The Last name search value") @RequestParam(required = false) name: String?,
            @Parameter(description = "A healthcare party Last name") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A healthcare party document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?) = mono {

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
    fun findBySsinOrNihii(
            @PathVariable searchValue: String,
            @Parameter(description = "A healthcare party Last name") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A healthcare party document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        healthcarePartyLogic.findHealthcarePartiesBySsinOrNihii(searchValue, paginationOffset, desc)
                .paginatedList<HealthcareParty, HealthcarePartyDto>(healthcarePartyToHealthcarePartyDto, realLimit)
    }

    @Operation(summary = "Find healthcare parties by name with(out) pagination", description = "Returns a list of healthcare parties.")
    @GetMapping("/byNameStrict/{name}")
    fun listByName(@Parameter(description = "The Last name search value")
                   @PathVariable name: String) =
            healthcarePartyLogic.listHealthcarePartiesByName(name)
                           .map { healthcarePartyMapper.map(it) }
                           .injectReactorContext()

    @Operation(summary = "Find healthcare parties by name with(out) pagination", description = "Returns a list of healthcare parties.")
    @GetMapping("/bySpecialityAndPostCode/{type}/{spec}/{firstCode}/to/{lastCode}")
    fun findBySpecialityAndPostCode(
            @Parameter(description = "The type of the HCP (persphysician)") @PathVariable type: String,
            @Parameter(description = "The speciality of the HCP") @PathVariable spec: String,
            @Parameter(description = "The first postCode for the HCP") @PathVariable firstCode: String,
            @Parameter(description = "The last postCode for the HCP") @PathVariable lastCode: String,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int) = mono {
        healthcarePartyLogic.listHealthcarePartiesBy(type, spec, firstCode, lastCode).paginatedList<HealthcareParty, HealthcarePartyDto>(healthcarePartyToHealthcarePartyDto, limit)
    }

    @Operation(summary = "Create a healthcare party", description = "One of Name or Last name+First name, Nihii, and Public key are required.")
    @PostMapping
    fun createHealthcareParty(@RequestBody h: HealthcarePartyDto) = mono {
        val hcParty = try {
            healthcarePartyLogic.createHealthcareParty(healthcarePartyMapper.map(h))
        } catch (e: MissingRequirementsException) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

        val succeed = hcParty != null
        if (succeed) {
            hcParty?.let { healthcarePartyMapper.map(it) }
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Healthcare party creation failed.")
        }
    }

    @Operation(summary = "Get the HcParty encrypted AES keys indexed by owner", description = "(key, value) of the map is as follows: (ID of the owner of the encrypted AES key, encrypted AES key)")
    @GetMapping("/{healthcarePartyId}/keys")
    fun getHcPartyKeysForDelegate(@PathVariable healthcarePartyId: String) = mono {
        healthcarePartyLogic.getHcPartyKeysForDelegate(healthcarePartyId)
    }

    @Operation(summary = "Get a healthcareParty by his ID", description = "General information about the healthcare Party")
    @GetMapping("/{healthcarePartyId}")
    fun getHealthcareParty(@PathVariable healthcarePartyId: String) = mono {
        val healthcareParty = healthcarePartyLogic.getHealthcareParty(healthcarePartyId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the healthcare party. Probable reasons: no such party exists, or server error. Please try again or read the server log.")
        healthcarePartyMapper.map(healthcareParty)
    }

    @Operation(summary = "Get healthcareParties by their IDs", description = "General information about the healthcare Party")
    @GetMapping("/byIds/{healthcarePartyIds}")
    fun getHealthcareParties(@PathVariable healthcarePartyIds: String) =
            healthcarePartyLogic.getHealthcareParties(healthcarePartyIds.split(','))
                    .map { healthcarePartyMapper.map(it) }
                    .injectReactorContext()

    @Operation(summary = "Get healthcareParties by their IDs", description = "General information about the healthcare Party")
    @PostMapping("/inGroup/{groupId}/byIds")
    fun getHealthcarePartiesInGroup(@PathVariable groupId: String, @RequestBody(required = false) healthcarePartyIds: ListOfIdsDto? = null) =
            healthcarePartyLogic.getHealthcareParties(groupId, healthcarePartyIds?.ids)
                    .map { healthcarePartyMapper.map(it) }
                    .injectReactorContext()

    @Operation(summary = "Find children of an healthcare parties", description = "Return a list of children hcp.")
    @GetMapping("/{parentId}/children")
    fun getHealthcarePartiesByParentId(@PathVariable parentId: String) =
            healthcarePartyLogic.getHealthcarePartiesByParentId(parentId)
                    .map { healthcarePartyMapper.map(it) }
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

    @Operation(summary = "Delete a healthcare party", description = "Deleting a healthcareParty. Response is an array containing the id of deleted healthcare party.")
    @DeleteMapping("/{healthcarePartyIds}")
    fun deleteHealthcareParties(@PathVariable healthcarePartyIds: String): Flux<DocIdentifier> {
        return try {
            healthcarePartyLogic.deleteHealthcareParties(healthcarePartyIds.split(',')).injectReactorContext()
        } catch (e: DeletionException) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "Modify a Healthcare Party.", description = "No particular return value. It's just a message.")
    @PutMapping
    fun modifyHealthcareParty(@RequestBody healthcarePartyDto: HealthcarePartyDto) = mono {
        try {
            healthcarePartyLogic.modifyHealthcareParty(healthcarePartyMapper.map(healthcarePartyDto))?.let {
                healthcarePartyMapper.map(it)
            }
        } catch (e: MissingRequirementsException) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Delete a healthcare party", description = "Deleting a healthcareParty. Response is an array containing the id of deleted healthcare party.")
    @DeleteMapping("/inGroup/{groupId}/{healthcarePartyIds}")
    fun deleteHealthcarePartiesInGroup(@PathVariable groupId: String, @PathVariable healthcarePartyIds: String): Flux<DocIdentifier> {
        return try {
            healthcarePartyLogic.deleteHealthcareParties(groupId, healthcarePartyIds.split(',')).injectReactorContext()
        } catch (e: DeletionException) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "Modify a Healthcare Party.", description = "No particular return value. It's just a message.")
    @PutMapping("/inGroup/{groupId}")
    fun modifyHealthcarePartyInGroup(@PathVariable groupId: String, @RequestBody healthcarePartyDto: HealthcarePartyDto) = mono {
        try {
            val modifiedHealthcareParty = healthcarePartyLogic.modifyHealthcareParty(groupId, healthcarePartyMapper.map(healthcarePartyDto)) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modification of the healthcare party failed. Read the server log.")
            healthcarePartyMapper.map(modifiedHealthcareParty)
        } catch (e: MissingRequirementsException) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Create a healthcare party", description = "One of Name or Last name+First name, Nihii, and Public key are required.")
    @PostMapping("/inGroup/{groupId}")
    fun createHealthcarePartyInGroup(@PathVariable groupId: String, @RequestBody h: HealthcarePartyDto) = mono {
        val hcParty = try {
            healthcarePartyLogic.createHealthcareParty(groupId, healthcarePartyMapper.map(h))
        } catch (e: MissingRequirementsException) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

        val succeed = hcParty != null
        if (succeed) {
            hcParty?.let { healthcarePartyMapper.map(it) }
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Healthcare party creation failed.")
        }
    }

}
