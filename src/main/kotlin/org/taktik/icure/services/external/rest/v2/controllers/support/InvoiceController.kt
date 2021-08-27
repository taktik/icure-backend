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

package org.taktik.icure.services.external.rest.v2.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.services.external.rest.v2.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v2.dto.InvoiceDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.data.LabelledOccurenceDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v2.dto.embed.InvoicingCodeDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v2.mapper.InvoiceV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.StubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.InvoicingCodeV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.filter.FilterChainV2Mapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
@RestController("invoiceControllerV2")
@RequestMapping("/rest/v2/invoice")
@Tag(name = "invoice")
class InvoiceController(
        private val invoiceLogic: InvoiceLogic,
        private val sessionLogic: AsyncSessionLogic,
        private val insuranceLogic: InsuranceLogic,
        private val userLogic: UserLogic,
        private val uuidGenerator: UUIDGenerator,
        private val invoiceV2Mapper: InvoiceV2Mapper,
        private val filterChainV2Mapper: FilterChainV2Mapper,
        private val delegationV2Mapper: DelegationV2Mapper,
        private val invoicingCodeV2Mapper: InvoicingCodeV2Mapper,
        private val stubV2Mapper: StubV2Mapper
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val invoiceToInvoiceDto = { it: Invoice -> invoiceV2Mapper.map(it) }

    private val DEFAULT_LIMIT = 1000

    @Operation(summary = "Creates an invoice")
    @PostMapping
    fun createInvoice(@RequestBody invoiceDto: InvoiceDto) = mono {
        val invoice = invoiceLogic.createInvoice(invoiceV2Mapper.map(invoiceDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice creation failed")
        invoiceV2Mapper.map(invoice)
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
        invoiceV2Mapper.map(invoice)
    }

    @Operation(summary = "Gets an invoice")
    @PostMapping("/byIds")
    fun getInvoices(@RequestBody invoiceIds: ListOfIdsDto) = invoiceLogic.getInvoices(invoiceIds.ids)
            .map { invoiceV2Mapper.map(it) }
            .injectReactorContext()

    @Operation(summary = "Modifies an invoice")
    @PutMapping
    fun modifyInvoice(@RequestBody invoiceDto: InvoiceDto) = mono {
        val invoice = invoiceLogic.modifyInvoice(invoiceV2Mapper.map(invoiceDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invoice modification failed")

        invoiceV2Mapper.map(invoice)
    }

    @Operation(summary = "Modifies an invoice")
    @PostMapping("/reassign")
    fun reassignInvoice(@RequestBody invoiceDto: InvoiceDto) = mono {
        val invoice = invoiceV2Mapper.map(invoiceDto).let { it.reassign(it.invoicingCodes, uuidGenerator) }

        invoiceV2Mapper.map(invoice)
    }

    @Operation(summary = "Adds a delegation to a invoice")
    @PutMapping("/{invoiceId}/delegate")
    fun newInvoiceDelegations(@PathVariable invoiceId: String, @RequestBody ds: List<DelegationDto>) = mono {
        val invoice = invoiceLogic.addDelegations(invoiceId, ds.map { delegationV2Mapper.map(it) })
        if (invoice?.delegations != null && invoice.delegations.isNotEmpty()) {
            invoiceV2Mapper.map(invoice)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for invoice failed")
        }
    }

    @Operation(summary = "Gets all invoices for author at date")
    @PostMapping("/mergeTo/{invoiceId}")
    fun mergeTo(@PathVariable invoiceId: String, @RequestBody ids: ListOfIdsDto) = mono {
        invoiceV2Mapper.map(invoiceLogic.mergeInvoices(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, invoiceLogic.getInvoices(ids.ids).toList(), invoiceLogic.getInvoice(invoiceId))
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice $invoiceId not found"))
    }

    @Operation(summary = "Gets all invoices for author at date")
    @PostMapping("/validate/{invoiceId}")
    fun validate(@PathVariable invoiceId: String, @RequestParam scheme: String, @RequestParam forcedValue: String) = mono {
        invoiceLogic.validateInvoice(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, invoiceLogic.getInvoice(invoiceId), scheme, forcedValue)?.let { invoiceV2Mapper.map(it) }
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
                invoicingCodes.map { ic -> invoicingCodeV2Mapper.map(ic) }, invoiceId, gracePeriod)
        emitAll( invoices.map { invoiceV2Mapper.map(it) })
    }.injectReactorContext()

    @Operation(summary = "Remove an invoice of an user")
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

            return invoices.map { invoiceV2Mapper.map(it) }.injectReactorContext()
        }
    }

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/byauthor/{hcPartyId}")
    fun listInvoicesByAuthor(@PathVariable hcPartyId: String,
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
        val findByAuthor = invoiceLogic.findInvoicesByAuthor(hcPartyId, fromDate, toDate, paginationOffset)
        findByAuthor.paginatedList<Invoice, InvoiceDto>(invoiceToInvoiceDto, realLimit)
    }

    @Operation(summary = "List invoices found By Healthcare Party and secret foreign patient keys.", description = "Keys have to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun listInvoicesByHCPartyAndPatientForeignKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<InvoiceDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        val elementList = invoiceLogic.listInvoicesByHcPartyAndPatientSks(hcPartyId, secretPatientKeys)

        return elementList.map { element -> invoiceV2Mapper.map(element) }.injectReactorContext()
    }

    @Operation(summary = "List helement stubs found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun listInvoicesDelegationsStubsByHCPartyAndPatientForeignKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }.toSet()
        return invoiceLogic.listInvoicesByHcPartyAndPatientSks(hcPartyId, secretPatientKeys).map { invoice -> stubV2Mapper.mapToStub(invoice) }.injectReactorContext()
    }

    @Operation(summary = "List invoices by groupId", description = "Keys have to delimited by coma")
    @GetMapping("/byHcPartyGroupId/{hcPartyId}/{groupId}")
    fun listInvoicesByHcPartyAndGroupId(@PathVariable hcPartyId: String, @PathVariable groupId: String): Flux<InvoiceDto> {
        val invoices = invoiceLogic.listInvoicesByHcPartyAndGroupId(hcPartyId, groupId)
        return invoices.map { el -> invoiceV2Mapper.map(el) }.injectReactorContext()
    }

    @Operation(summary = "List invoices by type, sent or unsent", description = "Keys have to delimited by coma")
    @GetMapping("/byHcParty/{hcPartyId}/mediumType/{sentMediumType}/invoiceType/{invoiceType}/sent/{sent}")
    fun listInvoicesByHcPartySentMediumTypeInvoiceTypeSentDate(@PathVariable hcPartyId: String,
                                                       @PathVariable sentMediumType: MediumType,
                                                       @PathVariable invoiceType: InvoiceType,
                                                       @PathVariable sent: Boolean,
                                                       @RequestParam(required = false) from: Long?,
                                                       @RequestParam(required = false) to: Long?): Flux<InvoiceDto> {
        val invoices = invoiceLogic.listInvoicesByHcPartySentMediumTypeInvoiceTypeSentDate(hcPartyId, sentMediumType, invoiceType, sent, from, to)
        return invoices.map { el -> invoiceV2Mapper.map(el) }.injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setInvoicesDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val invoices = invoiceLogic.getInvoices(stubs.map { it.id }).map { invoice ->
            stubs.find { s -> s.id == invoice.id }?.let { stub ->
                invoice.copy(
                        delegations = invoice.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = invoice.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = invoice.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels }
                )
            } ?: invoice
        }.toList()
        emitAll(invoiceLogic.modifyInvoices(invoices).map { stubV2Mapper.mapToStub(it) })
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @PostMapping("/byContacts")
    fun listInvoicesByContactIds(@RequestBody contactIds: ListOfIdsDto) = flow {
        emitAll(
                invoiceLogic.listInvoicesByHcPartyContacts(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, HashSet(contactIds.ids))
                        .map { invoiceV2Mapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/to/{recipientIds}")
    fun listInvoicesByRecipientsIds(@PathVariable recipientIds: String) = flow {
        emitAll(
                invoiceLogic.listInvoicesByHcPartyAndRecipientIds(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, recipientIds.split(',').toSet())
                        .map { invoiceV2Mapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toInsurances")
    fun listToInsurances(@RequestParam(required = false) userIds: String?): Flux<InvoiceDto> = flow<InvoiceDto>{
        val users = if (userIds == null) userLogic.getEntities() else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.getEntitiesIds().toSet()
        users
                .flatMapConcat { user -> invoiceLogic.listInvoicesByHcPartyAndRecipientIds(user.healthcarePartyId!!, insuranceIds).filter { iv -> user.id == iv.author } }
                .map { invoiceV2Mapper.map(it) }
                .toList()
                .sortedWith(Comparator.comparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) }.thenComparing { iv: InvoiceDto -> Optional.ofNullable(iv.sentDate).orElse(0L) })
                .forEach { emit(it) }
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toInsurances/unsent")
    fun listToInsurancesUnsent(@RequestParam(required = false) userIds: String?): Flux<InvoiceDto>  = flow{
        val users = if (userIds == null) userLogic.getEntities() else userLogic.getUsers(userIds.split(','))
        val insuranceIds = insuranceLogic.getEntitiesIds().toSet()
        users
                .flatMapConcat { u ->
                    invoiceLogic.listInvoicesByHcPartyAndRecipientIdsUnsent(u.healthcarePartyId!!, insuranceIds).filter { iv -> u.id == iv.author }
                }
                .map { invoiceV2Mapper.map(it) }
                .toList()
                .sortedWith(Comparator.comparing<InvoiceDto, Long> { invoiceDto -> Optional.ofNullable(invoiceDto.invoiceDate).orElse(0L) })
                .forEach { emit(it) }
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toPatients")
    fun listToPatients(@RequestParam(required = false) hcPartyId: String?): Flux<InvoiceDto> = flow{
        emitAll(
                invoiceLogic.listInvoicesByHcPartyAndRecipientIds(hcPartyId
                        ?: sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!,
                        setOf<String?>(null)).map { invoiceV2Mapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/toPatients/unsent")
    fun listToPatientsUnsent(@RequestParam(required = false) hcPartyId: String?): Flux<InvoiceDto> = flow{
        emitAll(
                invoiceLogic.listInvoicesByHcPartyAndRecipientIdsUnsent(hcPartyId
                        ?: sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!,
                        setOf<String?>(null)).map { invoiceV2Mapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/byIds/{invoiceIds}")
    fun listInvoicesByIds(@PathVariable invoiceIds: String): Flux<InvoiceDto> {
        return invoiceLogic.getInvoices(invoiceIds.split(',')).map { invoiceV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Get all invoices by author, by sending mode, by status and by date")
    @GetMapping("/byHcpartySendingModeStatusDate/{hcPartyId}")
    fun listInvoicesByHcpartySendingModeStatusDate(@PathVariable hcPartyId: String,
                                           @RequestParam(required = false) sendingMode: String?,
                                           @RequestParam(required = false) status: String?,
                                           @RequestParam(required = false) from: Long?,
                                           @RequestParam(required = false) to: Long?): Flux<InvoiceDto> {
        return invoiceLogic.listInvoicesByHcPartySendingModeStatus(hcPartyId, sendingMode, status, from, to).map { invoiceV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets all invoices for author at date")
    @GetMapping("/byServiceIds/{serviceIds}")
    fun listInvoicesByServiceIds(@PathVariable serviceIds: String): Flux<InvoiceDto> {
        return invoiceLogic.listInvoicesByServiceIds(serviceIds.split(',').toSet()).map { invoiceV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets all invoices per status")
    @PostMapping("/allHcpsByStatus/{status}")
    fun listAllHcpsByStatus(@PathVariable status: String,
                            @RequestParam(required = false) from: Long?,
                            @RequestParam(required = false) to: Long?,
                            @RequestBody hcpIds: ListOfIdsDto): Flux<InvoiceDto> {
        return invoiceLogic.listInvoicesHcpsByStatus(status, from, to, hcpIds.ids).map { invoiceV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Get the list of all used tarifications frequencies in invoices")
    @GetMapping("/codes/{minOccurences}")
    fun getTarificationsCodesOccurences(@PathVariable minOccurences: Long) = mono {
        invoiceLogic.getTarificationsCodesOccurences(sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, minOccurences).map { LabelledOccurenceDto(it.label, it.occurence) }
    }

    @Operation(summary = "Filter invoices for the current user (HcParty)", description = "Returns a list of invoices along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterInvoicesBy(@RequestBody filterChain: FilterChain<Invoice>): Flux<InvoiceDto> {
        val invoices = invoiceLogic.filter(filterChainV2Mapper.map(filterChain))
        return invoices.map { element -> invoiceV2Mapper.map(element) }.injectReactorContext()
    }

    @Operation(summary = "Modify a batch of invoices", description = "Returns the modified invoices.")
    @PutMapping("/batch")
    fun modifyInvoices(@RequestBody invoiceDtos: List<InvoiceDto>): Flux<InvoiceDto> {
        return try {
            invoiceLogic.modifyEntities(invoiceDtos.map { invoiceV2Mapper.map(it) }).map { invoiceV2Mapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Create a batch of invoices", description = "Returns the created invoices.")
    @PostMapping("/batch")
    fun createInvoices(@RequestBody invoiceDtos: List<InvoiceDto>): Flux<InvoiceDto> {
        return try {
            invoiceLogic.createEntities(invoiceDtos.map { invoiceV2Mapper.map(it) }).map { invoiceV2Mapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }


}
