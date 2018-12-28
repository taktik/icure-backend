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
import org.taktik.icure.entities.Form;
import org.taktik.icure.entities.FormTemplate;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.FormLogic;
import org.taktik.icure.logic.FormTemplateLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.FormDto;
import org.taktik.icure.services.external.rest.v1.dto.FormTemplateDto;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout;
import org.taktik.icure.utils.FormUtils;
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
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

@Component
@Path("/form")
@Api(tags = { "form" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class FormFacade implements OpenApiFacade {
	private static Logger log = LoggerFactory.getLogger(FormFacade.class);

	private MapperFacade mapper;

	private FormTemplateLogic formTemplateLogic;
	private FormLogic formLogic;
	private ICureSessionLogic sessionLogic;

	@ApiOperation(response = FormDto.class, value = "Gets a form")
	@GET
	@Path("/{formId}")
	public Response getForm(@PathParam("formId") String formId) {
		Response response;

		if (formId == null) {
			response = ResponseUtils.badRequest("Cannot retrieve form: provided form ID is null");

		} else {
			Form form = formLogic.getForm(formId);

			if (form == null) {
				response = ResponseUtils.internalServerError("FormTemplate fetching failed");
			} else {
				FormDto formDto = mapper.map(form, FormDto.class);
				response = ResponseUtils.ok(formDto);
			}
		}

		return response;
	}

	@ApiOperation(
			value = "Get a list of forms by ids",
			response = FormDto.class,
			responseContainer = "Array",
			httpMethod = "POST",
			notes = "Keys must be delimited by coma"
	)
	@POST
	@Path("/byIds")
	public Response getForms(ListOfIdsDto formIds) {
		Response response;

		if (formIds == null) {
			response = ResponseUtils.badRequest("Cannot retrieve form: provided form ID is null");

		} else {
			List<Form> forms = formLogic.getForms(formIds.getIds());

			if (forms == null) {
				response = ResponseUtils.internalServerError("FormTemplate fetching failed");
			} else {
				List<FormDto> formDtos = forms.stream().map((f) -> mapper.map(f, FormDto.class)).collect(Collectors.toList());
				response = ResponseUtils.ok(formDtos);
			}
		}

		return response;
	}

	@ApiOperation(
			value = "Get a list of forms by ids",
			response = FormDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/childrenOf/{formId}/{hcPartyId}")
	public Response getChildren(@PathParam("formId") String formId,
								@PathParam("hcPartyId") String hcPartyId) {
		Response response;

		if (formId == null) {
			response = ResponseUtils.badRequest("Cannot retrieve form: provided form ID is null");

		} else {
			List<Form> forms = formLogic.findByHcPartyParentId(hcPartyId, formId);

			if (forms == null) {
				response = ResponseUtils.internalServerError("FormTemplate fetching failed");
			} else {
				List<FormDto> formDtos = forms.stream().map((f) -> mapper.map(f, FormDto.class)).collect(Collectors.toList());
				response = ResponseUtils.ok(formDtos);
			}
		}

		return response;
	}

	@ApiOperation(
			value = "Create a form with the current user",
			response = FormDto.class,
			httpMethod = "POST",
			notes = "Returns an instance of created form."
	)
	@POST
	public Response createForm(FormDto ft) {
		if (ft == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Form form;
		try {
			form = formLogic.createForm(mapper.map(ft, Form.class));
		} catch (MissingRequirementsException e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

		boolean succeed = (form != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(form, FormDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Contact creation failed.").build();
		}
	}

	@ApiOperation(
		value = "Delegates a form to a healthcare party",
		response = FormDto.class,
		httpMethod = "POST",
		notes = "It delegates a form to a healthcare party. Returns the form with the new delegations."
	)
	@POST
	@Path("/delegate/{formId}")
	public Response newDelegations(@PathParam("formId") String formId, List<DelegationDto> ds) {
		if (formId == null || ds == null) {
			return Response.status(400).type(TEXT_PLAIN_TYPE)
				.entity("A required query parameter was not specified for this request.").build();
		}

		formLogic.addDelegations(formId, ds.stream().map(d->mapper.map(d, Delegation.class)).collect(Collectors.toList()));
		Form formWithDelegation = formLogic.getForm(formId);

		if (formWithDelegation != null && formWithDelegation.getDelegations() != null && formWithDelegation.getDelegations().size() > 0) {
			return Response.ok().entity(mapper.map(formWithDelegation, FormDto.class)).build();
		} else {
			return Response.status(500).type(TEXT_PLAIN_TYPE).entity("Delegation creation for Form failed").build();
		}
	}

	@ApiOperation(
			value = "Modify a form",
			response = FormDto.class,
			httpMethod = "PUT",
			notes = "Returns the modified form."
	)
	@PUT
	public Response modifyForm(FormDto formDto) {
		if (formDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		try {
			formLogic.modifyForm(mapper.map(formDto, Form.class));
			Form modifiedForm = formLogic.getForm(formDto.getId());

			boolean succeed = (modifiedForm != null);
			if (succeed) {
				return Response.ok().entity(mapper.map(modifiedForm, FormDto.class)).build();
			} else {
				return Response.status(500).type("text/plain").entity("Form modification failed.").build();
			}
		} catch (MissingRequirementsException e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@ApiOperation(
			value = "Modify a batch of forms",
			response = FormDto.class,
			responseContainer = "Array",
			httpMethod = "PUT",
			notes = "Returns the modified forms."
	)
	@PUT
	@Path("/batch")
	public Response modifyForms(List<FormDto> formDtos) {
		if (formDtos == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		try {
			List<Form> forms = formLogic.updateEntities(formDtos.stream().map(f -> mapper.map(f, Form.class)).collect(Collectors.toList()));
			return Response.ok().entity(forms.stream().map(f -> mapper.map(f, FormDto.class)).collect(Collectors.toList())).build();

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@ApiOperation(
			value = "List forms found By Healthcare Party and secret foreign keys.",
			response = FormDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys")
	public Response findByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId,
													@QueryParam("secretFKeys") String secretFKeys,
													@QueryParam("healthElementId") String healthElementId,
													@QueryParam("planOfActionId") String planOfActionId,
													@QueryParam("formTemplateId") String formTemplateId) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		List<Form> formsList = formLogic.findByHCPartyPatient(hcPartyId, new ArrayList<>(secretPatientKeys), healthElementId, planOfActionId, formTemplateId);

		boolean succeed = (formsList != null);
		if (succeed) {
			// mapping to Dto
			List<FormDto> formDtoList = formsList.stream().map(contact -> mapper.map(contact, FormDto.class)).collect(Collectors.toList());
			return Response.ok().entity(formDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Forms failed. Please try again or read the server log.").build();
		}
	}


	@ApiOperation(response = FormTemplateDto.class, value = "Gets a form template by guid")
	@GET
	@Path("/template/{formTemplateId}")
	public Response getFormTemplate(@PathParam("formTemplateId") String formTemplateId) {
		Response response;

		if (formTemplateId == null) {
			response = ResponseUtils.badRequest("Cannot retrieve form: provided form ID is null");

		} else {
			FormTemplate formTemplate = formTemplateLogic.getFormTemplateById(formTemplateId);

			if (formTemplate == null) {
				response = ResponseUtils.internalServerError("FormTemplate fetching failed");
			} else {
				FormTemplateDto formTemplateDto = mapper.map(formTemplate, FormTemplateDto.class);
				response = ResponseUtils.ok(formTemplateDto);
			}
		}

		return response;
	}

	@ApiOperation(response = FormTemplateDto.class, responseContainer = "Array", value = "Gets a form template")
	@GET
	@Path("/template/{specialityCode}/guid/{formTemplateGuid}")
	public Response getFormTemplatesByGuid(@PathParam("formTemplateGuid") String formTemplateGuid, @PathParam("specialityCode") String specialityCode) {
		Response response;

			List<FormTemplate> formTemplates = formTemplateLogic.getFormTemplatesByGuid(sessionLogic.getCurrentUserId(),specialityCode,formTemplateGuid);

			if (formTemplates == null) {
				response = ResponseUtils.internalServerError("FormTemplate fetching failed");
			} else {
				List<FormTemplateDto> formTemplateDtos = formTemplates.stream().map(ft->mapper.map(ft, FormTemplateDto.class)).collect(Collectors.toList());
				response = ResponseUtils.ok(formTemplateDtos);
			}

		return response;
	}


	@ApiOperation(
			response = FormTemplateDto.class,
			responseContainer = "Array",
			value = "Gets all form templates")
	@GET
	@Path("/template/bySpecialty/{specialityCode}")
	public Response findFormTemplatesBySpeciality(@PathParam("specialityCode") String specialityCode, @QueryParam("loadLayout") Boolean loadLayout) {
		Response response;

		if (specialityCode == null) {
			response = ResponseUtils.badRequest("Cannot retrieve form templates: provided speciality Code is null");
		} else {
			List<FormTemplate> formTemplates = formTemplateLogic.getFormTemplatesBySpecialty(specialityCode, false);
			return ResponseUtils.ok(formTemplates.stream().map((ft) -> mapper.map(ft, FormTemplateDto.class)).collect(Collectors.toList()));
		}

		return response;
	}

	@ApiOperation(
			response = FormTemplateDto.class,
			responseContainer = "Array",
			value = "Gets all form templates for current user")
	@GET
	@Path("/template")
	public Response findFormTemplates() {
		List<FormTemplate> formTemplates;
		try {
			formTemplates = formTemplateLogic.getFormTemplatesByUser(sessionLogic.getCurrentUserId(), false);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
		List<FormTemplateDto> templates = formTemplates.stream().map((ft) -> mapper.map(ft, FormTemplateDto.class)).collect(Collectors.toList());
		return ResponseUtils.ok(templates);
	}

	@ApiOperation(
			value = "Create a form template with the current user",
			response = FormTemplateDto.class,
			httpMethod = "POST",
			notes = "Returns an instance of created form template."
	)
	@POST
	@Path("/template")
	public Response createFormTemplate(FormTemplateDto ft) {
		if (ft == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		FormTemplate formTemplate;
		formTemplate = formTemplateLogic.createFormTemplate(mapper.map(ft, FormTemplate.class));

		boolean succeed = (formTemplate != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(formTemplate, FormTemplateDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Form creation failed.").build();
		}
	}

	@ApiOperation(
			value = "Delete a form template",
			response = Boolean.class,
			httpMethod = "DELETE"
	)
	@DELETE
	@Path("/template/{formTemplateId}")
	public Response deleteFormTemplate(@PathParam("formTemplateId") String formTemplateId) throws Exception {
		if (formTemplateId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}
		formTemplateLogic.deleteEntities(Collections.singletonList(formTemplateId));
		return Response.ok(true).build();
	}

	@ApiOperation(
			value = "Modify a form template with the current user",
			response = FormTemplateDto.class,
			httpMethod = "PUT",
			notes = "Returns an instance of created form template."
	)
	@PUT
	@Path("/template/{formTemplateId}")
	public Response updateFormTemplate(@PathParam("formTemplateId") String formTemplateId, FormTemplateDto ft) {
		if (ft == null || formTemplateId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}
		FormTemplate formTemplate;

		FormTemplate template = mapper.map(ft, FormTemplate.class);

		template.setId(formTemplateId);

		formTemplate = formTemplateLogic.modifyFormTemplate(template);

		boolean succeed = (formTemplate != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(formTemplate, FormTemplateDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Form modification failed.").build();
		}
	}

	@ApiOperation(
			value = "Convert legacy format layouts to a list of FormLayout",
			response = FormLayout.class,
			responseContainer = "Array",
			httpMethod = "PUT",
			notes = "Returns the converted layouts."
	)
	@PUT
	@Path("/template/legacy")
	@Consumes(APPLICATION_OCTET_STREAM)
	public Response convertLegacyFormTemplates(byte[] data) {
		try {
			List<FormLayout> formLayouts = new FormUtils().parseLegacyXml(new InputStreamReader(new ByteArrayInputStream(data), "UTF8")).stream().map(f->mapper.map(f,FormLayout.class)).collect(Collectors.toList());
			boolean succeed = (formLayouts != null);
			if (succeed) {
				return Response.ok().entity(formLayouts).build();
			} else {
				return Response.status(500).type("text/plain").entity("Form conversion failed.").build();
			}
		} catch (TransformerException | IOException e) {
			log.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setFormTemplateLogic(FormTemplateLogic formTemplateLogic) {
		this.formTemplateLogic = formTemplateLogic;
	}

	@Context
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Context
	public void setFormLogic(FormLogic formLogic) {
		this.formLogic = formLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		log.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
