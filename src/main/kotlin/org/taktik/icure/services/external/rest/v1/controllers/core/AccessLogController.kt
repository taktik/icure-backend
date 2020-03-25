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

import com.google.gson.Gson
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import reactor.core.publisher.Flux
import java.time.Instant


@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/accesslog")
@Tag(name = "accesslog")
class AccessLogController(private val mapper: MapperFacade,
                          private val accessLogLogic: AccessLogLogic) {

    private val DEFAULT_LIMIT = 1000

    @Operation(summary = "Creates an access log")
    @PostMapping
    fun createAccessLog(@RequestBody accessLogDto: AccessLogDto) = mono {
        val accessLog = accessLogLogic.createAccessLog(mapper.map(accessLogDto, AccessLog::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog creation failed")
        mapper.map(accessLog, AccessLogDto::class.java)
    }

    @Operation(summary = "Deletes an access log")
    @DeleteMapping("/{accessLogIds}")
    fun deleteAccessLog(@PathVariable accessLogIds: String): Flux<DocIdentifier> {
        return accessLogLogic.deleteAccessLogs(accessLogIds.split(',')).injectReactorContext()
    }

    @Operation(summary = "Gets an access log")
    @GetMapping("/{accessLogId}")
    fun getAccessLog(@PathVariable accessLogId: String) = mono {
        val accessLog = accessLogLogic.getAccessLog(accessLogId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AccessLog fetching failed")

        mapper.map(accessLog, AccessLogDto::class.java)
    }

    @Operation(summary = "Lists access logs")
    @GetMapping
    fun listAccessLogs(@RequestParam(required = false) fromEpoch: Long?, @RequestParam(required = false) toEpoch: Long?, @RequestParam(required = false) startKey: Long?, @RequestParam(required = false) startDocumentId: String?, @RequestParam(required = false) limit: Int?, @RequestParam(required = false) descending: Boolean?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit + 1) // fetch one more for nextKeyPair
        val accessLogs = accessLogLogic.listAccessLogs(fromEpoch ?: if(descending == true) Long.MAX_VALUE else 0, toEpoch ?: if(descending == true) 0 else Long.MAX_VALUE, paginationOffset, descending == true)
        accessLogs.paginatedList<AccessLog, AccessLogDto>(mapper, realLimit)
    }

    @Operation(summary = "Get Paginated List of Access logs")
    @GetMapping("/byUser")
    fun findByUserAfterDate(@ApiParam(value = "A User ID", required = true) @RequestParam userId: String,
                                    @ApiParam(value = "The type of access (COMPUTER or USER)") @RequestParam(required = false) accessType: String?,
                                    @ApiParam(value = "The start search epoch") @RequestParam(required = false) startDate: Long?,
                                    @ApiParam(value = "The start key for pagination") @RequestParam(required = false) startKey: String?,
                                    @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                    @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                                    @ApiParam(value = "Descending order") @RequestParam(required = false) descending: Boolean?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { Gson().fromJson(it, Array<String>::class.java).toList() }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)
        val accessLogs = accessLogLogic.findByUserAfterDate(userId, accessType, startDate?.let { Instant.ofEpochMilli(it) }, paginationOffset, descending
                ?: false)

        accessLogs.paginatedList<AccessLog, AccessLogDto>(mapper, realLimit)
    }

    @Operation(summary = "Modifies an access log")
    @PutMapping
    fun modifyAccessLog(@RequestBody accessLogDto: AccessLogDto) = mono {
        val accessLog = accessLogLogic.modifyAccessLog(mapper.map(accessLogDto, AccessLog::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
        mapper.map(accessLog, AccessLogDto::class.java)
    }
}
