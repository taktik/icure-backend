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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.DocumentType;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.security.CryptoUtils;
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.EMailDocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureDto;
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.be.GenericResult;
import org.taktik.icure.utils.FormUtils;
import org.taktik.icure.utils.ResponseUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.LoginException;
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
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Path("/document")
@Api(tags = { "document" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class DocumentFacade implements OpenApiFacade{
	private static Logger logger = LoggerFactory.getLogger(DocumentFacade.class);

	private DocumentLogic documentLogic;
	private MapperFacade mapper;
	private SessionLogic sessionLogic;

	@ApiOperation(response = DocumentDto.class, value = "Creates a document")
	@POST
	public Response createDocument(DocumentDto documentDto) throws LoginException, CreationException {
		Response response;

		if (documentDto == null) {
			return ResponseUtils.badRequest("Cannot create 'null' document. ");
		}

		Document document = mapper.map(documentDto, Document.class);
		Document createdDocument = documentLogic.createDocument(document, sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId());
		if (createdDocument != null) {
			response = ResponseUtils.ok(mapper.map(createdDocument, DocumentDto.class));
		} else {
			response = ResponseUtils.internalServerError("Document creation failed");
		}

		return response;
	}

	@ApiOperation(response = DocumentDto.class, value = "Deletes a document")
	@DELETE
	@Path("/{documentIds}")
	public Response deleteDocument(@PathParam("documentIds") String documentIds) throws DeletionException {
		Response response;

		if (documentIds == null) {
			return ResponseUtils.badRequest("Cannot delete document: provided document ID is null");
		}

        List<String> documentIdsList = Arrays.asList(documentIds.split(","));
		try {
			documentLogic.deleteEntities(documentIdsList);
			response = ResponseUtils.ok();
		} catch (Exception e) {
			response = ResponseUtils.internalServerError("Document deletion failed");
		}
		return response;
	}

	@ApiOperation(value = "Creates a document")
	@GET
	@Path("/{documentId}/attachment/{attachmentId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAttachment(@PathParam("documentId") String documentId, @PathParam("attachmentId") String attachmentId, @QueryParam("enckeys") String enckeys) {
		Response response;

		if (documentId == null) {
			return ResponseUtils.badRequest("Cannot retrieve attachment: provided document ID is null");
		}
		if (attachmentId == null) {
			return ResponseUtils.badRequest("Cannot retrieve attachment: provided attachment ID is null");
		}

		Document document = documentLogic.get(documentId);
		if (document == null) {
			response = ResponseUtils.notFound("Document not found");
		} else {
			byte[] attachment = document.getAttachment();
			if (attachment != null) {
				if (enckeys != null && enckeys.length()>0) {
					for (String sfk : enckeys.split(",")) {
						ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
						UUID uuid = UUID.fromString(sfk);
						bb.putLong(uuid.getMostSignificantBits());
						bb.putLong(uuid.getLeastSignificantBits());
						try {
							attachment = CryptoUtils.decryptAES(attachment, bb.array());
							break;
						} catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalArgumentException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException ignored) {
						}
					}
				}
				byte[] finalAttachment = attachment;
				response = ResponseUtils.ok((StreamingOutput) output -> {
					if (StringUtils.equals(document.getMainUti(),"org.taktik.icure.report")) {
						String styleSheet = "DocumentTemplateLegacyToNew.xml";

						final Source xmlSource = new StreamSource(new ByteArrayInputStream(finalAttachment));
						Source xsltSource = new StreamSource(FormUtils.class.getResourceAsStream(styleSheet));
						final Result result = new javax.xml.transform.stream.StreamResult(output);
						TransformerFactory transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
						try {
							final Transformer trans = transFact.newTransformer(xsltSource);
							trans.transform(xmlSource, result);
						} catch (TransformerException e) {
							throw new IllegalStateException("Could not convert legacy document");
						}
					} else {
						IOUtils.write(finalAttachment, output);
					}
				});
			} else {
				response = ResponseUtils.notFound("AttachmentDto not found");
			}
		}

		return response;
	}

	@ApiOperation(response = DocumentDto.class, value = "Deletes a document's attachment")
	@DELETE
	@Path("/{documentId}/attachment")
	public Response deleteAttachment(@PathParam("documentId") String documentId) {
		Response response;

		if (documentId == null) {
			return ResponseUtils.badRequest("Cannot delete attachment: provided document ID is null");
		}

		Document document = documentLogic.get(documentId);
		if (document == null) {
			response = ResponseUtils.notFound("Document not found");
		} else {
			document.setAttachment(null);
			documentLogic.modifyDocument(document);
			DocumentDto documentDto = mapper.map(document, DocumentDto.class);
			response = ResponseUtils.ok(documentDto);
		}

		return response;
	}

	@ApiOperation(response = DocumentDto.class, value = "Creates a document's attachment")
	@PUT
	@Path("/{documentId}/attachment")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAttachment(@PathParam("documentId") String documentId, @QueryParam("enckeys") String enckeys, byte[] payload) {
		Response response;

		if (documentId == null) {
			return ResponseUtils.badRequest("Cannot add attachment: provided document ID is null");
		}
		if (payload == null) {
			return ResponseUtils.badRequest("Cannot add null attachment");
		}

		if (enckeys != null && enckeys.length()>0) {
			for (String sfk : enckeys.split(",")) {
				ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
				UUID uuid = UUID.fromString(sfk);
				bb.putLong(uuid.getMostSignificantBits());
				bb.putLong(uuid.getLeastSignificantBits());
				try {
					payload = CryptoUtils.encryptAES(payload, bb.array());
					break; //should always work (no real check on key validity for encryption)
				} catch (Exception ignored) {
				}
			}
		}

		Document document = documentLogic.get(documentId);
		if (document != null) {
			document.setAttachment(payload);
			documentLogic.modifyDocument(document);
			response = ResponseUtils.ok(mapper.map(document, DocumentDto.class));
		} else {
			response = ResponseUtils.internalServerError("Document modification failed");
		}

		return response;
	}

	@ApiOperation(response = DocumentDto.class, value = "Creates a document's attachment")
	@PUT
	@Path("/{documentId}/attachment/multipart")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response setAttachmentMulti(@PathParam("documentId") String documentId, @QueryParam("enckeys") String enckeys, @FormDataParam("attachment") byte[] payload) {
		return setAttachment(documentId, enckeys, payload);
	}

	@ApiOperation(response = DocumentDto.class, value = "Gets a document")
	@GET
	@Path("/{documentId}")
	public Response getDocument(@PathParam("documentId") String documentId) {
		Response response;

		if (documentId == null) {
			return ResponseUtils.badRequest("Cannot retrieve document: provided document ID is null");
		}

		Document document = documentLogic.get(documentId);
		if (document != null) {
			response = ResponseUtils.ok(mapper.map(document, DocumentDto.class));
		} else {
			response = ResponseUtils.notFound("Document not found");
		}

		return response;
	}

	@ApiOperation(response = DocumentDto.class, responseContainer = "Array", value = "Gets a document")
	@POST
	@Path("/batch")
	public Response getDocuments(@RequestBody ListOfIdsDto documentIds) {
		Response response;

		if (documentIds == null) {
			return ResponseUtils.badRequest("Cannot retrieve document: provided document ID is null");
		}

		List<Document> documents = documentLogic.get(documentIds.getIds());
		if (documents != null) {
			response = ResponseUtils.ok(documents.stream().map(doc -> mapper.map(doc, DocumentDto.class)).collect(Collectors.toList()));
		} else {
			response = ResponseUtils.notFound("Documents not found");
		}

		return response;
	}

	@ApiOperation(response = DocumentDto.class, value = "Updates a document")
	@PUT
	public Response modifyDocument(DocumentDto documentDto) {
		Response response;

		if (documentDto == null || documentDto.getId() == null) {
			return ResponseUtils.badRequest("Cannot modify non-existing document");
		}

		Document document = mapper.map(documentDto, Document.class);

		if (documentDto.getAttachmentId()!=null) {
			Document prevDoc = documentLogic.get(document.getId());
			document.setAttachments(prevDoc.getAttachments());

			if (documentDto.getAttachmentId().equals(prevDoc.getAttachmentId())) {
				document.setAttachment(prevDoc.getAttachment());
			}
		}

		documentLogic.modifyDocument(document);
		if (document != null) {
			response = ResponseUtils.ok(mapper.map(document, DocumentDto.class));
		} else {
			response = ResponseUtils.internalServerError("Document modification failed");
		}

		return response;
	}

	@ApiOperation(
			value = "Updates a batch of documents",
			response = DocumentDto.class,
			responseContainer = "Array",
			httpMethod = "PUT",
			notes = "Returns the modified documents."
	)
	@PUT
	@Path("/batch")
	public Response modifyDocuments(List<DocumentDto> documentDtos) {
		Response response;

		if (documentDtos == null) {
			return ResponseUtils.badRequest("Cannot modify non-existing document");
		}

		try {

			List<Document> indocs = documentDtos.stream().map(f -> mapper.map(f, Document.class)).collect(Collectors.toList());
			for(int i = 0; i < documentDtos.size(); i++) {
				if (documentDtos.get(i).getAttachmentId()!=null) {
					Document prevDoc = documentLogic.get(indocs.get(i).getId());
					indocs.get(i).setAttachments(prevDoc.getAttachments());

					if (documentDtos.get(i).getAttachmentId().equals(indocs.get(i).getAttachmentId())) {
						indocs.get(i).setAttachment(prevDoc.getAttachment());
					}
				}
			}

			List<Document> docs = documentLogic.updateEntities(indocs);
			return Response.ok().entity(docs.stream().map(f -> mapper.map(f, DocumentDto.class)).collect(Collectors.toList())).build();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}
	}

	@ApiOperation(
			value = "List documents found By Healthcare Party and secret foreign keys.",
			response = DocumentDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys")
	public Response findByHCPartyMessageSecretFKeys(@QueryParam("hcPartyId") String hcPartyId,
													@QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretMessageKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		List<Document> documentList = documentLogic.findDocumentsByHCPartySecretMessageKeys(hcPartyId, new ArrayList<>(secretMessageKeys));

		boolean succeed = (documentList != null);
		if (succeed) {
			// mapping to Dto
			List<DocumentDto> documentDtoList = documentList.stream().map(document -> mapper.map(document, DocumentDto.class)).collect(Collectors.toList());
			return Response.ok().entity(documentDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Documents failed. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "List documents found By type, By Healthcare Party and secret foreign keys.",
			response = DocumentDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byTypeHcPartySecretForeignKeys")
	public Response findByTypeHCPartyMessageSecretFKeys(@QueryParam("documentTypeCode") String documentTypeCode,
													@QueryParam("hcPartyId") String hcPartyId,
													@QueryParam("secretFKeys") String secretFKeys) {
		DocumentType tmp = DocumentType.fromName(documentTypeCode);
		if (tmp == null || hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretMessageKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		List<Document> documentList = documentLogic.findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, new ArrayList<>(secretMessageKeys));

		boolean succeed = (documentList != null);
		if (succeed) {
			// mapping to Dto
			List<DocumentDto> documentDtoList = documentList.stream().map(document -> mapper.map(document, DocumentDto.class)).collect(Collectors.toList());
			return Response.ok().entity(documentDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Documents failed. Please try again or read the server log.").build();
		}
	}



	@ApiOperation(
		value = "List documents with no delegation",
		response = DocumentDto.class,
		responseContainer = "Array",
		httpMethod = "GET",
		notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/woDelegation")
	public Response findWithoutDelegation(@QueryParam("limit") Integer limit) {
		List<Document> documentList = documentLogic.findWithoutDelegation(limit == null ? 100 : limit);
		boolean succeed = (documentList != null);
		if (succeed) {
			// mapping to Dto
			List<DocumentDto> documentDtoList = documentList.stream().map(document -> mapper.map(document, DocumentDto.class)).collect(Collectors.toList());
			return Response.ok().entity(documentDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Documents failed. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Update delegations in healthElements.",
			httpMethod = "POST",
			notes = "Keys must be delimited by coma"
	)
	@POST
	@Path("/delegations")
	public Response setDocumentsDelegations(List<IcureStubDto> stubs) throws Exception {
		List<Document> invoices = documentLogic.getDocuments(stubs.stream().map(IcureDto::getId).collect(Collectors.toList()));
		invoices.forEach(healthElement -> stubs.stream().filter(s -> s.getId().equals(healthElement.getId())).findFirst().ifPresent(stub -> {
			stub.getDelegations().forEach((s, delegationDtos) -> healthElement.getDelegations().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getEncryptionKeys().forEach((s, delegationDtos) -> healthElement.getEncryptionKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getCryptedForeignKeys().forEach((s, delegationDtos) -> healthElement.getCryptedForeignKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
		}));
		documentLogic.updateDocuments(invoices);

		return Response.ok().build();
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setDocumentLogic(DocumentLogic documentLogic) {
		this.documentLogic = documentLogic;
	}

	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
