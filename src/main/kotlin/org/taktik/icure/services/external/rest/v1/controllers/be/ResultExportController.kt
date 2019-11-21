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

package org.taktik.icure.services.external.rest.v1.controllers.be

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.taktik.icure.be.format.logic.HealthOneLogic
import org.taktik.icure.be.format.logic.KmehrReportLogic
import org.taktik.icure.be.format.logic.MedidocLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.utils.FuzzyValues
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/rest/v1/be_result_export")
@Api(tags = ["be_result_export"])
class ResultExportController(private var healthOneLogic: HealthOneLogic,
                             private val medidocLogic: MedidocLogic,
                             private val kmehrReportLogic: KmehrReportLogic,
                             private val patientLogic: PatientLogic,
                             private val healthcarePartyLogic: HealthcarePartyLogic) {

    @ApiOperation(nickname = "exportMedidoc", value = "Export data")
    @PostMapping("/medidoc/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportMedidoc(@PathVariable fromHcpId: String,
                      @PathVariable toHcpId: String,
                      @PathVariable patId: String,
                      @PathVariable date: Long,
                      @PathVariable ref: String,
                      @RequestBody bodyText: ByteArray,
                      response: HttpServletResponse) {
        medidocLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, String(bodyText, Charsets.UTF_8), response.outputStream)
    }

    @ApiOperation(nickname = "exportHealthOne", value = "Export data")
    @PostMapping("/hl1/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportHealthOne(@PathVariable fromHcpId: String,
                        @PathVariable toHcpId: String,
                        @PathVariable patId: String,
                        @PathVariable date: Long,
                        @PathVariable ref: String,
                        @RequestBody bodyText: ByteArray,
                        response: HttpServletResponse) {
        healthOneLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, String(bodyText, Charsets.UTF_8), response.outputStream)
    }

    @ApiOperation(nickname = "exportKmehrReport", value = "Export data")
    @PostMapping("/kmehrreport/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportKmehrReport(@PathVariable fromHcpId: String,
                          @PathVariable toHcpId: String,
                          @PathVariable patId: String,
                          @PathVariable date: Long,
                          @PathVariable ref: String,
                          @RequestParam(required = false) mimeType: Boolean?,
                          @RequestBody bodyText: ByteArray,
                          response: HttpServletResponse) {
        kmehrReportLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, String(bodyText, Charsets.UTF_8), response.outputStream)
    }
}
