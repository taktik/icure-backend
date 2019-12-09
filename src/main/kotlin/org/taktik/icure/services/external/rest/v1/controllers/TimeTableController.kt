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
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.impl.TimeTableLogic
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.entities.TimeTableHour
import org.taktik.icure.entities.TimeTableItem
import org.taktik.icure.services.external.rest.v1.dto.TimeTableDto
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.reEmit
import reactor.core.publisher.Flux
import java.util.*

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/timeTable")
@Api(tags = ["timeTable"])
class TimeTableController(private val timeTableLogic: TimeTableLogic,
                          private val mapper: MapperFacade) {

    @ApiOperation(nickname = "createTimeTable", value = "Creates a timeTable")
    @PostMapping
    fun createTimeTable(@RequestBody timeTableDto: TimeTableDto) =
            mono {
                timeTableLogic.createTimeTable(mapper.map(timeTableDto, TimeTable::class.java))?.let { mapper.map(it, TimeTableDto::class.java) }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable creation failed")
            }

    @ApiOperation(nickname = "deleteTimeTable", value = "Deletes an timeTable")
    @DeleteMapping("/{timeTableIds}")
    fun deleteTimeTable(@PathVariable timeTableIds: String): Flow<DocIdentifier> {
        return timeTableLogic.deleteTimeTables(timeTableIds.split(','))
    }

    @ApiOperation(nickname = "getTimeTable", value = "Gets a timeTable")
    @GetMapping("/{timeTableId}")
    fun getTimeTable(@PathVariable timeTableId: String) =
            mono {
                if (timeTableId.equals("new", ignoreCase = true)) {
                    //Create an hourItem
                    val timeTableHour = TimeTableHour().apply {
                        startHour = java.lang.Long.parseLong("0800")
                        startHour = java.lang.Long.parseLong("0900")
                    }

                    //Create a timeTableItem
                    val timeTableItem = TimeTableItem().apply {
                        calendarItemTypeId = "consult"
                        days = ArrayList()
                        days.add("monday")
                        recurrenceTypes = ArrayList()
                        hours = ArrayList()
                        hours.add(timeTableHour)
                    }
                    //Create the timeTable
                    val timeTable = TimeTable().apply {
                        startTime = java.lang.Long.parseLong("20180601000")
                        endTime = java.lang.Long.parseLong("20180801000")
                        name = "myPeriod"
                        items = ArrayList()
                        items.add(timeTableItem)
                    }

                    //Return it
                    mapper.map(timeTable, TimeTableDto::class.java)
                } else {
                    timeTableLogic.getTimeTable(timeTableId).let { mapper.map(it, TimeTableDto::class.java) }
                            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "TimeTable fetching failed")
                }
            }

    @ApiOperation(nickname = "modifyTimeTable", value = "Modifies an timeTable")
    @PutMapping
    fun modifyTimeTable(@RequestBody timeTableDto: TimeTableDto) =
            mono {
                timeTableLogic.modifyTimeTable(mapper.map(timeTableDto, TimeTable::class.java))?.let { mapper.map(it, TimeTableDto::class.java) }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable modification failed")
            }

    @ApiOperation(nickname = "getTimeTablesByPeriodAndAgendaId", value = "Get TimeTables by Period and AgendaId")
    @PostMapping("/byPeriodAndAgendaId")
    fun getTimeTablesByPeriodAndAgendaId(@ApiParam(required = true) @RequestParam startDate: Long,
                                         @ApiParam(required = true) @RequestParam endDate: Long,
                                         @ApiParam(required = true) @RequestParam agendaId: String): Flux<TimeTableDto> =
            flow<TimeTableDto> {
                if (agendaId.isBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
                }
                emitAll(timeTableLogic.getTimeTablesByPeriodAndAgendaId(startDate, endDate, agendaId).map { mapper.map(it, TimeTableDto::class.java) })
            }.injectReactorContext()

    @ApiOperation(nickname = "getTimeTablesByAgendaId", value = "Get TimeTables by AgendaId")
    @PostMapping("/byAgendaId")
    fun getTimeTablesByAgendaId(@ApiParam(required = true) @RequestParam agendaId: String): Flux<TimeTableDto> =
            flow<TimeTableDto> {
                if (agendaId.isBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
                }
                emitAll(timeTableLogic.getTimeTablesByAgendaId(agendaId).map { mapper.map(it, TimeTableDto::class.java) })
            }.injectReactorContext()

}
