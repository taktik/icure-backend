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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.logic.addressbook.AddressBookLogic;
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/be_ab")
@Api(tags = { "be_ab" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class AddressBookFacade implements OpenApiFacade {
    private static Logger log = LoggerFactory.getLogger(AddressBookFacade.class);

    private MapperFacade mapper;
    private AddressBookLogic addressBookLogic;

    @ApiOperation(
            value = "Get a doctor information per ssin",
            response = HealthcarePartyDto.class,
            httpMethod = "GET",
            notes = "Returns a Healthcare Party."
    )
    @Path("/ssin/{ssin}/{language}")
    @GET
    public HealthcarePartyDto getHealthcarePartyBySsin(@PathParam("ssin") String ssin, @PathParam("language") String language) {
        return mapper.map(addressBookLogic.getHealthcarePartyBySsin(ssin, language), HealthcarePartyDto.class);
    }

    @ApiOperation(
            value = "Get a doctor information per nihii",
            response = HealthcarePartyDto.class,
            httpMethod = "GET",
            notes = "Returns a Healthcare Party."
    )
    @Path("/nihii/{nihii}/{language}")
    @GET
    public HealthcarePartyDto getHealthcarePartyByNihii(@PathParam("nihii") String nihii, @PathParam("language") String language) {
        return mapper.map(addressBookLogic.getHealthcarePartyByNihii(nihii, language), HealthcarePartyDto.class);
    }

    @ApiOperation(
            value = "Find doctors information per first and last name",
            response = HealthcarePartyDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = "Returns a Healthcare Party."
    )
    @Path("/find")
    @GET
    public List<HealthcarePartyDto> searchHealthcareParties(@QueryParam("lastName") String lastName, @QueryParam("firstName") String firstName) {
        return addressBookLogic.searchHealthcareParties(lastName,firstName).stream().map(it-> mapper.map(it,HealthcarePartyDto.class)).collect(Collectors.toList());
    }

	@ApiOperation(
		value = "Find hospitals information per name",
		response = HealthcarePartyDto.class,
		responseContainer = "Array",
		httpMethod = "GET",
		notes = "Returns a Healthcare Party."
	)
	@Path("/org/find")
	@GET
	public List<HealthcarePartyDto> searchOrganizations(@QueryParam("name") String name, @QueryParam("language") String language) {
		return addressBookLogic.searchOrganisations(name, language).stream().map(it-> mapper.map(it,HealthcarePartyDto.class)).collect(Collectors.toList());
	}


	@Context
    public void setAddressBookLogic(AddressBookLogic addressBookLogic) {
        this.addressBookLogic = addressBookLogic;
    }

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }
}
