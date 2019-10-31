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

import com.google.common.base.Splitter
import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.logic.MessageLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.MessageDto
import org.taktik.icure.services.external.rest.v1.dto.MessagePaginatedList
import org.taktik.icure.services.external.rest.v1.dto.MessagesReadStatusUpdate
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import java.util.*
import kotlin.streams.toList


@RestController
@RequestMapping("/rest/v1/message")
@Api(tags = ["message"])
class MessageController(private val messageLogic: MessageLogic, private val mapper: MapperFacade, private val sessionLogic: SessionLogic) {


    @ApiOperation(nickname = "createMessage", value = "Creates a message")
    @PostMapping
    fun createMessage(@RequestBody messageDto: MessageDto) =
            mapper.map(messageDto, Message::class.java)?.let { messageLogic.createMessage(it) }?.let { mapper.map(it, MessageDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message creation failed")
                            .also { logger.error(it.message) }

    @ApiOperation(nickname = "deleteDelegation", value = "Deletes a message delegation")
    @DeleteMapping("/{messageId}/delegate/{delegateId}")
    fun deleteDelegation(
            @PathVariable messageId: String,
            @PathVariable delegateId: String): MessageDto {
        val message = messageLogic.get(messageId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message with ID: $messageId not found")
                        .also { logger.error(it.message) }


        message.delegations.remove(delegateId)
        return messageLogic.updateEntities(listOf(message))?.takeIf { it.size == 1 }?.let { mapper.map(it[0], MessageDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Message delegation deletion failed")
                        .also { logger.error(it.message) }

    }

    @ApiOperation(nickname = "deleteMessages", value = "Deletes multiple messages")
    @DeleteMapping("/{messageIds}")
    fun deleteMessages(@PathVariable messageIds: String) {
        messageIds.split(',').takeIf { it.isNotEmpty() }
                ?.let {
                    try {
                        messageLogic.deleteEntities(it)
                        return
                    } catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
                                .also { logger.error(it.message) }

                    }
                }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Messages deletion failed")
                        .also { logger.error(it.message) }

    }

    @ApiOperation(nickname = "deleteMessagesBatch", value = "Deletes multiple messages")
    @PostMapping("/delete/byIds")
    fun deleteMessagesBatch(@RequestBody messagesIds: ListOfIdsDto) {
        messagesIds.ids?.takeIf { it.isNotEmpty() }
                ?.let {
                    try {
                        messageLogic.deleteEntities(it)
                        return
                    } catch (e: Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
                                .also { logger.error(it.message) }
                    }
                }
    }

    @ApiOperation(nickname = "getMessage", value = "Gets a message")
    @GetMapping("/{messageId}")
    fun getMessage(@PathVariable messageId: String) =
            messageLogic.get(messageId)?.let { mapper.map(it, MessageDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")
                            .also { logger.error(it.message) }


    @ApiOperation(nickname = "findByHCPartyPatientSecretFKeys", value = "List messages found By Healthcare Party and secret foreign keys.", notes = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam secretFKeys: String): List<MessageDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return messageLogic.listMessagesByHCPartySecretPatientKeys(ArrayList(secretPatientKeys))?.let { it.map { contact -> mapper.map(contact, MessageDto::class.java) } }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting Messages failed. Please try again or read the server log.")
                        .also { logger.error(it.message) }

    }

    @ApiOperation(nickname = "findMessages", value = "Get all messages (paginated) for current HC Party")
    @GetMapping
    fun findMessages(@RequestParam(required = false) startKey: String?,
                     @RequestParam(required = false) startDocumentId: String?,
                     @RequestParam(required = false) limit: Int?): MessagePaginatedList {
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyList, startDocumentId, null, limit)

        return messageLogic.findForCurrentHcParty(paginationOffset)?.let { mapper.map(it, MessagePaginatedList::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message listing failed")
                        .also { logger.error(it.message) }

    }

    @ApiOperation(nickname = "getChildren", value = "Get children messages of provided message")
    @GetMapping("/{messageId}/children")
    fun getChildren(@PathVariable messageId: String) =
            messageLogic.getChildren(messageId)?.map { mapper.map(it, MessageDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message listing failed")


    @ApiOperation(nickname = "getChildrenOfList", value = "Get children messages of provided message")
    @PostMapping("/children/batch")
    fun getChildrenOfList(@RequestBody parentIds: ListOfIdsDto) =
            messageLogic.getChildren(parentIds.ids).map { m -> m.stream().map { mm -> mapper.map(mm, MessageDto::class.java) }.toList() }.toList()

    @ApiOperation(nickname = "listMessagesByInvoiceIds", value = "Get children messages of provided message")
    @PostMapping("byInvoiceId")
    fun listMessagesByInvoiceIds(@RequestBody ids: ListOfIdsDto) =
            messageLogic.listMessagesByInvoiceIds(ids.ids).map { mapper.map(it, MessageDto::class.java) }

    @ApiOperation(nickname = "findMessagesByTransportGuid", value = "Get all messages (paginated) for current HC Party and provided transportGuid")
    @GetMapping("/byTransportGuid")
    fun findMessagesByTransportGuid(
            @RequestParam(required = false) transportGuid: String?,
            @RequestParam(required = false) received: Boolean?,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) hcpId: String?): MessagePaginatedList {
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyList, startDocumentId, null, limit)
        val hcpId = hcpId ?: sessionLogic.currentSessionContext.user.healthcarePartyId
        val messages = received?.takeIf { it }?.let { messageLogic.findByTransportGuidReceived(hcpId, transportGuid, paginationOffset) }
                ?: messageLogic.findByTransportGuid(hcpId, transportGuid, paginationOffset)
        return messages?.let { mapper.map(messages, MessagePaginatedList::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message listing failed")
    }

    @ApiOperation(nickname = "findMessagesByTransportGuidSentDate", value = "Get all messages starting by a prefix between two date", httpMethod = "GET")
    @GetMapping("/byTransportGuidSentDate")
    fun findMessagesByTransportGuidSentDate(
            @RequestParam(required = false) transportGuid: String?,
            @RequestParam(required = false, value = "from") fromDate: Long?,
            @RequestParam(required = false, value = "to") toDate: Long?,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) hcpId: String?): MessagePaginatedList {
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it) }
        val paginationOffset = PaginationOffset<List<Any>>(startKeyList, startDocumentId, null, limit)
        return messageLogic.findByTransportGuidSentDate(
                hcpId ?: sessionLogic.currentSessionContext.user.healthcarePartyId,
                transportGuid,
                fromDate,
                toDate,
                paginationOffset
        )?.let { mapper.map(it, MessagePaginatedList::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message listing failed")
    }


    @ApiOperation(nickname = "findMessagesByToAddress", value = "Get all messages (paginated) for current HC Party and provided to address")
    @GetMapping("/byToAddress")
    fun findMessagesByToAddress(
            @RequestParam(required = false) toAddress: String?,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) reverse: Boolean?,
            @RequestParam(required = false) hcpId: String?): MessagePaginatedList {
        val startKeyElements = Gson().fromJson(startKey, Array<Any>::class.java)
        val paginationOffset = PaginationOffset<List<Any>>(if (startKeyElements == null) null else listOf(*startKeyElements), startDocumentId, null, limit)
        val hcpId = hcpId ?: sessionLogic.currentSessionContext.user.healthcarePartyId
        return messageLogic.findByToAddress(hcpId, toAddress, paginationOffset, reverse)?.let { mapper.map(it, MessagePaginatedList::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message listing failed")
    }

    @ApiOperation(nickname = "findMessagesByFromAddress", value = "Get all messages (paginated) for current HC Party and provided from address", httpMethod = "GET")
    @GetMapping("/byFromAddress")
    fun findMessagesByFromAddress(
            @RequestParam(required = false) fromAddress: String?,
            @RequestParam(required = false) startKey: String?,
            @RequestParam(required = false) startDocumentId: String?,
            @RequestParam(required = false) limit: Int?,
            @RequestParam(required = false) hcpId: String?): MessagePaginatedList {
        val startKeyElements = Gson().fromJson(startKey, Array<Any>::class.java)
        val paginationOffset = PaginationOffset<List<Any>>(if (startKeyElements == null) null else listOf(*startKeyElements), startDocumentId, null, limit)
        val hcpId = hcpId ?: sessionLogic.currentSessionContext.user.healthcarePartyId
        return messageLogic.findByFromAddress(hcpId, fromAddress, paginationOffset)?.let { mapper.map(it, MessagePaginatedList::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Message listing failed")
    }

    @ApiOperation(nickname = "modifyMessage", value = "Updates a message")
    @PutMapping
    fun modifyMessage(messageDto: MessageDto): MessageDto {
        if (messageDto.id == null) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
                    .also { logger.error(it.message) }
        }
        return mapper.map(messageDto, Message::class.java)
                ?.let {
                    messageLogic.modifyMessage(it)
                    mapper.map(it, MessageDto::class.java)
                }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")
                        .also { logger.error(it.message) }

    }

    @ApiOperation(nickname = "setMessagesStatusBits", value = "Set status bits for given list of messages")
    @PutMapping("/status/{status}")
    fun setMessagesStatusBits(
            @PathVariable status: Int,
            @RequestBody messageIds: ListOfIdsDto) = messageLogic.setStatus(messageIds.ids, status).map { mapper.map(it, MessageDto::class.java) }

    @ApiOperation(nickname = "setMessagesReadStatus", value = "Set read status for given list of messages")
    @PutMapping("/readstatus")
    fun setMessagesReadStatus(@RequestBody data: MessagesReadStatusUpdate) =
            messageLogic.setReadStatus(data.ids, data.userId, data.status, data.time).map { mapper.map(it, MessageDto::class.java) }

    @ApiOperation(nickname = "newDelegations", value = "Adds a delegation to a message")
    @PutMapping("/{messageId}/delegate")
    fun newDelegations(
            @PathVariable messageId: String,
            @RequestBody ds: List<DelegationDto>) =
            messageLogic.addDelegations(messageId, ds.map { mapper.map(it, Delegation::class.java) })?.takeIf { it.delegations.isNotEmpty() }?.let { mapper.map(it, MessageDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "New delegation for message failed")

    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)
    }
}
