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
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.PlaceLogic
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.PlaceDto
import org.taktik.icure.services.external.rest.v2.mapper.PlaceV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@RestController("placeControllerV2")
@RequestMapping("/rest/v2/place")
@Tag(name = "place")
class PlaceController(
        private val placeLogic: PlaceLogic,
        private val placeV2Mapper: PlaceV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Creates a place")
    @PostMapping
    fun createPlace(@RequestBody placeDto: PlaceDto) = mono {
        placeLogic.createPlace(placeV2Mapper.map(placeDto))?.let { placeV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place creation failed")
    }

    @Operation(summary = "Deletes places")
    @PostMapping("/delete/batch")
    fun deletePlaces(@RequestBody placeIds: ListOfIdsDto) : Flux<DocIdentifier> {
        return placeIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        placeLogic.deleteEntities(ids).injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Gets an place")
    @GetMapping("/{placeId}")
    fun getPlace(@PathVariable placeId: String) = mono {
        placeLogic.getPlace(placeId)?.let { placeV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Place fetching failed")
    }

    @Operation(summary = "Gets all places")
    @GetMapping
    fun getPlaces() =
            placeLogic.getEntities().let { it.map { c -> placeV2Mapper.map(c) } }.injectReactorContext()

    @Operation(summary = "Modifies an place")
    @PutMapping
    fun modifyPlace(@RequestBody placeDto: PlaceDto) = mono {
        placeLogic.modifyPlace(placeV2Mapper.map(placeDto))?.let { placeV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Place modification failed")
    }
}
