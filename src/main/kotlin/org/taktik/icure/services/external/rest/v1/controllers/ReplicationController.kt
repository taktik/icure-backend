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
import org.taktik.icure.entities.Replication
import org.taktik.icure.logic.ReplicationLogic
import org.taktik.icure.services.external.rest.v1.dto.ReplicationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DatabaseSynchronizationDto
import java.util.*


@RestController
@RequestMapping("/replication")
@Api(tags = ["replication"])
class ReplicationController(private val replicationLogic: ReplicationLogic,
                            private val mapper: MapperFacade) {


    @ApiOperation(nickname = "createTemplateReplication", value = "Creates a replication for a speciality database")
    @PostMapping("/template/{replicationHost}/{language}/{specialtyCode}")
    fun createTemplateReplication(
            @RequestParam protocol: String,
            @RequestParam port: String,
            @PathVariable replicationHost: String,
            @PathVariable language: String,
            @PathVariable specialtyCode: String) =
            replicationLogic.createBaseTemplateReplication(protocol, replicationHost, port, language, specialtyCode).let { mapper.map(it, ReplicationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Replication creation failed")

    @ApiOperation(nickname = "createGroupReplication", value = "Creates a replication")
    @PostMapping("/group/{replicationHost}/{groupId}/{password}")
    fun createGroupReplication(
            @RequestParam protocol: String,
            @RequestParam port: String,
            @PathVariable replicationHost: String,
            @PathVariable groupId: String,
            @PathVariable password: String) =
            replicationLogic.createGroupReplication(protocol, replicationHost, port, groupId, password)
                    ?.let { mapper.map(it, ReplicationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Replication creation failed")


    @ApiOperation(nickname = "createReplication", value = "Creates a replication")
    @PostMapping
    fun createReplication(replicationDto: ReplicationDto): ReplicationDto {
        val createdEntities = emptyList<Replication>()
        replicationLogic.createEntities(listOf(mapper.map(replicationDto, Replication::class.java)), createdEntities)

        return createdEntities[0].let { mapper.map(it, ReplicationDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Replication creation failed")
    }

    @ApiOperation(nickname = "createStandardReplication", value = "Creates a standard replication")
    @PostMapping("/standard/{replicationHost}")
    fun createStandardReplication(@PathVariable replicationHost: String): ReplicationDto =
            if (!replicationHost.matches("https?://[a-zA-Z0-9-_.]+:[0-9]+".toRegex())) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create replication: supplied replicationHost is null")
            } else {
                with(ReplicationDto()) {
                    name = replicationHost
                    id = UUID.randomUUID().toString()
                    databaseSynchronizations = Arrays.asList(
                            DatabaseSynchronizationDto("$replicationHost/icure-patient", "http://127.0.0.1:5984/icure-patient"),
                            DatabaseSynchronizationDto("$replicationHost/icure-base", "http://127.0.0.1:5984/icure-base"),
                            DatabaseSynchronizationDto("$replicationHost/icure-healthdata", "http://127.0.0.1:5984/icure-healthdata"),
                            DatabaseSynchronizationDto("http://127.0.0.1:5984/icure-patient", "$replicationHost/icure-patient"),
                            DatabaseSynchronizationDto("http://127.0.0.1:5984/icure-base", "$replicationHost/icure-base"),
                            DatabaseSynchronizationDto("http://127.0.0.1:5984/icure-healthdata", "$replicationHost/icure-healthdata")
                    )
                    return createReplication(this)
                }
            }


    @ApiOperation(nickname = "deleteReplication", value = "Deletes a replication")
    @DeleteMapping("/{replicationId}")
    @Throws(Exception::class)
    fun deleteReplication(@PathVariable replicationId: String) { //TODO MB return id of deleted replication
        replicationLogic.deleteEntities(setOf(replicationId))
    }

    @ApiOperation(nickname = "getReplication", value = "Gets a replication")
    @GetMapping("/{replicationId}")
    fun getReplication(@PathVariable replicationId: String): ReplicationDto {
        return replicationLogic.getEntity(replicationId)?.let { mapper.map(it, ReplicationDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Replication fetching failed")

    }

    @ApiOperation(nickname = "listReplications", value = "Gets a replication")
    @GetMapping
    fun listReplications() = replicationLogic.allEntities?.map { i -> mapper.map(i, ReplicationDto::class.java) }
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing replications failed")

    @ApiOperation(nickname = "modifyReplication", value = "Modifies a replication")
    @PutMapping
    fun modifyReplication(@RequestBody replicationDto: ReplicationDto): ReplicationDto =
            with(listOf(mapper.map(replicationDto, Replication::class.java))) {
                replicationLogic.updateEntities(this)
                if (this.isNotEmpty()) {
                    mapper.map(this[0], ReplicationDto::class.java)
                }
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot modify replication: supplied replicationDto is null")
            }

}
