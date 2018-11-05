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
import org.taktik.icure.entities.Place;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.PlaceLogic;
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto;
import org.taktik.icure.services.external.rest.v1.dto.PlaceDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/place")
@Api(tags = {"place"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class PlaceFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(PlaceFacade.class);

    private PlaceLogic placeLogic;
    private MapperFacade mapper;

    @ApiOperation(response = PlaceDto.class, value = "Creates a place")
    @POST
    public Response createPlace(PlaceDto placeDto) {
        Response response;

        if (placeDto == null) {
            response = ResponseUtils.badRequest("Cannot create calendar item: supplied PlaceDto is null");

        } else {
            Place place = placeLogic.createPlace(mapper.map(placeDto, Place.class));
            if (place != null) {
                response = ResponseUtils.ok(mapper.map(place, PlaceDto.class));

            } else {
                response = ResponseUtils.internalServerError("Place creation failed");
            }
        }

        return response;
    }

    @ApiOperation(value = "Deletes an place")
    @DELETE
    @Path("/{placeIds}")
    public Response deletePlace(@PathParam("placeIds") String placeIds) throws DeletionException {
        Response response;

        if (placeIds == null) {
            response = ResponseUtils.badRequest("Cannot delete access log: supplied placeIds is null");

        } else {
            List<String> deletedPlaceIds = placeLogic.deletePlace(Arrays.asList(placeIds.split(",")));
            if (deletedPlaceIds != null) {
                response = Response.ok().entity(deletedPlaceIds).build();
            } else {
                return Response.status(500).type("text/plain").entity("Place deletion failed.").build();
            }
        }

        return response;
    }

    @ApiOperation(response = PlaceDto.class, value = "Gets an place")
    @GET
    @Path("/{placeId}")
    public Response getPlace(@PathParam("placeId") String placeId) {
        Response response;

        if (placeId == null) {
            response = ResponseUtils.badRequest("Cannot get access log: supplied placeId is null");

        } else {
            Place place = placeLogic.getPlace(placeId);
            if (place != null) {
                response = ResponseUtils.ok(mapper.map(place, PlaceDto.class));

            } else {
                response = ResponseUtils.internalServerError("Place fetching failed");
            }
        }
        return response;
    }

    @ApiOperation(
            value = "Gets all places",
            response = PlaceDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    public Response getPlaces() {
        Response response;
        List<Place> places = placeLogic.getAllEntities();
        if (places != null) {
            response = Response.ok().entity(places.stream().map(c -> mapper.map(c, PlaceDto.class)).collect(Collectors.toList())).build();

        } else {
            response = ResponseUtils.internalServerError("Places fetching failed");
        }
        return response;
    }

    @ApiOperation(response = PlaceDto.class, value = "Modifies an place")
    @PUT
    public Response modifyPlace(PlaceDto placeDto) {
        Response response;

        if (placeDto == null) {
            response = ResponseUtils.badRequest("Cannot modify calendar Item: supplied placeDto is null");

        } else {
            Place place = placeLogic.modifyPlace(mapper.map(placeDto, Place.class));
            if (place != null) {
                response = ResponseUtils.ok(mapper.map(place, PlaceDto.class));

            } else {
                response = ResponseUtils.internalServerError("Place modification failed");
            }
        }

        return response;
    }

    @Context
    public void setplaceLogic(PlaceLogic placeLogic) {
        this.placeLogic = placeLogic;
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

