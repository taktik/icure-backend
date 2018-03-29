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
import org.taktik.icure.entities.Tarification;
import org.taktik.icure.logic.TarificationLogic;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.TarificationDto;
import org.taktik.icure.services.external.rest.v1.dto.TarificationPaginatedList;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/tarification")
@Api(tags = { "tarification" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class TarificationFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(TarificationFacade.class);

	private MapperFacade mapper;
	private TarificationLogic tarificationLogic;

	@ApiOperation(
			value = "Finding tarifications by tarification, type and version with pagination.",
			response = TarificationPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of tarifications matched with given input."
	)
	@GET
	@Path("/byLabel")
	public Response findPaginatedTarificationsByLabel(
			@ApiParam(value = "region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "types", required = false) @QueryParam("types") String types,
			@ApiParam(value = "language", required = false) @QueryParam("language") String language,
			@ApiParam(value = "label", required = false) @QueryParam("label") String label,
			@ApiParam(value = "A tarification document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit) {

		Response response;

		PaginationOffset paginationOffset = new PaginationOffset(
				Arrays.asList(region,language,label),
				startDocumentId,
				null,
				limit == null ? null : Integer.valueOf(limit)
		);

		PaginatedList<Tarification> tarificationsList;
		tarificationsList = tarificationLogic.findTarificationsByLabel(region, language, label, paginationOffset);
		if (types != null) {
			List<String> typesList = Arrays.asList(types.split(","));
			tarificationsList.setRows(tarificationsList.getRows().stream().filter(c -> typesList.contains(c.getType())).collect(Collectors.toList()));
		}

		if (tarificationsList != null) {
			if (tarificationsList.getRows() == null) {
				tarificationsList.setRows(new ArrayList<>());
			}

			org.taktik.icure.services.external.rest.v1.dto.PaginatedList<TarificationDto> tarificationDtoPaginatedList =
					new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
			mapper.map(
					tarificationsList,
					tarificationDtoPaginatedList,
					new TypeBuilder<PaginatedList<Tarification>>() {}.build(),
					new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<TarificationDto>>() {}.build()
			);
			response = ResponseUtils.ok(tarificationDtoPaginatedList);

		} else {
			response = ResponseUtils.internalServerError("Finding tarifications failed");
		}

		return response;
	}

	@ApiOperation(
			value = "Finding tarifications by tarification, type and version with pagination.",
			response = TarificationPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of tarifications matched with given input."
	)
	@GET
	public Response findPaginatedTarifications(
			@ApiParam(value = "region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "type", required = false) @QueryParam("type") String type,
			@ApiParam(value = "tarification", required = false) @QueryParam("tarification") String tarification,
			@ApiParam(value = "version", required = false) @QueryParam("version") String version,
			@ApiParam(value = "A tarification document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit) {

		Response response;

		PaginationOffset paginationOffset = new PaginationOffset(
				getStartKey(region, type, tarification, version),
				startDocumentId,
				null,
				limit == null ? null : Integer.valueOf(limit)
		);

		PaginatedList<Tarification> tarificationsList;
		tarificationsList = tarificationLogic.findTarificationsBy(region, type, tarification, version, paginationOffset);

		if (tarificationsList != null) {
			if (tarificationsList.getRows() == null) {
				tarificationsList.setRows(new ArrayList<>());
			}

			org.taktik.icure.services.external.rest.v1.dto.PaginatedList<TarificationDto> tarificationDtoPaginatedList =
					new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
			mapper.map(
					tarificationsList,
					tarificationDtoPaginatedList,
					new TypeBuilder<PaginatedList<Tarification>>() {}.build(),
					new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<TarificationDto>>() {}.build()
			);
			response = ResponseUtils.ok(tarificationDtoPaginatedList);

		} else {
			response = ResponseUtils.internalServerError("Finding tarifications failed");
		}

		return response;
	}

	private Serializable getStartKey(String startKeyRegion, String startKeyType, String startKeyTarification, String startKeyVersion) {
		if ((startKeyRegion != null) && (startKeyType != null) && (startKeyTarification != null) && (startKeyVersion != null)) {
			return ((Serializable) Arrays.<String>asList(startKeyRegion, startKeyType, startKeyTarification, startKeyVersion));
		} else {
			return null;
		}
	}


	@ApiOperation(
			value = "Finding tarifications by tarification, type and version",
			response = TarificationDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of tarifications matched with given input."
	)
	@GET
	@Path("/byRegionTypeTarification")
	public Response findTarifications(
			@ApiParam(value = "Tarification region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "Tarification type", required = false) @QueryParam("type") String type,
			@ApiParam(value = "Tarification tarification", required = false) @QueryParam("tarification") String tarification,
			@ApiParam(value = "Tarification version", required = false) @QueryParam("version") String version) {

		List<Tarification> tarificationsList;
		tarificationsList = tarificationLogic.findTarificationsBy(region, type, tarification, version);

		if (tarificationsList == null) {
			tarificationsList = new ArrayList<>();
		}

		return ResponseUtils.ok(
				tarificationsList.stream().map(c -> mapper.map(c, TarificationDto.class)).collect(Collectors.toList())
		);
	}

	@ApiOperation(
			value = "Create a Tarification",
			response = TarificationDto.class,
			httpMethod = "POST",
			notes = "Type, Tarification and Version are required."
	)
    @POST
	public Response createTarification(TarificationDto c) {
		if (c == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Tarification tarification = tarificationLogic.create(mapper.map(c, Tarification.class));

		boolean succeed = (tarification != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(tarification, TarificationDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Tarification creation failed.").build();
		}
	}

	@ApiOperation(
			value = "Get a list of tarifications by ids",
			response = TarificationDto.class,
			responseContainer = "Array",
			httpMethod = "POST",
			notes = "Keys must be delimited by coma"
	)
	@POST
	@Path("/byIds")
	public Response getTarifications(ListOfIdsDto tarificationIds) {
		Response response;

		if (tarificationIds == null) {
			response = ResponseUtils.badRequest("Cannot retrieve tarifications: provided tarification Ids are null");
		} else {
			List<Tarification> tarifications = tarificationLogic.get(tarificationIds.getIds());

			if (tarifications == null) {
				response = ResponseUtils.notFound("No tarifications found with these ids");
			} else {
				List<TarificationDto> tarificationDtos = tarifications.stream().map((f) -> mapper.map(f, TarificationDto.class)).collect(Collectors.toList());
				response = ResponseUtils.ok(tarificationDtos);
			}
		}

		return response;
	}


	@ApiOperation(
            value = "Get a tarification",
            response = TarificationDto.class,
            httpMethod = "GET",
            notes = "Get a tarification based on ID or (tarification,type,version) as query strings. (tarification,type,version) is unique."
    )
    @GET
    @Path("/{tarificationId}")
    public Response getTarification(
					@ApiParam(value = "Tarification id", required = false) @PathParam("tarificationId") String tarificationId) {
		if (tarificationId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Tarification c =  tarificationLogic.get(tarificationId);

		boolean succeed = (c != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(c, TarificationDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("A problem regarding fetching the tarification. Read the app logs.").build();
		}
	}

    @ApiOperation(
            value = "Get a tarification",
            response = TarificationDto.class,
            httpMethod = "GET",
            notes = "Get a tarification based on ID or (tarification,type,version) as query strings. (tarification,type,version) is unique."
    )
    @GET
    @Path("/{type}/{tarification}/{version}")
    public Response getTarificationWithParts(
            @ApiParam(value = "Tarification type", required = false) @PathParam("type") String type,
            @ApiParam(value = "Tarification tarification", required = false) @PathParam("tarification") String tarification,
            @ApiParam(value = "Tarification version", required = false) @PathParam("version") String version) {
        if (type == null || tarification == null || version == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        Tarification c = tarificationLogic.get(type, tarification, version);

        boolean succeed = (c != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(c, TarificationDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("A problem regarding fetching the tarification. Read the app logs.").build();
        }
    }

    @ApiOperation(
			value = "Modify a tarification",
			response = TarificationDto.class,
			httpMethod = "PUT",
			notes = "Modification of (type, tarification, version) is not allowed."
	)
	@PUT
	public Response modifyTarification(TarificationDto tarificationDto) {
		if (tarificationDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Tarification modifiedTarification;
		try {
			 modifiedTarification = tarificationLogic.modify(mapper.map(tarificationDto, Tarification.class));
		} catch (Exception e) {
			return Response.status(500).type("text/plain").entity("A problem regarding modification of the tarification. Read the app logs: " + e.getMessage()).build();
		}

		boolean succeed = (modifiedTarification != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(modifiedTarification, TarificationDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Modification of the tarification failed. Read the server log.").build();
		}
	}

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setTarificationLogic(TarificationLogic tarificationLogic) {
		this.tarificationLogic = tarificationLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
