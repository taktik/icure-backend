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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
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
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.services.external.rest.v2.dto.AccessLogDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.AccessLogV2Mapper
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.time.Instant


@ExperimentalCoroutinesApi
@RestController("accessLogControllerV2")
@RequestMapping("/rest/v2/accesslog")
@Tag(name = "accesslog")
class AccessLogController(
        private val accessLogLogic: AccessLogLogic,
        private val accessLogV2Mapper: AccessLogV2Mapper,
        private val objectMapper: ObjectMapper
) {
    private val DEFAULT_LIMIT = 1000
    private val accessLogToAccessLogDto = { it: AccessLog -> accessLogV2Mapper.map(it) }
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "Creates an access log")
    @PostMapping
    fun createAccessLog(@RequestBody accessLogDto: AccessLogDto) = mono {
        val accessLog = accessLogLogic.createAccessLog(accessLogV2Mapper.map(accessLogDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog creation failed")
        accessLogV2Mapper.map(accessLog)
    }

    @Operation(summary = "Deletes an access log")
    @PostMapping("/delete/batch")
    fun deleteAccessLogs(@RequestBody accessLogIds: ListOfIdsDto): Flux<DocIdentifier> {
        try {
            return accessLogLogic.deleteAccessLogs(accessLogIds.ids).injectReactorContext()
        } catch (e: java.lang.Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
        }
    }

    @Operation(summary = "Gets an access log")
    @GetMapping("/{accessLogId}")
    fun getAccessLog(@PathVariable accessLogId: String) = mono {
        val accessLog = accessLogLogic.getAccessLog(accessLogId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AccessLog fetching failed")

        accessLogV2Mapper.map(accessLog)
    }

    @Operation(summary = "Get Paginated List of Access logs")
    @GetMapping
    fun findAccessLogsBy(@RequestParam(required = false) fromEpoch: Long?, @RequestParam(required = false) toEpoch: Long?, @RequestParam(required = false) startKey: Long?, @RequestParam(required = false) startDocumentId: String?, @RequestParam(required = false) limit: Int?, @RequestParam(required = false) descending: Boolean?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1) // fetch one more for nextKeyPair
        val accessLogs = accessLogLogic.listAccessLogsBy(fromEpoch ?: if(descending == true) Long.MAX_VALUE else 0, toEpoch ?: if(descending == true) 0 else Long.MAX_VALUE, paginationOffset, descending == true)
        accessLogs.paginatedList(accessLogToAccessLogDto, realLimit)
    }

    @Operation(summary = "Get Paginated List of Access logs by user after date")
    @GetMapping("/byUser")
    fun findAccessLogsByUserAfterDate(@Parameter(description = "A User ID", required = true) @RequestParam userId: String,
                                      @Parameter(description = "The type of access (COMPUTER or USER)") @RequestParam(required = false) accessType: String?,
                                      @Parameter(description = "The start search epoch") @RequestParam(required = false) startDate: Long?,
                                      @Parameter(description = "The start key for pagination") @RequestParam(required = false) startKey: String?,
                                      @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                      @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
                                      @Parameter(description = "Descending order") @RequestParam(required = false) descending: Boolean?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { objectMapper.readValue<List<Any>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, Object::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)
        val accessLogs = accessLogLogic.findAccessLogsByUserAfterDate(userId, accessType, startDate?.let { Instant.ofEpochMilli(it) }, paginationOffset, descending
                ?: false)

        accessLogs.paginatedList(accessLogToAccessLogDto, realLimit)
    }

    @Operation(summary = "List access logs found By Healthcare Party and secret foreign keyelementIds.")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun listAccessLogsByHCPartyAndPatientForeignKeys(@RequestParam("hcPartyId") hcPartyId: String, @RequestParam("secretFKeys") secretFKeys: String) = flow {
        val secretPatientKeys = HashSet(secretFKeys.split(","))
        emitAll(accessLogLogic.listAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys)).map { accessLogV2Mapper.map(it) } )
    }.injectReactorContext()

    @Operation(summary = "Modifies an access log")
    @PutMapping
    fun modifyAccessLog(@RequestBody accessLogDto: AccessLogDto) = mono {
        val accessLog = accessLogLogic.modifyAccessLog(accessLogV2Mapper.map(accessLogDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
        accessLogV2Mapper.map(accessLog)
    }
}
