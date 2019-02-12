package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.MedicalLocation;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.MedicalLocationLogic;
import org.taktik.icure.services.external.rest.v1.dto.MedicalLocationDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/medicallocation")
@Api(tags = {"medical-location"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class MedicalLocationFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(MedicalLocationFacade.class);

    private MedicalLocationLogic medicalLocationLogic;
    private MapperFacade mapper;

    @ApiOperation(response = MedicalLocationDto.class, value = "Creates a medical location")
    @POST
    public Response createMedicalLocation(MedicalLocationDto medicalLocationDto) {
        Response response;

        if (medicalLocationDto == null) {
            response = ResponseUtils.badRequest("Cannot create medical location: supplied medicalLocationDto is null");
        } else {
            MedicalLocation medicalLocation = medicalLocationLogic.createMedicalLocation(mapper.map(medicalLocationDto, MedicalLocation.class));
            if (medicalLocation != null) {
                response = ResponseUtils.ok(mapper.map(medicalLocation, MedicalLocationDto.class));

            } else {
                response = ResponseUtils.internalServerError("Medical location creation failed");
            }
        }

        return response;
    }

    @ApiOperation(value = "Deletes a medical location")
    @DELETE
    @Path("/{locationIds}")
    public Response deleteMedicalLocation(@PathParam("locationIds") String locationIds) throws DeletionException {
        Response response;

        if (locationIds == null) {
            response = ResponseUtils.badRequest("Cannot delete medical locations: supplied locationIds is null");
        } else {
            List<String> deletedLocationIds = medicalLocationLogic.deleteMedicalLocation(Arrays.asList(locationIds.split(",")));
            if (deletedLocationIds != null) {
                response = Response.ok().entity(deletedLocationIds).build();
            } else {
                return Response.status(500).type("text/plain").entity("medical location deletion failed.").build();
            }
        }

        return response;
    }

    @ApiOperation(response = MedicalLocationDto.class, value = "Gets a medical location")
    @GET
    @Path("/{locationId}")
    public Response getMedicalLocation(@PathParam("locationId") String locationId) {
        Response response;

        if (locationId == null) {
            response = ResponseUtils.badRequest("Cannot get medical location: supplied locationId is null");

        } else {
            MedicalLocation location = medicalLocationLogic.getMedicalLocation(locationId);
            if (location != null) {
                response = ResponseUtils.ok(mapper.map(location, MedicalLocationDto.class));
            } else {
                response = ResponseUtils.internalServerError("medical location fetching failed");
            }
        }
        return response;
    }

    @ApiOperation(
            value = "Gets all medical locations",
            response = MedicalLocationDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @GET
    public Response getMedicalLocations() {
        Response response;
        List<MedicalLocation> locations = medicalLocationLogic.getAllEntities();
        if (locations != null) {
            response = Response.ok().entity(locations.stream().map(c -> mapper.map(c, MedicalLocationDto.class)).collect(Collectors.toList())).build();

        } else {
            response = ResponseUtils.internalServerError("medical locations fetching failed");
        }
        return response;
    }

    @ApiOperation(response = MedicalLocationDto.class, value = "Modifies a medical location")
    @PUT
    public Response modifyMedicalLocation(MedicalLocationDto medicalLocationDto) {
        Response response;

        if (medicalLocationDto == null) {
            response = ResponseUtils.badRequest("Cannot modify medical location: supplied medicalLocationDto is null");
        } else {
            MedicalLocation location = medicalLocationLogic.modifyMedicalLocation(mapper.map(medicalLocationDto, MedicalLocation.class));
            if (location != null) {
                response = ResponseUtils.ok(mapper.map(location, MedicalLocationDto.class));
            } else {
                response = ResponseUtils.internalServerError("medical location modification failed");
            }
        }

        return response;
    }

    @Context
    public void setMedicalLocationLogic(MedicalLocationLogic medicalLocationLogic) {
        this.medicalLocationLogic = medicalLocationLogic;
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
