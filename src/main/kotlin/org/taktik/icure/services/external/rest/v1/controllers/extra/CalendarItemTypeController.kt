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

package org.taktik.icure.services.external.rest.v1.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.CalendarItemTypeLogic
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemTypeDto
import org.taktik.icure.services.external.rest.v1.mapper.CalendarItemTypeMapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/calendarItemType")
@Tag(name = "calendarItemType")
class CalendarItemTypeController(private val calendarItemTypeLogic: CalendarItemTypeLogic,
                                 private val calendarItemTypeMapper: CalendarItemTypeMapper) {

    @Operation(summary = "Gets all calendarItemTypes")
    @GetMapping
    fun getCalendarItemTypes(): Flux<CalendarItemTypeDto> =
            calendarItemTypeLogic.getEntities().map { calendarItemTypeMapper.map(it) }.injectReactorContext()

    @Operation(summary = "Gets all calendarItemTypes include deleted")
    @GetMapping("/includeDeleted")
    fun getCalendarItemTypesIncludeDeleted(): Flux<CalendarItemTypeDto> =
            calendarItemTypeLogic.getAllEntitiesIncludeDelete().map { calendarItemTypeMapper.map(it) }.injectReactorContext()

    @Operation(summary = "Creates a calendarItemType")
    @PostMapping
    fun createCalendarItemType(@RequestBody calendarItemTypeDto: CalendarItemTypeDto) = mono {
        calendarItemTypeLogic.createCalendarItemType(calendarItemTypeMapper.map(calendarItemTypeDto))?.let { calendarItemTypeMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemType creation failed")
    }

    @Operation(summary = "Deletes an calendarItemType")
    @DeleteMapping("/{calendarItemTypeIds}")
    fun deleteCalendarItemType(@PathVariable calendarItemTypeIds: String): Flux<DocIdentifier> =
            calendarItemTypeLogic.deleteCalendarItemTypes(calendarItemTypeIds.split(',')).injectReactorContext()

    @Operation(summary = "Gets an calendarItemType")
    @GetMapping("/{calendarItemTypeId}")
    fun getCalendarItemType(@PathVariable calendarItemTypeId: String) = mono {
        calendarItemTypeLogic.getCalendarItemType(calendarItemTypeId)?.let { calendarItemTypeMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "CalendarItemType fetching failed")
    }


    @Operation(summary = "Modifies an calendarItemType")
    @PutMapping
    fun modifyCalendarItemType(@RequestBody calendarItemTypeDto: CalendarItemTypeDto) = mono {
        calendarItemTypeLogic.modifyCalendarTypeItem(calendarItemTypeMapper.map(calendarItemTypeDto))?.let { calendarItemTypeMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemType modification failed")
    }
}
