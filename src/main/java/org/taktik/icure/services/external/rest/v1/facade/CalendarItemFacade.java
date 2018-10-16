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
import org.taktik.icure.entities.CalendarItem;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.CalendarItemLogic;
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/calendarItem")
@Api(tags = {"calendarItem"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class CalendarItemFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(CalendarItemFacade.class);

    private CalendarItemLogic calendarItemLogic;
    private MapperFacade mapper;

    @ApiOperation(response = CalendarItemDto.class, value = "Creates a calendarItem")
    @POST
    public Response createCalendarItem(CalendarItemDto calendarItemDto) {
        Response response;

        if (calendarItemDto == null) {
            response = ResponseUtils.badRequest("Cannot create calendar item: supplied CalendarItemDto is null");

        } else {
            CalendarItem calendarItem = calendarItemLogic.createCalendarItem(mapper.map(calendarItemDto, CalendarItem.class));
            if (calendarItem != null) {
                response = ResponseUtils.ok(mapper.map(calendarItem, CalendarItemDto.class));

            } else {
                response = ResponseUtils.internalServerError("CalendarItem creation failed");
            }
        }

        return response;
    }

    @ApiOperation(value = "Deletes an calendarItem")
    @DELETE
    @Path("/{calendarItemIds}")
    public Response deleteCalendarItem(@PathParam("calendarItemIds") String calendarItemIds) throws DeletionException {
        Response response;

        if (calendarItemIds == null) {
            response = ResponseUtils.badRequest("Cannot delete access log: supplied calendarItemIds is null");

        } else {
            List<String> deletedCalendarItemIds = calendarItemLogic.deleteCalendarItems(Arrays.asList(calendarItemIds.split(",")));
            if (deletedCalendarItemIds != null) {
                response = Response.ok().entity(deletedCalendarItemIds).build();
            } else {
                return Response.status(500).type("text/plain").entity("CalendarItem deletion failed.").build();
            }
        }

        return response;
    }

    @ApiOperation(response = CalendarItemDto.class, value = "Gets an calendarItem")
    @GET
    @Path("/{calendarItemId}")
    public Response getCalendarItem(@PathParam("calendarItemId") String calendarItemId) {
        Response response;

        if (calendarItemId == null) {
            response = ResponseUtils.badRequest("Cannot get access log: supplied calendarItemId is null");

        } else {
            CalendarItem calendarItem = calendarItemLogic.getCalendarItem(calendarItemId);
            if (calendarItem != null) {
                response = ResponseUtils.ok(mapper.map(calendarItem, CalendarItemDto.class));

            } else {
                response = ResponseUtils.internalServerError("CalendarItem fetching failed");
            }
        }
        return response;
    }


    @ApiOperation(response = CalendarItemDto.class, value = "Modifies an calendarItem")
    @PUT
    public Response modifyCalendarItem(CalendarItemDto calendarItemDto) {
        Response response;

        if (calendarItemDto == null) {
            response = ResponseUtils.badRequest("Cannot modify calendar Item: supplied calendarItemDto is null");

        } else {
            CalendarItem calendarItem = calendarItemLogic.modifyCalendarItem(mapper.map(calendarItemDto, CalendarItem.class));
            if (calendarItem != null) {
                response = ResponseUtils.ok(mapper.map(calendarItem, CalendarItemDto.class));

            } else {
                response = ResponseUtils.internalServerError("CalendarItem modification failed");
            }
        }

        return response;
    }


    @ApiOperation(
            value = "Get CalendarItems by Period and HcPartyId",
            response = CalendarItemDto.class,
            responseContainer = "Array",
            httpMethod = "POST",
            notes = ""
    )
    @POST
    @Path("/byPeriodAndHcPartyId")
    public Response getCalendarItemsByPeriodAndHcPartyId(@QueryParam("startDate") Long startDate, @QueryParam("endDate") Long endDate, @QueryParam("hcPartyId") String hcPartyId) {
        if (startDate == null || endDate == null || hcPartyId == null || hcPartyId.isEmpty()) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<CalendarItem> calendars = calendarItemLogic.getCalendarItemByPeriodAndHcPartyId(startDate, endDate, hcPartyId);

        boolean succeed = (calendars != null);
        if (succeed) {
            return Response.ok().entity(calendars.stream().map(c -> mapper.map(c, CalendarItemDto.class)).collect(Collectors.toList())).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting CalendarItem failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.").build();
        }
    }

    @ApiOperation(
            value = "Get CalendarItems by Period and AgendaId",
            response = CalendarItemDto.class,
            responseContainer = "Array",
            httpMethod = "POST",
            notes = ""
    )
    @POST
    @Path("/byPeriodAndAgendaId")
    public Response getCalendarsByPeriodAndAgendaId(@QueryParam("startDate") Long startDate, @QueryParam("endDate") Long endDate, @QueryParam("agendaId") String agendaId) {
        if (startDate == null || endDate == null || agendaId == null || agendaId.isEmpty()) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<CalendarItem> calendars = calendarItemLogic.getCalendarItemByPeriodAndAgendaId(startDate, endDate, agendaId);

        boolean succeed = (calendars != null);
        if (succeed) {
            return Response.ok().entity(calendars.stream().map(c -> mapper.map(c, CalendarItemDto.class)).collect(Collectors.toList())).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting CalendarItem failed. Possible reasons: no such contact exists, or server error. Please try again or read the server log.").build();
        }
    }

    @Context
    public void setcalendarItemLogic(CalendarItemLogic calendarItemLogic) {
        this.calendarItemLogic = calendarItemLogic;
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

    @ApiOperation(
            value = "Gets all calendarItems",
            response = CalendarItemDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    public Response getCalendarItems() {
        Response response;
        List<CalendarItem> calendarItems = calendarItemLogic.getAllEntities();
        if (calendarItems != null) {
            response = Response.ok().entity(calendarItems.stream().map(c -> mapper.map(c, CalendarItemDto.class)).collect(Collectors.toList())).build();

        } else {
            response = ResponseUtils.internalServerError("CalendarItemTypes fetching failed");
        }
        return response;
    }

}
