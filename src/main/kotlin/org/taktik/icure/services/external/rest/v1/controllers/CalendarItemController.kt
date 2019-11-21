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
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.logic.CalendarItemLogic
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto

@RestController
@RequestMapping("/rest/v1/calendarItem")
@Api(tags = ["calendarItem"])
class CalendarItemController(private val calendarItemLogic: CalendarItemLogic,
                             private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getCalendarItems", value = "Gets all calendarItems")
    @GetMapping
    fun getCalendarItems(): List<CalendarItemDto> {
        val calendarItems = calendarItemLogic.allEntities
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemTypes fetching failed")

        return calendarItems.map { mapper.map(it, CalendarItemDto::class.java) }
    }

    @ApiOperation(nickname = "createCalendarItem", value = "Creates a calendarItem")
    @PostMapping
    fun createCalendarItem(@RequestBody calendarItemDto: CalendarItemDto): CalendarItemDto {
        val calendarItem = calendarItemLogic.createCalendarItem(mapper.map(calendarItemDto, CalendarItem::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItem creation failed")

        return mapper.map(calendarItem, CalendarItemDto::class.java)
    }

    @ApiOperation(nickname = "deleteCalendarItem", value = "Deletes an calendarItem")
    @DeleteMapping("/{calendarItemIds}")
    fun deleteCalendarItem(@PathVariable calendarItemIds: String): List<String> {
        return calendarItemLogic.deleteCalendarItems(calendarItemIds.split(','))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItem deletion failed")
    }

    @ApiOperation(nickname = "getCalendarItem", value = "Gets an calendarItem")
    @GetMapping("/{calendarItemId}")
    fun getCalendarItem(@PathVariable calendarItemId: String): CalendarItemDto {
        val calendarItem = calendarItemLogic.getCalendarItem(calendarItemId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "CalendarItem fetching failed")

        return mapper.map(calendarItem, CalendarItemDto::class.java)
    }


    @ApiOperation(nickname = "modifyCalendarItem", value = "Modifies an calendarItem")
    @PutMapping
    fun modifyCalendarItem(@RequestBody calendarItemDto: CalendarItemDto): CalendarItemDto {
        val calendarItem = calendarItemLogic.modifyCalendarItem(mapper.map(calendarItemDto, CalendarItem::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItem modification failed")

        return mapper.map(calendarItem, CalendarItemDto::class.java)
    }


    @ApiOperation(nickname = "getCalendarItemsByPeriodAndHcPartyId", value = "Get CalendarItems by Period and HcPartyId")
    @PostMapping("/byPeriodAndHcPartyId")
    fun getCalendarItemsByPeriodAndHcPartyId(@RequestParam startDate: Long,
                                             @RequestParam endDate: Long,
                                             @RequestParam hcPartyId: String): List<CalendarItemDto> {
        if (hcPartyId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "hcPartyId was empty")
        }
        val calendars = calendarItemLogic.getCalendarItemByPeriodAndHcPartyId(startDate, endDate, hcPartyId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting CalendarItem failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.")
        return calendars.map { mapper.map(it, CalendarItemDto::class.java) }
    }

    @ApiOperation(nickname = "getCalendarsByPeriodAndAgendaId", value = "Get CalendarItems by Period and AgendaId")
    @PostMapping("/byPeriodAndAgendaId")
    fun getCalendarsByPeriodAndAgendaId(@RequestParam startDate: Long,
                                        @RequestParam endDate: Long,
                                        @RequestParam agendaId: String): List<CalendarItemDto> {
        if (agendaId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
        }
        val calendars = calendarItemLogic.getCalendarItemByPeriodAndAgendaId(startDate, endDate, agendaId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting CalendarItem failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.")
        return calendars.map { mapper.map(it, CalendarItemDto::class.java) }
    }
}
