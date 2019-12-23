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
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.be.ehealth.logic.kmehr.medex.MedexLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.services.external.rest.v1.dto.MedexInfoDto

@RestController
@RequestMapping("/rest/v1/medex")
@Api(tags = ["medex"])
class MedexController(private val medexLogic: MedexLogic, private val mapperFacade: MapperFacade) {

    @ApiOperation(nickname = "generateMedex", value = "Generate a Medex XML String", produces = MediaType.APPLICATION_XML_VALUE)
    @PostMapping("/generate", produces = [MediaType.APPLICATION_XML_VALUE])
    suspend fun generateMedex(@RequestBody infos: MedexInfoDto) = medexLogic.createMedex(
            mapperFacade.map(infos.author, HealthcareParty::class.java),
            mapperFacade.map(infos.patient, Patient::class.java),
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
