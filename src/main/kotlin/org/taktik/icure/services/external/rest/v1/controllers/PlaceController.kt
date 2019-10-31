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
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.Place
import org.taktik.icure.logic.PlaceLogic
import org.taktik.icure.services.external.rest.v1.dto.PlaceDto

@RestController
@RequestMapping("/place")
@Api(tags = ["place"])
class PlaceController(private val placeLogic: PlaceLogic, private val mapper: MapperFacade) {

    @ApiOperation(nickname = "createPlace", value = "Creates a place")
    @PostMapping
    fun createPlace(@RequestBody placeDto: PlaceDto) =
            placeLogic.createPlace(mapper.map(placeDto, Place::class.java)).let { mapper.map(it, PlaceDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place creation failed")

    @ApiOperation(nickname = "deletePlace", value = "Deletes an place")
    @DeleteMapping("/{placeIds}")
    fun deletePlace(@PathVariable placeIds: String): List<String> = placeLogic.deletePlace(placeIds.split(','))
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place deletion failed.")
                    .also { logger.error(it.message) }

    @ApiOperation(nickname = "getPlace", value = "Gets an place")
    @GetMapping("/{placeId}")
    fun getPlace(@PathVariable placeId: String) =
            placeLogic.getPlace(placeId).let { mapper.map(it, PlaceDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Place fetching failed")

    @ApiOperation(nickname = "getPlaces", value = "Gets all places", nickname = "getPlaces")
    @GetMapping
    fun getPlaces(): List<PlaceDto> =
            placeLogic.allEntities?.let { it.map { c -> mapper.map(c, PlaceDto::class.java) } }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Places fetching failed")

    @ApiOperation(nickname = "modifyPlace", value = "Modifies an place")
    @PutMapping
    fun modifyPlace(@RequestBody placeDto: PlaceDto) =
            placeLogic.modifyPlace(mapper.map(placeDto, Place::class.java)).let { mapper.map(it, PlaceDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place modification failed")

    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)
    }

}
