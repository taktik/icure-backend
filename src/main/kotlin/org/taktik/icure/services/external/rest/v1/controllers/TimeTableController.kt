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
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.entities.TimeTableHour
import org.taktik.icure.entities.TimeTableItem
import org.taktik.icure.logic.TimeTableLogic
import org.taktik.icure.services.external.rest.v1.dto.TimeTableDto
import java.util.*

@RestController
@RequestMapping("/timeTable")
@Api(tags = ["timeTable"])
class TimeTableFacade(private val timeTableLogic: TimeTableLogic,
                      private val mapper: MapperFacade) {

    @ApiOperation(nickname = "createTimeTable", value = "Creates a timeTable")
    @PostMapping
    fun createTimeTable(@RequestBody timeTableDto: TimeTableDto?) =
        timeTableLogic.createTimeTable(mapper.map(timeTableDto, TimeTable::class.java)).let { mapper.map(it, TimeTableDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable creation failed")



    @ApiOperation(nickname = "deleteTimeTable", value = "Deletes an timeTable")
    @DeleteMapping("/{timeTableIds}")
    fun deleteTimeTable(@PathVariable timeTableIds: String): List<String> =
            timeTableLogic.deleteTimeTables(timeTableIds.split(','))
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable deletion failed")

    @ApiOperation(nickname = "getTimeTable", value = "Gets a timeTable")
    @GetMapping("/{timeTableId}")
    fun getTimeTable(@PathVariable timeTableId: String): TimeTableDto =
            if (timeTableId.equals("new", ignoreCase = true)) {
                //Create an hourItem
                val timeTableHour = TimeTableHour()
                timeTableHour.startHour = java.lang.Long.parseLong("0800")
                timeTableHour.startHour = java.lang.Long.parseLong("0900")
                //Create a timeTableItem
                val timeTableItem = TimeTableItem()
                timeTableItem.calendarItemTypeId = "consult"
                timeTableItem.days = ArrayList()
                timeTableItem.days.add("monday")
                timeTableItem.recurrenceTypes = ArrayList()
                timeTableItem.hours = ArrayList()
                timeTableItem.hours.add(timeTableHour)
                //Create the timeTable
                val timeTable = TimeTable()
                timeTable.startTime = java.lang.Long.parseLong("20180601000")
                timeTable.endTime = java.lang.Long.parseLong("20180801000")
                timeTable.name = "myPeriod"
                timeTable.items = ArrayList()
                timeTable.items.add(timeTableItem)

                //Return it
                mapper.map(timeTable, TimeTableDto::class.java)
            } else {
                timeTableLogic.getTimeTable(timeTableId).let { mapper.map(it, TimeTableDto::class.java) }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable fetching failed")
            }

    @ApiOperation(nickname = "modifyTimeTable", value = "Modifies an timeTable")
    @PutMapping
    fun modifyTimeTable(@RequestBody timeTableDto: TimeTableDto?) =
            timeTableLogic.modifyTimeTable(mapper.map(timeTableDto, TimeTable::class.java))?.let { mapper.map(it, TimeTableDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable modification failed")

    @ApiOperation(nickname = "getTimeTablesByPeriodAndAgendaId", value = "Get TimeTables by Period and AgendaId")
    @PostMapping("/byPeriodAndAgendaId")
    fun getTimeTablesByPeriodAndAgendaId(@ApiParam(required = true) @RequestParam startDate: Long,
                                         @ApiParam(required = true) @RequestParam endDate: Long,
                                         @ApiParam(required = true) @RequestParam agendaId: String): List<TimeTableDto> {
        if (agendaId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
        }
        timeTableLogic.getTimeTablesByPeriodAndAgendaId(startDate, endDate, agendaId)?.let { return it.map { mapper.map(it, TimeTableDto::class.java) } }
                ?: throw ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Getting TimeTable failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log."
                    )
    }

    @ApiOperation(nickname = "getTimeTablesByAgendaId", value = "Get TimeTables by AgendaId")
    @PostMapping("/byAgendaId")
    fun getTimeTablesByAgendaId(@ApiParam(required = true) @RequestParam agendaId: String): List<TimeTableDto> {
        if (agendaId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
        }

        timeTableLogic.getTimeTablesByAgendaId(agendaId)?.let { return it.map { mapper.map(it, TimeTableDto::class.java) } }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting TimeTable failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.")

    }
}
