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

package org.taktik.icure.dao;

import org.ektorp.Attachment;
import org.ektorp.support.View;
import org.taktik.icure.entities.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface DocumentDAO extends GenericDAO<Document> {

	List<Document> listConflicts();

	List<Document> findDocumentsByHCPartySecretMessageKeys(String hcPartyId, ArrayList<String> secretForeignKeys);

	List<Document> findDocumentsWithNoDelegations(int limit);

	List<Document> findDocumentsByDocumentTypeHCPartySecretMessageKeys(String documentTypeCode, String hcPartyId, ArrayList<String> secretForeignKeys);

	InputStream readAttachment(String documentId, String attachmentId);

}
