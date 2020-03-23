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

import com.google.common.collect.Lists
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.CalendarItemLogic
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto
import org.taktik.icure.services.external.rest.v1.dto.ClassificationDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/calendarItem")
@Api(tags = ["calendarItem"])
class CalendarItemController(private val calendarItemLogic: CalendarItemLogic,
                             private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getCalendarItems", value = "Gets all calendarItems")
    @GetMapping
    fun getCalendarItems(): Flux<CalendarItemDto> {
        val calendarItems = calendarItemLogic.getAllEntities()
        return calendarItems.map { mapper.map(it, CalendarItemDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "createCalendarItem", value = "Creates a calendarItem")
    @PostMapping
    fun createCalendarItem(@RequestBody calendarItemDto: CalendarItemDto) = mono {
        val calendarItem = calendarItemLogic.createCalendarItem(mapper.map(calendarItemDto, CalendarItem::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItem creation failed")

        mapper.map(calendarItem, CalendarItemDto::class.java)
    }

    @ApiOperation(nickname = "deleteCalendarItem", value = "Deletes an calendarItem")
    @DeleteMapping("/{calendarItemIds}")
    fun deleteCalendarItem(@PathVariable calendarItemIds: String): Flux<DocIdentifier> {
        return calendarItemLogic.deleteCalendarItems(calendarItemIds.split(',')).injectReactorContext()
    }

    @ApiOperation(nickname = "getCalendarItem", value = "Gets an calendarItem")
    @GetMapping("/{calendarItemId}")
    fun getCalendarItem(@PathVariable calendarItemId: String) = mono {
        val calendarItem = calendarItemLogic.getCalendarItem(calendarItemId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "CalendarItem fetching failed")

        mapper.map(calendarItem, CalendarItemDto::class.java)
    }


    @ApiOperation(nickname = "modifyCalendarItem", value = "Modifies an calendarItem")
    @PutMapping
    fun modifyCalendarItem(@RequestBody calendarItemDto: CalendarItemDto) = mono {
        val calendarItem = calendarItemLogic.modifyCalendarItem(mapper.map(calendarItemDto, CalendarItem::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItem modification failed")

        mapper.map(calendarItem, CalendarItemDto::class.java)
    }


    @ApiOperation(nickname = "getCalendarItemsByPeriodAndHcPartyId", value = "Get CalendarItems by Period and HcPartyId")
    @PostMapping("/byPeriodAndHcPartyId")
    fun getCalendarItemsByPeriodAndHcPartyId(@RequestParam startDate: Long,
                                             @RequestParam endDate: Long,
                                             @RequestParam hcPartyId: String): Flux<CalendarItemDto> {
        if (hcPartyId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "hcPartyId was empty")
        }
        val calendars = calendarItemLogic.getCalendarItemByPeriodAndHcPartyId(startDate, endDate, hcPartyId)
        return calendars.map { mapper.map(it, CalendarItemDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "getCalendarsByPeriodAndAgendaId", value = "Get CalendarItems by Period and AgendaId")
    @PostMapping("/byPeriodAndAgendaId")
    fun getCalendarsByPeriodAndAgendaId(@RequestParam startDate: Long,
                                        @RequestParam endDate: Long,
                                        @RequestParam agendaId: String): Flux<CalendarItemDto> {
        if (agendaId.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "agendaId was empty")
        }
        val calendars = calendarItemLogic.getCalendarItemByPeriodAndAgendaId(startDate, endDate, agendaId)
        return calendars.map { mapper.map(it, CalendarItemDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(value = "Get calendarItems by id", responseContainer = "Array", response = CalendarItemDto::class, httpMethod = "POST")
    @PostMapping("/byIds")
    fun getCalendarItemsWithIds(@RequestBody calendarItemIds: ListOfIdsDto?): Flux<CalendarItemDto> {
        if (calendarItemIds == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "calendarItemIds was empty")
        }
        val calendars = calendarItemLogic.getCalendarItemByIds(calendarItemIds.ids)
        return calendars.map { mapper.map(it, CalendarItemDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(value = "Find CalendarItems by hcparty and patient", response = CalendarItemDto::class, responseContainer = "Array", httpMethod = "GET", notes = "")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,@RequestParam secretFKeys: String): Flux<CalendarItemDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = calendarItemLogic.findByHCPartySecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys))

        return elementList.map { mapper.map(it, CalendarItemDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(value = "Update delegations in calendarItems", httpMethod = "POST")
    @POST
    @Path("/delegations")
    @Throws(Exception::class)
    fun setCalendarItemsDelegations(stubs: List<IcureStubDto>) = flow {
        val calendarItems = calendarItemLogic.getCalendarItemByIds(stubs.stream().map { obj: IcureStubDto -> obj.id }.collect(Collectors.toList()))
        calendarItems.onEach { calendarItem: CalendarItem ->
            stubs.stream().filter { s: IcureStubDto -> s.id == calendarItem.id }.findFirst().ifPresent { stub: IcureStubDto ->
                stub.delegations.forEach { (s: String?, delegationDtos: List<DelegationDto?>) -> calendarItem.delegations[s] = delegationDtos.stream().map { ddto: DelegationDto? -> mapper.map(ddto, Delegation::class.java) }.collect(Collectors.toSet()) }
                stub.encryptionKeys.forEach { (s: String?, delegationDtos: Set<DelegationDto?>) -> calendarItem.encryptionKeys[s] = delegationDtos.stream().map { ddto: DelegationDto? -> mapper.map(ddto, Delegation::class.java) }.collect(Collectors.toSet()) }
                stub.cryptedForeignKeys.forEach { (s: String?, delegationDtos: List<DelegationDto?>) -> calendarItem.cryptedForeignKeys[s] = delegationDtos.stream().map { ddto: DelegationDto? -> mapper.map(ddto, Delegation::class.java) }.collect(Collectors.toSet()) }
            }
        }
        emitAll(calendarItemLogic.updateEntities(calendarItems.toList()))
    }

}
