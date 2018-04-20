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

package org.taktik.icure.services.external.rest.v1.facade.be;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.be.format.logic.HealthOneLogic;
import org.taktik.icure.be.format.logic.MedidocLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.FuzzyValues;
import org.taktik.icure.utils.ResponseUtils;

@Component
@Path("/be_result_export")
@Api(tags = { "be_result_export" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class ResultExportFacade implements OpenApiFacade {
    private static final Logger logger = LoggerFactory.getLogger(ResultExportFacade.class);

    private HealthOneLogic  healthOneLogic;
    private MedidocLogic    medidocLogic;
    private PatientLogic    patientLogic;
    private HealthcarePartyLogic healthcarePartyLogic;

    @Context
    public void setPatientLogic(PatientLogic patientLogic) {
        this.patientLogic = patientLogic;
    }

    @Context
    public void setMedidocLogic(MedidocLogic medidocLogic) {
        this.medidocLogic = medidocLogic;
    }

	@Context
	public void setHealthOneLogic(HealthOneLogic healthOneLogic) {
		this.healthOneLogic = healthOneLogic;
	}

	@Context
    public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
        this.healthcarePartyLogic = healthcarePartyLogic;
    }

    @ApiOperation(value = "Export data", httpMethod = "POST")
    @POST
    @Path("/medidoc/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportMedidoc(@PathParam("fromHcpId") String fromHcpId, @PathParam("toHcpId") String toHcpId, @PathParam("patId") String patId, @PathParam("date") Long date, @PathParam("ref") String ref, @QueryParam("mustCrypt") Boolean mustCrypt, byte[] bodyText) {
        return ResponseUtils.ok((StreamingOutput) output -> medidocLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, new String(bodyText,"UTF8"), output));
    }

    @ApiOperation(value = "Export data", httpMethod = "POST")
    @POST
    @Path("/hl1/{fromHcpId}/{toHcpId}/{patId}/{date}/{ref}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportHealthOne(@PathParam("fromHcpId") String fromHcpId, @PathParam("toHcpId") String toHcpId, @PathParam("patId") String patId, @PathParam("date") Long date, @PathParam("ref") String ref, @QueryParam("mustCrypt") Boolean mustCrypt, byte[] bodyText) {
        return ResponseUtils.ok((StreamingOutput) output -> healthOneLogic.doExport(healthcarePartyLogic.getHealthcareParty(fromHcpId), healthcarePartyLogic.getHealthcareParty(toHcpId), patientLogic.getPatient(patId), FuzzyValues.getDateTime(date), ref, new String(bodyText,"UTF8"), output));
    }

    @ExceptionHandler(Exception.class)
    Response exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseUtils.internalServerError(e.getMessage());
    }

}
