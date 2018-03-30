/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import be.ehealth.technicalconnector.exception.ConnectorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.generalinsurability.GeneralInsurabilityLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.insurability.InsurabilityInfo;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;

@Component
@Path("/be_genins")
@Api(tags = { "be_genins" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class GeneralInsurabilityFacade implements OpenApiFacade {
    private static Logger log = LoggerFactory.getLogger(GeneralInsurabilityFacade.class);

    private MapperFacade mapper;
    private GeneralInsurabilityLogic generalInsurabilityLogic;

    @ApiOperation(
            value = "Request a General Insurability agreement",
            response = InsurabilityInfo.class,
            httpMethod = "GET",
            notes = "Returns an InsurabilityInfo."
    )
    @Path("/{token}/{niss}")
    @GET
    public InsurabilityInfo getGeneralInsurability(@PathParam("token") String token, @PathParam("niss") String patientNiss, @QueryParam("date") Long date, @QueryParam("hospitalized") Boolean hospitalized) throws TokenNotAvailableException, ConnectorException {
        return mapper.map(generalInsurabilityLogic.getGeneralInsurabity(token, patientNiss, null, null, date != null ? new Date(date) : new Date(), hospitalized != null ? hospitalized : false), InsurabilityInfo.class);
    }

    @ApiOperation(
            value = "Request a General Insurability agreement using the Registration Number",
            response = InsurabilityInfo.class,
            httpMethod = "GET",
            notes = "Returns an InsurabilityInfo."
    )
    @Path("/{token}/{insurance}/{regNumber}")
    @GET
    public InsurabilityInfo getGeneralInsurabilityWithRegNumber(@PathParam("token") String token, @PathParam("insurance") String insurance, @PathParam("regNumber") String regNumber, @QueryParam("hospitalized") Boolean hospitalized, @QueryParam("date") Long date) throws TokenNotAvailableException, ConnectorException {
        return mapper.map(generalInsurabilityLogic.getGeneralInsurabity(token, null, insurance, regNumber, date != null ? new Date(date) : new Date(), hospitalized != null ? hospitalized : false), InsurabilityInfo.class);
    }

    @Context
    public void setGeneralInsurabilityLogic(GeneralInsurabilityLogic generalInsurabilityLogic) {
        this.generalInsurabilityLogic = generalInsurabilityLogic;
    }

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }
}
