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
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.logic.CalendarItemTypeLogic
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemTypeDto

@RestController
@RequestMapping("/calendarItemType")
@Api(tags = ["calendarItemType"])
class CalendarItemTypeFacade(private val calendarItemTypeLogic: CalendarItemTypeLogic,
                             private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getCalendarItemTypes", value = "Gets all calendarItemTypes")
    @GetMapping
    fun getCalendarItemTypes(): List<CalendarItemTypeDto> {
        val calendarItemTypes = calendarItemTypeLogic.allEntities
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemTypes fetching failed")
        return calendarItemTypes.map { mapper.map(it, CalendarItemTypeDto::class.java) }
    }

    @ApiOperation(nickname = "getCalendarItemTypesIncludeDeleted", value = "Gets all calendarItemTypes include deleted")
    @GetMapping("/includeDeleted")
    fun getCalendarItemTypesIncludeDeleted(): List<CalendarItemTypeDto> {
        val calendarItemTypes = calendarItemTypeLogic.allEntitiesIncludeDelete
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemTypes fetching failed")

        return calendarItemTypes.map { mapper.map(it, CalendarItemTypeDto::class.java) }
    }

    @ApiOperation(nickname = "createCalendarItemType", value = "Creates a calendarItemType")
    @PostMapping
    fun createCalendarItemType(@RequestBody calendarItemTypeDto: CalendarItemTypeDto): CalendarItemTypeDto {
        val calendarItemType = calendarItemTypeLogic.createCalendarItemType(mapper.map(calendarItemTypeDto, CalendarItemType::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemType creation failed")
        return mapper.map(calendarItemType, CalendarItemTypeDto::class.java)
    }

    @ApiOperation(nickname = "deleteCalendarItemType", value = "Deletes an calendarItemType")
    @DeleteMapping("/{calendarItemTypeIds}")
    fun deleteCalendarItemType(@PathVariable("calendarItemTypeIds") calendarItemTypeIds: String): List<String> {
        return calendarItemTypeLogic.deleteCalendarItemTypes(calendarItemTypeIds.split(','))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemType deletion failed")
    }

    @ApiOperation(nickname = "getCalendarItemType", value = "Gets an calendarItemType")
    @GetMapping("/{calendarItemTypeId}")
    fun getCalendarItemType(@PathVariable calendarItemTypeId: String): CalendarItemTypeDto {
        val calendarItemType = calendarItemTypeLogic.getCalendarItemType(calendarItemTypeId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemType fetching failed")

        return mapper.map(calendarItemType, CalendarItemTypeDto::class.java)
    }


    @ApiOperation(nickname = "modifyCalendarItemType", value = "Modifies an calendarItemType")
    @PutMapping
    fun modifyCalendarItemType(calendarItemTypeDto: CalendarItemTypeDto?): CalendarItemTypeDto {
        val calendarItemType = calendarItemTypeLogic.modifyCalendarTypeItem(mapper.map(calendarItemTypeDto, CalendarItemType::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemType modification failed")

        return mapper.map(calendarItemType, CalendarItemTypeDto::class.java)
    }
}
