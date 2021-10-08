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

package org.taktik.icure.services.external.rest.v2.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.services.external.rest.v2.dto.InsuranceDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.InsuranceV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController("insuranceControllerV2")
@RequestMapping("/rest/v2/insurance")
@Tag(name = "insurance")
class InsuranceController(private val insuranceLogic: InsuranceLogic,
                          private val insuranceV2Mapper: InsuranceV2Mapper) {

    @Operation(summary = "Creates an insurance")
    @PostMapping
    fun createInsurance(@RequestBody insuranceDto: InsuranceDto) = mono {
        val insurance = insuranceLogic.createInsurance(insuranceV2Mapper.map(insuranceDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Insurance creation failed")

        insuranceV2Mapper.map(insurance)
    }

    @Operation(summary = "Deletes an insurance")
    @DeleteMapping("/{insuranceId}")
    fun deleteInsurance(@PathVariable insuranceId: String) = mono {
        insuranceLogic.deleteInsurance(insuranceId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Insurance deletion failed")
    }

    @Operation(summary = "Gets an insurance")
    @GetMapping("/{insuranceId}")
    fun getInsurance(@PathVariable insuranceId: String) = mono {
        val insurance = insuranceLogic.getInsurance(insuranceId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Insurance fetching failed")
        insuranceV2Mapper.map(insurance)
    }

    @Operation(summary = "Gets insurances by id")
    @PostMapping("/byIds")
    fun getInsurances(@RequestBody insuranceIds: ListOfIdsDto): Flux<InsuranceDto> {
        val insurances = insuranceLogic.getInsurances(HashSet(insuranceIds.ids))
        return insurances.map { insuranceV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets an insurance")
    @GetMapping("/byCode/{insuranceCode}")
    fun listInsurancesByCode(@PathVariable insuranceCode: String): Flux<InsuranceDto> {
        val insurances = insuranceLogic.listInsurancesByCode(insuranceCode)
        return insurances.map { insuranceV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets an insurance")
    @GetMapping("/byName/{insuranceName}")
    fun listInsurancesByName(@PathVariable insuranceName: String): Flux<InsuranceDto> {
        val insurances = insuranceLogic.listInsurancesByName(insuranceName)

        return insurances.map { insuranceV2Mapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Modifies an insurance")
    @PutMapping
    fun modifyInsurance(@RequestBody insuranceDto: InsuranceDto) = mono {
        val insurance = insuranceLogic.modifyInsurance(insuranceV2Mapper.map(insuranceDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Insurance modification failed")

        insuranceV2Mapper.map(insurance)
    }
}
