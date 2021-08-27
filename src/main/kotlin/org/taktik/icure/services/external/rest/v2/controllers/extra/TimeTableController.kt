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

package org.taktik.icure.services.external.rest.v2.controllers.extra

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
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.TimeTableLogic
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.entities.embed.TimeTableHour
import org.taktik.icure.entities.embed.TimeTableItem
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.TimeTableDto
import org.taktik.icure.services.external.rest.v2.mapper.TimeTableV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.util.*

@ExperimentalCoroutinesApi
@RestController("timeTableControllerV2")
@RequestMapping("/rest/v2/timeTable")
@Tag(name = "timeTable")
class TimeTableController(private val timeTableLogic: TimeTableLogic,
                          private val timeTableV2Mapper: TimeTableV2Mapper) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Creates a timeTable")
    @PostMapping
    fun createTimeTable(@RequestBody timeTableDto: TimeTableDto) =
            mono {
                timeTableLogic.createTimeTable(timeTableV2Mapper.map(timeTableDto))?.let { timeTableV2Mapper.map(it) }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable creation failed")
            }

    @Operation(summary = "Deletes an timeTable")
    @PostMapping("/delete/batch")
    fun deleteTimeTable(@RequestBody timeTableIds: ListOfIdsDto): Flux<DocIdentifier> {
        return timeTableIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        timeTableLogic.deleteEntities(HashSet(ids)).injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Gets a timeTable")
    @GetMapping("/{timeTableId}")
    fun getTimeTable(@PathVariable timeTableId: String) =
            mono {
                if (timeTableId.equals("new", ignoreCase = true)) {
                    //Create an hourItem
                    val timeTableHour = TimeTableHour(
                            startHour = java.lang.Long.parseLong("0800"),
                            endHour = java.lang.Long.parseLong("0900"))

                    //Create a timeTableItem
                    val timeTableItem = TimeTableItem(
                            calendarItemTypeId = "consult",
                            days = mutableListOf("monday"),
                            recurrenceTypes = ArrayList(),
                            hours = mutableListOf(timeTableHour)
                    )
                    //Create the timeTable
                    val timeTable = TimeTable(
                            id = UUID.randomUUID().toString(),
                            startTime = java.lang.Long.parseLong("20180601000"),
                            endTime = java.lang.Long.parseLong("20180801000"),
                            name = "myPeriod",
                            items = mutableListOf(timeTableItem)
                    )

                    //Return it
                    timeTableV2Mapper.map(timeTable)
                } else {
                    timeTableLogic.getTimeTable(timeTableId)?.let { timeTableV2Mapper.map(it) }
                            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "TimeTable fetching failed")
                }
            }

    @Operation(summary = "Modifies an timeTable")
    @PutMapping
    fun modifyTimeTable(@RequestBody timeTableDto: TimeTableDto) =
            mono {
                timeTableLogic.modifyTimeTable(timeTableV2Mapper.map(timeTableDto))?.let { timeTableV2Mapper.map(it) }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "TimeTable modification failed")
            }

    @Operation(summary = "Get TimeTables by Period and AgendaId")
    @PostMapping("/byPeriodAndAgendaId")
    fun getTimeTablesByPeriodAndAgendaId(@Parameter(required = true) @RequestParam startDate: Long,
                                         @Parameter(required = true) @RequestParam endDate: Long,
                                         @Parameter(required = true) @RequestParam agendaId: String): Flux<TimeTableDto> =
            flow {
                if (agendaId.isBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
                }
                emitAll(timeTableLogic.getTimeTablesByPeriodAndAgendaId(startDate, endDate, agendaId).map { timeTableV2Mapper.map(it) })
            }.injectReactorContext()

    @Operation(summary = "Get TimeTables by AgendaId")
    @PostMapping("/byAgendaId")
    fun getTimeTablesByAgendaId(@Parameter(required = true) @RequestParam agendaId: String): Flux<TimeTableDto> =
            flow {
                if (agendaId.isBlank()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
                }
                emitAll(timeTableLogic.getTimeTablesByAgendaId(agendaId).map { timeTableV2Mapper.map(it) })
            }.injectReactorContext()

}
