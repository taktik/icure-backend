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

package org.taktik.icure.services.external.rest.v1.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.reactor.mono
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
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.couchdb.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.InvoiceDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.data.LabelledOccurenceDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoicingCodeDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v1.mapper.InvoiceMapper
import org.taktik.icure.services.external.rest.v1.mapper.StubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.InvoicingCodeMapper
import org.taktik.icure.services.external.rest.v1.mapper.filter.FilterChainMapper
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import reactor.core.publisher.Flux
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/invoice")
@Tag(name = "invoice")
class InvoiceController(
        private val invoiceLogic: InvoiceLogic,
        private val sessionLogic: AsyncSessionLogic,
        private val insuranceLogic: InsuranceLogic,
        private val userLogic: UserLogic,
        private val uuidGenerator: UUIDGenerator,
        private val invoiceMapper: InvoiceMapper,
        private val filterChainMapper: FilterChainMapper,
        private val delegationMapper: DelegationMapper,
        private val invoicingCodeMapper: InvoicingCodeMapper,
        private val stubMapper: StubMapper
) {

    private val invoiceToInvoiceDto = { it: Invoice -> invoiceMapper.map(it) }

    private val DEFAULT_LIMIT = 1000

    @Operation(summary = "Creates an invoice")
    @PostMapping
    fun createInvoice(@RequestBody invoiceDto: InvoiceDto) = mono {
        val invoice = invoiceLogic.createInvoice(invoiceMapper.map(invoiceDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice creation failed")
        invoiceMapper.map(invoice)
    }

    @Operation(summary = "Deletes an invoice")
    @DeleteMapping("/{invoiceId}")
    fun deleteInvoice(@PathVariable invoiceId: String) = mono {
        invoiceLogic.deleteInvoice(invoiceId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice deletion failed")
    }

    @Operation(summary = "Gets an invoice")
    @GetMapping("/{invoiceId}")
    fun getInvoice(@PathVariable invoiceId: String) = mono {
        val invoice = invoiceLogic.getInvoice(invoiceId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice fetching failed")
        invoiceMapper.map(invoice)
    }

    @Operation(summary = "Gets an invoice")
    @PostMapping("/byIds")
    fun getInvoices(@RequestBody invoiceIds: ListOfIdsDto) = invoiceLogic.getInvoices(invoiceIds.ids)
            .map { invoiceMapper.map(it) }
            .injectReactorContext()

    @Operation(summary = "Modifies an invoice")
    @PutMapping
    fun modifyInvoice(@RequestBody invoiceDto: InvoiceDto) = mono {
        val invoice = invoiceLogic.modifyInvoice(invoiceMapper.map(invoiceDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

        invoiceMapper.map(invoice)
    }

    @Operation(summary = "Modifies an invoice")
    @PostMapping("/reassign")
    fun reassignInvoice(@RequestBody invoiceDto: InvoiceDto) = mono {
        val invoice = invoiceMapper.map(invoiceDto).let { it.reassign(it.invoicingCodes, uuidGenerator) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

        invoiceMapper.map(invoice)
    }

    @Operation(summary = "Adds a delegation to a invoice")
    @PutMapping("/{invoiceId}/delegate")
    fun newInvoiceDelegations(@PathVariable invoiceId: String, @RequestBody ds: List<DelegationDto>) = mono {
        val invoice = invoiceLogic.addDelegations(invoiceId, ds.map { delegationMapper.map(it) })
        if (invoice?.delegations != null && invoice.delegations.isNotEmpty()) {
            invoiceMapper.map(invoice)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for invoice failed")
        }
    }

    @Operation(summary = "Gets all invoices for author at date")
    @PostMapping("/mergeTo/{invoiceId}")
    fun mergeTo(@PathVariable invoiceId: String, @RequestBody ids: ListOfIdsDto) = mono {
        invoiceMapper.map(invoiceLogic.mergeInvoices(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, invoiceLogic.getInvoices(ids.ids).toList(), invoiceLogic.getInvoice(invoiceId))
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice $invoiceId not found"))
    }

    @Operation(summary = "Gets all invoices for author at date")
    @PostMapping("/validate/{invoiceId}")
    fun validate(@PathVariable invoiceId: String, @RequestParam scheme: String, @RequestParam forcedValue: String) = mono {
        invoiceLogic.validateInvoice(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, invoiceLogic.getInvoice(invoiceId), scheme, forcedValue)?.let { invoiceMapper.map(it) }
    }

    @Operation(summary = "Gets all invoices for author at date")
    @PostMapping("/byauthor/{userId}/append/{type}/{sentMediumType}")
    fun appendCodes(@PathVariable userId: String,
                            @PathVariable type: String,
                            @PathVariable sentMediumType: String,
                            @RequestParam secretFKeys: String,
                            @RequestParam(required = false) insuranceId: String?,
                            @RequestParam(required = false) invoiceId: String?,
                            @RequestParam(required = false) gracePeriod: Int?,
                            @RequestBody invoicingCodes: List<InvoicingCodeDto>): Flux<InvoiceDto> = flow{
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        val invoices = invoiceLogic.appendCodes(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, userId, insuranceId, secretPatientKeys, InvoiceType.valueOf(type), MediumType.valueOf(sentMediumType),
                invoicingCodes.map { ic -> invoicingCodeMapper.map(ic) }, invoiceId, gracePeriod)
        emitAll( invoices.map { invoiceMapper.map(it) })
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
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

            return invoices.map { invoiceMapper.map(it) }.injectReactorContext()
        }
    }

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/byauthor/{hcPartyId}")
    fun findByAuthor(@PathVariable hcPartyId: String,
                     @RequestParam(required = false) fromDate: Long?,
                     @RequestParam(required = false) toDate: Long?,
                     @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam("startKey", required = false) startKey: String?,
                     @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                     @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {
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
        findByAuthor.paginatedList<Invoice, InvoiceDto>(invoiceToInvoiceDto, realLimit)
    }

    @Operation(summary = "List invoices found By Healthcare Party and secret foreign patient keys.", description = "Keys have to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findInvoicesByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<InvoiceDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        val elementList = invoiceLogic.listByHcPartyPatientSks(hcPartyId, secretPatientKeys)

        return elementList.map { element -> invoiceMapper.map(element) }.injectReactorContext()
    }

    @Operation(summary = "List helement stubs found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findInvoicesDelegationsStubsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        return invoiceLogic.listByHcPartyPatientSks(hcPartyId, secretPatientKeys).map { invoice -> stubMapper.mapToStub(invoice) }.injectReactorContext()
    }

    @Operation(summary = "List invoices by groupId", description = "Keys have to delimited by coma")
    @GetMapping("/byHcPartyGroupId/{hcPartyId}/{groupId}")
    fun listByHcPartyGroupId(@PathVariable hcPartyId: String, @PathVariable groupId: String): Flux<InvoiceDto> {
        val invoices = invoiceLogic.listByHcPartyGroupId(hcPartyId, groupId)
        return invoices.map { el -> invoiceMapper.map(el) }.injectReactorContext()
    }

    @Operation(summary = "List invoices by type, sent or unsent", description = "Keys have to delimited by coma")
    @GetMapping("/byHcParty/{hcPartyId}/mediumType/{sentMediumType}/invoiceType/{invoiceType}/sent/{sent}")
    fun listByHcPartySentMediumTypeInvoiceTypeSentDate(@PathVariable hcPartyId: String,
                                                       @PathVariable sentMediumType: MediumType,
                                                       @PathVariable invoiceType: InvoiceType,
                                                       @PathVariable sent: Boolean,
                                                       @RequestParam(required = false) from: Long?,
                                                       @RequestParam(required = false) to: Long?): Flux<InvoiceDto> {
        val invoices = invoiceLogic.listByHcPartySentMediumTypeInvoiceTypeSentDate(hcPartyId, sentMediumType, invoiceType, sent, from, to)
        return invoices.map { el -> invoiceMapper.map(el) }.injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setInvoicesDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val invoices = invoiceLogic.getInvoices(stubs.map { it.id }).map { invoice ->
            stubs.find { s -> s.id == invoice.id }?.let { stub ->
                invoice.copy(
                        delegations = invoice.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = invoice.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = invoice.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels }
                )
            } ?: invoice
        }.toList()
        emitAll(invoiceLogic.updateInvoices(invoices).map { stubMapper.mapToStub(it) })
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @PostMapping("/byCtcts")
    fun listByContactIds(@RequestBody contactIds: ListOfIdsDto) = flow {
        emitAll(
                invoiceLogic.listByHcPartyContacts(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, HashSet(contactIds.ids))
                        .map { invoiceMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/to/{recipientIds}")
    fun listByRecipientsIds(@PathVariable recipientIds: String) = flow {
        emitAll(
                invoiceLogic.listByHcPartyRecipientIds(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, recipientIds.split(',').toSet())
                        .map { invoiceMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toInsurances")
    fun listToInsurances(@RequestParam(required = false) userIds: String?): Flux<InvoiceDto> = flow<InvoiceDto>{
        val users = if (userIds == null) userLogic.getAllEntities() else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.getAllEntityIds().toSet()
        users
                .flatMapConcat { user -> invoiceLogic.listByHcPartyRecipientIds(user.healthcarePartyId!!, insuranceIds).filter { iv -> user.id == iv.author } }
                .map { invoiceMapper.map(it) }
                .toList()
                .sortedWith(Comparator.comparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) }.thenComparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) })
                .forEach { emit(it) }
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toInsurances/unsent")
    fun listToInsurancesUnsent(@RequestParam(required = false) userIds: String?): Flux<InvoiceDto>  = flow{
        val users = if (userIds == null) userLogic.getAllEntities() else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.getAllEntityIds().toSet()
        users
                .flatMapConcat { u ->
                    invoiceLogic.listByHcPartyRecipientIdsUnsent(u.healthcarePartyId!!, insuranceIds).filter { iv -> u.id == iv.author }
                }
                .map { invoiceMapper.map(it) }
                .toList()
                .sortedWith(Comparator.comparing<InvoiceDto, Long> { invoiceDto -> Optional.ofNullable(invoiceDto.invoiceDate).orElse(0L) })
                .forEach { emit(it) }
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toPatients")
    fun listToPatients(@RequestParam(required = false) hcPartyId: String?): Flux<InvoiceDto> = flow{
        emitAll(
                invoiceLogic.listByHcPartyRecipientIds(hcPartyId
                        ?: sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!,
                        setOf<String?>(null)).map { invoiceMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toPatients/unsent")
    fun listToPatientsUnsent(@RequestParam(required = false) hcPartyId: String?): Flux<InvoiceDto> = flow{
        emitAll(
                invoiceLogic.listByHcPartyRecipientIdsUnsent(hcPartyId
                        ?: sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!,
                        setOf<String?>(null)).map { invoiceMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/byIds/{invoiceIds}")
    fun listByIds(@PathVariable invoiceIds: String): Flux<InvoiceDto> {
        return invoiceLogic.getInvoices(invoiceIds.split(',')).map { invoiceMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Get all invoices by author, by sending mode, by status and by date")
    @GetMapping("/byHcpartySendingModeStatusDate/{hcPartyId}")
    fun listByHcpartySendingModeStatusDate(@PathVariable hcPartyId: String,
                                           @RequestParam(required = false) sendingMode: String?,
                                           @RequestParam(required = false) status: String?,
                                           @RequestParam(required = false) from: Long?,
                                           @RequestParam(required = false) to: Long?): Flux<InvoiceDto> {
        return invoiceLogic.listByHcPartySendingModeStatus(hcPartyId, sendingMode, status, from, to).map { invoiceMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/byServiceIds/{serviceIds}")
    fun listByServiceIds(@PathVariable serviceIds: String): Flux<InvoiceDto> {
        return invoiceLogic.listByServiceIds(serviceIds.split(',').toSet()).map { invoiceMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets all invoices per status")
    @PostMapping("/allHcpsByStatus/{status}")
    fun listAllHcpsByStatus(@PathVariable status: String,
                            @RequestParam(required = false) from: Long?,
                            @RequestParam(required = false) to: Long?,
                            @RequestBody hcpIds: ListOfIdsDto): Flux<InvoiceDto> {
        return invoiceLogic.listAllHcpsByStatus(status, from, to, hcpIds.ids).map { invoiceMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Get the list of all used tarifications frequencies in invoices")
    @GetMapping("/codes/{minOccurences}")
    fun getTarificationsCodesOccurences(@PathVariable minOccurences: Long) = mono {
        invoiceLogic.getTarificationsCodesOccurences(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, minOccurences).map { LabelledOccurenceDto(it.label, it.occurence) }
    }

    @Operation(summary = "Filter invoices for the current user (HcParty)", description = "Returns a list of invoices along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterInvoicesBy(@RequestBody filterChain: FilterChain<Invoice>): Flux<InvoiceDto> {
        val invoices = invoiceLogic.filter(filterChainMapper.map(filterChain))
        return invoices.map { element -> invoiceMapper.map(element) }.injectReactorContext()
    }
}
