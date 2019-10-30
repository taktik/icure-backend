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
import ma.glasnost.orika.MapperFacade
import org.ektorp.ComplexKey
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.logic.InsuranceLogic
import org.taktik.icure.logic.InvoiceLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.logic.UserLogic
import org.taktik.icure.services.external.rest.v1.dto.*
import org.taktik.icure.services.external.rest.v1.dto.data.LabelledOccurenceDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoicingCodeDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import java.util.*

@RestController
@RequestMapping("/rest/v1/invoice")
@Api(tags = ["invoice"])
class InvoiceController(private val invoiceLogic: InvoiceLogic,
                        private val mapper: MapperFacade,
                        private val sessionLogic: SessionLogic,
                        private val insuranceLogic: InsuranceLogic,
                        private val userLogic: UserLogic,
                        private val uuidGenerator: UUIDGenerator) {

    @ApiOperation(nickname = "createInvoice", value = "Creates an invoice")
    @PostMapping
    fun createInvoice(@RequestBody invoiceDto: InvoiceDto): InvoiceDto {
        val invoice = invoiceLogic.createInvoice(mapper.map(invoiceDto, Invoice::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice creation failed")
        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "deleteInvoice", value = "Deletes an invoice")
    @DeleteMapping("/{invoiceId}")
    fun deleteInvoice(@PathVariable invoiceId: String): String {
        return invoiceLogic.deleteInvoice(invoiceId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog deletion failed")
    }

    @ApiOperation(nickname = "getInvoice", value = "Gets an invoice")
    @GetMapping("/{invoiceId}")
    fun getInvoice(@PathVariable invoiceId: String): InvoiceDto {
        val invoice = invoiceLogic.getInvoice(invoiceId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice fetching failed")
        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "getInvoices", value = "Gets an invoice")
    @PostMapping("/byIds")
    fun getInvoices(@RequestBody invoiceIds: ListOfIdsDto): List<InvoiceDto> {
        val invoices = invoiceLogic.getInvoices(invoiceIds.ids)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoices fetching failed")
        return invoices.map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "modifyInvoice", value = "Modifies an invoice")
    @PutMapping
    fun modifyInvoice(@RequestBody invoiceDto: InvoiceDto): InvoiceDto {
        val invoice = invoiceLogic.modifyInvoice(mapper.map(invoiceDto, Invoice::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "reassignInvoice", value = "Modifies an invoice")
    @PostMapping("/reassign")
    fun reassignInvoice(@RequestBody invoiceDto: InvoiceDto): InvoiceDto {
        val invoice = Invoice.reassignationInvoiceFromOtherInvoice(mapper.map(invoiceDto, Invoice::class.java), uuidGenerator)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

        return mapper.map(invoice, InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "newDelegations", value = "Adds a delegation to a invoice")
    @PutMapping("/{invoiceId}/delegate")
    fun newDelegations(@PathVariable invoiceId: String, @RequestBody ds: List<DelegationDto>): MessageDto {
        val invoice = invoiceLogic.addDelegations(invoiceId, ds.map { mapper.map(it, Delegation::class.java) })
        if (invoice != null && invoice.delegations != null && invoice.delegations.isNotEmpty()) {
            return mapper.map(invoice, MessageDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for invoice failed")
        }
    }

    @ApiOperation(nickname = "mergeTo", value = "Gets all invoices for author at date")
    @PostMapping("/mergeTo/{invoiceId}")
    fun mergeTo(@PathVariable invoiceId: String, @RequestBody ids: ListOfIdsDto): InvoiceDto {
        return mapper.map(invoiceLogic.mergeInvoices(sessionLogic.currentSessionContext.user.healthcarePartyId, invoiceLogic.getInvoices(ids.ids), invoiceLogic.getInvoice(invoiceId)), InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "validate", value = "Gets all invoices for author at date")
    @PostMapping("/validate/{invoiceId}")
    fun validate(@PathVariable invoiceId: String, @RequestParam scheme: String, @RequestParam forcedValue: String): InvoiceDto {
        return mapper.map(invoiceLogic.validateInvoice(sessionLogic.currentSessionContext.user.healthcarePartyId, invoiceLogic.getInvoice(invoiceId), scheme, forcedValue), InvoiceDto::class.java)
    }

    @ApiOperation(nickname = "appendCodes", value = "Gets all invoices for author at date")
    @PostMapping("/byauthor/{userId}/append/{type}/{sentMediumType}")
    fun appendCodes(@PathVariable userId: String,
                    @PathVariable type: String,
                    @PathVariable sentMediumType: String,
                    @RequestParam(required = false) insuranceId: String?,
                    @RequestParam secretFKeys: String,
                    @RequestParam(required = false) invoiceId: String?,
                    @RequestParam(required = false) gracePeriod: Int?,
                    @RequestBody invoicingCodes: List<InvoicingCodeDto>): List<InvoiceDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        val invoices = invoiceLogic.appendCodes(sessionLogic.currentSessionContext.user.healthcarePartyId, userId, insuranceId, secretPatientKeys, InvoiceType.valueOf(type), MediumType.valueOf(sentMediumType),
                invoicingCodes.map { ic -> mapper.map(ic, InvoicingCode::class.java) }, invoiceId, gracePeriod)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")
        return invoices.map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "removeCodes", value = "Gets all invoices for author at date")
    @PostMapping("/byauthor/{userId}/service/{serviceId}")
    fun removeCodes(@PathVariable userId: String,
                    @PathVariable serviceId: String,
                    @RequestParam secretFKeys: String,
                    @RequestBody tarificationIds: List<String>): List<InvoiceDto> {
        if (tarificationIds.isEmpty()) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot modify invoice: tarificationIds is empty")
        } else {
            val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()

            val invoices = invoiceLogic.removeCodes(userId, secretPatientKeys, serviceId, tarificationIds)
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

            return invoices.map { mapper.map(it, InvoiceDto::class.java) }
        }
    }

    @ApiOperation(nickname = "findByAuthor", value = "Gets all invoices for author at date")
    @GetMapping("/byauthor/{hcPartyId}")
    fun findByAuthor(@PathVariable hcPartyId: String,
                     @RequestParam(required = false) fromDate: Long?,
                     @RequestParam(required = false) toDate: Long?,
                     @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam("startKey", required = false) startKey: String?,
                     @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                     @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): InvoicePaginatedList {

        val sk: Array<String>
        var startKey1 = ""
        var startKey2: Long? = 0L
        if (startKey != null) {
            sk = startKey.split(',').toTypedArray()
            startKey1 = sk[0]
            startKey2 = java.lang.Long.parseLong(sk[1])
        }
        val paginationOffset = startKey?.let { PaginationOffset(ComplexKey.of(listOf(startKey1, startKey2)), startDocumentId, 0, limit) }
        val findByAuthor = invoiceLogic.findByAuthor(hcPartyId, fromDate, toDate, paginationOffset)
        return mapper.map(findByAuthor, InvoicePaginatedList::class.java)
    }

    @ApiOperation(nickname = "findByHCPartyPatientSecretFKeys", value = "List invoices found By Healthcare Party and secret foreign patient keys.", notes = "Keys have to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): List<InvoiceDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        val elementList = invoiceLogic.listByHcPartyPatientSks(hcPartyId, secretPatientKeys)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting the invoices failed. Please try again or read the server log.")

        return elementList.map { element -> mapper.map(element, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "findDelegationsStubsByHCPartyPatientSecretFKeys", value = "List helement stubs found By Healthcare Party and secret foreign keys.", notes = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findDelegationsStubsByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): List<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        return invoiceLogic.listByHcPartyPatientSks(hcPartyId, secretPatientKeys).map { contact -> mapper.map(contact, IcureStubDto::class.java) }
    }

    @ApiOperation(nickname = "listByHcPartyGroupId", value = "List invoices by groupId", notes = "Keys have to delimited by coma")
    @GetMapping("/byHcPartyGroupId/{hcPartyId}/{groupId}")
    fun listByHcPartyGroupId(@PathVariable hcPartyId: String, @PathVariable groupId: String): List<InvoiceDto> {
        val invoices = invoiceLogic.listByHcPartyGroupId(hcPartyId, groupId)
        return invoices.map { el -> mapper.map(el, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByHcPartySentMediumTypeInvoiceTypeSentDate", value = "List invoices by type, sent or unsent", notes = "Keys have to delimited by coma")
    @GetMapping("/byHcParty/{hcPartyId}/mediumType/{sentMediumType}/invoiceType/{invoiceType}/sent/{sent}")
    fun listByHcPartySentMediumTypeInvoiceTypeSentDate(@PathVariable hcPartyId: String,
                                                       @PathVariable sentMediumType: MediumType,
                                                       @PathVariable invoiceType: InvoiceType,
                                                       @PathVariable sent: Boolean,
                                                       @RequestParam(required = false) from: Long?,
                                                       @RequestParam(required = false) to: Long?): List<InvoiceDto> {
        val invoices = invoiceLogic.listByHcPartySentMediumTypeInvoiceTypeSentDate(hcPartyId, sentMediumType, invoiceType, sent, from, to)
        return invoices.map { el -> mapper.map(el, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "setInvoicesDelegations", value = "Update delegations in healthElements.", notes = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setInvoicesDelegations(stubs: List<IcureStubDto>) {
        val invoices = invoiceLogic.getInvoices(stubs.map { it.id })
        invoices.forEach { healthElement ->
            stubs.find { s -> s.id == healthElement.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> healthElement.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> healthElement.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> healthElement.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
            }
        }
        invoiceLogic.updateInvoices(invoices)
    }

    @ApiOperation(nickname = "listByContactIds", value = "Gets all invoices for author at date")
    @PostMapping("/byCtcts")
    fun listByContactIds(contactIds: ListOfIdsDto): List<InvoiceDto> {
        return invoiceLogic.listByHcPartyContacts(sessionLogic.currentSessionContext.user.healthcarePartyId, HashSet(contactIds.ids)).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByRecipientsIds", value = "Gets all invoices for author at date")
    @GetMapping("/to/{recipientIds}")
    fun listByRecipientsIds(@PathVariable recipientIds: String): List<InvoiceDto> {
        return invoiceLogic.listByHcPartyRecipientIds(sessionLogic.currentSessionContext.user.healthcarePartyId, recipientIds.split(',').toSet()).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listToInsurances", value = "Gets all invoices for author at date")
    @GetMapping("/toInsurances")
    fun listToInsurances(@RequestParam(required = false) userIds: String?): List<InvoiceDto> {
        val users = if (userIds == null) userLogic.allEntities else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.allEntityIds.toSet()
        return users
                .flatMap { u -> invoiceLogic.listByHcPartyRecipientIds(u.healthcarePartyId, insuranceIds).filter { iv -> u.id == iv.author } }
                .map { i -> mapper.map(i, InvoiceDto::class.java) }
                .sortedWith(Comparator.comparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) }.thenComparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) })
    }

    @ApiOperation(nickname = "listToInsurancesUnsent", value = "Gets all invoices for author at date")
    @GetMapping("/toInsurances/unsent")
    fun listToInsurancesUnsent(@RequestParam(required = false) userIds: String?): List<InvoiceDto> {
        val users = if (userIds == null) userLogic.allEntities else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.allEntityIds.toSet()
        return users
                .flatMap { u ->
                    invoiceLogic.listByHcPartyRecipientIdsUnsent(u.healthcarePartyId, insuranceIds)
                            .filter { iv -> u.id == iv.author }
                }
                .map { mapper.map(it, InvoiceDto::class.java) }
                .sortedWith(Comparator.comparing<InvoiceDto, Long> { invoiceDto -> Optional.ofNullable(invoiceDto.invoiceDate).orElse(0L) })
    }

    @ApiOperation(nickname = "listToPatients", value = "Gets all invoices for author at date")
    @GetMapping("/toPatients")
    fun listToPatients(@RequestParam(required = false) hcPartyId: String?): List<InvoiceDto> {
        return invoiceLogic.listByHcPartyRecipientIds(hcPartyId
                ?: sessionLogic.currentSessionContext.user.healthcarePartyId,
                setOf(null)).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listToPatientsUnsent", value = "Gets all invoices for author at date")
    @GetMapping("/toPatients/unsent")
    fun listToPatientsUnsent(@RequestParam(required = false) hcPartyId: String?): List<InvoiceDto> {
        return invoiceLogic.listByHcPartyRecipientIdsUnsent(hcPartyId
                ?: sessionLogic.currentSessionContext.user.healthcarePartyId,
                setOf(null)).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByIds", value = "Gets all invoices for author at date")
    @GetMapping("/byIds/{invoiceIds}")
    fun listByIds(@PathVariable invoiceIds: String): List<InvoiceDto> {
        return invoiceLogic.getInvoices(invoiceIds.split(',')).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByHcpartySendingModeStatusDate", value = "Get all invoices by author, by sending mode, by status and by date")
    @GetMapping("/byHcpartySendingModeStatusDate/{hcPartyId}")
    fun listByHcpartySendingModeStatusDate(@PathVariable hcPartyId: String,
                                           @RequestParam(required = false) sendingMode: String?,
                                           @RequestParam(required = false) status: String?,
                                           @RequestParam(required = false) from: Long?,
                                           @RequestParam(required = false) to: Long?): List<InvoiceDto> {
        return invoiceLogic.listByHcPartySendingModeStatus(hcPartyId, sendingMode, status, from, to).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listByServiceIds", value = "Gets all invoices for author at date")
    @GetMapping("/byServiceIds/{serviceIds}")
    fun listByServiceIds(@PathVariable serviceIds: String): List<InvoiceDto> {
        return invoiceLogic.listByServiceIds(serviceIds.split(',').toSet()).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "listAllHcpsByStatus", value = "Gets all invoices per status")
    @PostMapping("/allHcpsByStatus/{status}")
    fun listAllHcpsByStatus(@PathVariable status: String,
                            @RequestParam(required = false) from: Long?,
                            @RequestParam(required = false) to: Long?,
                            @RequestBody hcpIds: ListOfIdsDto): List<InvoiceDto> {
        return invoiceLogic.listAllHcpsByStatus(status, from, to, hcpIds.ids).map { mapper.map(it, InvoiceDto::class.java) }
    }

    @ApiOperation(nickname = "getTarificationsCodesOccurences", value = "Get the list of all used tarifications frequencies in invoices")
    @GetMapping("/codes/{minOccurences}")
    fun getTarificationsCodesOccurences(@PathVariable minOccurences: Long): List<LabelledOccurenceDto> {
        return invoiceLogic.getTarificationsCodesOccurences(sessionLogic.currentSessionContext.user.healthcarePartyId, minOccurences).map { mapper.map(it, LabelledOccurenceDto::class.java) }
    }

    @ApiOperation(nickname = "filterBy", value = "Filter invoices for the current user (HcParty)", notes = "Returns a list of invoices along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterBy(@RequestBody filterChain: FilterChain): List<InvoiceDto> {
        val invoices = invoiceLogic.filter(org.taktik.icure.dto.filter.chain.FilterChain(filterChain.filter as Filter<String, Invoice>, mapper.map(filterChain.predicate, Predicate::class.java)))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing and filtering of invoices failed.")

        return invoices.map { element -> mapper.map(element, InvoiceDto::class.java) }
    }
}
