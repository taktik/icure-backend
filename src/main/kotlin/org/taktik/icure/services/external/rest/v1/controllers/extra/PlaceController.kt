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

package org.taktik.icure.services.external.rest.v1.controllers.extra

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.Place
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.services.external.rest.v1.dto.PlaceDto
import org.taktik.icure.utils.injectReactorContext

@RestController
@RequestMapping("/rest/v1/place")
@Tag(name = "place")
class PlaceController(private val placeLogic: PlaceLogic, private val mapper: MapperFacade) {

    @Operation(summary = "Creates a place")
    @PostMapping
    suspend fun createPlace(@RequestBody placeDto: PlaceDto) =
            placeLogic.createPlace(Mappers.getMapper(Place::class.java)).let { mapper.map(it, PlaceMapper::class.java).map(placeDto) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place creation failed")

    @Operation(summary = "Deletes an place")
    @DeleteMapping("/{placeIds}")
    fun deletePlace(@PathVariable placeIds: String) = placeLogic.deletePlace(placeIds.split(',')).injectReactorContext()

    @Operation(summary = "Gets an place")
    @GetMapping("/{placeId}")
    suspend fun getPlace(@PathVariable placeId: String) =
            placeLogic.getPlace(placeId).let { Mappers.getMapper(PlaceMapper::class.java).map(it) }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Place fetching failed")

    @Operation(summary = "Gets all places")
    @GetMapping
    fun getPlaces() =
            placeLogic.getAllEntities().let { it.map { c -> Mappers.getMapper(PlaceMapper::class.java).map(c) } }.injectReactorContext()

    @Operation(summary = "Modifies an place")
    @PutMapping
    suspend fun modifyPlace(@RequestBody placeDto: PlaceDto) =
            placeLogic.modifyPlace(Mappers.getMapper(Place::class.java)).let { mapper.map(it, PlaceMapper::class.java).map(placeDto) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place modification failed")
}
