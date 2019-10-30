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
import org.taktik.icure.entities.MedicalLocation
import org.taktik.icure.logic.MedicalLocationLogic
import org.taktik.icure.services.external.rest.v1.dto.MedicalLocationDto

@RestController
@RequestMapping("/rest/v1/medicallocation")
@Api(tags = ["medicallocation"])
class MedicalLocationController(private val medicalLocationLogic: MedicalLocationLogic, private val mapper: MapperFacade) {

    @ApiOperation(nickname = "createMedicalLocation", value = "Creates a medical location")
    @PostMapping
    fun createMedicalLocation(@RequestBody medicalLocationDto: MedicalLocationDto) =
            medicalLocationLogic.createMedicalLocation(mapper.map(medicalLocationDto, MedicalLocation::class.java))?.let { mapper.map(it, MedicalLocationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Medical location creation failed")

    @ApiOperation(nickname = "deleteMedicalLocation", value = "Deletes a medical location")
    @DeleteMapping("/{locationIds}")
    fun deleteMedicalLocation(@PathVariable locationIds: String) =
            medicalLocationLogic.deleteMedicalLocation(locationIds.split(','))
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "medical location deletion failed.")

    @ApiOperation(nickname = "getMedicalLocation", response = MedicalLocationDto::class, value = "Gets a medical location")
    @GetMapping("/{locationId}")
    fun getMedicalLocation(@PathVariable locationId: String) =
            medicalLocationLogic.getMedicalLocation(locationId)?.let { mapper.map(it, MedicalLocationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "medical location fetching failed")

    @ApiOperation(nickname = "getMedicalLocations", value = "Gets all medical locations")
    @GetMapping
    fun getMedicalLocations() = medicalLocationLogic.allEntities?.let { it.map { c -> mapper.map(c, MedicalLocationDto::class.java) } }
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "medical locations fetching failed")

    @ApiOperation(nickname = "modifyMedicalLocation", value = "Modifies a medical location")
    @PutMapping
    fun modifyMedicalLocation(@RequestBody medicalLocationDto: MedicalLocationDto) =
            medicalLocationLogic.modifyMedicalLocation(mapper.map(medicalLocationDto, MedicalLocation::class.java))?.let { mapper.map(it, MedicalLocationDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "medical location modification failed")
}
