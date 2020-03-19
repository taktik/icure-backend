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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AgendaLogic
import org.taktik.icure.entities.Agenda
import org.taktik.icure.services.external.rest.v1.dto.AgendaDto
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/agenda")
@Api(tags = ["agenda"])
class AgendaController(private val agendaLogic: AgendaLogic,
                       private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getAgendas", value = "Gets all agendas")
    @GetMapping
    fun getAgendas(): Flux<AgendaDto> {
        val agendas = agendaLogic.getAllEntities()
        return agendas.map { mapper.map(it, AgendaDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "createAgenda", value = "Creates a agenda")
    @PostMapping
    fun createAgenda(@RequestBody agendaDto: AgendaDto) = mono {
        val agenda = agendaLogic.createAgenda(mapper.map(agendaDto, Agenda::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agenda creation failed")

        mapper.map(agenda, AgendaDto::class.java)
    }

    @ApiOperation(nickname = "deleteAgenda", value = "Deletes an agenda")
    @DeleteMapping("/{agendaIds}")
    fun deleteAgenda(@PathVariable agendaIds: String): Flux<DocIdentifier> {
        return agendaLogic.deleteAgenda(agendaIds.split(',')).injectReactorContext()
    }

    @ApiOperation(nickname = "getAgenda", value = "Gets an agenda")
    @GetMapping("/{agendaId}")
    fun getAgenda(@PathVariable agendaId: String) = mono {
        val agenda = agendaLogic.getAgenda(agendaId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda fetching failed")
        mapper.map(agenda, AgendaDto::class.java)
    }

    @ApiOperation(nickname = "getAgendasForUser", value = "Gets all agendas for user")
    @GetMapping("/byUser")
    fun getAgendasForUser(@RequestParam userId: String) = mono {
        agendaLogic.getAllAgendaForUser(userId).firstOrNull()?.let { mapper.map(it, AgendaDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Agendas fetching failed")
    }

    @ApiOperation(nickname = "getReadableAgendasForUser", value = "Gets readable agendas for user")
    @GetMapping("/readableForUser")
    fun getReadableAgendasForUser(@RequestParam userId: String): Flux<AgendaDto> {
        val agendas = agendaLogic.getReadableAgendaForUser(userId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Readable agendas fetching failed")
        return agendas.map { mapper.map(it, AgendaDto::class.java) }.injectReactorContext()
    }


    @ApiOperation(nickname = "modifyAgenda", value = "Modifies an agenda")
    @PutMapping
    fun modifyAgenda(@RequestBody agendaDto: AgendaDto) = mono {
        val agenda = agendaLogic.modifyAgenda(mapper.map(agendaDto, Agenda::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agenda modification failed")

        mapper.map(agenda, AgendaDto::class.java)
    }
}
