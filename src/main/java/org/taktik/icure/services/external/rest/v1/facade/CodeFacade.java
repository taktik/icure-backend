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

import com.google.gson.Gson;
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
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.logic.CodeLogic;
import org.taktik.icure.services.external.rest.v1.dto.CodeDto;
import org.taktik.icure.services.external.rest.v1.dto.CodePaginatedList;
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
@Path("/code")
@Api(tags = { "code" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class CodeFacade implements OpenApiFacade {

	private static final Logger logger = LoggerFactory.getLogger(CodeFacade.class);

	private MapperFacade mapper;
	private CodeLogic codeLogic;

	@ApiOperation(
			value = "Finding codes by code, type and version with pagination.",
			response = CodePaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported"
	)
	@GET
	@Path("/byLabel")
	public Response findPaginatedCodesByLabel(
			@ApiParam(value = "region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "types", required = false) @QueryParam("types") String types,
			@ApiParam(value = "language", required = false) @QueryParam("language") String language,
			@ApiParam(value = "label", required = false) @QueryParam("label") String label,
			@ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " +
					"components to form the Complex Key's startKey") @QueryParam("startKey") String startKey,
			@ApiParam(value = "A code document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit) {

		Response response;

		List<String> startKeyElements = startKey == null ? null : new Gson().fromJson(startKey, List.class);
		@SuppressWarnings("unchecked") PaginationOffset paginationOffset = new PaginationOffset(startKeyElements, startDocumentId, null, limit);

		PaginatedList<Code> codesList;
		if (types != null) {
			List<String> typesList = Arrays.asList(types.split(","));

			if (typesList.size()>1) {
				List<Code> codes = typesList.stream()
						.flatMap(type -> codeLogic.findCodesByLabel(region, language, type, label, paginationOffset).getRows().stream())
						.collect(Collectors.toList());
				int pageSize = Math.min(limit, codes.size());
				codesList = new PaginatedList<>(pageSize, codes.size(), codes.subList(0, pageSize), null);
				codesList.setRows(codesList.getRows().stream().filter(c -> typesList.contains(c.getType())).distinct().collect(Collectors.toList()));
			} else {
				codesList = codeLogic.findCodesByLabel(region, language, typesList.get(0), label, paginationOffset);
			}
		} else {
			codesList = codeLogic.findCodesByLabel(region, language, label, paginationOffset);
		}

		if (codesList.getRows() == null) {
			codesList.setRows(new ArrayList<>());
		}

		org.taktik.icure.services.external.rest.v1.dto.PaginatedList<CodeDto> codeDtoPaginatedList =
				new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
		mapper.map(
				codesList,
				codeDtoPaginatedList,
				new TypeBuilder<PaginatedList<Code>>() {}.build(),
				new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<CodeDto>>() {}.build()
		);
		response = ResponseUtils.ok(codeDtoPaginatedList);

		return response;
	}

	@ApiOperation(
			value = "Finding codes by code, type and version with pagination.",
			response = CodePaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of codes matched with given input."
	)
	@GET
	public Response findPaginatedCodes(
			@ApiParam(value = "region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "type", required = false) @QueryParam("type") String type,
			@ApiParam(value = "code", required = false) @QueryParam("code") String code,
			@ApiParam(value = "version", required = false) @QueryParam("version") String version,
			@ApiParam(value = "A code document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit) {

		Response response;

		PaginationOffset paginationOffset = new PaginationOffset(
				getStartKey(region, type, code, version),
				startDocumentId,
				null,
				limit == null ? null : Integer.valueOf(limit)
		);

		PaginatedList<Code> codesList;
		codesList = codeLogic.findCodesBy(region, type, code, version, paginationOffset);

		if (codesList != null) {
			if (codesList.getRows() == null) {
				codesList.setRows(new ArrayList<>());
			}

			org.taktik.icure.services.external.rest.v1.dto.PaginatedList<CodeDto> codeDtoPaginatedList =
					new org.taktik.icure.services.external.rest.v1.dto.PaginatedList<>();
			mapper.map(
					codesList,
					codeDtoPaginatedList,
					new TypeBuilder<PaginatedList<Code>>() {}.build(),
					new TypeBuilder<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<CodeDto>>() {}.build()
			);
			response = ResponseUtils.ok(codeDtoPaginatedList);

		} else {
			response = ResponseUtils.internalServerError("Finding codes failed");
		}

		return response;
	}

	private Serializable getStartKey(String startKeyRegion, String startKeyType, String startKeyCode, String startKeyVersion) {
		if ((startKeyRegion != null) && (startKeyType != null) && (startKeyCode != null) && (startKeyVersion != null)) {
			return ((Serializable) Arrays.<String>asList(startKeyRegion, startKeyType, startKeyCode, startKeyVersion));
		} else {
			return null;
		}
	}


	@ApiOperation(
			value = "Finding codes by code, type and version",
			response = CodeDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of codes matched with given input."
	)
	@GET
	@Path("/byRegionTypeCode")
	public Response findCodes(
			@ApiParam(value = "Code region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "Code type", required = false) @QueryParam("type") String type,
			@ApiParam(value = "Code code", required = false) @QueryParam("code") String code,
			@ApiParam(value = "Code version", required = false) @QueryParam("version") String version) {

		List<Code> codesList;
		codesList = codeLogic.findCodesBy(region, type, code, version);

		return ResponseUtils.ok(
				codesList.stream().map(c -> mapper.map(c, CodeDto.class)).collect(Collectors.toList())
		);
	}

    @ApiOperation(
            value = "Finding code types.",
            response = String.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = "Returns a list of code types matched with given input."
    )
    @GET
    @Path("/codetype/byRegionType")
    public Response findCodeTypes(
			@ApiParam(value = "Code region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "Code type", required = false) @QueryParam("type") String type) {
        List<String> codesList = codeLogic.findCodeTypes(region, type);
        return ResponseUtils.ok(codesList);
    }

	@ApiOperation(
			value = "Finding tag types.",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of tag types matched with given input."
	)
	@GET
	@Path("/tagtype/byRegionType")
	public Response findTagTypes(
			@ApiParam(value = "Code region", required = false) @QueryParam("region") String region,
			@ApiParam(value = "Code type", required = false) @QueryParam("type") String type) {
		List<String> tagTypeCandidates = codeLogic.getTagTypeCandidates();
		List<String> codesList =  codeLogic.findCodeTypes(region, type).stream().filter(tagTypeCandidates::contains).collect(Collectors.toList());
		return ResponseUtils.ok(codesList);
	}

	@ApiOperation(
			value = "Create a Code",
			response = CodeDto.class,
			httpMethod = "POST",
			notes = "Type, Code and Version are required."
	)
    @POST
	public Response createCode(CodeDto c) {
		if (c == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Code code = codeLogic.create(mapper.map(c, Code.class));
		return Response.ok().entity(mapper.map(code, CodeDto.class)).build();
	}

	@ApiOperation(
			value = "Get a list of codes by ids",
			response = CodeDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byIds/{codeIds}")
	public Response getCodes(@PathParam("codeIds") String codeIds) {
		Response response;

		if (codeIds == null) {
			response = ResponseUtils.badRequest("Cannot retrieve codes: provided code Ids are null");

		} else {
			List<Code> codes = codeLogic.get(Arrays.asList(codeIds.split(",")));

			List<CodeDto> codeDtos = codes.stream().map((f) -> mapper.map(f, CodeDto.class)).collect(Collectors.toList());
			response = ResponseUtils.ok(codeDtos);
		}

		return response;
	}


	@ApiOperation(
            value = "Get a code",
            response = CodeDto.class,
            httpMethod = "GET",
            notes = "Get a code based on ID or (code,type,version) as query strings. (code,type,version) is unique."
    )
    @GET
    @Path("/{codeId}")
    public Response getCode(
					@ApiParam(value = "Code id", required = false) @PathParam("codeId") String codeId) {
		if (codeId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Code c =  codeLogic.get(codeId);

		boolean succeed = (c != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(c, CodeDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("A problem regarding fetching the code. Read the app logs.").build();
		}
	}

    @ApiOperation(
            value = "Get a code",
            response = CodeDto.class,
            httpMethod = "GET",
            notes = "Get a code based on ID or (code,type,version) as query strings. (code,type,version) is unique."
    )
    @GET
    @Path("/{type}/{code}/{version}")
    public Response getCodeWithParts(
            @ApiParam(value = "Code type", required = false) @PathParam("type") String type,
            @ApiParam(value = "Code code", required = false) @PathParam("code") String code,
            @ApiParam(value = "Code version", required = false) @PathParam("version") String version) {
        if (type == null || code == null || version == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        Code c = codeLogic.get(type, code, version);
 	    return Response.ok().entity(mapper.map(c, CodeDto.class)).build();
    }

    @ApiOperation(
			value = "Modify a code",
			response = CodeDto.class,
			httpMethod = "PUT",
			notes = "Modification of (type, code, version) is not allowed."
	)
	@PUT
	public Response modifyCode(CodeDto codeDto) {
		if (codeDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Code modifiedCode;
		try {
			 modifiedCode = codeLogic.modify(mapper.map(codeDto, Code.class));
		} catch (Exception e) {
			return Response.status(500).type("text/plain").entity("A problem regarding modification of the code. Read the app logs: " + e.getMessage()).build();
		}

	    return Response.ok().entity(mapper.map(modifiedCode, CodeDto.class)).build();
    }

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setCodeLogic(CodeLogic codeLogic) {
		this.codeLogic = codeLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
