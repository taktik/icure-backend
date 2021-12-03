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
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ReceiptLogic
import org.taktik.icure.entities.embed.ReceiptBlobType
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.ReceiptDto
import org.taktik.icure.services.external.rest.v2.mapper.ReceiptV2Mapper
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.writeTo
import reactor.core.publisher.Flux
import java.io.ByteArrayOutputStream

@ExperimentalCoroutinesApi
@RestController("receiptControllerV2")
@RequestMapping("/rest/v2/receipt")
@Tag(name = "receipt")
class ReceiptController(
        private val receiptLogic: ReceiptLogic,
        private val receiptV2Mapper: ReceiptV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Creates a receipt")
    @PostMapping
    fun createReceipt(@RequestBody receiptDto: ReceiptDto) = mono {
        try {
            val created = receiptLogic.createEntities(listOf(receiptV2Mapper.map(receiptDto)))
            created.firstOrNull()?.let { receiptV2Mapper.map(it) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Receipt creation failed.")
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Receipt creation failed.")
        }
    }

    @Operation(summary = "Deletes receipts")
    @DeleteMapping("/delete/batch")
    fun deleteReceipts(@RequestBody receiptIds: ListOfIdsDto): Flux<DocIdentifier> {
        return receiptIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        receiptLogic.deleteEntities(HashSet(ids)).injectReactorContext()
                    } catch (e: Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Receipt deletion failed").also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }


    @Operation(summary = "Get an attachment", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @GetMapping("/{receiptId}/attachment/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getReceiptAttachment(
            @PathVariable receiptId: String,
            @PathVariable attachmentId: String,
            @RequestParam enckeys: String) = mono {
        val attachment = ByteArrayOutputStream().use {
            receiptLogic.getAttachment(receiptId, attachmentId).writeTo(it)
            it.toByteArray()
        }
        if (attachment.isEmpty()) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found")
                .also { logger.error(it.message) }
        CryptoUtils.decryptAESWithAnyKey(attachment, enckeys.split('.'))
    }

    @Operation(summary = "Creates a receipt's attachment")
    @PutMapping("/{receiptId}/attachment/{blobType}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun setReceiptAttachment(
            @PathVariable receiptId: String,
            @PathVariable blobType: String,
            @RequestParam(required = false) enckeys: String?,
            @RequestBody payload: ByteArray) = mono {

        var encryptedPayload = payload
        if (enckeys?.isNotEmpty() == true) {
            CryptoUtils.encryptAESWithAnyKey(encryptedPayload, enckeys.split(',')[0])?.let { encryptedPayload = it }
        }

        val receipt = receiptLogic.getEntity(receiptId)
        if (receipt != null) {
            receiptLogic.addReceiptAttachment(receipt, ReceiptBlobType.valueOf(blobType), encryptedPayload)
            receiptV2Mapper.map(receipt)
        } else throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Receipt modification failed")
    }

    @Operation(summary = "Gets a receipt")
    @GetMapping("/{receiptId}")
    fun getReceipt(@PathVariable receiptId: String) = mono {
        receiptLogic.getEntity(receiptId)?.let { receiptV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt not found")
    }

    @Operation(summary = "Gets a receipt")
    @GetMapping("/byRef/{ref}")
    fun listByReference(@PathVariable ref: String): Flux<ReceiptDto> =
            receiptLogic.listReceiptsByReference(ref).map { receiptV2Mapper.map(it) }.injectReactorContext()

    @Operation(summary = "Updates a receipt")
    @PutMapping
    fun modifyReceipt(@RequestBody receiptDto: ReceiptDto) = mono {
        val receipt = receiptV2Mapper.map(receiptDto)
        try {
            receiptLogic.modifyEntities(listOf(receipt)).map { receiptV2Mapper.map(it) }.firstOrNull()
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update receipt")

        } catch (e: Exception) {
            logger.error("Cannot update receipt", e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Receipt modification failed")
        }
    }

}
