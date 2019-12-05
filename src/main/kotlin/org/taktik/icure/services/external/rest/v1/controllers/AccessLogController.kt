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

import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.reactive.awaitSingle
import ma.glasnost.orika.MapperFacade
import org.ektorp.ComplexKey
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asynclogic.impl.AsyncSessionLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.utils.paginatedList
import java.time.Instant


@RestController
@RequestMapping("/rest/v1/accesslog")
@Api(tags = ["accesslog"])
class AccessLogController(private val mapper: MapperFacade,
                          private val accessLogLogic: AccessLogLogic,
                          private val sessionLogic: AsyncSessionLogic) {

    private val DEFAULT_LIMIT = 1000

//    override fun injectIt(paginationOffset: PaginationOffset<Long>, descending: Boolean, myFun: (URI, String, PaginationOffset<Long>, Boolean) -> Flow<ViewQueryResultEvent>): Flux<ViewQueryResultEvent> {
//        return injectReactorContext(
//                flow {
//                    val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.awaitSingle()!!
//                    val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.awaitSingle()!!
//                    myFun(paginationOffset, descending).collect {
//                        emit(it)
//                    }
//                }
//        )
//    }

    @ApiOperation(nickname = "createAccessLog", value = "Creates an access log")
    @PostMapping
    suspend fun createAccessLog(@RequestBody accessLogDto: AccessLogDto): AccessLogDto {
        val accessLog = accessLogLogic.createAccessLog(mapper.map(accessLogDto, AccessLog::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog creation failed")
        return mapper.map(accessLog, AccessLogDto::class.java)
    }

    @ApiOperation(nickname = "deleteAccessLog", value = "Deletes an access log")
    @DeleteMapping("/{accessLogIds}")
    suspend fun deleteAccessLog(@PathVariable accessLogIds: String): List<String> {
        return accessLogLogic.deleteAccessLogs(accessLogIds.split(','))
    }

    @ApiOperation(nickname = "getAccessLog", value = "Gets an access log")
    @GetMapping("/{accessLogId}")
    suspend fun getAccessLog(@PathVariable accessLogId: String): AccessLogDto {
        val accessLog = accessLogLogic.getAccessLog(accessLogId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AccessLog fetching failed")

        return mapper.map(accessLog, AccessLogDto::class.java)
    }

    // TODO don't serialize null fields
    @ApiOperation(nickname = "listAccessLogs", value = "Lists access logs")
    @GetMapping // TODO SH limit as int instread of string?
    suspend fun listAccessLogs(@RequestParam(required = false) startKey: String?, @RequestParam(required = false) startDocumentId: String?, @RequestParam(required = false) limit: String?, @RequestParam(required = false) descending: Boolean = false): PaginatedList<AccessLogDto> {
        // TODO SH make limit non-nullable in PaginationOffset
        val realLimit = limit?.let { Integer.valueOf(it) } ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset<Long>(null, startDocumentId, null, realLimit + 1) // fetch one more for nextKeyPair
        val accessLogs = accessLogLogic.listAccessLogs(paginationOffset, descending)
        return accessLogs.paginatedList<AccessLog, AccessLogDto>(mapper, realLimit)
    }

    @ApiOperation(nickname = "findByUserAfterDate", value = "Get Paginated List of Access logs")
    @GetMapping("/byUser")
    suspend fun findByUserAfterDate(@ApiParam(value = "A User ID", required = true) @RequestParam userId: String,
                                    @ApiParam(value = "The type of access (COMPUTER or USER)") @RequestParam(required = false) accessType: String?,
                                    @ApiParam(value = "The start search epoch") @RequestParam(required = false) startDate: Long?,
                                    @ApiParam(value = "The start key for pagination") @RequestParam(required = false) startKey: String?,
                                    @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                    @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                                    @ApiParam(value = "Descending order") @RequestParam(required = false) descending: Boolean?): PaginatedList<AccessLogDto> {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
        val paginationOffset = PaginationOffset(ComplexKey.of(startKeyElements), startDocumentId, null, realLimit + 1)
        val accessLogs = accessLogLogic.findByUserAfterDate(userId, accessType, startDate?.let { Instant.ofEpochMilli(it) }, paginationOffset, descending
                ?: false)

        return accessLogs.paginatedList<AccessLog, AccessLogDto>(mapper, realLimit)
    }

    @ApiOperation(nickname = "modifyAccessLog", value = "Modifies an access log")
    @PutMapping
    suspend fun modifyAccessLog(@RequestBody accessLogDto: AccessLogDto): AccessLogDto {
        val accessLog = accessLogLogic.modifyAccessLog(mapper.map(accessLogDto, AccessLog::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
        return mapper.map(accessLog, AccessLogDto::class.java)
    }
}
