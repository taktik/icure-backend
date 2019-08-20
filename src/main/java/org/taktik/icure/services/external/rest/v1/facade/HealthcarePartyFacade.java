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
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Replication;
import org.taktik.icure.entities.User;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.exceptions.UserRegistrationException;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.ReplicationLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;
import org.taktik.icure.services.external.rest.v1.dto.PublicKeyDto;
import org.taktik.icure.services.external.rest.v1.dto.ReplicationDto;
import org.taktik.icure.services.external.rest.v1.dto.SignUpDto;
import org.taktik.icure.services.external.rest.v1.dto.UserDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Path("/hcparty")
@Api(tags = { "hcparty" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class HealthcarePartyFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(HealthcarePartyFacade.class);

	private MapperFacade mapper;
	private UserLogic userLogic;
	private HealthcarePartyLogic healthcarePartyLogic;
	private ReplicationLogic replicationLogic;
	private ICureSessionLogic sessionLogic;

	private org.taktik.icure.services.external.rest.v1.dto.PaginatedList<HealthcarePartyDto> getHealthcarePartyDtoPaginatedList(PaginatedList<HealthcareParty> healthcareParties) {
		if (healthcareParties.getRows() == null) {
			healthcareParties.setRows(new ArrayList<>());
		}

		org.taktik.icure.services.external.rest.v1.dto.PaginatedList<HealthcarePartyDto> healthcarePartyDtoPaginatedList =
				new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
		mapper.map(healthcareParties, healthcarePartyDtoPaginatedList, new TypeBuilder<PaginatedList<HealthcareParty>>() {
		}.build(), new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<HealthcarePartyDto>>() {
		}.build());
		return healthcarePartyDtoPaginatedList;
	}

	@ApiOperation(
            value = "List healthcare parties with(out) pagination",
            response = org.taktik.icure.services.external.rest.v1.dto.HcPartyPaginatedList.class,
            httpMethod = "GET",
            notes = "Returns a list of healthcare parties."
    )
    @GET
    public Response listHealthcareParties(
            @ApiParam(value = "A healthcare party Last name", required = false) @QueryParam("startKey") String startKey,
            @ApiParam(value = "A healthcare party document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
            @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit,
			@ApiParam(value = "Descending", required = false) @QueryParam("desc") Boolean desc) {

        Response response;

        PaginationOffset<String> paginationOffset = new PaginationOffset<>(startKey, startDocumentId, null, limit);

        PaginatedList<HealthcareParty> healthcareParties;
        healthcareParties = healthcarePartyLogic.listHealthcareParties(paginationOffset, desc);

        if (healthcareParties != null) {
			org.taktik.icure.services.external.rest.v1.dto.PaginatedList<HealthcarePartyDto> healthcarePartyDtoPaginatedList = getHealthcarePartyDtoPaginatedList(healthcareParties);
            response = ResponseUtils.ok(healthcarePartyDtoPaginatedList);

        } else {
            response = ResponseUtils.internalServerError("Listing healthcare parties failed");
        }

        return response;
    }

    @ApiOperation(
			value = "Find healthcare parties by name with(out) pagination",
			response = org.taktik.icure.services.external.rest.v1.dto.HcPartyPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of healthcare parties."
	)
	@GET
    @Path("/byName")
	public Response findByName(
            @ApiParam(value = "The Last name search value", required = false) @QueryParam("name") String name,
			@ApiParam(value = "A healthcare party Last name", required = false) @QueryParam("startKey") String startKey,
			@ApiParam(value = "A healthcare party document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit,
			@ApiParam(value = "Descending", required = false) @QueryParam("desc") Boolean desc) {

		if (name==null || name.length()==0) {
			return this.listHealthcareParties(startKey, startDocumentId, limit, desc);
		}

		Response response;

		PaginationOffset<String> paginationOffset = new PaginationOffset<>(startKey, startDocumentId, null, limit);

		PaginatedList<HealthcareParty> healthcareParties;
		healthcareParties = healthcarePartyLogic.findHealthcareParties(name, paginationOffset, desc);

		if (healthcareParties != null) {
			org.taktik.icure.services.external.rest.v1.dto.PaginatedList<HealthcarePartyDto> healthcarePartyDtoPaginatedList = getHealthcarePartyDtoPaginatedList(healthcareParties);
			response = ResponseUtils.ok(healthcarePartyDtoPaginatedList);
		} else {
			response = ResponseUtils.internalServerError("Listing healthcare parties failed");
		}

		return response;
	}

	@ApiOperation(
		value = "Find healthcare parties by nihii or ssin with(out) pagination",
		response = org.taktik.icure.services.external.rest.v1.dto.HcPartyPaginatedList.class,
		httpMethod = "GET",
		notes = "Returns a list of healthcare parties."
	)
	@GET
	@Path("/byNihiiOrSsin/{searchValue}")
	public Response findBySsinOrNihii(
		@PathParam("searchValue") String searchValue,
		@ApiParam(value = "A healthcare party Last name", required = false) @QueryParam("startKey") String startKey,
		@ApiParam(value = "A healthcare party document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
		@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit,
		@ApiParam(value = "Descending", required = false) @QueryParam("desc") Boolean desc) {

		Response response;

		PaginationOffset<String> paginationOffset = new PaginationOffset<>(startKey, startDocumentId, null, limit);

		PaginatedList<HealthcareParty> healthcareParties;
		healthcareParties = healthcarePartyLogic.findHealthcarePartiesBySsinOrNihii(searchValue, paginationOffset, desc);

		if (healthcareParties != null) {
			org.taktik.icure.services.external.rest.v1.dto.PaginatedList<HealthcarePartyDto> healthcarePartyDtoPaginatedList = getHealthcarePartyDtoPaginatedList(healthcareParties);
			response = ResponseUtils.ok(healthcarePartyDtoPaginatedList);
		} else {
			response = ResponseUtils.internalServerError("Listing healthcare parties failed");
		}

		return response;
	}

	@ApiOperation(
			value = "Find healthcare parties by name with(out) pagination",
			response = HealthcarePartyDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of healthcare parties."
	)
	@GET
	@Path("/byNameStrict/{name}")
	public Response listByName(
			@ApiParam(value = "The Last name search value", required = false) @PathParam("name") String name) {

		Response response;

		List<HealthcareParty> healthcareParties = healthcarePartyLogic.listByName(name);

		if (healthcareParties != null) {
			response = ResponseUtils.ok(healthcareParties.stream().map(h->mapper.map(h,HealthcarePartyDto.class)).collect(Collectors.toList()));
		} else {
			response = ResponseUtils.internalServerError("Listing healthcare parties failed");
		}

		return response;
	}

	@ApiOperation(
			value = "Find healthcare parties by name with(out) pagination",
			response = org.taktik.icure.services.external.rest.v1.dto.HcPartyPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of healthcare parties."
	)
	@GET
	@Path("/bySpecialityAndPostCode/{type}/{spec}/{firstCode}/to/{lastCode}")
	public Response findBySpecialityAndPostCode(
			@ApiParam(value = "The type of the HCP (persphysician)", required = false) @QueryParam("type") String type,
			@ApiParam(value = "The speciality of the HCP", required = false) @QueryParam("spec") String spec,
			@ApiParam(value = "The first postCode for the HCP", required = false) @QueryParam("firstCode") String firstCode,
			@ApiParam(value = "The last postCode for the HCP", required = false) @QueryParam("lastCode") String lastCode,
			@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit) {

		Response response;

		PaginatedList<HealthcareParty> healthcareParties;
		healthcareParties = healthcarePartyLogic.findHealthcareParties(type,spec,firstCode,lastCode);

		if (healthcareParties != null) {
			org.taktik.icure.services.external.rest.v1.dto.PaginatedList<HealthcarePartyDto> healthcarePartyDtoPaginatedList = getHealthcarePartyDtoPaginatedList(healthcareParties);
			response = ResponseUtils.ok(healthcarePartyDtoPaginatedList);

		} else {
			response = ResponseUtils.internalServerError("Listing healthcare parties failed");
		}

		return response;
	}

	@ApiOperation(
			value = "Create a healthcare party",
			response = HealthcarePartyDto.class,
			httpMethod = "POST",
			notes = "One of Name or Last name+First name, Nihii, and Public key are required."
	)
    @POST
	public Response createHealthcareParty(HealthcarePartyDto h) {
		if (h == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		HealthcareParty hcParty;
		try {
			hcParty = healthcarePartyLogic.createHealthcareParty(mapper.map(h, HealthcareParty.class));
		} catch (MissingRequirementsException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

		boolean succeed = (hcParty != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(hcParty, HealthcarePartyDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Healthcare party creation failed.").build();
		}
	}

	@ApiOperation(response = ReplicationDto.class, value = "Creates a replication for a speciality database")
	@POST
	@Path("/replication/template/{replicationHost}/{language}/{specialtyCode}")
	public Response createTemplateReplication(@QueryParam("protocol") String protocol, @PathParam("replicationHost") String replicationHost, @QueryParam("port") String port, @PathParam("language") String language, @PathParam("specialtyCode") String specialtyCode) throws Exception {
		Replication rep = replicationLogic.createBaseTemplateReplication(protocol, replicationHost, port, language, specialtyCode);
		return rep != null ? ResponseUtils.ok(rep) : ResponseUtils.internalServerError("Replication creation failed");
	}

	@ApiOperation(
			value = "Create a healthcare party sign up procedure",
			response = HealthcarePartyDto.class,
			httpMethod = "POST",
			notes = "Email, Last name, First name and Nihii are required"
	)
    @POST
    @Path("/signup")
	public Response createHealthcarePartySignUp(SignUpDto h) {
		if (h == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		HealthcareParty hcParty;
		try {
			hcParty = healthcarePartyLogic.createHealthcareParty(mapper.map(h.getHealthcarePartyDto(), HealthcareParty.class));
		} catch (MissingRequirementsException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

		if (hcParty == null) {
			return Response.status(500).type("text/plain").entity("Healthcare party signup failed.").build();
		}

		User user;
		try {
			UserDto userDto = h.getUserDto();
			user = userLogic.registerUser(mapper.map(userDto,User.class), userDto.getPassword());
		} catch (UserRegistrationException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

		if (user == null) {
			return Response.status(500).type("text/plain").entity("Healthcare party signup failed.").build();
		}

		return Response.ok().entity(mapper.map(hcParty, HealthcarePartyDto.class)).build();
	}


    @ApiOperation(
            value = "Get the HcParty encrypted AES keys indexed by owner",
			httpMethod = "GET",
            notes = "(key, value) of the map is as follows: (ID of the owner of the encrypted AES key, encrypted AES key)"
    )
    @GET
	@Path("/{healthcarePartyId}/keys")
    public Map<String,String> getHcPartyKeysForDelegate(@PathParam("healthcarePartyId") String healthcarePartyId) {
		if (healthcarePartyId == null) {
			throw new IllegalArgumentException("A required query parameter was not specified for this request.");
			//return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Map<String, String> hcPartyKeysForDelegate = healthcarePartyLogic.getHcPartyKeysForDelegate(healthcarePartyId);

		boolean succeed = (hcPartyKeysForDelegate != null);
		if (succeed) {
			return hcPartyKeysForDelegate;//Response.ok().entity(hcPartyKeysForDelegate).build();
		} else {
			throw new IllegalStateException("A problem regarding fetching keys. Read the app logs.");
			//return Response.status(500).type("text/plain").entity("A problem regarding fetching keys. Read the app logs.").build();
		}
	}

	@ApiOperation(
			value = "Get a healthcareParty by his ID",
			response = HealthcarePartyDto.class,
			httpMethod = "GET",
			notes = "General information about the healthcare Party"
	)
	@GET
	@Path("/{healthcarePartyId}")
	public Response getHealthcareParty(@PathParam("healthcarePartyId") String healthcarePartyId) {
		if (healthcarePartyId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		HealthcareParty healthcareParty = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);

		boolean succeed = (healthcareParty != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(healthcareParty, HealthcarePartyDto.class)).build();
		} else {
			return Response.status(404).type("text/plain").entity("A problem regarding fetching the healthcare party. Probable reasons: no such party exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Get healthcareParties by their IDs",
			response = HealthcarePartyDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "General information about the healthcare Party"
	)
	@GET
	@Path("/byIds/{healthcarePartyIds}")
	public Response getHealthcareParties(@PathParam("healthcarePartyIds") String healthcarePartyIds) {
		if (healthcarePartyIds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<HealthcareParty> healthcareParties = healthcarePartyLogic.getHealthcareParties(Arrays.asList(healthcarePartyIds.split(",")));

		boolean succeed = (healthcareParties != null);
		if (succeed) {
			return Response.ok().entity(healthcareParties.stream().map((h) -> mapper.map(h, HealthcarePartyDto.class)).collect(Collectors.toList())).build();
		} else {
			return Response.status(404).type("text/plain").entity("A problem regarding fetching the healthcare party. Probable reasons: no such party exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Find children of an healthcare parties",
			response = HealthcarePartyDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Return a list of children hcp."
	)
	@GET
	@Path("/{parentId}/children")
	public Response getHealthcarePartiesByParentId(@PathParam("parentId") String parentId){
		if (parentId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<HealthcareParty> healthcareParties = healthcarePartyLogic.getHealthcarePartiesByParentId(parentId);

		boolean succeed = (healthcareParties != null);
		if (succeed) {
			return Response.ok().entity(healthcareParties.stream().map((h) -> mapper.map(h, HealthcarePartyDto.class)).collect(Collectors.toList())).build();
		} else {
			return Response.status(404).type("text/plain").entity("A problem regarding fetching the healthcare party. Probable reasons: no such party exists, or server error. Please try again or read the server log.").build();
		}
	}


	@ApiOperation(
			value = "Get public key of a healthcare party",
			response = PublicKeyDto.class,
			httpMethod = "GET",
			notes = "Returns the public key of a healthcare party in Hex"
	)
	@GET
	@Path("/{healthcarePartyId}/publicKey")
	public Response getPublicKey(@PathParam("healthcarePartyId") String healthcarePartyId) {

		String publicKey;
		try {
			publicKey = healthcarePartyLogic.getPublicKey(healthcarePartyId);
		} catch (DocumentNotFoundException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(404).type("text/plain").entity(e.getMessage()).build();
		}

		boolean succeed = (publicKey != null);
		if (succeed) {
			return Response.ok().entity(new PublicKeyDto(healthcarePartyId, publicKey)).build();
		} else {
			return Response.status(500).type("text/plain").entity("No public key is found.").build();
		}
	}

	@ApiOperation(
			value = "Get the current healthcare party if logged in.",
			response = HealthcarePartyDto.class,
			httpMethod = "GET",
			notes = "General information about the current healthcare Party"
	)
	@GET
	@Path("/current")
	public Response getCurrentHealthcareParty() {

		HealthcareParty healthcareParty;
		healthcareParty = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentHealthcarePartyId());

		boolean succeed = (healthcareParty != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(healthcareParty, HealthcarePartyDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("A problem regarding fetching the current healthcare party. Probable reasons: no healthcare party is logged in, or server error. Please try again or read the server log.").build();
		}
	}


	@ApiOperation(
			value = "Delete a healthcare party",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "DELETE",
			notes = "Deleting a healthcareParty. Response is an array containing the id of deleted healthcare party."
	)
	@DELETE
	@Path("/{healthcarePartyIds}")
	public Response deleteHealthcareParties(@PathParam("healthcarePartyIds") String healthcarePartyIds){
		if (healthcarePartyIds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<String> deletedIds;
		try {
			deletedIds = healthcarePartyLogic.deleteHealthcareParties(Arrays.asList(healthcarePartyIds.split(",")));
		} catch (DeletionException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(500).type("text/plain").entity(e.getMessage()).build();
		}

		boolean succeed = (deletedIds != null);
		if (succeed) {
			return Response.ok().entity(deletedIds).build();
		} else {
			return Response.status(500).type("text/plain").entity("Deletion of the healthcare party failed. Read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Modify a Healthcare Party.",
			response = HealthcarePartyDto.class,
			httpMethod = "PUT",
			notes = "No particular return value. It's just a message."
	)
	@PUT
	public Response modifyHealthcareParty(HealthcarePartyDto healthcarePartyDto) {
		if (healthcarePartyDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		try {
			healthcarePartyLogic.modifyHealthcareParty(mapper.map(healthcarePartyDto, HealthcareParty.class));
			HealthcareParty modifiedHealthcareParty = healthcarePartyLogic.getHealthcareParty(healthcarePartyDto.getId());

			boolean succeed = (modifiedHealthcareParty != null);
			if (succeed) {
				return Response.ok().entity(mapper.map(modifiedHealthcareParty, HealthcarePartyDto.class)).build();
			} else {
				return Response.status(500).type("text/plain").entity("Modification of the healthcare party failed. Read the server log.").build();
			}
		} catch (MissingRequirementsException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Context
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Context
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Context
	public void setReplicationLogic(ReplicationLogic replicationLogic) {
		this.replicationLogic = replicationLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
