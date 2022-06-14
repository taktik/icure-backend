package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.ApplicationSettings;
import org.taktik.icure.logic.ApplicationSettingsLogic;
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto;
import org.taktik.icure.services.external.rest.v1.dto.ApplicationSettingsDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/appsettings")
@Api(tags = {"application-settings"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class ApplicationSettingsFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(ApplicationSettingsFacade.class);
    private ApplicationSettingsLogic applicationSettingsLogic;
    private MapperFacade mapper;

    @ApiOperation(
        value = "Gets all application settings",
        response = ApplicationSettingsDto.class,
        responseContainer = "Array"
    )
    @GET
    public Response getApplicationSettings() {
        Response response;

        List<ApplicationSettings> applicationSettings = applicationSettingsLogic.getAllEntities();
        if (applicationSettings != null) {
            response = Response.ok().entity(applicationSettings.stream().map(c -> mapper.map(c, ApplicationSettingsDto.class)).collect(Collectors.toList())).build();
        } else {
            response = ResponseUtils.internalServerError("application settings fetching failed");
        }
        return response;
    }

    @Context
    public void setApplicationSettingsLogic(ApplicationSettingsLogic applicationSettingsLogic) {
        this.applicationSettingsLogic = applicationSettingsLogic;
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

    @ApiOperation(response = ApplicationSettingsDto.class, value = "Create an application settings")
    @POST
    public Response createApplicationSettings(ApplicationSettingsDto applicationSettingsDto){
        Response response;
        if(applicationSettingsDto == null){
            response = ResponseUtils.badRequest("Cannot create application settings: supplied application settings is null");
        }else{
            ApplicationSettings applicationSettings = applicationSettingsLogic.createApplicationSettings(mapper.map(applicationSettingsDto, ApplicationSettings.class));
            if(applicationSettings != null){
                response = ResponseUtils.ok(mapper.map(applicationSettingsDto, ApplicationSettingsDto.class));
            }else{
                response = ResponseUtils.internalServerError("Cannot create application settings");
            }
        }

        return response;
    }

    @ApiOperation(response = ApplicationSettingsDto.class, value = "Update an application settings")
    @POST
    public Response updateApplicationSettings(ApplicationSettingsDto applicationSettingsDto){
        Response response;
        if(applicationSettingsDto == null){
            response = ResponseUtils.badRequest("Cannot update application settings: supplied application settings is null");
        }else{
            ApplicationSettings applicationSettings = applicationSettingsLogic.modifyApplicationSettings(mapper.map(applicationSettingsDto, ApplicationSettings.class));
            if(applicationSettings != null){
                response = ResponseUtils.ok(mapper.map(applicationSettingsDto, ApplicationSettingsDto.class));
            }else{
                response = ResponseUtils.internalServerError("Cannot update application settings");
            }
        }
        return response;
    }
}

