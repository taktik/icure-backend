/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.facade.be;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
//import org.taktik.icure.be.ehealth.logic.vitalink.VitalinkLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.recipe.Prescription;
import org.taktik.icure.services.external.rest.v1.dto.be.vitalink.DataEntry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/be_vitalink")
@Api(tags = { "be_vitalink" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class VitalinkFacade {
//	private VitalinkLogic vitalinkLogic;
	private MapperFacade mapper;
    @ApiOperation(
            value = "List sumehrs",
            response = Prescription.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/{niss}")
    @GET
    public List<DataEntry> getSumehrsFromVitalink(@PathParam("niss") String niss, @QueryParam("includeBusinessData") Boolean includeBusinessData, @QueryParam("breakTheGlass") Boolean breakTheGlass, @QueryParam("reason") String reason) {
        return null;
    }

    @ApiOperation(
            value = "Put sumehr",
            response = Prescription.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/{token}/{niss}")
    @POST
    public List<DataEntry> putSumehrOnVitalink(@PathParam("token") String token, @PathParam("niss") String niss, @QueryParam("formatCode") String formatCode, @QueryParam("reference") String reference, @QueryParam("previousVersionId") String previousVersionId, @QueryParam("previousVersionNumber") String previousVersionNumber, String sumehr) throws TokenNotAvailableException, TechnicalConnectorException {
        return null;//vitalinkLogic.putSumehr(token, sumehr, formatCode, reference, niss, previousVersionId, previousVersionNumber).stream().map(de->mapper.map(de, DataEntry.class)).collect(Collectors.toList());
    }

	//@Context
	//public void setVitalinkLogic(VitalinkLogic vitalinkLogic) {
	//	this.vitalinkLogic = vitalinkLogic;
	//}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
}
