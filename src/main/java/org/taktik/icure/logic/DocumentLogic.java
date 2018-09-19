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

package org.taktik.icure.logic;

import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.services.external.rest.v1.dto.EMailDocumentDto;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface DocumentLogic extends EntityPersister<Document, String> {

	Document createDocument(Document document, String ownerHealthcarePartyId) throws CreationException;

	Document get(String documentId);

	List<Document> get(List<String> documentIds);

	String getAttachment(String documentId, String attachmentId);

	InputStream readAttachment(String documentId, String attachmentId);

	void modifyDocument(Document document);

	List<Document> findDocumentsByDocumentTypeHCPartySecretMessageKeys(String documentTypeCode,String hcPartyId, ArrayList<String> secretForeignKeys);
	List<Document> findDocumentsByHCPartySecretMessageKeys(String hcPartyId, ArrayList<String> secretForeignKeys);

	List<Document> findWithoutDelegation(int limit);

	List<Document> getDocuments(List<String> documentIds);

	List<Document> updateDocuments(List<Document> documents);
}
