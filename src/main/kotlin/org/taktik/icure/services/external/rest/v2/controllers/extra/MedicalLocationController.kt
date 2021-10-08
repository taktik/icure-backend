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
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.MedicalLocationLogic
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.MedicalLocationDto
import org.taktik.icure.services.external.rest.v2.mapper.MedicalLocationV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@RestController("medicalLocationControllerV2")
@RequestMapping("/rest/v2/medicallocation")
@Tag(name = "medicallocation")
class MedicalLocationController(
        private val medicalLocationLogic: MedicalLocationLogic,
        private val medicalLocationV2Mapper: MedicalLocationV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Creates a medical location")
    @PostMapping
    fun createMedicalLocation(@RequestBody medicalLocationDto: MedicalLocationDto) = mono {
        medicalLocationLogic.createMedicalLocation(medicalLocationV2Mapper.map(medicalLocationDto))?.let { medicalLocationV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Medical location creation failed")
    }

    @Operation(summary = "Deletes medical locations")
    @DeleteMapping("/delete/batch")
    fun deleteMedicalLocations(@RequestBody locationIds: ListOfIdsDto): Flux<DocIdentifier> {
        return locationIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        medicalLocationLogic.deleteEntities(HashSet(ids)).injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }


    @Operation(summary = "Gets a medical location")
    @GetMapping("/{locationId}")
    fun getMedicalLocation(@PathVariable locationId: String) = mono {
        medicalLocationLogic.getMedicalLocation(locationId)?.let { medicalLocationV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "medical location fetching failed")
    }

    @Operation(summary = "Gets all medical locations")
    @GetMapping
    fun getMedicalLocations() = medicalLocationLogic.getEntities().map { c -> medicalLocationV2Mapper.map(c) }.injectReactorContext()

    @Operation(summary = "Modifies a medical location")
    @PutMapping
    fun modifyMedicalLocation(@RequestBody medicalLocationDto: MedicalLocationDto) = mono {
        medicalLocationLogic.modifyMedicalLocation(medicalLocationV2Mapper.map(medicalLocationDto))?.let { medicalLocationV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "medical location modification failed")
    }
}
