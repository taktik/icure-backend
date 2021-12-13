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
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
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
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v2.dto.ContactDto
import org.taktik.icure.services.external.rest.v2.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v2.dto.base.IdentifierDto
import org.taktik.icure.services.external.rest.v2.dto.data.LabelledOccurenceDto
import org.taktik.icure.services.external.rest.v2.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v2.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v2.mapper.ContactV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.IndexedIdentifierV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.StubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.base.IdentifierV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.ServiceV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterChainV2Mapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Collections

@ExperimentalCoroutinesApi
@RestController("contactControllerV2")
@RequestMapping("/rest/v2/contact")
@Tag(name = "contact")
class ContactController(private val filters: org.taktik.icure.asynclogic.impl.filter.Filters,
                        private val contactLogic: ContactLogic,
                        private val sessionLogic: AsyncSessionLogic,
                        private val contactV2Mapper: ContactV2Mapper,
                        private val serviceV2Mapper: ServiceV2Mapper,
                        private val delegationV2Mapper: DelegationV2Mapper,
                        private val filterChainV2Mapper: FilterChainV2Mapper,
                        private val stubV2Mapper: StubV2Mapper,
                        private val identifierV2Mapper: IdentifierV2Mapper,
                        private val indexedIdentifierV2Mapper: IndexedIdentifierV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    val DEFAULT_LIMIT = 1000
    private val contactToContactDto = { it: Contact -> contactV2Mapper.map(it) }

    @Operation(summary = "Get an empty content")
    @GetMapping("/service/content/empty")
    fun getEmptyContent() = ContentDto()

    @Operation(summary = "Create a contact with the current user", description = "Returns an instance of created contact.")
    @PostMapping
    fun createContact(@RequestBody c: ContactDto) = mono {
        val contact = try {
            // handling services' indexes
            contactLogic.createContact(contactV2Mapper.map(handleServiceIndexes(c)))
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Contact creation failed")
        } catch (e: MissingRequirementsException) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        contactV2Mapper.map(contact)
    }

    protected fun handleServiceIndexes(c: ContactDto) = if (c.services.any { it.index == null }) {
        val maxIndex = c.services.maxByOrNull { it.index ?: 0 }?.index ?: 0
        c.copy(
                services = c.services.mapIndexed { idx, it ->
                    if (it.index == null) {
                        it.copy(
                            index = idx + maxIndex
                        )
                    } else it
                }.toSet()
        )
    } else c

    @Operation(summary = "Get a contact")
    @GetMapping("/{contactId}")
    fun getContact(@PathVariable contactId: String) = mono {
        val contact = contactLogic.getContact(contactId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting Contact failed. Possible reasons: no such contact exists, or server error. Please try again or read the server logger.")
        contactV2Mapper.map(contact)
    }

    @Operation(summary = "Get contacts")
    @PostMapping("/byIds")
    fun getContacts(@RequestBody contactIds: ListOfIdsDto): Flux<ContactDto> {
        return contactIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        contactLogic
                                .getContacts(HashSet(ids))
                                .map { c -> contactV2Mapper.map(c) }
                                .injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Get the list of all used codes frequencies in services")
    @GetMapping("/service/codes/{codeType}/{minOccurences}")
    fun getServiceCodesOccurences(@PathVariable codeType: String,
                                  @PathVariable minOccurences: Long) = mono {
        contactLogic.getServiceCodesOccurences(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, codeType, minOccurences)
                .map { LabelledOccurenceDto(it.label, it.occurence) }
    }

    @Operation(summary = "List contacts found By Healthcare Party and service Id.")
    @GetMapping("/byHcPartyServiceId")
    fun listContactByHCPartyServiceId(@RequestParam hcPartyId: String, @RequestParam serviceId: String): Flux<ContactDto> {
        val contactList = contactLogic.listContactsByHCPartyServiceId(hcPartyId, serviceId)
        return contactList.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By externalId.")
    @PostMapping("/byExternalId")
    fun listContactsByExternalId(@RequestParam externalId: String): Flux<ContactDto> {
        val contactList = contactLogic.listContactsByExternalId(externalId)
        return contactList.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By Healthcare Party and form Id.")
    @GetMapping("/byHcPartyFormId")
    fun listContactsByHCPartyAndFormId(@RequestParam hcPartyId: String, @RequestParam formId: String): Flux<ContactDto> {
        val contactList = contactLogic.listContactsByHcPartyAndFormId(hcPartyId, formId)
        return contactList.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By Healthcare Party and form Id.")
    @PostMapping("/byHcPartyFormIds")
    fun listContactsByHCPartyAndFormIds(@RequestParam hcPartyId: String, @RequestBody formIds: ListOfIdsDto): Flux<ContactDto> {
        if (formIds.ids.size == 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }
        val contactList = contactLogic.listContactsByHcPartyAndFormIds(hcPartyId, formIds.ids)

        return contactList.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By Healthcare Party and Patient foreign keys.")
    @PostMapping("/byHcPartyPatientForeignKeys")
    fun listContactsByHCPartyAndPatientForeignKeys(@RequestParam hcPartyId: String, @RequestBody patientForeignKeys: ListOfIdsDto): Flux<ContactDto> {
        if (patientForeignKeys.ids.size == 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        }
        val contactList = contactLogic.listContactsByHCPartyAndPatient(hcPartyId, patientForeignKeys.ids)

        return contactList.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
    }

    @Operation(summary = "List contacts found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun listContactsByHCPartyAndPatientSecretFKeys(@RequestParam hcPartyId: String,
                                                   @RequestParam secretFKeys: String,
                                                   @RequestParam(required = false) planOfActionsIds: String?,
                                                   @RequestParam(required = false) skipClosedContacts: Boolean?): Flux<ContactDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val contactList = contactLogic.listContactsByHCPartyAndPatient(hcPartyId, ArrayList(secretPatientKeys))

        return if (planOfActionsIds != null) {
            val poaids = planOfActionsIds.split(',')
            contactList.filter { c -> (skipClosedContacts == null || !skipClosedContacts || c.closingDate == null) && !Collections.disjoint(c.subContacts.map { it.planOfActionId }, poaids) }.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
        } else {
            contactList.filter { c -> skipClosedContacts == null || !skipClosedContacts || c.closingDate == null }.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
        }
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setContactsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val contacts = contactLogic.getContacts(stubs.map { it.id }).map { contact ->
            stubs.find { s -> s.id == contact.id }?.let { stub ->
                contact.copy(
                        delegations = contact.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = contact.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = contact.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels }
                )
            } ?: contact
        }
        emitAll(contactLogic.modifyEntities(contacts.toList()).map { contactV2Mapper.map(it) })
    }.injectReactorContext()

    @Operation(summary = "List contacts found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun listContactsDelegationsStubsByHCPartyAndPatientForeignKeys(@RequestParam hcPartyId: String,
                                                                   @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return contactLogic.listContactsByHCPartyAndPatient(hcPartyId, secretPatientKeys).map { contact -> stubV2Mapper.mapToStub(contact) }.injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PutMapping("/delegations")
    fun modifyContactsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val contacts = contactLogic.getContacts(stubs.map { it.id }).map { contact ->
            stubs.find { s -> s.id == contact.id }?.let { stub ->
                contact.copy(
                        delegations = contact.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = contact.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = contact.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels }
                )
            } ?: contact
        }
        emitAll(contactLogic.modifyEntities(contacts.toList()).map { contactV2Mapper.map(it) })
    }.injectReactorContext()


    @Operation(summary = "Close contacts for Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @PutMapping("/byHcPartySecretForeignKeys/close")
    fun closeForHCPartyPatientForeignKeys(@RequestParam hcPartyId: String,
                                          @RequestParam secretFKeys: String): Flux<ContactDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val contactFlow = contactLogic.listContactsByHCPartyAndPatient(hcPartyId, secretPatientKeys)

        val savedOrFailed = contactFlow.mapNotNull { c ->
            if (c.closingDate == null) {
                contactLogic.modifyContact(c.copy(closingDate = FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS)))
            } else {
                null
            }
        }

        return savedOrFailed.map { contact -> contactV2Mapper.map(contact) }.injectReactorContext()
    }

    @Operation(summary = "Delete contacts.", description = "Response is a set containing the ID's of deleted contacts.")
    @PostMapping("/delete/batch")
    fun deleteContacts(@RequestBody contactIds: ListOfIdsDto): Flux<DocIdentifier> {
        return contactIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        contactLogic.deleteContacts(HashSet(ids)).injectReactorContext()
                    } catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Modify a contact", description = "Returns the modified contact.")
    @PutMapping
    fun modifyContact(@RequestBody contactDto: ContactDto) = mono {
        handleServiceIndexes(contactDto)

        contactLogic.modifyContact(contactV2Mapper.map(contactDto))?.let {
            contactV2Mapper.map(it)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Contact modification failed.")
    }

    @Operation(summary = "Modify a batch of contacts", description = "Returns the modified contacts.")
    @PutMapping("/batch")
    fun modifyContacts(@RequestBody contactDtos: List<ContactDto>): Flux<ContactDto> {
        return try {
            val contacts = contactLogic.modifyEntities(contactDtos.map { c -> handleServiceIndexes(c) }.map { f -> contactV2Mapper.map(f) })
            contacts.map { f -> contactV2Mapper.map(f) }.injectReactorContext()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Create a batch of contacts", description = "Returns the modified contacts.")
    @PostMapping("/batch")
    fun createContacts(@RequestBody contactDtos: List<ContactDto>): Flux<ContactDto> {
        return try {
            val contacts = contactLogic.createEntities(contactDtos.map { c -> handleServiceIndexes(c) }.map { f -> contactV2Mapper.map(f) })
            contacts.map { f -> contactV2Mapper.map(f) }.injectReactorContext()
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Delegates a contact to a healthcare party", description = "It delegates a contact to a healthcare party (By current healthcare party). Returns the contact with new delegations.")
    @PostMapping("/{contactId}/delegate")
    fun newContactDelegations(@PathVariable contactId: String, @RequestBody d: DelegationDto) = mono {
        contactLogic.addDelegation(contactId, delegationV2Mapper.map(d))
        val contactWithDelegation = contactLogic.getContact(contactId)

        // TODO: write more function to add complete access to all contacts of a patient to a HcParty, or a contact or a subset of contacts
        // TODO: kind of adding new secretForeignKeys and cryptedForeignKeys

        val succeed = contactWithDelegation?.delegations != null && contactWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            contactWithDelegation?.let { contactV2Mapper.map(it) }
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for Contact failed.")
        }
    }

    @Operation(summary = "List contacts for the current user (HcParty) or the given hcparty in the filter ", description = "Returns a list of contacts along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterContactsBy(
            @Parameter(description = "A Contact document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @RequestBody filterChain: FilterChain<Contact>) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT

        val paginationOffset = PaginationOffset(null, startDocumentId, null, realLimit+1)

        val contacts = contactLogic.filterContacts(paginationOffset, filterChainV2Mapper.map(filterChain))

        contacts.paginatedList(contactToContactDto, realLimit)
    }

    @Operation(summary = "Get ids of contacts matching the provided filter for the current user (HcParty) ")
    @PostMapping("/match")
    fun matchContactsBy(@RequestBody filter: AbstractFilterDto<Contact>) = filters.resolve(filter).injectReactorContext()

    // TODO SH MB test this for PaginatedList construction...
    @Operation(summary = "List services for the current user (HcParty) or the given hcparty in the filter ", description = "Returns a list of contacts along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/service/filter")
    fun filterServicesBy(
            @Parameter(description = "A Contact document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @RequestBody filterChain: FilterChain<Service>) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT

        val paginationOffset = PaginationOffset(null, startDocumentId, null, realLimit+1)

        val services: List<ServiceDto> = filterChainV2Mapper.map(filterChain).applyTo(contactLogic.filterServices(paginationOffset, filterChainV2Mapper.map(filterChain)))
                .map { serviceV2Mapper.map(it) }.toList()

        val totalSize = services.size // TODO SH AD: this is wrong! totalSize is ids.size from filterServices, which can be retrieved from the TotalCount ViewQueryResultEvent, but we can't easily recover it...

        if (services.size <= realLimit) {
            org.taktik.icure.services.external.rest.v2.dto.PaginatedList<ServiceDto>(services.size, totalSize, services, null)
        } else {
            val nextKeyPair = services.lastOrNull()?.let { PaginatedDocumentKeyIdPair(null, it.id) }
            val rows = services.subList(0, services.size-1)
            org.taktik.icure.services.external.rest.v2.dto.PaginatedList(realLimit, totalSize, rows, nextKeyPair)
        }
    }

    @Operation(summary = "List services with provided ids ", description = "Returns a list of services")
    @PostMapping("/service")
    fun getServices(@RequestBody ids: ListOfIdsDto) = contactLogic.getServices(ids.ids).map { svc -> serviceV2Mapper.map(svc) }.injectReactorContext()

    @Operation(summary = "List services linked to provided ids ", description = "Returns a list of services")
    @PostMapping("/service/linkedTo")
    fun getServicesLinkedTo(
            @Parameter(description = "The type of the link") @RequestParam(required = false) linkType: String?,
            @RequestBody ids: ListOfIdsDto
    ) = contactLogic.getServicesLinkedTo(ids.ids, linkType).map { svc -> serviceV2Mapper.map(svc) }.injectReactorContext()

    @Operation(summary = "List services by related association id", description = "Returns a list of services")
    @GetMapping("/service/associationId")
    fun listServicesByAssociationId(
            @RequestParam associationId: String,
    ) = contactLogic.listServicesByAssociationId(associationId).map { svc -> serviceV2Mapper.map(svc) }.injectReactorContext()

    @Operation(summary = "Get service by identifier", description = "It gets service data based on the identifier (root & extension) parameters.")
    @GetMapping("/service/{hcPartyId}/{value}")
    fun getServiceByHealthcarepartyAndIdentifier(@PathVariable hcPartyId: String, @PathVariable value: String, @RequestParam(required = false) system: String?) = mono {
        when {
            !system.isNullOrEmpty() -> {
                val serviceIds = contactLogic.listServiceIdsByHcpartyAndIdentifiers(hcPartyId, listOf(Identifier(system= system, value = value))).map { (serviceId, _) -> serviceId }.takeIf { it.count() > 0 }?.toList() ?: listOf(value)
                contactLogic.getServices(serviceIds).map { serviceV2Mapper.map(it) }.firstOrNull() ?: throw IllegalArgumentException("No service found for identifier $value")
            }
            else -> contactLogic.getServices(listOf(value)).map { serviceV2Mapper.map(it) }.firstOrNull() ?: throw IllegalArgumentException("No service found for identifier $value")
        }
    }

    @Operation(summary = "Get services ids by identifiers", description = "For each provided identifier, links corresponding iCure service id")
    @GetMapping("/services/ids/{hcPartyId}/byIdentifiers")
    fun getServicesIdsByHealthcarePartyAndIdentifiers(@PathVariable hcPartyId: String,
                                                      @RequestParam(required = true) identifiers: List<IdentifierDto>
    ) = contactLogic.listServiceIdsByHcpartyAndIdentifiers(hcPartyId, identifiers.map { identifierV2Mapper.map(it) }).map { indexedIdentifierV2Mapper.map(it) }

    @Operation(summary = "List contacts by opening date parties with(out) pagination", description = "Returns a list of contacts.")
    @GetMapping("/byOpeningDate")
    fun findContactsByOpeningDate(
            @Parameter(description = "The contact openingDate", required = true) @RequestParam startKey: Long,
            @Parameter(description = "The contact max openingDate", required = true) @RequestParam endKey: Long,
            @Parameter(description = "hcpartyid", required = true) @RequestParam hcpartyid: String,
            @Parameter(description = "A contact party document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT

        val paginationOffset = PaginationOffset<List<String>>(null, startDocumentId, null, realLimit+1) // startKey is null since it is already a parameter of the subsequent function
        val contacts = contactLogic.listContactsByOpeningDate(hcpartyid, startKey, endKey, paginationOffset)

        contacts.paginatedList<Contact, ContactDto>(contactToContactDto, realLimit)
    }
}

