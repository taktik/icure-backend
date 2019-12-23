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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.services.external.rest.v1.dto.*
import org.taktik.icure.services.external.rest.v1.dto.data.LabelledOccurenceDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoicingCodeDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import reactor.core.publisher.Flux
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/invoice")
@Api(tags = ["invoice"])
class InvoiceController(private val invoiceLogic: InvoiceLogic,
                        private val mapper: MapperFacade,
                        private val sessionLogic: AsyncSessionLogic,
                        private val insuranceLogic: InsuranceLogic,
                        private val userLogic: UserLogic,
                        private val uuidGenerator: UUIDGenerator) {

    private val DEFAULT_LIMIT = 1000

    @ApiOperation(nickname = "createInvoice", value = "Creates an invoice")
    @PostMapping
    suspend fun createInvoice(@RequestBody invoiceDto: InvoiceDto): InvoiceDto {
        val invoice = invoiceLogic.createInvoice(mapper.map(invoiceDto, Invoice::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice creation failed")
        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "deleteInvoice", value = "Deletes an invoice")
    @DeleteMapping("/{invoiceId}")
    suspend fun deleteInvoice(@PathVariable invoiceId: String): DocIdentifier {
        return invoiceLogic.deleteInvoice(invoiceId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice deletion failed")
    }

    @ApiOperation(nickname = "getInvoice", value = "Gets an invoice")
    @GetMapping("/{invoiceId}")
    suspend fun getInvoice(@PathVariable invoiceId: String): InvoiceDto {
        val invoice = invoiceLogic.getInvoice(invoiceId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice fetching failed")
        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "getInvoices", value = "Gets an invoice")
    @PostMapping("/byIds")
    fun getInvoices(@RequestBody invoiceIds: ListOfIdsDto) = invoiceLogic.getInvoices(invoiceIds.ids)
            .map { mapper.map(it, InvoiceDto::class.java) }
            .injectReactorContext()

    @ApiOperation(nickname = "modifyInvoice", value = "Modifies an invoice")
    @PutMapping
    suspend fun modifyInvoice(@RequestBody invoiceDto: InvoiceDto): InvoiceDto {
        val invoice = invoiceLogic.modifyInvoice(mapper.map(invoiceDto, Invoice::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "reassignInvoice", value = "Modifies an invoice")
    @PostMapping("/reassign")
    suspend fun reassignInvoice(@RequestBody invoiceDto: InvoiceDto): InvoiceDto {
        val invoice = Invoice.reassignationInvoiceFromOtherInvoice(mapper.map(invoiceDto, Invoice::class.java), uuidGenerator)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "newDelegations", value = "Adds a delegation to a invoice")
    @PutMapping("/{invoiceId}/delegate")
    suspend fun newDelegations(@PathVariable invoiceId: String, @RequestBody ds: List<DelegationDto>): MessageDto {
        val invoice = invoiceLogic.addDelegations(invoiceId, ds.map { mapper.map(it, Delegation::class.java) })
        if (invoice != null && invoice.delegations != null && invoice.delegations.isNotEmpty()) {
            return mapper.map(invoice, MessageDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for invoice failed")
        }
    }

    @ApiOperation(nickname = "mergeTo", value = "Gets all invoices for author at date")
    @PostMapping("/mergeTo/{invoiceId}")
    suspend fun mergeTo(@PathVariable invoiceId: String, @RequestBody ids: ListOfIdsDto): InvoiceDto {
        return mapper.map(invoiceLogic.mergeInvoices(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId, invoiceLogic.getInvoices(ids.ids).toList(), invoiceLogic.getInvoice(invoiceId))
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice $invoiceId not found"), InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "validate", value = "Gets all invoices for author at date")
    @PostMapping("/validate/{invoiceId}")
    suspend fun validate(@PathVariable invoiceId: String, @RequestParam scheme: String, @RequestParam forcedValue: String): InvoiceDto {
        return mapper.map(invoiceLogic.validateInvoice(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId, invoiceLogic.getInvoice(invoiceId), scheme, forcedValue), InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "appendCodes", value = "Gets all invoices for author at date")
    @PostMapping("/byauthor/{userId}/append/{type}/{sentMediumType}")
    suspend fun appendCodes(@PathVariable userId: String,
                            @PathVariable type: String,
                            @PathVariable sentMediumType: String,
                            @RequestParam(required = false) insuranceId: String?,
                            @RequestParam secretFKeys: String,
                            @RequestParam(required = false) invoiceId: String?,
                            @RequestParam(required = false) gracePeriod: Int?,
                            @RequestBody invoicingCodes: List<InvoicingCodeDto>): Flux<InvoiceDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        val invoices = invoiceLogic.appendCodes(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId, userId, insuranceId, secretPatientKeys, InvoiceType.valueOf(type), MediumType.valueOf(sentMediumType),
                invoicingCodes.map { ic -> mapper.map(ic, InvoicingCode::class.java) }, invoiceId, gracePeriod)
        return invoices.map { mapper.map(it, InvoiceDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "removeCodes", value = "Gets all invoices for author at date")
    @PostMapping("/byauthor/{userId}/service/{serviceId}")
    fun removeCodes(@PathVariable userId: String,
                    @PathVariable serviceId: String,
                    @RequestParam secretFKeys: String,
                    @RequestBody tarificationIds: List<String>): Flux<InvoiceDto> {
        if (tarificationIds.isEmpty()) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot modify invoice: tarificationIds is empty")
        } else {
            val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()

            val invoices = invoiceLogic.removeCodes(userId, secretPatientKeys, serviceId, tarificationIds)

            return invoices.map { mapper.map(it, InvoiceDto::class.java) }.injectReactorContext()
        }
    }

    @ApiOperation(nickname = "findByAuthor", value = "Gets all invoices for author at date")
    @GetMapping("/byauthor/{hcPartyId}")
    suspend fun findByAuthor(@PathVariable hcPartyId: String,
                             @RequestParam(required = false) fromDate: Long?,
                             @RequestParam(required = false) toDate: Long?,
                             @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam("startKey", required = false) startKey: String?,
                             @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                             @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): PaginatedList<InvoiceDto> {
        val realLimit = limit ?: DEFAULT_LIMIT
        val sk: Array<String>
        var startKey1 = ""
        var startKey2 = ""
        if (startKey != null) {
            sk = startKey.split(',').toTypedArray()
            startKey1 = sk[0]
            startKey2 = sk[1]
        }
        val paginationOffset = PaginationOffset<List<String>>(listOf<String>(startKey1, startKey2), startDocumentId, 0, realLimit + 1) // fetch one more for nextKeyPair
        val findByAuthor = invoiceLogic.findByAuthor(hcPartyId, fromDate, toDate, paginationOffset)
        return findByAuthor.paginatedList<Invoice, InvoiceDto>(mapper, realLimit)
    }

    @ApiOperation(nickname = "findByHCPartyPatientSecretFKeys", value = "List invoices found By Healthcare Party and secret foreign patient keys.", notes = "Keys have to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<InvoiceDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        val elementList = invoiceLogic.listByHcPartyPatientSks(hcPartyId, secretPatientKeys)

        return elementList.map { element -> mapper.map(element, InvoiceDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "findDelegationsStubsByHCPartyPatientSecretFKeys", value = "List helement stubs found By Healthcare Party and secret foreign keys.", notes = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findDelegationsStubsByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        return invoiceLogic.listByHcPartyPatientSks(hcPartyId, secretPatientKeys).map { contact -> mapper.map(contact, IcureStubDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "listByHcPartyGroupId", value = "List invoices by groupId", notes = "Keys have to delimited by coma")
    @GetMapping("/byHcPartyGroupId/{hcPartyId}/{groupId}")
    fun listByHcPartyGroupId(@PathVariable hcPartyId: String, @PathVariable groupId: String): Flux<InvoiceDto> {
        val invoices = invoiceLogic.listByHcPartyGroupId(hcPartyId, groupId)
        return invoices.map { el -> mapper.map(el, InvoiceDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "listByHcPartySentMediumTypeInvoiceTypeSentDate", value = "List invoices by type, sent or unsent", notes = "Keys have to delimited by coma")
    @GetMapping("/byHcParty/{hcPartyId}/mediumType/{sentMediumType}/invoiceType/{invoiceType}/sent/{sent}")
    fun listByHcPartySentMediumTypeInvoiceTypeSentDate(@PathVariable hcPartyId: String,
                                                       @PathVariable sentMediumType: MediumType,
                                                       @PathVariable invoiceType: InvoiceType,
                                                       @PathVariable sent: Boolean,
                                                       @RequestParam(required = false) from: Long?,
                                                       @RequestParam(required = false) to: Long?): Flux<InvoiceDto> {
        val invoices = invoiceLogic.listByHcPartySentMediumTypeInvoiceTypeSentDate(hcPartyId, sentMediumType, invoiceType, sent, from, to)
        return invoices.map { el -> mapper.map(el, InvoiceDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "setInvoicesDelegations", value = "Update delegations in healthElements.", notes = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    suspend fun setInvoicesDelegations(@RequestBody stubs: List<IcureStubDto>) {
        val invoices = invoiceLogic.getInvoices(stubs.map { it.id }).toList()
        invoices.forEach { healthElement ->
            stubs.find { s -> s.id == healthElement.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> healthElement.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> healthElement.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> healthElement.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
            }
        }
        invoiceLogic.updateInvoices(invoices).collect()
    }

    @ApiOperation(nickname = "listByContactIds", value = "Gets all invoices for author at date")
    @PostMapping("/byCtcts")
    suspend fun listByContactIds(@RequestBody contactIds: ListOfIdsDto) =
            invoiceLogic.listByHcPartyContacts(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId, HashSet(contactIds.ids))
                    .map { mapper.map(it, InvoiceDto::class.java) }
                    .injectReactorContext()

    @ApiOperation(nickname = "listByRecipientsIds", value = "Gets all invoices for author at date")
    @GetMapping("/to/{recipientIds}")
    suspend fun listByRecipientsIds(@PathVariable recipientIds: String) =
            invoiceLogic.listByHcPartyRecipientIds(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId, recipientIds.split(',').toSet())
                    .map { mapper.map(it, InvoiceDto::class.java) }
                    .injectReactorContext()

    @ApiOperation(nickname = "listToInsurances", value = "Gets all invoices for author at date")
    @GetMapping("/toInsurances")
    suspend fun listToInsurances(@RequestParam(required = false) userIds: String?): Flux<InvoiceDto> {
        val users = if (userIds == null) userLogic.getAllEntities() else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.getAllEntityIds().toSet()
        return users
                .map { user ->
                    invoiceLogic.listByHcPartyRecipientIds(user.healthcarePartyId, insuranceIds)
                            .filter { iv -> user.id == iv.author }
                }
                .map { mapper.map(it, InvoiceDto::class.java) }
                .toList()
                .sortedWith(Comparator.comparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) }.thenComparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) })
                .asFlow()
                .injectReactorContext()
    }

    @ApiOperation(nickname = "listToInsurancesUnsent", value = "Gets all invoices for author at date")
    @GetMapping("/toInsurances/unsent")
    suspend fun listToInsurancesUnsent(@RequestParam(required = false) userIds: String?): Flux<InvoiceDto> {
        val users = if (userIds == null) userLogic.getAllEntities() else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.getAllEntityIds().toSet()
        return users
                .flatMapConcat { u ->
                    invoiceLogic.listByHcPartyRecipientIdsUnsent(u.healthcarePartyId, insuranceIds).filter { iv -> u.id == iv.author }
                }
                .map { mapper.map(it, InvoiceDto::class.java) }
                .toList()
                .sortedWith(Comparator.comparing<InvoiceDto, Long> { invoiceDto -> Optional.ofNullable(invoiceDto.invoiceDate).orElse(0L) })
                .asFlow()
                .injectReactorContext()
    }

    @ApiOperation(nickname = "listToPatients", value = "Gets all invoices for author at date")
    @GetMapping("/toPatients")
    suspend fun listToPatients(@RequestParam(required = false) hcPartyId: String?): Flow<InvoiceDto> {
        return invoiceLogic.listByHcPartyRecipientIds(hcPartyId
                ?: sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId,
                setOf<String?>(null)).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listToPatientsUnsent", value = "Gets all invoices for author at date")
    @GetMapping("/toPatients/unsent")
    suspend fun listToPatientsUnsent(@RequestParam(required = false) hcPartyId: String?): Flow<InvoiceDto> {
        return invoiceLogic.listByHcPartyRecipientIdsUnsent(hcPartyId
                ?: sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId,
                setOf<String?>(null)).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByIds", value = "Gets all invoices for author at date")
    @GetMapping("/byIds/{invoiceIds}")
    fun listByIds(@PathVariable invoiceIds: String): Flow<InvoiceDto> {
        return invoiceLogic.getInvoices(invoiceIds.split(',')).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByHcpartySendingModeStatusDate", value = "Get all invoices by author, by sending mode, by status and by date")
    @GetMapping("/byHcpartySendingModeStatusDate/{hcPartyId}")
    fun listByHcpartySendingModeStatusDate(@PathVariable hcPartyId: String,
                                           @RequestParam(required = false) sendingMode: String?,
                                           @RequestParam(required = false) status: String?,
                                           @RequestParam(required = false) from: Long?,
                                           @RequestParam(required = false) to: Long?): Flow<InvoiceDto> {
        return invoiceLogic.listByHcPartySendingModeStatus(hcPartyId, sendingMode, status, from, to).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByServiceIds", value = "Gets all invoices for author at date")
    @GetMapping("/byServiceIds/{serviceIds}")
    fun listByServiceIds(@PathVariable serviceIds: String): Flow<InvoiceDto> {
        return invoiceLogic.listByServiceIds(serviceIds.split(',').toSet()).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listAllHcpsByStatus", value = "Gets all invoices per status")
    @PostMapping("/allHcpsByStatus/{status}")
    fun listAllHcpsByStatus(@PathVariable status: String,
                            @RequestParam(required = false) from: Long?,
                            @RequestParam(required = false) to: Long?,
                            @RequestBody hcpIds: ListOfIdsDto): Flow<InvoiceDto> {
        return invoiceLogic.listAllHcpsByStatus(status, from, to, hcpIds.ids).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "getTarificationsCodesOccurences", value = "Get the list of all used tarifications frequencies in invoices")
    @GetMapping("/codes/{minOccurences}")
    suspend fun getTarificationsCodesOccurences(@PathVariable minOccurences: Long): List<LabelledOccurenceDto> {
        return invoiceLogic.getTarificationsCodesOccurences(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId, minOccurences).map { mapper.map(it, LabelledOccurenceDto::class.java) }
    }

    @ApiOperation(nickname = "filterBy", value = "Filter invoices for the current user (HcParty)", notes = "Returns a list of invoices along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    suspend fun filterBy(@RequestBody filterChain: FilterChain): Flow<InvoiceDto> {
        val invoices = invoiceLogic.filter(org.taktik.icure.dto.filter.chain.FilterChain(filterChain.filter as Filter<String, Invoice>, mapper.map(filterChain.predicate, Predicate::class.java)))

        return invoices.map { element -> mapper.map(element, InvoiceDto::class.java) }
    }
}
