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
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
import org.taktik.icure.asynclogic.CalendarItemLogic
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v2.dto.CalendarItemDto
import org.taktik.icure.services.external.rest.v2.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.CalendarItemV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController("calendarItemControllerV2")
@RequestMapping("/rest/v2/calendarItem")
@Tag(name = "calendarItem")
class CalendarItemController(private val calendarItemLogic: CalendarItemLogic,
                             private val calendarItemV2Mapper: CalendarItemV2Mapper,
                             private val delegationV2Mapper: DelegationV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Gets all calendarItems")
    @GetMapping
    fun getCalendarItems(): Flux<CalendarItemDto> {
        val calendarItems = calendarItemLogic.getEntities()
        return calendarItems.map { calendarItemV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Creates a calendarItem")
    @PostMapping
    fun createCalendarItem(@RequestBody calendarItemDto: CalendarItemDto) = mono {
        val calendarItem = calendarItemLogic.createCalendarItem(calendarItemV2Mapper.map(calendarItemDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItem creation failed")

        calendarItemV2Mapper.map(calendarItem)
    }

    @Operation(summary = "Deletes calendarItems")
    @PostMapping("/delete/batch")
    fun deleteCalendarItems(@RequestBody calendarItemIds: ListOfIdsDto): Flux<DocIdentifier> =
            calendarItemLogic.deleteCalendarItems(calendarItemIds.ids).injectReactorContext()

    @Deprecated(message = "Use deleteItemCalendars instead")
    @Operation(summary = "Deletes an calendarItem")
    @DeleteMapping("/{calendarItemIds}")
    fun deleteCalendarItem(@PathVariable calendarItemIds: String): Flux<DocIdentifier> =
            calendarItemLogic.deleteCalendarItems(calendarItemIds.split(',')).injectReactorContext()


    @Operation(summary = "Gets an calendarItem")
    @GetMapping("/{calendarItemId}")
    fun getCalendarItem(@PathVariable calendarItemId: String) = mono {
        val calendarItem = calendarItemLogic.getCalendarItem(calendarItemId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "CalendarItem fetching failed")

        calendarItemV2Mapper.map(calendarItem)
    }


    @Operation(summary = "Modifies an calendarItem")
    @PutMapping
    fun modifyCalendarItem(@RequestBody calendarItemDto: CalendarItemDto) = mono {
        val calendarItem = calendarItemLogic.modifyCalendarItem(calendarItemV2Mapper.map(calendarItemDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItem modification failed")

        calendarItemV2Mapper.map(calendarItem)
    }


    @Operation(summary = "Get CalendarItems by Period and HcPartyId")
    @PostMapping("/byPeriodAndHcPartyId")
    fun getCalendarItemsByPeriodAndHcPartyId(@RequestParam startDate: Long,
                                             @RequestParam endDate: Long,
                                             @RequestParam hcPartyId: String): Flux<CalendarItemDto> {
        if (hcPartyId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "hcPartyId was empty")
        }
        val calendars = calendarItemLogic.getCalendarItemByPeriodAndHcPartyId(startDate, endDate, hcPartyId)
        return calendars.map { calendarItemV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Get CalendarItems by Period and AgendaId")
    @PostMapping("/byPeriodAndAgendaId")
    fun getCalendarsByPeriodAndAgendaId(@RequestParam startDate: Long,
                                        @RequestParam endDate: Long,
                                        @RequestParam agendaId: String): Flux<CalendarItemDto> {
        if (agendaId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
        }
        val calendars = calendarItemLogic.getCalendarItemByPeriodAndAgendaId(startDate, endDate, agendaId)
        return calendars.map { calendarItemV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Get calendarItems by ids")
    @PostMapping("/byIds")
    fun getCalendarItemsWithIds(@RequestBody calendarItemIds: ListOfIdsDto?): Flux<CalendarItemDto> {
        if (calendarItemIds == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "calendarItemIds was empty")
        }
        val calendars = calendarItemLogic.getCalendarItems(calendarItemIds.ids)
        return calendars.map { calendarItemV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Find CalendarItems by hcparty and patient", description = "")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findCalendarItemsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String,@RequestParam secretFKeys: String): Flux<CalendarItemDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = calendarItemLogic.listCalendarItemsByHCPartyAndSecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys))

        return elementList.map { calendarItemV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Find CalendarItems by recurrenceId", description = "")
    @GetMapping("/byRecurrenceId")
    fun findCalendarItemsByRecurrenceId (@RequestParam recurrenceId: String): Flux<CalendarItemDto> {
        val elementList = calendarItemLogic.getCalendarItemsByRecurrenceId(recurrenceId)
        return elementList.map { calendarItemV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Update delegations in calendarItems")
    @PostMapping("/delegations")
    fun setCalendarItemsDelegations(stubs: List<IcureStubDto>) = flow {
        val calendarItems = calendarItemLogic.getCalendarItems(stubs.map { obj: IcureStubDto -> obj.id }).map { ci ->
            stubs.find { s -> s.id == ci.id }?.let { stub ->
                ci.copy(
                        delegations = ci.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = ci.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = ci.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels }
                )
            } ?: ci
        }
        emitAll(calendarItemLogic.modifyEntities(calendarItems.toList()).map { calendarItemV2Mapper.map(it) })
    }.injectReactorContext()
}
