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

package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.logic.kmehr.medex.MedexLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.services.external.rest.v1.dto.MedexInfoDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component
@Path("/medex")
@Api(tags = { "medex" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class MedexFacade implements OpenApiFacade {

    private static final Logger logger = LoggerFactory.getLogger(MedexFacade.class);

    private MedexLogic medexLogic;
    private MapperFacade mapperFacade;

    @ApiOperation(
            value = "Generate a Medex XML String",
            response = String.class,
            httpMethod = "POST",
            produces = "application/xml"
    )
    @POST
    @Produces("application/xml")
    @Path("/generate")
    public Response generateMedex(MedexInfoDto infos) {
        Response response = ResponseUtils.ok(medexLogic.createMedex(
                mapperFacade.map(infos.getAuthor(), HealthcareParty.class),
                mapperFacade.map(infos.getPatient(), Patient.class),
                infos.getPatientLanguage(),
                infos.getIncapacityType(),
                infos.getIncapacityReason(),
                infos.getOutOfHomeAllowed(),
                infos.getCertificateDate(),
                infos.getContentDate(),
                infos.getBeginDate(),
                infos.getEndDate(),
                infos.getDiagnosisICD(),
                infos.getDiagnosisICPC(),
                infos.getDiagnosisDescr()
        ));
        return response;
    }

    @Context
    public void setMedexLogic(MedexLogic medexLogic) {
        this.medexLogic = medexLogic;
    }

    @Context
    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
}
