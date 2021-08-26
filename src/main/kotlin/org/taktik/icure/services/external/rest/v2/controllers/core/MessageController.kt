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

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Splitter
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.MessageDto
import org.taktik.icure.services.external.rest.v2.dto.MessagesReadStatusUpdate
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v2.mapper.MessageMapper
import org.taktik.icure.services.external.rest.v2.mapper.StubMapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import kotlin.streams.toList


@FlowPreview
@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v2/message")
@Tag(name = "message")
class MessageController(
        private val messageLogic: MessageLogic,
        private val sessionLogic: AsyncSessionLogic,
        private val messageMapper: MessageMapper,
        private val delegationMapper: DelegationMapper,
        private val stubMapper: StubMapper,
        private val objectMapper: ObjectMapper
) {
    val DEFAULT_LIMIT = 1000
    private val messageToMessageDto = { it: Message -> messageMapper.map(it) }
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "Creates a message")
    @PostMapping
    fun createMessage(@RequestBody messageDto: MessageDto) = mono {
        messageLogic.createMessage(messageMapper.map(messageDto))?.let { messageMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message creation failed")
                        .also { logger.error(it.message) }
    }

    @Operation(summary = "Deletes a message delegation")
    @DeleteMapping("/{messageId}/delegate/{delegateId}")
    fun deleteDelegation(
            @PathVariable messageId: String,
            @PathVariable delegateId: String) = mono {
        val message = messageLogic.getMessage(messageId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message with ID: $messageId not found").also { logger.error(it.message) }

        messageLogic.modifyEntities(listOf(message.copy(delegations = message.delegations - delegateId))).firstOrNull()?.let { messageMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message delegation deletion failed").also { logger.error(it.message) }
    }

    @Operation(summary = "Deletes multiple messages")
    @PostMapping("/delete/batch")
    fun deleteMessages(@PathVariable messageIds: ListOfIdsDto): Flux<DocIdentifier> {
        return messageIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        messageLogic.deleteEntities(ids).injectReactorContext()
                    } catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Gets a message")
    @GetMapping("/{messageId}")
    fun getMessage(@PathVariable messageId: String) = mono {
        messageLogic.getMessage(messageId)?.let { messageMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")
                        .also { logger.error(it.message) }
    }

    @Operation(summary = "Get all messages for current HC Party and provided transportGuids")
    @PostMapping("/byTransportGuid/list")
    fun listMessagesByTransportGuids(@RequestParam("hcpId") hcpId: String, @RequestBody transportGuids: ListOfIdsDto) =
            messageLogic.getMessagesByTransportGuids(hcpId, transportGuids.ids.toSet()).map { messageMapper.map(it) }.injectReactorContext()

    @Operation(summary = "List messages found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findMessagesByHCPartyPatientForeignKeys(@RequestParam secretFKeys: String): Flux<MessageDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return messageLogic.listMessagesByHCPartySecretPatientKeys(secretPatientKeys)
                .map { contact -> messageMapper.map(contact) }
                .injectReactorContext()
    }

    @Operation(summary = "Get all messages (paginated) for current HC Party")
    @GetMapping
    fun findMessages(@RequestParam(required = false) startKey: String?,
                     @RequestParam(required = false) startDocumentId: String?,
                     @RequestParam(required = false) limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyList, startDocumentId, null, realLimit + 1)

        messageLogic.findForCurrentHcParty(paginationOffset).paginatedList(messageToMessageDto, realLimit)
    }

    @Operation(summary = "Get children messages of provided message")
    @GetMapping("/{messageId}/children")
    fun getChildrenMessages(@PathVariable messageId: String) =
            messageLogic.getMessageChildren(messageId).map { messageMapper.map(it) }.injectReactorContext()


    @Operation(summary = "Get children messages of provided message")
    @PostMapping("/children/batch")
    fun getChildrenMessagesOfList(@RequestBody parentIds: ListOfIdsDto) =
            messageLogic.getMessagesChildren(parentIds.ids)
                    .map { m -> m.stream().map { mm -> messageMapper.map(mm) }.toList().asFlow() }
                    .flattenConcat()
                    .injectReactorContext()

    @Operation(summary = "Get children messages of provided message")
    @PostMapping("byInvoiceId")
    fun listMessagesByInvoiceIds(@RequestBody ids: ListOfIdsDto) =
            messageLogic.listMessagesByInvoiceIds(ids.ids).map { messageMapper.map(it) }.injectReactorContext()

    @Operation(summary = "Get all messages (paginated) for current HC Party and provided transportGuid")
    @GetMapping("/byTransportGuid")
    fun findMessagesByTransportGuid(
            @RequestParam(required = false) transportGuid: String?,
            @RequestParam(required = false) received: Boolean?,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) hcpId: String?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyList, startDocumentId, null, realLimit + 1)
        val hcpId = hcpId ?: sessionLogic.getCurrentHealthcarePartyId()
        val messages = received?.takeIf { it }?.let { messageLogic.findMessagesByTransportGuidReceived(hcpId, transportGuid, paginationOffset) }
                ?: messageLogic.findMessagesByTransportGuid(hcpId, transportGuid, paginationOffset)
        messages.paginatedList(messageToMessageDto, realLimit)
    }

    @Operation(summary = "Get all messages starting by a prefix between two date")
    @GetMapping("/byTransportGuidSentDate")
    fun findMessagesByTransportGuidSentDate(
            @RequestParam(required = false) transportGuid: String,
            @RequestParam(required = false, value = "from") fromDate: Long,
            @RequestParam(required = false, value = "to") toDate: Long,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) hcpId: String?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyList, startDocumentId, null, realLimit + 1)
        messageLogic.findMessagesByTransportGuidSentDate(
                hcpId ?: sessionLogic.getCurrentHealthcarePartyId(),
                transportGuid,
                fromDate,
                toDate,
                paginationOffset
        ).paginatedList<Message, MessageDto>(messageToMessageDto, realLimit)
    }


    @Operation(summary = "Get all messages (paginated) for current HC Party and provided to address")
    @GetMapping("/byToAddress")
    fun findMessagesByToAddress(
            @RequestParam(required = false) toAddress: String,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) reverse: Boolean?,
            @RequestParam(required = false) hcpId: String?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.takeIf { it.isNotEmpty() }?.let { objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyElements, startDocumentId, null, realLimit + 1)
        val hcpId = hcpId ?: sessionLogic.getCurrentHealthcarePartyId()
        messageLogic.findMessagesByToAddress(hcpId, toAddress, paginationOffset, reverse).paginatedList<Message, MessageDto>(messageToMessageDto, realLimit)
    }

    @Operation(summary = "Get all messages (paginated) for current HC Party and provided from address")
    @GetMapping("/byFromAddress")
    fun findMessagesByFromAddress(
            @RequestParam(required = false) fromAddress: String,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) hcpId: String?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.takeIf { it.isNotEmpty() }?.let { objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyElements, startDocumentId, null, realLimit + 1)
        val hcpId = hcpId ?: sessionLogic.getCurrentHealthcarePartyId()
        messageLogic.findMessagesByFromAddress(hcpId, fromAddress, paginationOffset).paginatedList<Message, MessageDto>(messageToMessageDto, realLimit)
    }

    @Operation(summary = "Updates a message")
    @PutMapping
    fun modifyMessage(@RequestBody messageDto: MessageDto) = mono {
        if (messageDto.id == null) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
                    .also { logger.error(it.message) }
        }
        messageMapper.map(messageDto)
            messageLogic.modifyMessage(messageMapper.map(messageDto))?.let { messageMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
                        .also { logger.error(it.message) }
    }

    @Operation(summary = "Set status bits for given list of messages")
    @PutMapping("/status/{status}")
    fun setMessagesStatusBits(
            @PathVariable status: Int,
            @RequestBody messageIds: ListOfIdsDto) = messageLogic.setStatus(messageIds.ids, status).map { messageMapper.map(it) }.injectReactorContext()

    @Operation(summary = "Set read status for given list of messages")
    @PutMapping("/readstatus")
    fun setMessagesReadStatus(@RequestBody data: MessagesReadStatusUpdate) = flow {
        emitAll(messageLogic.setReadStatus(data.ids ?: listOf(), data.userId ?: sessionLogic.getCurrentUserId(), data.status
                ?: false, data.time ?: System.currentTimeMillis()).map { messageMapper.map(it) })
    }.injectReactorContext()

    @Operation(summary = "Adds a delegation to a message")
    @PutMapping("/{messageId}/delegate")
    fun newMessageDelegations(
            @PathVariable messageId: String,
            @RequestBody ds: List<DelegationDto>) = mono {
        messageLogic.addDelegations(messageId, ds.map { delegationMapper.map(it) })?.takeIf { it.delegations.isNotEmpty() }?.let { stubMapper.mapToStub(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
    }
}
