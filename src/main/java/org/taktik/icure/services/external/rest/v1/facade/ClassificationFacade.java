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
import org.springframework.web.bind.annotation.PathVariable;
import org.taktik.icure.entities.Classification;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.logic.ClassificationLogic;
import org.taktik.icure.services.external.rest.v1.dto.ClassificationDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Path("/classification")
@Api(tags = { "classification" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class ClassificationFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(ClassificationFacade.class);

	private MapperFacade mapper;
	private ClassificationLogic classificationLogic;

	@ApiOperation(
			value = "Create a classification with the current user",
			response = ClassificationDto.class,
			httpMethod = "POST",
			notes = "Returns an instance of created classification Template."
	)
	@POST
	public Response createClassification(ClassificationDto c) {
		if (c == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Classification element = classificationLogic.createClassification(mapper.map(c, Classification.class));

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, ClassificationDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Classification creation failed.").build();
		}
	}

	@ApiOperation(
			value = "Get a classification Template",
			response = ClassificationDto.class,
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/{classificationId}")
	public Response getClassification(@PathParam("classificationId") String classificationId) {
		if (classificationId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Classification element = classificationLogic.getClassification(classificationId);

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, ClassificationDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting classification failed. Possible reasons: no such classification exists, or server error. Please try again or read the server log.").build();
		}
	}

    @ApiOperation(
        value = "Get a list of classifications",
        response = ClassificationDto.class,
        httpMethod = "GET",
        responseContainer = "Array",
        notes = "Ids are seperated by a coma"
    )
    @GET
    @Path("/byIds/{ids}")
    public Response getClassificationByHcPartyId(@PathParam( "ids") String ids) {
        if (ids == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        List<Classification> elements = classificationLogic.getClassificationByIds(Arrays.asList(ids.split(",")));

        boolean succeed = (elements != null);
        if (succeed) {
            return Response.ok().entity(elements.stream().map(x -> mapper.map(x, ClassificationDto.class)).collect(Collectors.toList())).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting classification failed. Possible reasons: no such classification exists, or server error. Please try again or read the server log.").build();
        }
    }



	@ApiOperation(
			value = "List classification Templates found By Healthcare Party and secret foreign keyelementIds.",
			response = ClassificationDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys hast to delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys")
	public Response findByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId, @QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		List<Classification> elementList = classificationLogic.findByHCPartySecretPatientKeys(hcPartyId, new ArrayList<>(secretPatientKeys));

		boolean succeed = (elementList != null);
		if (succeed) {
			// mapping to Dto
			List<ClassificationDto> elementDtoList = elementList.stream().map(element -> mapper.map(element, ClassificationDto.class)).collect(Collectors.toList());
			return Response.ok().entity(elementDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting the classification failed. Please try again or read the server log.").build();
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
	@Path("/{classificationIds}")
	public Response deleteClassifications(@PathParam("classificationIds") String classificationIds) {
        if (classificationIds==null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }
        List<String> ids = Arrays.asList(classificationIds.split(","));
        if (ids.size() == 0) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> deletedIds = classificationLogic.deleteClassifications(new HashSet<>(ids));

		boolean succeed = (deletedIds != null);
		if (succeed) {
			return Response.ok().entity(deletedIds).build();
		} else {
			return Response.status(500).type("text/plain").entity("Classification deletion failed.").build();
		}
	}

	@ApiOperation(
			value = "Modify a classification Template",
			response = ClassificationDto.class,
			httpMethod = "PUT",
			notes = "Returns the modified classification Template."
	)
	@PUT
	public Response modifyClassification(ClassificationDto classificationDto) {
		if (classificationDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}


		classificationLogic.modifyClassification(mapper.map(classificationDto, Classification.class));
        Classification modifiedClassification = classificationLogic.getClassification(classificationDto.getId());

        boolean succeed = (modifiedClassification != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(modifiedClassification, ClassificationDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Classification modification failed.").build();
        }
	}


	@ApiOperation(
			value = "Delegates a classification to a healthcare party",
			response = ClassificationDto.class,
			httpMethod = "POST",
			notes = "It delegates a classification to a healthcare party (By current healthcare party). Returns the element with new delegations."
	)
	@POST
	@Path("/{classificationId}/delegate")
	public Response newDelegations(@PathParam("classificationId") String classificationId, List<DelegationDto> ds) {
		if (classificationId == null || ds == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		classificationLogic.addDelegations(classificationId, ds.stream().map(d->mapper.map(d, Delegation.class)).collect(Collectors.toList()));
		Classification classificationWithDelegation = classificationLogic.getClassification(classificationId);

		boolean succeed = (classificationWithDelegation != null && classificationWithDelegation.getDelegations() != null && classificationWithDelegation.getDelegations().size() > 0);
		if (succeed) {
			return Response.ok().entity(mapper.map(classificationWithDelegation, ClassificationDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Delegation creation for classification failed.").build();
		}
	}

	@ApiOperation(
			value = "Update delegations in classification",
			httpMethod = "POST",
			notes = "Keys must be delimited by coma"
	)
	@POST
	@Path("/delegations")
	public Response setClassificationsDelegations(List<IcureStubDto> stubs) throws Exception {
		List<Classification> classifications = classificationLogic.getClassificationByIds(stubs.stream().map(IcureDto::getId).collect(Collectors.toList()));
		classifications.forEach(classification -> stubs.stream().filter(s -> s.getId().equals(classification.getId())).findFirst().ifPresent(stub -> {
			stub.getDelegations().forEach((s, delegationDtos) -> classification.getDelegations().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getEncryptionKeys().forEach((s, delegationDtos) -> classification.getEncryptionKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getCryptedForeignKeys().forEach((s, delegationDtos) -> classification.getCryptedForeignKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
		}));
		classificationLogic.updateEntities(classifications);

		return Response.ok().build();
	}


    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setClassificationLogic(ClassificationLogic classificationLogic) {
		this.classificationLogic = classificationLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
