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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.services.external.rest.v1.dto.PlaceDto
import org.taktik.icure.services.external.rest.v1.mapper.PlaceMapper
import org.taktik.icure.utils.injectReactorContext

@RestController
@RequestMapping("/rest/v1/place")
@Tag(name = "place")
class PlaceController(
        private val placeLogic: PlaceLogic,
        private val placeMapper: PlaceMapper
) {

    @Operation(summary = "Creates a place")
    @PostMapping
    fun createPlace(@RequestBody placeDto: PlaceDto) = mono {
        placeLogic.createPlace(placeMapper.map(placeDto))?.let { placeMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place creation failed")
    }

    @Operation(summary = "Deletes an place")
    @DeleteMapping("/{placeIds}")
    fun deletePlace(@PathVariable placeIds: String) = placeLogic.deletePlace(placeIds.split(',')).injectReactorContext()

    @Operation(summary = "Gets an place")
    @GetMapping("/{placeId}")
    fun getPlace(@PathVariable placeId: String) = mono {
        placeLogic.getPlace(placeId)?.let { placeMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Place fetching failed")
    }

    @Operation(summary = "Gets all places")
    @GetMapping
    fun getPlaces() =
            placeLogic.getAllEntities().let { it.map { c -> placeMapper.map(c) } }.injectReactorContext()

    @Operation(summary = "Modifies an place")
    @PutMapping
    fun modifyPlace(@RequestBody placeDto: PlaceDto) = mono {
        placeLogic.modifyPlace(placeMapper.map(placeDto))?.let { placeMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place modification failed")
    }
}
