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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.taktik.icure.entities.DocumentTemplate;
import org.taktik.icure.entities.embed.DocumentType;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.DocumentTemplateLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.DocumentTemplateDto;
import org.taktik.icure.services.external.rest.v1.dto.data.ByteArrayDto;
import org.taktik.icure.utils.FormUtils;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/doctemplate")
@Api(tags = {"doctemplate"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class DocumentTemplateFacade implements OpenApiFacade {
    private static Logger log = LoggerFactory.getLogger(DocumentTemplateFacade.class);

    private MapperFacade mapper;

    private DocumentTemplateLogic documentTemplateLogic;

    private ICureSessionLogic sessionLogic;

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Context
    public void setDocumentTemplateLogic(DocumentTemplateLogic documentTemplateLogic) {
        this.documentTemplateLogic = documentTemplateLogic;
    }

    @Context
    public void setSessionLogic(ICureSessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @ApiOperation(response = DocumentTemplateDto.class, value = "Gets a document template")
    @GET
    @Path("/{documentTemplateId}")
    public Response getDocumentTemplate(@PathParam("documentTemplateId") String documentTemplateId) {
        Response response;

        if (documentTemplateId == null) {
            response = ResponseUtils.badRequest("Cannot retrieve document template: provided document template ID is null");

        } else {
            DocumentTemplate documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId);

            if (documentTemplate == null) {
                response = ResponseUtils.internalServerError("DocumentTemplate fetching failed");
            } else {
                DocumentTemplateDto documentTemplateDto = mapper.map(documentTemplate, DocumentTemplateDto.class);
                response = ResponseUtils.ok(documentTemplateDto);
            }
        }

        return response;
    }

    @ApiOperation(response = DocumentDto.class, value = "Deletes a document template")
    @DELETE
    @Path("/{documentTemplateIds}")
    public Response deleteDocumentTemplate(@PathParam("documentTemplateIds") String documentTemplateIds) throws DeletionException {
        Response response;

        if (documentTemplateIds == null) {
            return ResponseUtils.badRequest("Cannot delete document template: provided document template ID is null");
        }

        List<String> documentTemplateIdsList = Arrays.asList(documentTemplateIds.split(","));
        try {
            documentTemplateLogic.deleteEntities(documentTemplateIdsList);
            response = ResponseUtils.ok();
        } catch (Exception e) {
            response = ResponseUtils.internalServerError("Document template deletion failed");
        }
        return response;
    }

    @ApiOperation(
            response = DocumentTemplateDto.class,
            responseContainer = "Array",
            value = "Gets all document templates")
    @GET
    @Path("/bySpecialty/{specialityCode}")
    public Response findDocumentTemplatesBySpeciality(@PathParam("specialityCode") String specialityCode) {
        Response response;

        if (specialityCode == null) {
            response = ResponseUtils.badRequest("Cannot retrieve document templates: provided speciality Code is null");
        } else {
            List<DocumentTemplate> documentTemplates = documentTemplateLogic.getDocumentTemplatesBySpecialty(specialityCode);
            return ResponseUtils.ok(documentTemplates.stream().map((ft) -> mapper.map(ft, DocumentTemplateDto.class)).collect(Collectors.toList()));
        }

        return response;
    }

    @ApiOperation(
            response = DocumentTemplateDto.class,
            responseContainer = "Array",
            value = "Gets all document templates by Type")
    @GET
    @Path("/byDocumentType/{documentTypeCode}")
    public Response findDocumentTemplatesByDocumentType(@PathParam("documentTypeCode") String documentTypeCode) {
        Response response;

        DocumentType documentType = DocumentType.fromName(documentTypeCode);
        if (documentTypeCode == null) {
            response = ResponseUtils.badRequest("Cannot retrieve document templates: provided DocumentType Code is null");
        } else if (documentType == null) {
            response = ResponseUtils.badRequest("Cannot retrieve document templates: provided Document Type Code doesn't exists");

        } else {
            List<DocumentTemplate> documentTemplates = documentTemplateLogic.getDocumentTemplatesByDocumentType(documentTypeCode);
            return ResponseUtils.ok(documentTemplates.stream().map((ft) -> mapper.map(ft, DocumentTemplateDto.class)).collect(Collectors.toList()));
        }

        return response;
    }

    @ApiOperation(
            response = DocumentTemplateDto.class,
            responseContainer = "Array",
            value = "Gets all document templates by Type For currentUser")
    @GET
    @Path("/byDocumentTypeForCurrentUser/{documentTypeCode}")
    public Response findDocumentTemplatesByDocumentTypeForCurrentUser(@PathParam("documentTypeCode") String documentTypeCode) {
        Response response;

        DocumentType documentType = DocumentType.fromName(documentTypeCode);
        if (documentTypeCode == null) {
            response = ResponseUtils.badRequest("Cannot retrieve document templates: provided DocumentType Code is null");
        } else if (documentType == null) {
            response = ResponseUtils.badRequest("Cannot retrieve document templates: provided Document Type Code doesn't exists");

        } else {
            List<DocumentTemplate> documentTemplates = documentTemplateLogic.getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode,sessionLogic.getCurrentUserId());
            return ResponseUtils.ok(documentTemplates.stream().map((ft) -> mapper.map(ft, DocumentTemplateDto.class)).collect(Collectors.toList()));
        }

        return response;
    }

    @ApiOperation(
            response = DocumentTemplateDto.class,
            responseContainer = "Array",
            value = "Gets all document templates for current user")
    @GET
    public Response findDocumentTemplates() {
        List<DocumentTemplate> documentTemplates;
        documentTemplates = documentTemplateLogic.getDocumentTemplatesByUser(sessionLogic.getCurrentUserId());
        return ResponseUtils.ok(documentTemplates.stream().map((ft) -> mapper.map(ft, DocumentTemplateDto.class)).collect(Collectors.toList()));
    }

    @ApiOperation(
            response = DocumentTemplateDto.class,
            responseContainer = "Array",
            value = "Gets all document templates for all users")
    @GET
    @Path("/find/all")
    public Response findAllDocumentTemplates() {
        List<DocumentTemplate> documentTemplates;
        documentTemplates = documentTemplateLogic.getAllEntities();
        return ResponseUtils.ok(documentTemplates.stream().map((ft) -> mapper.map(ft, DocumentTemplateDto.class)).collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Create a document template with the current user",
            response = DocumentTemplateDto.class,
            httpMethod = "POST",
            notes = "Returns an instance of created document template."
    )
    @POST
    public Response createDocumentTemplate(DocumentTemplateDto ft) {
        if (ft == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        DocumentTemplate documentTemplate;
        documentTemplate = documentTemplateLogic.createDocumentTemplate(mapper.map(ft, DocumentTemplate.class));

        boolean succeed = (documentTemplate != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(documentTemplate, DocumentTemplateDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Contact creation failed.").build();
        }
    }

    @ApiOperation(
            value = "Modify a document template with the current user",
            response = DocumentTemplateDto.class,
            httpMethod = "PUT",
            notes = "Returns an instance of created document template."
    )
    @PUT
    @Path("/{documentTemplateId}")
    public Response updateDocumentTemplate(@PathParam("documentTemplateId") String documentTemplateId, DocumentTemplateDto ft) {
        if (ft == null || documentTemplateId == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }
        DocumentTemplate documentTemplate;

        DocumentTemplate template = mapper.map(ft, DocumentTemplate.class);

        template.setId(documentTemplateId);

        documentTemplate = documentTemplateLogic.modifyDocumentTemplate(template);

        boolean succeed = (documentTemplate != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(documentTemplate, DocumentTemplateDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Contact creation failed.").build();
        }
    }

    @ApiOperation(value = "Download a the document template attachment")
    @GET
    @Path("/{documentTemplateId}/attachment/{attachmentId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAttachment(@PathParam("documentTemplateId") String documentTemplateId, @PathParam("attachmentId") String attachmentId) {
        Response response;

        if (documentTemplateId == null) {
            return ResponseUtils.badRequest("Cannot retrieve attachment: provided document ID is null");
        }
        if (attachmentId == null) {
            return ResponseUtils.badRequest("Cannot retrieve attachment: provided attachment ID is null");
        }

        DocumentTemplate document = documentTemplateLogic.getDocumentTemplateById(documentTemplateId);
        if (document == null) {
            response = ResponseUtils.notFound("Document not found");
        } else {
            if (document.getAttachment() != null) {
                response = ResponseUtils.ok((StreamingOutput) output -> {
                    if (document.getVersion() == null) {
                        final Source xmlSource = new StreamSource(new ByteArrayInputStream(document.getAttachment()));
                        Source xsltSource = new StreamSource(FormUtils.class.getResourceAsStream("DocumentTemplateLegacyToNew.xml"));
                        final Result result = new javax.xml.transform.stream.StreamResult(output);
                        TransformerFactory transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
                        try {
                            final Transformer trans = transFact.newTransformer(xsltSource);
                            trans.transform(xmlSource, result);
                        } catch (TransformerException e) {
                            throw new IllegalStateException("Could not convert legacy document");
                        }
                    } else {
                        IOUtils.write(document.getAttachment(), output);
                    }
                });
            } else {
                response = ResponseUtils.notFound("AttachmentDto not found");
            }
        }

        return response;
    }

    @ApiOperation(value = "Download a the document template attachment")
    @GET
    @Path("/{documentTemplateId}/attachmentText/{attachmentId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAttachmentText(@PathParam("documentTemplateId") String documentTemplateId, @PathParam("attachmentId") String attachmentId) {
        Response response;

        if (documentTemplateId == null) {
            return ResponseUtils.badRequest("Cannot retrieve attachment: provided document ID is null");
        }
        if (attachmentId == null) {
            return ResponseUtils.badRequest("Cannot retrieve attachment: provided attachment ID is null");
        }

        DocumentTemplate document = documentTemplateLogic.getDocumentTemplateById(documentTemplateId);
        if (document == null) {
            response = ResponseUtils.notFound("Document not found");
        } else {
            if (document.getAttachment() != null) {
                response = ResponseUtils.ok(document.getAttachment(), MediaType.APPLICATION_OCTET_STREAM);
            } else {
                response = ResponseUtils.notFound("AttachmentDto not found");
            }
        }

        return response;
    }

    @ApiOperation(response = DocumentTemplateDto.class, value = "Creates a document's attachment")
    @PUT
    @Path("/{documentTemplateId}/attachment")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response setAttachment(@PathParam("documentTemplateId") String documentTemplateId, byte[] payload) {
        Response response;

        if (documentTemplateId == null) {
            return ResponseUtils.badRequest("Cannot add attachment: provided documentTemplate ID is null");
        }
        if (payload == null) {
            return ResponseUtils.badRequest("Cannot add null attachment");
        }

        DocumentTemplate documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId);
        if (documentTemplate != null) {
            documentTemplate.setAttachment(payload);
            response = ResponseUtils.ok(mapper.map(documentTemplateLogic.modifyDocumentTemplate(documentTemplate), DocumentTemplateDto.class));
        } else {
            response = ResponseUtils.internalServerError("Document modification failed");
        }

        return response;
    }

    @ApiOperation(response = DocumentTemplateDto.class, value = "Creates a document's attachment")
    @PUT
    @Path("/{documentTemplateId}/attachmentJson")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setAttachmentJson(@PathParam("documentTemplateId") String documentTemplateId, ByteArrayDto payload) {
        Response response;

        if (documentTemplateId == null) {
            return ResponseUtils.badRequest("Cannot add attachment: provided documentTemplate ID is null");
        }
        if (payload == null) {
            return ResponseUtils.badRequest("Cannot add null attachment");
        }

        DocumentTemplate documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId);
        if (documentTemplate != null) {
            documentTemplate.setAttachment(payload.getData());
            response = ResponseUtils.ok(mapper.map(documentTemplateLogic.modifyDocumentTemplate(documentTemplate), DocumentTemplateDto.class));
        } else {
            response = ResponseUtils.internalServerError("Document modification failed");
        }

        return response;
    }
}
