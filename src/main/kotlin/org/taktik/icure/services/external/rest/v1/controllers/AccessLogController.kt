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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.TotalCount
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asynclogic.impl.AccessLogLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/rest/v1/accesslog")
@Api(tags = ["accesslog"])
class AccessLogController(private val mapper: MapperFacade,
                          private val accessLogLogic: AccessLogLogic) {

//    @ApiOperation(nickname = "createAccessLog", value = "Creates an access log")
//    @PostMapping
//    @FlowPreview // TODO is this needed?
//    suspend fun createAccessLog(@RequestBody accessLogDto: AccessLogDto): AccessLogDto { // TODO add '?' or not?
//        val accessLog = accessLogLogic.createAccessLog(mapper.map(accessLogDto, AccessLog::class.java))
//                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog creation failed")
//        return mapper.map(accessLog, AccessLogDto::class.java)
//    }
//
//    @ApiOperation(nickname = "deleteAccessLog", value = "Deletes an access log")
//    @DeleteMapping("/{accessLogIds}")
//    fun deleteAccessLog(@PathVariable accessLogIds: String): List<String> {
//        return accessLogLogic.deleteAccessLogs(accessLogIds.split(','))
//                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog deletion failed")
//    }
//
//    @ApiOperation(nickname = "getAccessLog", value = "Gets an access log")
//    @GetMapping("/{accessLogId}")
//    fun getAccessLog(@PathVariable accessLogId: String): AccessLogDto {
//        val accessLog = accessLogLogic.getAccessLog(accessLogId)
//                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AccessLog fetching failed")
//
//        return mapper.map(accessLog, AccessLogDto::class.java)
//    }

    @ApiOperation(nickname = "listAccessLogs", value = "Lists access logs")
    @GetMapping
    fun listAccessLogs(@RequestParam(required = false) startKey: String?, @RequestParam(required = false) startDocumentId: String?, @RequestParam(required = false) limit: String?, @RequestParam(required = false) ascending: Boolean = false): Flux<Int> { //Flux<AccessLogDto> {
        val paginationOffset = PaginationOffset<Long>(null, startDocumentId, null, if (limit != null) Integer.valueOf(limit) else null)
        val accessLogDtos = PaginatedList<AccessLogDto>()
        val accessLogs = accessLogLogic.listAccessLogs(paginationOffset, ascending)
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog listing failed")

        /*mapper.map(accessLogs, accessLogDtos, object : TypeBuilder<org.taktik.icure.db.PaginatedList<AccessLog>>() {
        }.build(), object : TypeBuilder<PaginatedList<AccessLogDto>>() {
        }.build())
        return accessLogDtos.rows.asFlow()*/
        return accessLogs.map { println(it); if (it is TotalCount) it.total else -1 }
    }

//    @ApiOperation(nickname = "findByUserAfterDate", value = "Get Paginated List of Access logs")
//    @GetMapping("/byUser")
//    fun findByUserAfterDate(@ApiParam(value = "A User ID", required = true) @RequestParam userId: String,
//                            @ApiParam(value = "The type of access (COMPUTER or USER)") @RequestParam(required = false) accessType: String?,
//                            @ApiParam(value = "The start search epoch") @RequestParam(required = false) startDate: Long?,
//                            @ApiParam(value = "The start key for pagination") @RequestParam(required = false) startKey: String?,
//                            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
//                            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
//                            @ApiParam(value = "Descending order") @RequestParam(required = false) descending: Boolean?): AccessLogPaginatedList {
//        val startKeyElements = if (startKey == null) null else Gson().fromJson(startKey, List::class.java)
//        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)
//        val accessLogs = accessLogLogic.findByUserAfterDate(userId, accessType, if (startDate != null) Instant.ofEpochMilli(startDate) else null, paginationOffset, descending
//                ?: false)
//                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog listing failed")
//
//        val accessLogDtos = AccessLogPaginatedList()
//        mapper.map(accessLogs, accessLogDtos, object : TypeBuilder<org.taktik.icure.db.PaginatedList<AccessLog>>() {
//        }.build(), object : TypeBuilder<PaginatedList<AccessLogDto>>() {
//        }.build())
//        return accessLogDtos
//    }
//
//    @ApiOperation(nickname = "modifyAccessLog", value = "Modifies an access log")
//    @PutMapping
//    fun modifyAccessLog(@RequestBody accessLogDto: AccessLogDto): AccessLogDto {
//        val accessLog = accessLogLogic.modifyAccessLog(mapper.map(accessLogDto, AccessLog::class.java))
//                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
//        return mapper.map(accessLog, AccessLogDto::class.java)
//    }
}
