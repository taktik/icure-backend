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
import org.taktik.icure.entities.CalendarItem;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.CalendarItemLogic;
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
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
import java.util.List;
import java.util.Set;
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

    @ApiOperation(
            value = "Find CalendarItems by hcparty and patient",
            response = CalendarItemDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    @Path("/byHcPartySecretForeignKeys")
    public Response findByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId, @QueryParam("secretFKeys") String secretFKeys) {
        if (hcPartyId == null || secretFKeys == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
        List<CalendarItem> calendarItemsList = calendarItemLogic.findByHCPartySecretPatientKeys(hcPartyId, new ArrayList<>(secretPatientKeys));

        boolean succeed = (calendarItemsList != null);
        if (succeed) {
            // mapping to Dto
            List<CalendarItemDto> elementDtoList = calendarItemsList.stream().map(element -> mapper.map(element, CalendarItemDto.class)).collect(Collectors.toList());
            return Response.ok().entity(elementDtoList).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting the CalendarItems failed. Please try again or read the server log.").build();
        }
    }

    @ApiOperation(
            value = "Update delegations in calendarItems",
            httpMethod = "POST"
    )
    @POST
    @Path("/delegations")
    public Response setCalendarItemsDelegations(List<IcureStubDto> stubs) throws Exception {
        List<CalendarItem> calendarItems = calendarItemLogic.getCalendarItemByIds(stubs.stream().map(IcureDto::getId).collect(Collectors.toList()));
        calendarItems.forEach(calendarItem -> stubs.stream().filter(s -> s.getId().equals(calendarItem.getId())).findFirst().ifPresent(stub -> {
            stub.getDelegations().forEach((s, delegationDtos) -> calendarItem.getDelegations().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
            stub.getEncryptionKeys().forEach((s, delegationDtos) -> calendarItem.getEncryptionKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
            stub.getCryptedForeignKeys().forEach((s, delegationDtos) -> calendarItem.getCryptedForeignKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
        }));
        calendarItemLogic.updateEntities(calendarItems);

        return Response.ok().build();
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

    @ApiOperation(
            value = "Get calendarItems by id",
            responseContainer = "Array",
            response = CalendarItemDto.class,
            httpMethod = "POST"
    )
    @POST
    @Path("/byIds")
    public Response getCalendarItemsWithIds(ListOfIdsDto calendarItemIds) {
        if (calendarItemIds == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<CalendarItem> calendarItems = calendarItemLogic.getCalendarItemByIds(calendarItemIds.getIds());

        boolean succeed = (calendarItems != null);
        if (succeed) {
            return Response.ok().entity(calendarItems.stream().map(p -> mapper.map(p, CalendarItemDto.class)).collect(Collectors.toList())).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting calendarItems failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.").build();
        }
    }
}
