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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.TimeTable;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.TimeTableLogic;
import org.taktik.icure.services.external.rest.v1.dto.TimeTableDto;
import org.taktik.icure.services.external.rest.v1.dto.ContactDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/timeTable")
@Api(tags = {"timeTable"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class TimeTableFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(TimeTableFacade.class);

    private TimeTableLogic timeTableLogic;
    private MapperFacade mapper;

    @ApiOperation(response = TimeTableDto.class, value = "Creates a timeTable")
    @POST
    public Response createTimeTable(TimeTableDto timeTableDto) {
        Response response;

        if (timeTableDto == null) {
            response = ResponseUtils.badRequest("Cannot create calendar item: supplied TimeTableDto is null");

        } else {
            TimeTable timeTable = timeTableLogic.createTimeTable(mapper.map(timeTableDto, TimeTable.class));
            if (timeTable != null) {
                response = ResponseUtils.ok(mapper.map(timeTable, TimeTableDto.class));

            } else {
                response = ResponseUtils.internalServerError("TimeTable creation failed");
            }
        }

        return response;
    }

    @ApiOperation(value = "Deletes an timeTable")
    @DELETE
    @Path("/{timeTableIds}")
    public Response deleteTimeTable(@PathParam("timeTableIds") String timeTableIds) throws DeletionException {
        Response response;

        if (timeTableIds == null) {
            response = ResponseUtils.badRequest("Cannot delete access log: supplied timeTableIds is null");

        } else {
            List<String> deletedTimeTableIds = timeTableLogic.deleteTimeTables(Arrays.asList(timeTableIds.split(",")));
            if (deletedTimeTableIds != null) {
                response = ResponseUtils.ok();
            } else {
                response = ResponseUtils.internalServerError("TimeTable deletion failed");
            }
        }

        return response;
    }

    @ApiOperation(response = TimeTableDto.class, value = "Gets an timeTable")
    @GET
    @Path("/{timeTableId}")
    public Response getTimeTable(@PathParam("timeTableId") String timeTableId) {
        Response response;

        if (timeTableId == null) {
            response = ResponseUtils.badRequest("Cannot get access log: supplied timeTableId is null");

        } else {
            TimeTable timeTable = timeTableLogic.getTimeTable(timeTableId);
            if (timeTable != null) {
                response = ResponseUtils.ok(mapper.map(timeTable, TimeTableDto.class));

            } else {
                response = ResponseUtils.internalServerError("TimeTable fetching failed");
            }
        }
        return response;
    }


    @ApiOperation(response = TimeTableDto.class, value = "Modifies an timeTable")
    @PUT
    public Response modifyTimeTable(TimeTableDto timeTableDto) {
        Response response;

        if (timeTableDto == null) {
            response = ResponseUtils.badRequest("Cannot modify calendar Item: supplied timeTableDto is null");

        } else {
            TimeTable timeTable = timeTableLogic.modifyTimeTable(mapper.map(timeTableDto, TimeTable.class));
            if (timeTable != null) {
                response = ResponseUtils.ok(mapper.map(timeTable, TimeTableDto.class));

            } else {
                response = ResponseUtils.internalServerError("TimeTable modification failed");
            }
        }

        return response;
    }


    @ApiOperation(
            value = "Get TimeTables by Period and HcPartyId",
            response = TimeTableDto.class,
            responseContainer = "Array",
            httpMethod = "POST",
            notes = ""
    )
    @POST
    @Path("/byPeriodAndHcPartyId")
    public Response getContacts(@QueryParam("startDate") Long startDate,@QueryParam("endDate") Long endDate,@QueryParam("hcPartyId") String hcPartyId) {
        if (startDate == null || endDate == null || hcPartyId == null || hcPartyId.isEmpty()) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<TimeTable> contacts = timeTableLogic.getTimeTableByPeriodAndHcPartyId(startDate,endDate,hcPartyId);

        boolean succeed = (contacts != null);
        if (succeed) {
            return Response.ok().entity(contacts.stream().map(c->mapper.map(c, ContactDto.class)).collect(Collectors.toList())).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting TimeTable failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.").build();
        }
    }

    @Context
    public void setTimeTableLogic(TimeTableLogic timeTableLogic) {
        this.timeTableLogic = timeTableLogic;
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
