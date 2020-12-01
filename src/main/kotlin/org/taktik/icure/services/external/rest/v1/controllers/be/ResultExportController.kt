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

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.be.format.logic.HealthOneLogic
import org.taktik.icure.be.format.logic.KmehrReportLogic
import org.taktik.icure.be.format.logic.MedidocLogic
import org.taktik.icure.utils.FuzzyValues

@RestController
@RequestMapping("/rest/v1/be_result_export")
@Tag(name = "beresultexport")
class ResultExportController(private var healthOneLogic: HealthOneLogic,
                             private val medidocLogic: MedidocLogic,
                             private val kmehrReportLogic: KmehrReportLogic,
                             private val patientLogic: PatientLogic,
                             private val healthcarePartyLogic: HealthcarePartyLogic) {

    @Operation(summary = "Export data", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/medidoc/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportMedidoc(@PathVariable fromHcpId: String,
                      @PathVariable toHcpId: String,
                      @PathVariable patId: String,
                      @PathVariable date: Long,
                      @PathVariable ref: String,
                      @RequestBody bodyText: ByteArray
    ) = mono {
        DefaultDataBufferFactory().join(medidocLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, String(bodyText, Charsets.UTF_8)).toList()).asByteBuffer()
    }

    @Operation(summary = "Export data", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/hl1/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportHealthOne(@PathVariable fromHcpId: String,
                        @PathVariable toHcpId: String,
                        @PathVariable patId: String,
                        @PathVariable date: Long,
                        @PathVariable ref: String,
                        @RequestBody bodyText: ByteArray
    ) = mono {
        DefaultDataBufferFactory().join(healthOneLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, String(bodyText, Charsets.UTF_8)).toList()).asByteBuffer()
    }

    @Operation(summary = "Export data", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @PostMapping("/kmehrreport/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportKmehrReport(@PathVariable fromHcpId: String,
                          @PathVariable toHcpId: String,
                          @PathVariable patId: String,
                          @PathVariable date: Long,
                          @PathVariable ref: String,
                          @RequestParam(required = false) mimeType: Boolean?,
                          @RequestBody bodyText: ByteArray
    ) = mono {
        DefaultDataBufferFactory().join(kmehrReportLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, String(bodyText, Charsets.UTF_8)).toList()).asByteBuffer()
    }
}
