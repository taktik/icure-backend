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
import org.taktik.icure.entities.CalendarItemType;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.CalendarItemTypeLogic;
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto;
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemTypeDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/calendarItemType")
@Api(tags = {"calendarItemType"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class CalendarItemTypeFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(CalendarItemTypeFacade.class);

    private CalendarItemTypeLogic calendarItemTypeLogic;
    private MapperFacade mapper;

    @ApiOperation(response = CalendarItemTypeDto.class, value = "Creates a calendarItemType")
    @POST
    public Response createCalendarItemType(CalendarItemTypeDto calendarItemTypeDto) {
        Response response;

        if (calendarItemTypeDto == null) {
            response = ResponseUtils.badRequest("Cannot create calendar item: supplied CalendarItemTypeDto is null");

        } else {
            CalendarItemType calendarItemType = calendarItemTypeLogic.createCalendarItemType(mapper.map(calendarItemTypeDto, CalendarItemType.class));
            if (calendarItemType != null) {
                response = ResponseUtils.ok(mapper.map(calendarItemType, CalendarItemTypeDto.class));

            } else {
                response = ResponseUtils.internalServerError("CalendarItemType creation failed");
            }
        }

        return response;
    }

    @ApiOperation(value = "Deletes an calendarItemType")
    @DELETE
    @Path("/{calendarItemTypeIds}")
    public Response deleteCalendarItemType(@PathParam("calendarItemTypeIds") String calendarItemTypeIds) throws DeletionException {
        Response response;

        if (calendarItemTypeIds == null) {
            response = ResponseUtils.badRequest("Cannot delete access log: supplied calendarItemTypeIds is null");

        } else {
            List<String> deletedCalendarItemTypeIds = calendarItemTypeLogic.deleteCalendarItemTypes(Arrays.asList(calendarItemTypeIds.split(",")));
            if (deletedCalendarItemTypeIds != null) {
                response = Response.ok().entity(deletedCalendarItemTypeIds).build();
            } else {
                return Response.status(500).type("text/plain").entity("CalendarItemType deletion failed.").build();
            }
        }

        return response;
    }

    @ApiOperation(response = CalendarItemTypeDto.class, value = "Gets an calendarItemType")
    @GET
    @Path("/{calendarItemTypeId}")
    public Response getCalendarItemType(@PathParam("calendarItemTypeId") String calendarItemTypeId) {
        Response response;

        if (calendarItemTypeId == null) {
            response = ResponseUtils.badRequest("Cannot get access log: supplied calendarItemTypeId is null");

        } else {
            CalendarItemType calendarItemType = calendarItemTypeLogic.getCalendarItemType(calendarItemTypeId);
            if (calendarItemType != null) {
                response = ResponseUtils.ok(mapper.map(calendarItemType, CalendarItemTypeDto.class));

            } else {
                response = ResponseUtils.internalServerError("CalendarItemType fetching failed");
            }
        }
        return response;
    }

    @ApiOperation(
            value = "Gets all calendarItemTypes",
            response = CalendarItemTypeDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    public Response getCalendarItemTypes() {
        Response response;
        List<CalendarItemType> calendarItemTypes = calendarItemTypeLogic.getAllEntities();
        if (calendarItemTypes != null) {
            response = Response.ok().entity(calendarItemTypes.stream().map(c -> mapper.map(c, CalendarItemTypeDto.class)).collect(Collectors.toList())).build();
        } else {
            response = ResponseUtils.internalServerError("CalendarItemTypes fetching failed");
        }
        return response;
    }

    @ApiOperation(
            value = "Gets all calendarItemTypes include deleted",
            response = CalendarItemTypeDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    @Path("/includeDeleted")
    public Response getCalendarItemTypesIncludeDeleted() {
        Response response;
        List<CalendarItemType> calendarItemTypes = calendarItemTypeLogic.getAllEntitiesIncludeDelete();
        if (calendarItemTypes != null) {
            response = Response.ok().entity(calendarItemTypes.stream().map(c -> mapper.map(c, CalendarItemTypeDto.class)).collect(Collectors.toList())).build();

        } else {
            response = ResponseUtils.internalServerError("CalendarItemTypes fetching failed");
        }
        return response;
    }


    @ApiOperation(response = CalendarItemTypeDto.class, value = "Modifies an calendarItemType")
    @PUT
    public Response modifyCalendarItemType(CalendarItemTypeDto calendarItemTypeDto) {
        Response response;

        if (calendarItemTypeDto == null) {
            response = ResponseUtils.badRequest("Cannot modify calendar Item: supplied calendarItemTypeDto is null");

        } else {
            CalendarItemType calendarItemType = calendarItemTypeLogic.modifyCalendarTypeItem(mapper.map(calendarItemTypeDto, CalendarItemType.class));
            if (calendarItemType != null) {
                response = ResponseUtils.ok(mapper.map(calendarItemType, CalendarItemTypeDto.class));

            } else {
                response = ResponseUtils.internalServerError("CalendarItemType modification failed");
            }
        }

        return response;
    }

    @Context
    public void setcalendarItemTypeLogic(CalendarItemTypeLogic calendarItemTypeLogic) {
        this.calendarItemTypeLogic = calendarItemTypeLogic;
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
