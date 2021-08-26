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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AgendaLogic
import org.taktik.icure.services.external.rest.v2.dto.AgendaDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.AgendaMapper
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v2/agenda")
@Tag(name = "agenda")
class AgendaController(private val agendaLogic: AgendaLogic,
                       private val agendaMapper: AgendaMapper) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Gets all agendas")
    @GetMapping
    fun getAgendas(): Flux<AgendaDto> {
        val agendas = agendaLogic.getAllEntities()
        return agendas.map { agendaMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Creates a agenda")
    @PostMapping
    fun createAgenda(@RequestBody agendaDto: AgendaDto) = mono {
        val agenda = agendaLogic.createAgenda(agendaMapper.map(agendaDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agenda creation failed")

        agendaMapper.map(agenda)
    }

    @Operation(summary = "Deletes agendas")
    @PostMapping("/delete/batch")
    fun deleteAgendas(@RequestBody agendaIds: ListOfIdsDto): Flux<DocIdentifier> {
        return agendaIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        agendaLogic.deleteByIds(HashSet(ids)).injectReactorContext()
                    }catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Gets an agenda")
    @GetMapping("/{agendaId}")
    fun getAgenda(@PathVariable agendaId: String) = mono {
        val agenda = agendaLogic.getAgenda(agendaId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda fetching failed")
        agendaMapper.map(agenda)
    }

    @Operation(summary = "Gets all agendas for user")
    @GetMapping("/byUser")
    fun getAgendasForUser(@RequestParam userId: String) = mono {
        agendaLogic.getAllAgendaForUser(userId).firstOrNull()?.let { agendaMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Agendas fetching failed")
    }

    @Operation(summary = "Gets readable agendas for user")
    @GetMapping("/readableForUser")
    fun getReadableAgendasForUser(@RequestParam userId: String): Flux<AgendaDto> {
        val agendas = agendaLogic.getReadableAgendaForUser(userId)
        return agendas.map { agendaMapper.map(it) }.injectReactorContext()
    }


    @Operation(summary = "Modifies an agenda")
    @PutMapping
    fun modifyAgenda(@RequestBody agendaDto: AgendaDto) = mono {
        val agenda = agendaLogic.modifyAgenda(agendaMapper.map(agendaDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agenda modification failed")

        agendaMapper.map(agenda)
    }
}
