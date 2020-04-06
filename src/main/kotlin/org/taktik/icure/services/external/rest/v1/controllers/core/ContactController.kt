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

import com.google.common.collect.Lists
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v1.dto.data.LabelledOccurenceDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/contact")
@Tag(name = "contact")
class ContactController(private val mapper: MapperFacade,
                        private val filters: org.taktik.icure.asynclogic.impl.filter.Filters,
                        private val contactLogic: ContactLogic,
                        private val sessionLogic: AsyncSessionLogic) {
    private val log = LoggerFactory.getLogger(javaClass)
    val DEFAULT_LIMIT = 1000

    @Operation(summary = "Get an empty content")
    @GetMapping("/service/content/empty")
    fun getEmptyContent() = ContentDto()

    @Operation(summary = "Create a contact with the current user", description = "Returns an instance of created contact.")
    @PostMapping
    fun createContact(@RequestBody c: ContactDto) = mono {
        val contact = try {
            // handling services' indexes
            handleServiceIndexes(c)
            contactLogic.createContact(mapper.map(c, Contact::class.java))
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Contact creation failed")
        } catch (e: MissingRequirementsException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        mapper.map(contact, ContactDto::class.java)
    }

    protected fun handleServiceIndexes(c: ContactDto) {
        var max = 0L
        var baseIndex = 0L
        var nullFound = false
        if (c.services != null) {
            for (service in c.services) {
                var index = service.index
                if (index == null) {
                    nullFound = true
                    baseIndex = max + 1
                    max = 0L
                }
                if (nullFound) {
                    index = baseIndex + 1 + (index ?: 0L)
                    service.index = index
                }
                if (index != null && index > max) {
                    max = index
                }
            }
        } else {
            // no null pointer exception in Orika in case of no services
            c.services = Lists.newArrayList()
        }
    }

    @Operation(summary = "Get a contact")
    @GetMapping("/{contactId}")
    fun getContact(@PathVariable contactId: String) = mono {
        val contact = contactLogic.getContact(contactId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting Contact failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.")
        mapper.map(contact, ContactDto::class.java)
    }

    @Operation(summary = "Get contacts")
    @PostMapping("/byIds")
    fun getContacts(@RequestBody contactIds: ListOfIdsDto): Flux<ContactDto> {
        val contacts = contactLogic.getContacts(contactIds.ids)
        return contacts.map { c -> mapper.map(c, ContactDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "Get the list of all used codes frequencies in services")
    @GetMapping("/service/codes/{codeType}/{minOccurences}")
    fun getServiceCodesOccurences(@PathVariable codeType: String,
                                  @PathVariable minOccurences: Long) = mono {
        contactLogic.getServiceCodesOccurences(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, codeType, minOccurences)
                .map { mapper.map(it, LabelledOccurenceDto::class.java) }
    }

    @Operation(summary = "List contacts found By Healthcare Party and form Id.")
    @GetMapping("/byHcPartyFormId")
    fun findByHCPartyFormId(@RequestParam hcPartyId: String, @RequestParam formId: String): Flux<ContactDto> {
        val contactList = contactLogic.findContactsByHCPartyFormId(hcPartyId, formId)
        return contactList.map { contact -> mapper.map(contact, ContactDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By Healthcare Party and form Id.")
    @PostMapping("/byHcPartyFormIds")
    fun findByHCPartyFormIds(@RequestParam hcPartyId: String, @RequestBody formIds: ListOfIdsDto): Flux<ContactDto> {
        if (formIds.ids.size == 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }
        val contactList = contactLogic.findContactsByHCPartyFormIds(hcPartyId, formIds.ids)

        return contactList.map { contact -> mapper.map(contact, ContactDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By Healthcare Party and Patient foreign keys.")
    @PostMapping("/byHcPartyPatientForeignKeys")
    fun findByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String, @RequestBody patientForeignKeys: ListOfIdsDto): Flux<ContactDto> {
        if (patientForeignKeys.ids.size == 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }
        val contactList = contactLogic.findByHCPartyPatient(hcPartyId, patientForeignKeys.ids)

        return contactList.map { contact -> mapper.map(contact, ContactDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                        @RequestParam secretFKeys: String,
                                        @RequestParam(required = false) planOfActionsIds: String?,
                                        @RequestParam(required = false) skipClosedContacts: Boolean?): Flux<ContactDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val contactList = contactLogic.findByHCPartyPatient(hcPartyId, ArrayList(secretPatientKeys))

        return if (planOfActionsIds != null) {
            val poaids = planOfActionsIds.split(',')
            contactList.filter { c -> (skipClosedContacts == null || !skipClosedContacts || c.closingDate == null) && !Collections.disjoint(c.subContacts.map { it.planOfActionId }, poaids) }.map { contact -> mapper.map(contact, ContactDto::class.java) }.injectReactorContext()
        } else {
            contactList.filter { c -> skipClosedContacts == null || !skipClosedContacts || c.closingDate == null }.map { contact -> mapper.map(contact, ContactDto::class.java) }.injectReactorContext()
        }
    }

    @Operation(summary = "List contacts found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findDelegationsStubsByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return contactLogic.findByHCPartyPatient(hcPartyId, secretPatientKeys).map { contact -> mapper.map(contact, IcureStubDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setContactsDelegations(@RequestBody stubs: List<IcureStubDto>) = mono {
        val contacts = contactLogic.getContacts(stubs.map { it.id })
        contacts.onEach { contact ->
            stubs.find { s -> s.id == contact.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> contact.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> contact.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> contact.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toMutableSet() }
            }
        }
        contactLogic.updateEntities(contacts.toList()).collect()
    }


    @Operation(summary = "Close contacts for Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @PutMapping("/byHcPartySecretForeignKeys/close")
    fun closeForHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                          @RequestParam secretFKeys: String): Flux<ContactDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val contactFlow = contactLogic.findByHCPartyPatient(hcPartyId, secretPatientKeys)

        val savedOrFailed = contactFlow.mapNotNull { c ->
            if (c.closingDate == null) {
                c.closingDate = FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS)
                contactLogic.modifyContact(c)
            } else {
                null
            }
        }

        return savedOrFailed.map { contact -> mapper.map(contact, ContactDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "Delete contacts.", description = "Response is a set containing the ID's of deleted contacts.")
    @DeleteMapping("/{contactIds}")
    fun deleteContacts(@PathVariable contactIds: String): Flux<DocIdentifier> {
        if (contactIds.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }
        // TODO versioning?
        return contactLogic.deleteContacts(contactIds.split(',').toSet()).injectReactorContext()
    }

    @Operation(summary = "Modify a contact", description = "Returns the modified contact.")
    @PutMapping
    fun modifyContact(@RequestBody contactDto: ContactDto) = mono {
        handleServiceIndexes(contactDto)

        contactLogic.modifyContact(mapper.map(contactDto, Contact::class.java))?.let {
            mapper.map(it, ContactDto::class.java)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Contact modification failed.")
    }

    @Operation(summary = "Modify a batch of contacts", description = "Returns the modified contacts.")
    @PutMapping("/batch")
    fun modifyContacts(@RequestBody contactDtos: List<ContactDto>): Flux<ContactDto> {
        return try {
            contactDtos.forEach { c -> handleServiceIndexes(c) }

            val contacts = contactLogic.updateEntities(contactDtos.map { f -> mapper.map(f, Contact::class.java) })
            contacts.map { f -> mapper.map(f, ContactDto::class.java) }.injectReactorContext()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

    }

    @Operation(summary = "Delegates a contact to a healthcare party", description = "It delegates a contact to a healthcare party (By current healthcare party). Returns the contact with new delegations.")
    @PostMapping("/{contactId}/delegate")
    fun newDelegations(@PathVariable contactId: String, @RequestBody d: DelegationDto) = mono {
        contactLogic.addDelegation(contactId, mapper.map(d, Delegation::class.java))
        val contactWithDelegation = contactLogic.getContact(contactId)

        // TODO: write more function to add complete access to all contacts of a patient to a HcParty, or a contact or a subset of contacts
        // TODO: kind of adding new secretForeignKeys and cryptedForeignKeys

        val succeed = contactWithDelegation != null && contactWithDelegation.delegations != null && contactWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            mapper.map(contactWithDelegation, ContactDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for Contact failed.")
        }
    }

    @Operation(summary = "List contacts for the current user (HcParty) or the given hcparty in the filter ", description = "Returns a list of contacts along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterBy(
            @ApiParam(value = "A Contact document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
            @RequestBody filterChain: FilterChain) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT

        val paginationOffset = PaginationOffset(null, startDocumentId, null, realLimit+1)

        val contacts = contactLogic.filterContacts(paginationOffset, org.taktik.icure.dto.filter.chain.FilterChain(filterChain.filter as org.taktik.icure.dto.filter.Filter<String, Contact>, mapper.map(filterChain.predicate, Predicate::class.java)))

        contacts.paginatedList<Contact, ContactDto>(mapper, realLimit)
    }

    @Operation(summary = "Get ids of contacts matching the provided filter for the current user (HcParty) ")
    @PostMapping("/match")
    fun matchBy(@RequestBody filter: FilterDto<*>) = filters.resolve(filter).injectReactorContext()

    // TODO SH MB test this for PaginatedList construction...
    @Operation(summary = "List services for the current user (HcParty) or the given hcparty in the filter ", description = "Returns a list of contacts along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/service/filter")
    fun filterServicesBy(
            @ApiParam(value = "A Contact document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
            @RequestBody filterChain: FilterChain) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT

        val paginationOffset = PaginationOffset(null, startDocumentId, null, realLimit+1)

        val services = contactLogic.filterServices(paginationOffset, org.taktik.icure.dto.filter.chain.FilterChain(filterChain.filter as org.taktik.icure.dto.filter.Filter<String, Service>, mapper.map(filterChain.predicate, Predicate::class.java)))
                .toList().let { filterChain.filter.applyTo(it) } // TODO AD is this correct?
                .map { mapper.map(it, ServiceDto::class.java) }

        val totalSize = services.size // TODO SH AD: this is wrong! totalSize is ids.size from filterServices, which can be retrieved from the TotalCount ViewQueryResultEvent, but we can't easily recover it...

        if (services.size <= realLimit) {
            org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ServiceDto>(services.size, totalSize, services, null)
        } else {
            val nextKeyPair = services.lastOrNull()?.let { PaginatedDocumentKeyIdPair(null, it.id) }
            val rows = services.subList(0, services.size-1)
            org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ServiceDto>(realLimit, totalSize, rows, nextKeyPair)
        }
    }

    @Operation(summary = "List contacts bu opening date parties with(out) pagination", description = "Returns a list of contacts.")
    @GetMapping("/byOpeningDate")
    fun listContactsByOpeningDate(
            @ApiParam(value = "The contact openingDate", required = true) @RequestParam startKey: Long,
            @ApiParam(value = "The contact max openingDate", required = true) @RequestParam endKey: Long,
            @ApiParam(value = "hcpartyid", required = true) @RequestParam hcpartyid: String,
            @ApiParam(value = "A contact party document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT

        val paginationOffset = PaginationOffset<List<String>>(null, startDocumentId, null, realLimit+1) // startKey is null since it is already a parameter of the subsequent function
        val contacts = contactLogic.listContactsByOpeningDate(hcpartyid, startKey, endKey, paginationOffset)

        contacts.paginatedList<Contact, ContactDto>(mapper, realLimit)
    }
}
