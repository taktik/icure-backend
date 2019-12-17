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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.entities.Insurance
import org.taktik.icure.services.external.rest.v1.dto.InsuranceDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import java.util.*

@RestController
@RequestMapping("/rest/v1/insurance")
@Api(tags = ["insurance"])
class InsuranceController(private val insuranceLogic: InsuranceLogic,
                          private val mapper: MapperFacade) {

    @ApiOperation(nickname = "createInsurance", value = "Creates an insurance")
    @PostMapping
    suspend fun createInsurance(@RequestBody insuranceDto: InsuranceDto): InsuranceDto {
        val insurance = insuranceLogic.createInsurance(mapper.map(insuranceDto, Insurance::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Insurance creation failed")

        return mapper.map(insurance, InsuranceDto::class.java)
    }

    @ApiOperation(nickname = "deleteInsurance", value = "Deletes an insurance")
    @DeleteMapping("/{insuranceId}")
    fun deleteInsurance(@PathVariable insuranceId: String): Flow<DocIdentifier> { // TODO SH now: for all delete endpoints: should we return a flow or use .awaitFirst?
        return insuranceLogic.deleteInsurance(insuranceId)
    }

    @ApiOperation(nickname = "getInsurance", value = "Gets an insurance")
    @GetMapping("/{insuranceId}")
    suspend fun getInsurance(@PathVariable insuranceId: String): InsuranceDto {
        val insurance = insuranceLogic.getInsurance(insuranceId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Insurance fetching failed")
        return mapper.map(insurance, InsuranceDto::class.java)
    }

    @ApiOperation(nickname = "getInsurances", value = "Gets insurances by id")
    @PostMapping("/byIds")
    fun getInsurances(@RequestBody insuranceIds: ListOfIdsDto): Flow<InsuranceDto> {
        val insurances = insuranceLogic.getInsurances(HashSet(insuranceIds.ids))
        return insurances.map { mapper.map(it, InsuranceDto::class.java) }
    }

    @ApiOperation(nickname = "listInsurancesByCode", value = "Gets an insurance")
    @GetMapping("/byCode/{insuranceCode}")
    fun listInsurancesByCode(@PathVariable insuranceCode: String): Flow<InsuranceDto> {
        val insurances = insuranceLogic.listInsurancesByCode(insuranceCode)
        return insurances.map { mapper.map(it, InsuranceDto::class.java) }
    }

    @ApiOperation(nickname = "listInsurancesByName", value = "Gets an insurance")
    @GetMapping("/byName/{insuranceName}")
    fun listInsurancesByName(@PathVariable insuranceName: String): Flow<InsuranceDto> {
        val insurances = insuranceLogic.listInsurancesByName(insuranceName)

        return insurances.map { mapper.map(it, InsuranceDto::class.java) }
    }

    @ApiOperation(nickname = "modifyInsurance", value = "Modifies an insurance")
    @PutMapping
    suspend fun modifyInsurance(@RequestBody insuranceDto: InsuranceDto): InsuranceDto {
        val insurance = insuranceLogic.modifyInsurance(mapper.map(insuranceDto, Insurance::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Insurance modification failed")

        return mapper.map(insurance, InsuranceDto::class.java)
    }
}
