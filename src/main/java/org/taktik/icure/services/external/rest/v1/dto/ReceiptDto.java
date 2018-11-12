package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.entities.embed.ReceiptBlobType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiptDto extends IcureDto {
	private Map<ReceiptBlobType, String> attachmentIds;
	private List<String> references = new ArrayList<>(); //nipReference:027263GFF152, errorCode:186, errorPath:/request/transaction, org.taktik.icure.entities;tarification:id, org.taktik.entities.Invoice:UUID

	//The ICureDocument (Invoice, Contact, ...) this document is linked to
	private String documentId;
	private String category;
	private String subCategory;

	private String encryptedSelf;

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

	public String getEncryptedSelf() {
		return encryptedSelf;
	}

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
