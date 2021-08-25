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
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v1.mapper.HealthElementMapper
import org.taktik.icure.services.external.rest.v1.mapper.StubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.filter.FilterChainMapper
import org.taktik.icure.services.external.rest.v1.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.util.*

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/helement")
@Tag(name = "helement")
class HealthElementController(
        private val healthElementLogic: HealthElementLogic,
        private val healthElementMapper: HealthElementMapper,
        private val delegationMapper: DelegationMapper,
        private val filterChainMapper: FilterChainMapper,
        private val stubMapper: StubMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Create a health element with the current user", description = "Returns an instance of created health element.")
    @PostMapping
    fun createHealthElement(@RequestBody c: HealthElementDto) = mono {
        val element = healthElementLogic.createHealthElement(healthElementMapper.map(c))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Health element creation failed.")

        healthElementMapper.map(element)
    }

    @Operation(summary = "Get a health element")
    @GetMapping("/{healthElementId}")
    fun getHealthElement(@PathVariable healthElementId: String) = mono {
        val element = healthElementLogic.getHealthElement(healthElementId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting health element failed. Possible reasons: no such health element exists, or server error. Please try again or read the server log.")

        healthElementMapper.map(element)
    }

    @Operation(summary = "List health elements found By Healthcare Party and secret foreign keyelementIds.", description = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findHealthElementsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<HealthElementDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = healthElementLogic.findByHCPartySecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys))

        return elementList
                .map { element -> healthElementMapper.map(element) }
                .injectReactorContext()
    }

    @Operation(summary = "List helement stubs found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findHealthElementsDelegationsStubsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return healthElementLogic.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys)
                .map { healthElement -> stubMapper.mapToStub(healthElement) }
                .injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setHealthElementsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val healthElements = healthElementLogic.getHealthElements(stubs.map { it.id }).map { he ->
            stubs.find { s -> s.id == he.id }?.let { stub ->
                he.copy(
                        delegations = he.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = he.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = he.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels }
                )
            } ?: he
        }
        emitAll(healthElementLogic.updateEntities(healthElements.toList()).map { healthElementMapper.map(it) })
    }.injectReactorContext()

    @Operation(summary = "Delete health elements.", description = "Response is a set containing the ID's of deleted health elements.")
    @DeleteMapping("/{healthElementIds}")
    fun deleteHealthElements(@PathVariable healthElementIds: String): Flux<DocIdentifier> {
        val ids = healthElementIds.split(',')
        if (ids.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }

        return healthElementLogic.deleteHealthElements(HashSet(ids))
                .injectReactorContext()
    }

    @Operation(summary = "Modify a health element", description = "Returns the modified health element.")
    @PutMapping
    fun modifyHealthElement(@RequestBody healthElementDto: HealthElementDto) = mono {
        val modifiedHealthElement = healthElementLogic.modifyHealthElement(healthElementMapper.map(healthElementDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Health element modification failed.")
        healthElementMapper.map(modifiedHealthElement)
    }

    @Operation(summary = "Modify a batch of health elements", description = "Returns the modified health elements.")
    @PutMapping("/batch")
    fun modifyHealthElements(@RequestBody healthElementDtos: List<HealthElementDto>): Flux<HealthElementDto> =
        try {
            val hes = healthElementLogic.updateEntities(healthElementDtos.map { f -> healthElementMapper.map(f) })
            hes.map { healthElementMapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

    @Operation(summary = "Create a batch of healthcare elements", description = "Returns the created healthcare elements.")
    @PostMapping("/batch")
    fun createHealthElements(@RequestBody healthElementDtos: List<HealthElementDto>): Flux<HealthElementDto> =
        try {
            val hes = healthElementLogic.createEntities(healthElementDtos.map { f -> healthElementMapper.map(f) })
            hes.map { healthElementMapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

    @Operation(summary = "Delegates a health element to a healthcare party", description = "It delegates a health element to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{healthElementId}/delegate")
    fun newHealthElementDelegations(@PathVariable healthElementId: String, @RequestBody ds: List<DelegationDto>) = mono {
        healthElementLogic.addDelegations(healthElementId, ds.map { d -> delegationMapper.map(d) })
        val healthElementWithDelegation = healthElementLogic.getHealthElement(healthElementId)

        val succeed = healthElementWithDelegation?.delegations != null && healthElementWithDelegation.delegations!!.isNotEmpty()
        if (succeed) {
            healthElementWithDelegation?.let { healthElementMapper.map(it) }
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for health element failed.")
        }
    }

    @Operation(summary = "Filter health elements for the current user (HcParty)", description = "Returns a list of health elements along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterHealthElementsBy(@RequestBody filterChain: FilterChain<HealthElement>) =
            healthElementLogic.filter(filterChainMapper.map(filterChain))
                    .map { healthElementMapper.map(it) }
                    .injectReactorContext()
}
