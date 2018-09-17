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
import org.taktik.icure.entities.ClassificationTemplate;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.logic.ClassificationTemplateLogic;
import org.taktik.icure.services.external.rest.v1.dto.ClassificationTemplateDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Path("/classificationTemplate")
@Api(tags = { "classificationTemplate" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class ClassificationTemplateFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(ClassificationTemplateFacade.class);

	private MapperFacade mapper;
	private ClassificationTemplateLogic classificationTemplateLogic;

	@ApiOperation(
			value = "Create a classification Template with the current user",
			response = ClassificationTemplateDto.class,
			httpMethod = "POST",
			notes = "Returns an instance of created classification Template."
	)
	@POST
	public Response createClassificationTemplate(ClassificationTemplateDto c) {
		if (c == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		ClassificationTemplate element = classificationTemplateLogic.createClassificationTemplate(mapper.map(c, ClassificationTemplate.class));

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, ClassificationTemplateDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Classification Template creation failed.").build();
		}
	}

	@ApiOperation(
			value = "Get a classification Template",
			response = ClassificationTemplateDto.class,
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/{classificationTemplateId}")
	public Response getClassificationTemplate(@PathParam("classificationTemplateId") String classificationTemplateId) {
		if (classificationTemplateId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		ClassificationTemplate element = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId);

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, ClassificationTemplateDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting classification Template failed. Possible reasons: no such classification Template exists, or server error. Please try again or read the server log.").build();
		}
	}


	@ApiOperation(
			value = "Delete classification Templates.",
			response = String.class,
            responseContainer = "Array",
            httpMethod = "DELETE",
			notes = "Response is a set containing the ID's of deleted classification Templates."
	)
	@DELETE
	@Path("/{classificationTemplateIds}")
	public Response deleteClassificationTemplates(@PathParam("classificationTemplateIds") String classificationTemplateIds) {
        if (classificationTemplateIds==null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }
        List<String> ids = Arrays.asList(classificationTemplateIds.split(","));
        if (ids.size() == 0) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> deletedIds = classificationTemplateLogic.deleteClassificationTemplates(new HashSet<>(ids));

		boolean succeed = (deletedIds != null);
		if (succeed) {
			return Response.ok().entity(deletedIds).build();
		} else {
			return Response.status(500).type("text/plain").entity("Classification Template deletion failed.").build();
		}
	}

	@ApiOperation(
			value = "Modify a classification Template",
			response = ClassificationTemplateDto.class,
			httpMethod = "PUT",
			notes = "Returns the modified classification Template."
	)
	@PUT
	public Response modifyClassificationTemplate(ClassificationTemplateDto classificationTemplateDto) {
		if (classificationTemplateDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

	//TODO Ne modifier que le label
		classificationTemplateLogic.modifyClassificationTemplate(mapper.map(classificationTemplateDto, ClassificationTemplate.class));
        ClassificationTemplate modifiedClassificationTemplate = classificationTemplateLogic.getClassificationTemplate(classificationTemplateDto.getId());

        boolean succeed = (modifiedClassificationTemplate != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(modifiedClassificationTemplate, ClassificationTemplateDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Classification Template modification failed.").build();
        }
	}


	@ApiOperation(
			value = "Delegates a classification Template to a healthcare party",
			response = ClassificationTemplateDto.class,
			httpMethod = "POST",
			notes = "It delegates a classification Template to a healthcare party (By current healthcare party). Returns the element with new delegations."
	)
	@POST
	@Path("/{classificationTemplateId}/delegate")
	public Response newDelegations(@PathParam("classificationTemplateId") String classificationTemplateId, List<DelegationDto> ds) {
		if (classificationTemplateId == null || ds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		classificationTemplateLogic.addDelegations(classificationTemplateId, ds.stream().map(d->mapper.map(d, Delegation.class)).collect(Collectors.toList()));
		ClassificationTemplate classificationTemplateWithDelegation = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId);

		boolean succeed = (classificationTemplateWithDelegation != null && classificationTemplateWithDelegation.getDelegations() != null && classificationTemplateWithDelegation.getDelegations().size() > 0);
		if (succeed) {
			return Response.ok().entity(mapper.map(classificationTemplateWithDelegation, ClassificationTemplateDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Delegation creation for classification Template failed.").build();
		}
	}


    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setClassificationTemplateLogic(ClassificationTemplateLogic classificationTemplateLogic) {
		this.classificationTemplateLogic = classificationTemplateLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
