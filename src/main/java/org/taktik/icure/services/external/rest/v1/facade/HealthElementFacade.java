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

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.logic.HealthElementLogic;
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.utils.ResponseUtils;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Path("/helement")
@Api(tags = { "helement" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class HealthElementFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(HealthElementFacade.class);

	private MapperFacade mapper;
	private HealthElementLogic healthElementLogic;

	@ApiOperation(
			value = "Create a health element with the current user",
			response = HealthElementDto.class,
			httpMethod = "POST",
			notes = "Returns an instance of created health element."
	)
	@POST
	public Response createHealthElement(HealthElementDto c) {
		if (c == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		HealthElement element = healthElementLogic.createHealthElement(mapper.map(c, HealthElement.class));

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, HealthElementDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Health element creation failed.").build();
		}
	}

	@ApiOperation(
			value = "Get a health element",
			response = HealthElementDto.class,
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/{healthElementId}")
	public Response getHealthElement(@PathParam("healthElementId") String healthElementId) {
		if (healthElementId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		HealthElement element = healthElementLogic.getHealthElement(healthElementId);

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, HealthElementDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting health element failed. Possible reasons: no such health element exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "List health elements found By Healthcare Party and secret foreign keyelementIds.",
			response = HealthElementDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys hast to delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys")
	public Response findByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId, @QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		List<HealthElement> elementList = healthElementLogic.findByHCPartySecretPatientKeys(hcPartyId, new ArrayList<>(secretPatientKeys));

		boolean succeed = (elementList != null);
		if (succeed) {
			// mapping to Dto
			List<HealthElementDto> elementDtoList = elementList.stream().map(element -> mapper.map(element, HealthElementDto.class)).collect(Collectors.toList());
			return Response.ok().entity(elementDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting the health element failed. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "List helement stubs found By Healthcare Party and secret foreign keys.",
			response = IcureStubDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys/delegations")
	public Response findDelegationsStubsByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId,
	                                                                @QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		return Response.ok().entity(healthElementLogic.findByHCPartySecretPatientKeys(hcPartyId, new ArrayList<>(secretPatientKeys)).stream().map(contact -> mapper.map(contact, IcureStubDto.class)).collect(Collectors.toList())).build();
	}

	@ApiOperation(
			value = "Update delegations in healthElements.",
			httpMethod = "POST",
			notes = "Keys must be delimited by coma"
	)
	@POST
	@Path("/delegations")
	public Response setHealthElementsDelegations(List<IcureStubDto> stubs) throws Exception {
		List<HealthElement> healthElements = healthElementLogic.getHealthElements(stubs.stream().map(IcureDto::getId).collect(Collectors.toList()));
		healthElements.forEach(healthElement -> {
			stubs.stream().filter(s -> s.getId().equals(healthElement.getId())).findFirst().ifPresent(stub -> {
				stub.getDelegations().forEach((s, delegationDtos) -> healthElement.getDelegations().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
				stub.getEncryptionKeys().forEach((s, delegationDtos) -> healthElement.getEncryptionKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
				stub.getCryptedForeignKeys().forEach((s, delegationDtos) -> healthElement.getCryptedForeignKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			});
		});
		healthElementLogic.updateEntities(healthElements);
		return Response.ok().build();
	}

	@ApiOperation(
			value = "Delete health elements.",
			response = String.class,
            responseContainer = "Array",
            httpMethod = "DELETE",
			notes = "Response is a set containing the ID's of deleted health elements."
	)
	@DELETE
	@Path("/{healthElementIds}")
	public Response deleteHealthElements(@PathParam("healthElementIds") String healthElementIds) {
        if (healthElementIds==null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }
        List<String> ids = Arrays.asList(healthElementIds.split(","));
        if (ids.size() == 0) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> deletedIds = healthElementLogic.deleteHealthElements(new HashSet<>(ids));

		boolean succeed = (deletedIds != null);
		if (succeed) {
			return Response.ok().entity(deletedIds).build();
		} else {
			return Response.status(500).type("text/plain").entity("Health element deletion failed.").build();
		}
	}

	@ApiOperation(
			value = "Modify a health element",
			response = HealthElementDto.class,
			httpMethod = "PUT",
			notes = "Returns the modified health element."
	)
	@PUT
	public Response modifyHealthElement(HealthElementDto healthElementDto) {
		if (healthElementDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}


		healthElementLogic.modifyHealthElement(mapper.map(healthElementDto, HealthElement.class));
        HealthElement modifiedHealthElement = healthElementLogic.getHealthElement(healthElementDto.getId());

        boolean succeed = (modifiedHealthElement != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(modifiedHealthElement, HealthElementDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Health element modification failed.").build();
        }
	}

	@ApiOperation(
			value = "Modify a batch of health elements",
			response = HealthElementDto.class,
			responseContainer = "Array",
			httpMethod = "PUT",
			notes = "Returns the modified health elements."
	)
	@PUT
	@Path("/batch")
	public Response modifyHealthElements(List<HealthElementDto> healthElementDtos) {
		if (healthElementDtos == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		try {
			List<HealthElement> hes = healthElementLogic.updateEntities(healthElementDtos.stream().map(f -> mapper.map(f, HealthElement.class)).collect(Collectors.toList()));
			return Response.ok().entity(hes.stream().map(f -> mapper.map(f, HealthElementDto.class)).collect(Collectors.toList())).build();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@ApiOperation(
			value = "Delegates a health element to a healthcare party",
			response = HealthElementDto.class,
			httpMethod = "POST",
			notes = "It delegates a health element to a healthcare party (By current healthcare party). Returns the element with new delegations."
	)
	@POST
	@Path("/{healthElementId}/delegate")
	public Response newDelegations(@PathParam("healthElementId") String healthElementId, List<DelegationDto> ds) {
		if (healthElementId == null || ds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		healthElementLogic.addDelegations(healthElementId, ds.stream().map(d->mapper.map(d, Delegation.class)).collect(Collectors.toList()));
		HealthElement healthElementWithDelegation = healthElementLogic.getHealthElement(healthElementId);

		boolean succeed = (healthElementWithDelegation != null && healthElementWithDelegation.getDelegations() != null && healthElementWithDelegation.getDelegations().size() > 0);
		if (succeed) {
			return Response.ok().entity(mapper.map(healthElementWithDelegation, HealthElementDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Delegation creation for health element failed.").build();
		}
	}


    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setHealthElementLogic(HealthElementLogic healthElementLogic) {
		this.healthElementLogic = healthElementLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
