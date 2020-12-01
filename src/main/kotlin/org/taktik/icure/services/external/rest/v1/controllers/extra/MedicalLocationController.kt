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

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
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
import org.taktik.icure.asynclogic.MedicalLocationLogic
import org.taktik.icure.services.external.rest.v1.dto.MedicalLocationDto
import org.taktik.icure.services.external.rest.v1.mapper.MedicalLocationMapper
import org.taktik.icure.utils.injectReactorContext

@RestController
@RequestMapping("/rest/v1/medicallocation")
@Tag(name = "medicallocation")
class MedicalLocationController(
        private val medicalLocationLogic: MedicalLocationLogic,
        private val medicalLocationMapper: MedicalLocationMapper
) {

    @Operation(summary = "Creates a medical location")
    @PostMapping
    fun createMedicalLocation(@RequestBody medicalLocationDto: MedicalLocationDto) = mono {
        medicalLocationLogic.createMedicalLocation(medicalLocationMapper.map(medicalLocationDto))?.let { medicalLocationMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Medical location creation failed")
    }

    @Operation(summary = "Deletes a medical location")
    @DeleteMapping("/{locationIds}")
    fun deleteMedicalLocation(@PathVariable locationIds: String) =
            medicalLocationLogic.deleteMedicalLocations(locationIds.split(',')).injectReactorContext()


    @Operation(summary = "Gets a medical location")
    @GetMapping("/{locationId}")
    fun getMedicalLocation(@PathVariable locationId: String) = mono {
        medicalLocationLogic.getMedicalLocation(locationId)?.let { medicalLocationMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "medical location fetching failed")
    }

    @Operation(summary = "Gets all medical locations")
    @GetMapping
    fun getMedicalLocations() = medicalLocationLogic.getAllEntities().map { c -> medicalLocationMapper.map(c) }.injectReactorContext()

    @Operation(summary = "Modifies a medical location")
    @PutMapping
    fun modifyMedicalLocation(@RequestBody medicalLocationDto: MedicalLocationDto) = mono {
        medicalLocationLogic.modifyMedicalLocation(medicalLocationMapper.map(medicalLocationDto))?.let { medicalLocationMapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "medical location modification failed")
    }
}
