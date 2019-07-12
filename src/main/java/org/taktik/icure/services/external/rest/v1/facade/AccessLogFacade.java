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
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.AccessLog;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.AccessLogLogic;
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto;
import org.taktik.icure.services.external.rest.v1.dto.AccessLogPaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Path("/accesslog")
@Api(tags = { "accesslog" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class AccessLogFacade implements OpenApiFacade{

	private static Logger logger = LoggerFactory.getLogger(AccessLogFacade.class);

	private AccessLogLogic accessLogLogic;
	private MapperFacade mapper;

	@ApiOperation(response = AccessLogDto.class, value = "Creates an access log")
	@POST
	public Response createAccessLog(AccessLogDto accessLogDto) {
		Response response;

		if (accessLogDto == null) {
			response = ResponseUtils.badRequest("Cannot create access log: supplied accessLogDto is null");

		} else {
			AccessLog accessLog = accessLogLogic.createAccessLog(mapper.map(accessLogDto, AccessLog.class));
			if (accessLog != null) {
				response = ResponseUtils.ok(mapper.map(accessLog, AccessLogDto.class));

			} else {
				response = ResponseUtils.internalServerError("AccessLog creation failed");
			}
		}

		return response;
	}

	@ApiOperation(value = "Deletes an access log")
	@DELETE
	@Path("/{accessLogIds}")
	public Response deleteAccessLog(@PathParam("accessLogIds") String accessLogIds) throws DeletionException {
		Response response;

		if (accessLogIds == null) {
			response = ResponseUtils.badRequest("Cannot delete access log: supplied accessLogIds is null");

		} else {
			List<String> deletedAccessLogIds = accessLogLogic.deleteAccessLogs(Arrays.asList(accessLogIds.split(",")));
			if (deletedAccessLogIds != null) {
				response = ResponseUtils.ok();
			} else {
				response = ResponseUtils.internalServerError("AccessLog deletion failed");
			}
		}

		return response;
	}

	@ApiOperation(response = AccessLogDto.class, value = "Gets an access log")
	@GET
	@Path("/{accessLogId}")
	public Response getAccessLog(@PathParam("accessLogId") String accessLogId) {
		Response response;

		if (accessLogId == null) {
			response = ResponseUtils.badRequest("Cannot get access log: supplied accessLogId is null");

		} else {
			AccessLog accessLog = accessLogLogic.getAccessLog(accessLogId);
			if (accessLog != null) {
				response = ResponseUtils.ok(mapper.map(accessLog, AccessLogDto.class));

			} else {
				response = ResponseUtils.internalServerError("AccessLog fetching failed");
			}
		}

		return response;
	}

	@ApiOperation(response = AccessLogDto.class, responseContainer = "Array", value = "Lists access logs")
	@GET
	public Response listAccessLogs(@QueryParam("startKey") String startKey, @QueryParam("startDocumentId") String startDocumentId, @QueryParam("limit") String limit) {
		Response response;

		PaginationOffset paginationOffset = new PaginationOffset(null, startDocumentId, null, limit != null ? Integer.valueOf(limit) : null);
		PaginatedList<AccessLogDto> accessLogDtos = new PaginatedList<>();

		org.taktik.icure.db.PaginatedList<AccessLog> accessLogs = accessLogLogic.listAccessLogs(paginationOffset);
		if (accessLogs != null) {
			mapper.map(accessLogs, accessLogDtos, new TypeBuilder<org.taktik.icure.db.PaginatedList<AccessLog>>() {
			}.build(), new TypeBuilder<PaginatedList<AccessLogDto>>() {
			}.build());
			response = ResponseUtils.ok(accessLogDtos);

		} else {
			response = ResponseUtils.internalServerError("AccessLog listing failed");
		}

		return response;
	}

    @ApiOperation(response = AccessLogPaginatedList.class, value = "Get Paginated List of Access logs")
    @GET
    @Path("/byUser")
    public Response findByUserAfterDate(@ApiParam(value = "A User ID", required = true) @QueryParam("userId") String userId,
                                        @ApiParam(value = "The type of access (COMPUTER or USER)", required = false) @QueryParam("accessType") String accessType,
                                        @ApiParam(value = "The start search epoch", required = false) @QueryParam("startDate") Long startDate, @ApiParam(value = "The start key for pagination", required = false) @QueryParam("startKey") String startKey,
                                        @ApiParam(value = "A patient document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
                                        @ApiParam(value = "Number of rows", required = false) @QueryParam("limit") Integer limit,
										@ApiParam(value = "Descending order", required = false) @QueryParam("descending") Boolean descending) {

        PaginationOffset paginationOffset = new PaginationOffset(startKey, startDocumentId, null, limit);
        org.taktik.icure.db.PaginatedList<AccessLog> accessLogs = accessLogLogic.findByUserAfterDate(userId, accessType, Instant.ofEpochMilli(startDate), paginationOffset, descending!=null?descending:false);

        if (accessLogs != null) {
            AccessLogPaginatedList accessLogDtos = new AccessLogPaginatedList();
            mapper.map(accessLogs, accessLogDtos, new TypeBuilder<org.taktik.icure.db.PaginatedList<AccessLog>>() {}.build(), new TypeBuilder<PaginatedList<AccessLogDto>>() {}.build());
            return ResponseUtils.ok(accessLogDtos);
        } else {
            return ResponseUtils.internalServerError("AccessLog listing failed");
        }
    }

	@ApiOperation(
			value = "List access logs found By Healthcare Party and secret foreign keyelementIds.",
			response = AccessLogDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be separatedby coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys")
	public Response findByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId, @QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		List<AccessLog> accessLogList = accessLogLogic.findByHCPartySecretPatientKeys(hcPartyId, new ArrayList<>(secretPatientKeys));

		boolean succeed = (accessLogList != null);
		if (succeed) {
			// mapping to Dto
			List<AccessLogDto> elementDtoList = accessLogList.stream().map(element -> mapper.map(element, AccessLogDto.class)).collect(Collectors.toList());
			return Response.ok().entity(elementDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting the Access Logs failed. Please try again or read the server log.").build();
		}
	}

    @ApiOperation(response = AccessLogDto.class, value = "Modifies an access log")
	@PUT
	public Response modifyAccessLog(AccessLogDto accessLogDto) {
		Response response;

		if (accessLogDto == null) {
			response = ResponseUtils.badRequest("Cannot modify access log: supplied accessLogDto is null");

		} else {
			AccessLog accessLog = accessLogLogic.modifyAccessLog(mapper.map(accessLogDto, AccessLog.class));
			if (accessLog != null) {
				response = ResponseUtils.ok(mapper.map(accessLog, AccessLogDto.class));

			} else {
				response = ResponseUtils.internalServerError("AccessLog modification failed");
			}
		}

		return response;
	}

     @Context
	public void setAccessLogLogic(AccessLogLogic accessLogLogic) {
		this.accessLogLogic = accessLogLogic;
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
