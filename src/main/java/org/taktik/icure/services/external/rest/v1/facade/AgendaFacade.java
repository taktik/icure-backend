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
import org.taktik.icure.entities.Agenda;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.AgendaLogic;
import org.taktik.icure.services.external.rest.v1.dto.AgendaDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/agenda")
@Api(tags = {"agenda"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class AgendaFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(AgendaFacade.class);

    private AgendaLogic agendaLogic;
    private MapperFacade mapper;

    @ApiOperation(response = AgendaDto.class, value = "Creates a agenda")
    @POST
    public Response createAgenda(AgendaDto agendaDto) {
        Response response;

        if (agendaDto == null) {
            response = ResponseUtils.badRequest("Cannot create calendar item: supplied AgendaDto is null");

        } else {
            Agenda agenda = agendaLogic.createAgenda(mapper.map(agendaDto, Agenda.class));
            if (agenda != null) {
                response = ResponseUtils.ok(mapper.map(agenda, AgendaDto.class));

            } else {
                response = ResponseUtils.internalServerError("Agenda creation failed");
            }
        }

        return response;
    }

    @ApiOperation(value = "Deletes an agenda")
    @DELETE
    @Path("/{agendaIds}")
    public Response deleteAgenda(@PathParam("agendaIds") String agendaIds) throws DeletionException {
        Response response;

        if (agendaIds == null) {
            response = ResponseUtils.badRequest("Cannot delete access log: supplied agendaIds is null");

        } else {
            List<String> deletedAgendaIds = agendaLogic.deleteAgenda(Arrays.asList(agendaIds.split(",")));
            if (deletedAgendaIds != null) {
                response = Response.ok().entity(deletedAgendaIds).build();
            } else {
                return Response.status(500).type("text/plain").entity("Agenda deletion failed.").build();
            }
        }

        return response;
    }

    @ApiOperation(response = AgendaDto.class, value = "Gets an agenda")
    @GET
    @Path("/{agendaId}")
    public Response getAgenda(@PathParam("agendaId") String agendaId) {
        Response response;

        if (agendaId == null) {
            response = ResponseUtils.badRequest("Cannot get access log: supplied agendaId is null");

        } else {
            Agenda agenda = agendaLogic.getAgenda(agendaId);
            if (agenda != null) {
                response = ResponseUtils.ok(mapper.map(agenda, AgendaDto.class));
            } else {
                response = Response.noContent().build();
            }
        }
        return response;
    }

    @ApiOperation(
            value = "Gets all agendas",
            response = AgendaDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    public Response getAgendas() {
        Response response;
        List<Agenda> agendas = agendaLogic.getAllEntities();
        if (agendas != null) {
            response = Response.ok().entity(agendas.stream().map(c -> mapper.map(c, AgendaDto.class)).collect(Collectors.toList())).build();
        } else {
            response = ResponseUtils.internalServerError("Agendas fetching failed");
        }
        return response;
    }

    @ApiOperation(
            value = "Gets all agendas for user",
            response = AgendaDto.class,
            httpMethod = "GET",
            notes = ""
    )
    @GET
    @Path("/byUser")
    public Response getAgendasForUser(@QueryParam("userId") String userId) {
        Response response;
        List<Agenda> agendas = agendaLogic.getAllAgendaForUser(userId);
        if (agendas != null && agendas.size() > 0) {
            response = Response.ok().entity(mapper.map(agendas.get(0), AgendaDto.class)).build();
        } else if(agendas != null){
            response = Response.noContent().build();
        } else {
            response = ResponseUtils.internalServerError("Agendas fetching failed");
        }
        return response;
    }

    @ApiOperation(
            value = "Gets readable agendas for user",
            response = AgendaDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    @Path("/readableForUser")
    public Response getReadableAgendasForUser(@QueryParam("userId") String userId) {
        Response response;
        List<Agenda> agendas = agendaLogic.getReadableAgendaForUser(userId);
        if (agendas != null) {
            response = Response.ok().entity(agendas.stream().map(c -> mapper.map(c, AgendaDto.class)).collect(Collectors.toList())).build();
        } else {
            response = ResponseUtils.internalServerError("Readable agendas fetching failed");
        }
        return response;
    }


    @ApiOperation(response = AgendaDto.class, value = "Modifies an agenda")
    @PUT
    public Response modifyAgenda(AgendaDto agendaDto) {
        Response response;

        if (agendaDto == null) {
            response = ResponseUtils.badRequest("Cannot modify calendar Item: supplied agendaDto is null");

        } else {
            Agenda agenda = agendaLogic.modifyAgenda(mapper.map(agendaDto, Agenda.class));
            if (agenda != null) {
                response = ResponseUtils.ok(mapper.map(agenda, AgendaDto.class));

            } else {
                response = ResponseUtils.internalServerError("Agenda modification failed");
            }
        }

        return response;
    }

    @Context
    public void setagendaLogic(AgendaLogic agendaLogic) {
        this.agendaLogic = agendaLogic;
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
