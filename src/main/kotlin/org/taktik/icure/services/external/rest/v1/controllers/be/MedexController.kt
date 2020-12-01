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
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.be.ehealth.logic.kmehr.medex.MedexLogic
import org.taktik.icure.services.external.rest.v1.dto.MedexInfoDto
import org.taktik.icure.services.external.rest.v1.mapper.HealthcarePartyMapper
import org.taktik.icure.services.external.rest.v1.mapper.PatientMapper

@RestController
@RequestMapping("/rest/v1/medex")
@Tag(name = "medex")
class MedexController(
        private val medexLogic: MedexLogic,
        private val healthcarePartyMapper: HealthcarePartyMapper,
        private val patientMapper: PatientMapper
) {

    @Operation(summary = "Generate a Medex XML String")
    @PostMapping("/generate", produces = [MediaType.APPLICATION_XML_VALUE])
    fun generateMedex(@RequestBody infos: MedexInfoDto) = mono {
        medexLogic.createMedex(
                healthcarePartyMapper.map(infos.author!!),
                patientMapper.map(infos.patient!!),
                infos.patientLanguage,
                infos.incapacityType,
                infos.incapacityReason,
                infos.outOfHomeAllowed,
                infos.certificateDate,
                infos.contentDate,
                infos.beginDate,
                infos.endDate,
                infos.diagnosisICD,
                infos.diagnosisICPC,
                infos.diagnosisDescr
        )
    }
}
