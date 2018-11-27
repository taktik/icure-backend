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

package org.taktik.icure.services.external.rest.v1.facade.be;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.be.format.logic.MultiFormatLogic;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.services.external.rest.v1.dto.ContactDto;
import org.taktik.icure.services.external.rest.v1.dto.ResultInfoDto;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/be_result_import")
@Api(tags = { "be_result_import" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class ResultImportFacade implements OpenApiFacade {
    private static final Logger logger = LoggerFactory.getLogger(ResultImportFacade.class);

    private MultiFormatLogic multiFormatLogic;
    private DocumentLogic documentLogic;
    private MapperFacade mapper;

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Context
    public void setMultiFormatLogic(MultiFormatLogic multiFormatLogic) {
        this.multiFormatLogic = multiFormatLogic;
    }

    @Context
    public void setDocumentLogic(DocumentLogic documentLogic) {
        this.documentLogic = documentLogic;
    }

    @ApiOperation(
            value = "Can we handle this document",
            response = Boolean.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/canhandle/{id}")
    @GET
    public Boolean canHandle(@PathParam("id") String id) throws IOException {
        return multiFormatLogic.canHandle(documentLogic.get(id));
    }

    @ApiOperation(
            value = "Extract general infos from document",
            response = ResultInfoDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/infos/{id}")
    @GET
    public List<ResultInfoDto> getInfos(@PathParam("id") String id) throws IOException {
        return multiFormatLogic.getInfos(documentLogic.get(id)).stream().map(i->mapper.map(i,ResultInfoDto.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "import document",
            response = ContactDto.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/import/{documentId}/{hcpId}/{language}")
    @POST
    public ContactDto doImport(@PathParam("documentId") String documentId, @PathParam("hcpId") String hcpId, @PathParam("language") String language, @QueryParam("protocolIds") String protocolIds, @QueryParam("formIds") String formIds, @QueryParam("planOfActionId") String planOfActionId, ContactDto ctc) throws IOException {
        return mapper.map(multiFormatLogic.doImport(language, documentLogic.get(documentId), hcpId, Arrays.asList(protocolIds.split(",")), Arrays.asList(formIds.split(",")), planOfActionId, mapper.map(ctc, Contact.class)), ContactDto.class);
    }

    @ExceptionHandler(Exception.class)
    Response exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseUtils.internalServerError(e.getMessage());
    }

}
