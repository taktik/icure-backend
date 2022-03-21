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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v2.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v2.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v2.mapper.HealthElementV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.StubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterChainV2Mapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController("healthElementControllerV2")
@RequestMapping("/rest/v2/helement")
@Tag(name = "helement")
class HealthElementController(
    private val filters: Filters,
    private val healthElementLogic: HealthElementLogic,
    private val healthElementV2Mapper: HealthElementV2Mapper,
    private val delegationV2Mapper: DelegationV2Mapper,
    private val filterChainV2Mapper: FilterChainV2Mapper,
    private val stubV2Mapper: StubV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val DEFAULT_LIMIT = 1000
    private val healthElementToHealthElementDto = { it: HealthElement -> healthElementV2Mapper.map(it) }

    @Operation(
        summary = "Create a health element with the current user",
        description = "Returns an instance of created health element."
    )
    @PostMapping
    fun createHealthElement(@RequestBody c: HealthElementDto) = mono {
        val element = healthElementLogic.createHealthElement(healthElementV2Mapper.map(c))
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Health element creation failed.")

        healthElementV2Mapper.map(element)
    }

    @Operation(summary = "Get a health element")
    @GetMapping("/{healthElementId}")
    fun getHealthElement(@PathVariable healthElementId: String) = mono {
        val element = healthElementLogic.getHealthElement(healthElementId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting health element failed. Possible reasons: no such health element exists, or server error. Please try again or read the server log.")

        healthElementV2Mapper.map(element)
    }

    @Operation(summary = "Get healthElements by batch", description = "Get a list of healthElement by ids/keys.")
    @PostMapping("/byIds")
    fun getHealthElements(@RequestBody healthElementIds: ListOfIdsDto): Flux<HealthElementDto> {
        val healthElements = healthElementLogic.getHealthElements(healthElementIds.ids)
        return healthElements.map { c -> healthElementV2Mapper.map(c) }.injectReactorContext()
    }

    @Operation(summary = "List health elements found By Healthcare Party and secret foreign keyelementIds.", description = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun listHealthElementsByHCPartyAndPatientForeignKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<HealthElementDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = healthElementLogic.listHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys))

        return elementList
                .map { element -> healthElementV2Mapper.map(element) }
                .injectReactorContext()
    }

    @Operation(summary = "List helement stubs found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun listHealthElementsDelegationsStubsByHCPartyAndPatientForeignKeys(@RequestParam hcPartyId: String,
                                                                         @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return healthElementLogic.listHealthElementsByHcPartyAndSecretPatientKeys(hcPartyId, secretPatientKeys)
                .map { healthElement -> stubV2Mapper.mapToStub(healthElement) }
                .injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setHealthElementsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val healthElements = healthElementLogic.getHealthElements(stubs.map { it.id }).map { he ->
            stubs.find { s -> s.id == he.id }?.let { stub ->
                he.copy(
                        delegations = he.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = he.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = he.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels }
                )
            } ?: he
        }
        emitAll(healthElementLogic.modifyEntities(healthElements.toList()).map { healthElementV2Mapper.map(it) })
    }.injectReactorContext()

    @Operation(summary = "Delete health elements.", description = "Response is a set containing the ID's of deleted health elements.")
    @DeleteMapping("/delete/batch")
    fun deleteHealthElements(@RequestBody healthElementIds: ListOfIdsDto): Flux<DocIdentifier> {
        return healthElementIds.ids.takeIf{it.isNotEmpty()}
                ?.let { ids ->
                    try {
                        healthElementLogic.deleteHealthElements(HashSet(ids)).injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
    }

    @Operation(summary = "Modify a health element", description = "Returns the modified health element.")
    @PutMapping
    fun modifyHealthElement(@RequestBody healthElementDto: HealthElementDto) = mono {
        val modifiedHealthElement = healthElementLogic.modifyHealthElement(healthElementV2Mapper.map(healthElementDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Health element modification failed.")
        healthElementV2Mapper.map(modifiedHealthElement)
    }

    @Operation(summary = "Modify a batch of health elements", description = "Returns the modified health elements.")
    @PutMapping("/batch")
    fun modifyHealthElements(@RequestBody healthElementDtos: List<HealthElementDto>): Flux<HealthElementDto> =
        try {
            val hes = healthElementLogic.modifyEntities(healthElementDtos.map { f -> healthElementV2Mapper.map(f) })
            hes.map { healthElementV2Mapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

    @Operation(summary = "Create a batch of healthcare elements", description = "Returns the created healthcare elements.")
    @PostMapping("/batch")
    fun createHealthElements(@RequestBody healthElementDtos: List<HealthElementDto>): Flux<HealthElementDto> =
        try {
            val hes = healthElementLogic.createEntities(healthElementDtos.map { f -> healthElementV2Mapper.map(f) })
            hes.map { healthElementV2Mapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

    @Operation(summary = "Delegates a health element to a healthcare party", description = "It delegates a health element to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{healthElementId}/delegate")
    fun newHealthElementDelegations(@PathVariable healthElementId: String, @RequestBody ds: List<DelegationDto>) = mono {
        healthElementLogic.addDelegations(healthElementId, ds.map { d -> delegationV2Mapper.map(d) })
        val healthElementWithDelegation = healthElementLogic.getHealthElement(healthElementId)

        val succeed = healthElementWithDelegation?.delegations != null && healthElementWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            healthElementWithDelegation?.let { healthElementV2Mapper.map(it) }
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for health element failed.")
        }
    }

    @Operation(
        summary = "Filter health elements for the current user (HcParty)",
        description = "Returns a list of health elements along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page."
    )
    @PostMapping("/filter")
    fun filterHealthElementsBy(
        @Parameter(description = "A HealthElement document ID") @RequestParam(required = false) startDocumentId: String?,
        @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
        @RequestBody filterChain: FilterChain<HealthElement>
    ) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(null, startDocumentId, null, realLimit + 1)

        val healthElements = healthElementLogic.filter(paginationOffset, filterChainV2Mapper.map(filterChain))

        healthElements.paginatedList(healthElementToHealthElementDto, realLimit)
    }

    @Operation(summary = "Get ids of health element matching the provided filter for the current user (HcParty) ")
    @PostMapping("/match")
    fun matchHealthElementsBy(@RequestBody filter: AbstractFilterDto<HealthElement>) = filters.resolve(filter).injectReactorContext()
}
