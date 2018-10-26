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
import org.taktik.icure.entities.TimeTableHour;
import org.taktik.icure.entities.TimeTableItem;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.TimeTableLogic;
import org.taktik.icure.services.external.rest.v1.dto.TimeTableDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
                response = Response.ok().entity(deletedTimeTableIds).build();
            } else {
                response = ResponseUtils.internalServerError("TimeTable deletion failed");
            }
        }

        return response;
    }

    @ApiOperation(response = TimeTableDto.class, value = "Gets a timeTable")
    @GET
    @Path("/{timeTableId}")
    public Response getTimeTable(@PathParam("timeTableId") String timeTableId) {
        Response response;

        if (timeTableId == null) {
            response = ResponseUtils.badRequest("Cannot get access log: supplied timeTableId is null");

        }
        if (timeTableId.equalsIgnoreCase("new")) {
            //Create an hourItem
            TimeTableHour timeTableHour = new TimeTableHour();
            timeTableHour.setStartHour(Long.parseLong("0800"));
            timeTableHour.setStartHour(Long.parseLong("0900"));
            //Create a timeTableItem
            TimeTableItem timeTableItem = new TimeTableItem();
            timeTableItem.setCalendarItemTypeId("consult");
            timeTableItem.setDays(new ArrayList<>());
            timeTableItem.getDays().add("monday");
            timeTableItem.setRecurrenceTypes(new ArrayList<>());
            timeTableItem.setHours(new ArrayList<>());
            timeTableItem.getHours().add(timeTableHour);
            //Create the timeTable
            TimeTable timeTable = new TimeTable();
            timeTable.setStartTime(Long.parseLong("20180601000"));
            timeTable.setEndTime(Long.parseLong("20180801000"));
            timeTable.setName("myPeriod");
            timeTable.setItems(new ArrayList<>());
            timeTable.getItems().add(timeTableItem);
            //Return it
            response = ResponseUtils.ok(mapper.map(timeTable, TimeTableDto.class));

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
            value = "Get TimeTables by Period and AgendaId",
            response = TimeTableDto.class,
            responseContainer = "Array",
            httpMethod = "POST",
            notes = ""
    )
    @POST
    @Path("/byPeriodAndAgendaId")
    public Response getTimeTablesByPeriodAndAgendaId(@QueryParam("startDate") Long startDate, @QueryParam("endDate") Long endDate, @QueryParam("agendaId") String agendaId) {
        if (startDate == null || endDate == null || agendaId == null || agendaId.isEmpty()) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<TimeTable> timeTables = timeTableLogic.getTimeTablesByPeriodAndAgendaId(startDate, endDate, agendaId);

        boolean succeed = (timeTables != null);
        if (succeed) {
            return Response.ok().entity(timeTables.stream().map(c -> mapper.map(c, TimeTableDto.class)).collect(Collectors.toList())).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting TimeTable failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.").build();
        }
    }


    @ApiOperation(
            value = "Get TimeTables by AgendaId",
            response = TimeTableDto.class,
            responseContainer = "Array",
            httpMethod = "POST",
            notes = ""
    )
    @POST
    @Path("/byAgendaId")
    public Response getTimeTablesByAgendaId(@QueryParam("agendaId") String agendaId) {
        if (agendaId == null || agendaId.isEmpty()) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<TimeTable> timeTables = timeTableLogic.getTimeTablesByAgendaId(agendaId);

        boolean succeed = (timeTables != null);
        if (succeed) {
            return Response.ok().entity(timeTables.stream().map(c -> mapper.map(c, TimeTableDto.class)).collect(Collectors.toList())).build();
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
