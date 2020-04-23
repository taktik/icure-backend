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

import com.google.common.base.Splitter
import com.google.gson.Gson
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
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
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.MessageDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.services.external.rest.v1.dto.MessagesReadStatusUpdate
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import reactor.core.publisher.Flux
import kotlin.streams.toList


@FlowPreview
@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/message")
@Tag(name = "message")
class MessageController(private val messageLogic: MessageLogic, private val mapper: MapperFacade, private val sessionLogic: AsyncSessionLogic) {
    val DEFAULT_LIMIT = 1000

    @Operation(summary = "Creates a message")
    @PostMapping
    fun createMessage(@RequestBody messageDto: MessageDto) = mono {
        mapper.map(messageDto, Message::class.java)?.let { messageLogic.createMessage(it) }?.let { mapper.map(it, MessageDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message creation failed")
                        .also { logger.error(it.message) }
    }

    @Operation(summary = "Deletes a message delegation")
    @DeleteMapping("/{messageId}/delegate/{delegateId}")
    fun deleteDelegation(
            @PathVariable messageId: String,
            @PathVariable delegateId: String) = mono {
        val message = messageLogic.get(messageId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message with ID: $messageId not found").also { logger.error(it.message) }

        message.delegations.remove(delegateId)
        messageLogic.updateEntities(listOf(message)).firstOrNull()?.let { mapper.map(it, MessageDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message delegation deletion failed").also { logger.error(it.message) }
    }

    @Operation(summary = "Deletes multiple messages")
    @DeleteMapping("/{messageIds}")
    fun deleteMessages(@PathVariable messageIds: String): Flux<DocIdentifier> {
        return messageIds.split(',').takeIf { it.isNotEmpty() }
                ?.let {
                    try {
                        messageLogic.deleteByIds(it).injectReactorContext()
                    } catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Messages deletion failed").also { logger.error(it.message) }

    }

    @Operation(summary = "Deletes multiple messages")
    @PostMapping("/delete/byIds")
    fun deleteMessagesBatch(@RequestBody messagesIds: ListOfIdsDto): Flux<DocIdentifier>? {
        return messagesIds.ids?.takeIf { it.isNotEmpty() }
                ?.let {
                    try {
                        messageLogic.deleteByIds(it).injectReactorContext()
                    } catch (e: Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
    }

    @Operation(summary = "Gets a message")
    @GetMapping("/{messageId}")
    fun getMessage(@PathVariable messageId: String) = mono {
        messageLogic.get(messageId)?.let { mapper.map(it, MessageDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")
                        .also { logger.error(it.message) }
    }


    @Operation(summary = "List messages found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findMessagesByHCPartyPatientForeignKeys(@RequestParam secretFKeys: String): Flux<MessageDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return messageLogic.listMessagesByHCPartySecretPatientKeys(secretPatientKeys)
                .map { contact -> mapper.map(contact, MessageDto::class.java) }
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

        PaginatedList(messageLogic.findForCurrentHcParty(paginationOffset).paginatedList<Message, MessageDto>(mapper, realLimit))
    }

    @Operation(summary = "Get children messages of provided message")
    @GetMapping("/{messageId}/children")
    fun getChildrenMessages(@PathVariable messageId: String) =
            messageLogic.getChildren(messageId).map { mapper.map(it, MessageDto::class.java) }.injectReactorContext()


    @Operation(summary = "Get children messages of provided message")
    @PostMapping("/children/batch")
    fun getChildrenMessagesOfList(@RequestBody parentIds: ListOfIdsDto) =
            messageLogic.getChildren(parentIds.ids)
                    .map { m -> m.stream().map { mm -> mapper.map(mm, MessageDto::class.java) }.toList().asFlow() }
                    .flattenConcat()
                    .injectReactorContext()

    @Operation(summary = "Get children messages of provided message")
    @PostMapping("byInvoiceId")
    fun listMessagesByInvoiceIds(@RequestBody ids: ListOfIdsDto) =
            messageLogic.listMessagesByInvoiceIds(ids.ids).map { mapper.map(it, MessageDto::class.java) }.injectReactorContext()

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
        val messages = received?.takeIf { it }?.let { messageLogic.findByTransportGuidReceived(hcpId, transportGuid, paginationOffset) }
                ?: messageLogic.findByTransportGuid(hcpId, transportGuid, paginationOffset)
        PaginatedList(messages.paginatedList<Message, MessageDto>(mapper, realLimit))
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
        PaginatedList(messageLogic.findByTransportGuidSentDate(
                hcpId ?: sessionLogic.getCurrentHealthcarePartyId(),
                transportGuid,
                fromDate,
                toDate,
                paginationOffset
        ).paginatedList<Message, MessageDto>(mapper, realLimit))
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
        val startKeyElements = Gson().fromJson(startKey, Array<Any>::class.java)
        val paginationOffset = PaginationOffset<List<Any>>(if (startKeyElements == null) null else listOf(*startKeyElements), startDocumentId, null, realLimit + 1)
        val hcpId = hcpId ?: sessionLogic.getCurrentHealthcarePartyId()
        PaginatedList(messageLogic.findByToAddress(hcpId, toAddress, paginationOffset, reverse).paginatedList<Message, MessageDto>(mapper, realLimit))
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
        val startKeyElements = Gson().fromJson(startKey, Array<Any>::class.java)
        val paginationOffset = PaginationOffset<List<Any>>(if (startKeyElements == null) null else listOf(*startKeyElements), startDocumentId, null, realLimit + 1)
        val hcpId = hcpId ?: sessionLogic.getCurrentHealthcarePartyId()
        PaginatedList(messageLogic.findByFromAddress(hcpId, fromAddress, paginationOffset).paginatedList<Message, MessageDto>(mapper, realLimit))
    }

    @Operation(summary = "Updates a message")
    @PutMapping
    fun modifyMessage(@RequestBody messageDto: MessageDto) = mono {
        if (messageDto.id == null) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
                    .also { logger.error(it.message) }
        }
        mapper.map(messageDto, Message::class.java)
                ?.let {
                    messageLogic.modifyMessage(it)
                    mapper.map(it, MessageDto::class.java)
                }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
                        .also { logger.error(it.message) }
    }

    @Operation(summary = "Set status bits for given list of messages")
    @PutMapping("/status/{status}")
    fun setMessagesStatusBits(
            @PathVariable status: Int,
            @RequestBody messageIds: ListOfIdsDto) = messageLogic.setStatus(messageIds.ids, status).map { mapper.map(it, MessageDto::class.java) }.injectReactorContext()

    @Operation(summary = "Set read status for given list of messages")
    @PutMapping("/readstatus")
    fun setMessagesReadStatus(@RequestBody data: MessagesReadStatusUpdate) =
            messageLogic.setReadStatus(data.ids, data.userId, data.status, data.time).map { mapper.map(it, MessageDto::class.java) }.injectReactorContext()

    @Operation(summary = "Adds a delegation to a message")
    @PutMapping("/{messageId}/delegate")
    fun newMessageDelegations(
            @PathVariable messageId: String,
            @RequestBody ds: List<DelegationDto>) = mono {
        messageLogic.addDelegations(messageId, ds.map { mapper.map(it, Delegation::class.java) })?.takeIf { it.delegations.isNotEmpty() }?.let { mapper.map(it, MessageDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)
    }
}
