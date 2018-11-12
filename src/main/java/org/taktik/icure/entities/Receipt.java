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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.ReceiptBlobType;
import org.taktik.icure.entities.utils.MergeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Receipt extends StoredICureDocument implements Serializable {
	private Map<ReceiptBlobType, String> attachmentIds = new HashMap<>();
    private List<String> references = new ArrayList<>(); //nipReference:027263GFF152, errorCode:186, errorPath:/request/transaction, org.taktik.icure.entities;tarification:id, org.taktik.entities.Invoice:UUID

    //The ICureDocument (Invoice, Contact, ...) this document is linked to
    private String documentId;
	private String category;
	private String subCategory;

	public Receipt solveConflictWith(Receipt other) {
		super.solveConflictsWith(other);

		if (this.attachmentIds != null && other.attachmentIds != null) {
			other.attachmentIds.putAll(this.attachmentIds);
		}
		if (other.attachmentIds != null) {
			this.attachmentIds = other.attachmentIds;
		}
		MergeUtil.mergeListsDistinct(this.references,other.references, String::equals, (a, b)->a);

		if (this.documentId == null && other.documentId != null) { this.documentId = other.documentId; }

		return this;
	}

	public Map<ReceiptBlobType, String> getAttachmentIds() {
		return attachmentIds;
	}

	public void setAttachmentIds(Map<ReceiptBlobType, String> attachmentIds) {
		this.attachmentIds = attachmentIds;
	}

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> references) {
		this.references = references;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	private String encryptedSelf;
	@Override
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	@Override
	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
}
