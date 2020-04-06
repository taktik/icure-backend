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

package org.taktik.icure.services.external.rest.v1.controllers.core

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.util.*

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/helement")
@Tag(name = "helement")
class HealthElementController(private val mapper: MapperFacade,
                              private val healthElementLogic: HealthElementLogic) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Create a health element with the current user", description = "Returns an instance of created health element.")
    @PostMapping
    fun createHealthElement(@RequestBody c: HealthElementDto) = mono {
        val element = healthElementLogic.createHealthElement(mapper.map(c, HealthElement::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Health element creation failed.")

        mapper.map(element, HealthElementDto::class.java)
    }

    @Operation(summary = "Get a health element")
    @GetMapping("/{healthElementId}")
    fun getHealthElement(@PathVariable healthElementId: String) = mono {
        val element = healthElementLogic.getHealthElement(healthElementId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting health element failed. Possible reasons: no such health element exists, or server error. Please try again or read the server log.")

        mapper.map(element, HealthElementDto::class.java)
    }

    @Operation(summary = "List health elements found By Healthcare Party and secret foreign keyelementIds.", description = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<HealthElementDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = healthElementLogic.findByHCPartySecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys))

        return elementList
                .map { element -> mapper.map(element, HealthElementDto::class.java) }
                .injectReactorContext()
    }

    @Operation(summary = "List helement stubs found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findDelegationsStubsByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return healthElementLogic.findByHCPartySecretPatientKeys(hcPartyId, secretPatientKeys)
                .map { contact -> mapper.map(contact, IcureStubDto::class.java) }
                .injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setHealthElementsDelegations(@RequestBody stubs: List<IcureStubDto>) = mono {
        val healthElements = healthElementLogic.getHealthElements(stubs.map { it.id })
        healthElements.onEach { healthElement ->
            stubs.find { s -> s.id == healthElement.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> healthElement.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> healthElement.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> healthElement.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
            }
        }
        healthElementLogic.updateEntities(healthElements.toList())
    }

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
        val modifiedHealthElement = healthElementLogic.modifyHealthElement(mapper.map(healthElementDto, HealthElement::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Health element modification failed.")
        mapper.map(modifiedHealthElement, HealthElementDto::class.java)
    }

    @Operation(summary = "Modify a batch of health elements", description = "Returns the modified health elements.")
    @PutMapping("/batch")
    fun modifyHealthElements(@RequestBody healthElementDtos: List<HealthElementDto>): Flux<HealthElementDto> =
        try {
            val hes = healthElementLogic.updateEntities(healthElementDtos.map { f -> mapper.map(f, HealthElement::class.java) })
            hes.map { mapper.map(it, HealthElementDto::class.java) }.injectReactorContext()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }


    @Operation(summary = "Delegates a health element to a healthcare party", description = "It delegates a health element to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{healthElementId}/delegate")
    fun newDelegations(@PathVariable healthElementId: String, @RequestBody ds: List<DelegationDto>) = mono {
        healthElementLogic.addDelegations(healthElementId, ds.map { d -> mapper.map(d, Delegation::class.java) })
        val healthElementWithDelegation = healthElementLogic.getHealthElement(healthElementId)

        val succeed = healthElementWithDelegation != null && healthElementWithDelegation.delegations != null && healthElementWithDelegation.delegations!!.isNotEmpty()
        if (succeed) {
            mapper.map(healthElementWithDelegation, HealthElementDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for health element failed.")
        }
    }

    @Operation(summary = "Filter health elements for the current user (HcParty)", description = "Returns a list of health elements along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterBy(@RequestBody filterChain: FilterChain) =
            healthElementLogic.filter(org.taktik.icure.dto.filter.chain.FilterChain(filterChain.filter as org.taktik.icure.dto.filter.Filter<String, HealthElement>, mapper.map(filterChain.predicate, Predicate::class.java)))
                    .map { mapper.map(it, HealthElementDto::class.java) }
                    .injectReactorContext()
}
