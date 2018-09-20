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

package org.taktik.icure.logic.impl;

import org.ektorp.UpdateConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.DocumentDAO;
import org.taktik.icure.entities.Document;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.services.external.rest.v1.dto.EMailDocumentDto;
import org.taktik.icure.validation.aspect.Check;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;

@Service
public class DocumentLogicImpl extends GenericLogicImpl<Document, DocumentDAO> implements DocumentLogic {
	private static final Logger logger = LoggerFactory.getLogger(DocumentLogicImpl.class);

	private DocumentDAO documentDAO;

	@Override
	public Document createDocument(@Check @NotNull Document document, String ownerHealthcarePartyId) throws CreationException {
		if (document == null) {
			throw new CreationException("Cannot create 'null' document");
		}
		if (ownerHealthcarePartyId == null) {
			throw new CreationException("Document must have an owner; specified owner ID was 'null'. ");
		}

		// Fill audit details
		document.setAuthor(ownerHealthcarePartyId);
		document.setResponsible(ownerHealthcarePartyId);

		List<Document> createdDocuments = new ArrayList<>(1);
		try {
			createEntities(Collections.singleton(document), createdDocuments);
		} catch (Exception e) {
			throw new CreationException("Could not create document. ", e);
		}

		return createdDocuments.size() > 0 ? createdDocuments.get(0) : null;
	}


	@Override
	public Document get(String documentId) {
		return documentDAO.get(documentId);
	}

	@Override
	public List<Document> get(List<String> documentIds) {
		return documentDAO.getList(documentIds);
	}

	@Override
	public String getAttachment(String documentId, String attachmentId) {
		return documentDAO.getAttachment(documentId, attachmentId);
	}

	@Override
	public InputStream readAttachment(String documentId, String attachmentId) {
		return documentDAO.readAttachment(documentId, attachmentId);
	}

	@Override
	public void modifyDocument(@Check @NotNull Document document) {
		try {
			documentDAO.save(document);
		} catch (UpdateConflictException e) {
			logger.warn("Documents of class {} with id {} and rev {} could not be merged",document.getClass().getSimpleName(),document.getId(),document.getRev());
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<Document> findDocumentsByDocumentTypeHCPartySecretMessageKeys(String documentTypeCode, String hcPartyId, ArrayList<String> secretForeignKeys) {
		return documentDAO.findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, secretForeignKeys);
	}

	@Override
	public List<Document> findDocumentsByHCPartySecretMessageKeys(String hcPartyId, ArrayList<String> secretForeignKeys) {
		return documentDAO.findDocumentsByHCPartySecretMessageKeys(hcPartyId, secretForeignKeys);
	}

	@Override
	public List<Document> findWithoutDelegation(int limit) {
		return documentDAO.findDocumentsWithNoDelegations(limit);
	}

	@Override
	public List<Document> getDocuments(List<String> documentIds) {
		return documentDAO.getList(documentIds);
	}

	@Override
	public List<Document> updateDocuments(List<Document> documents) {
		return documentDAO.save(documents);
	}

	@Override
	protected DocumentDAO getGenericDAO() {
		return documentDAO;
	}

	@Autowired
	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

}
