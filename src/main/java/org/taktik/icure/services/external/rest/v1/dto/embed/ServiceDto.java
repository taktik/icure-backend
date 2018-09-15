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

package org.taktik.icure.services.external.rest.v1.dto.embed;


import org.taktik.icure.services.external.rest.v1.dto.CodeDto;


import java.io.Serializable;
import java.util.*;

public class ServiceDto implements Serializable {
	protected String id; //Two version of the same service in two separate contacts have the same id
	private String contactId; //Only used when the Service is emitted outside of its contact
	private Set<String> secretForeignKeys; //Only used when the Service is emitted outside of its contact
	private Set<String> subContactIds; //Only used when the Service is emitted outside of its contact
	private Set<String> plansOfActionIds; //Only used when the Service is emitted outside of its contact
	private Set<String> healthElementsIds; //Only used when the Service is emitted outside of its contact
	private Map<String,List<DelegationDto>> cryptedForeignKeys; //Only used when the Service is emitted outside of its contact
	private Map<String, List<DelegationDto>> delegations; //Only used when the Service is emitted outside of its contact
	private Map<String, List<DelegationDto>> encryptionKeys; //Only used when the Service is emitted outside of its contact


	protected String label;
	protected String dataClassName;
	protected Long index; //Used for sorting

	protected Map<String, ContentDto> content; //Series of values: each value is localized, in the case when the service contains a document, the document id is the SerializableValue
	protected String encryptedContent; //Crypted (AES+base64) version of the above
	protected Map<String, String> textIndexes; //Same structure as content but used for full text indexation

	protected Long valueDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000

	protected Long openingDate;
	protected Long closingDate;

	protected Long created;
	protected Long modified;
	protected Long endOfLife;

	protected String formId; //Initial formId used for debugging purposes

	protected String author; //userId
	protected String responsible; //healthcarePartyId

	protected String comment;
	protected Integer status; //bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present

	protected Set<String> invoicingCodes = new HashSet<>();
	protected Set<CodeDto> codes = new HashSet<>(); //stub object of the Code
	protected Set<CodeDto> tags = new HashSet<>(); //stub object of yhr tag

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public Set<String> getSecretForeignKeys() {
		return secretForeignKeys;
	}

	public void setSecretForeignKeys(Set<String> secretForeignKeys) {
		this.secretForeignKeys = secretForeignKeys;
	}

	public Map<String, List<DelegationDto>> getEncryptionKeys() {
		return encryptionKeys;
	}

	public void setEncryptionKeys(Map<String, List<DelegationDto>> encryptionKeys) {
		this.encryptionKeys = encryptionKeys;
	}

	public Map<String, List<DelegationDto>> getCryptedForeignKeys() {
		return cryptedForeignKeys;
	}

	public void setCryptedForeignKeys(Map<String, List<DelegationDto>> cryptedForeignKeys) {
		this.cryptedForeignKeys = cryptedForeignKeys;
	}

	public Set<String> getSubContactIds() {
		return subContactIds;
	}

	public void setSubContactIds(Set<String> subContactIds) {
		this.subContactIds = subContactIds;
	}

	public Set<String> getPlansOfActionIds() {
		return plansOfActionIds;
	}

	public void setPlansOfActionIds(Set<String> plansOfActionIds) {
		this.plansOfActionIds = plansOfActionIds;
	}

	public Set<String> getHealthElementsIds() {
		return healthElementsIds;
	}

	public void setHealthElementsIds(Set<String> healthElementsIds) {
		this.healthElementsIds = healthElementsIds;
	}

	public Map<String, List<DelegationDto>> getDelegations() {
		return delegations;
	}

	public void setDelegations(Map<String, List<DelegationDto>> delegations) {
		this.delegations = delegations;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDataClassName() {
		return dataClassName;
	}

	public void setDataClassName(String dataClassName) {
		this.dataClassName = dataClassName;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

    public Map<String, ContentDto> getContent() {
        return content;
    }

    public void setContent(Map<String, ContentDto> content) {
        this.content = content;
    }

	public String getEncryptedContent() {
		return encryptedContent;
	}

	public void setEncryptedContent(String encryptedContent) {
		this.encryptedContent = encryptedContent;
	}

	public Map<String, String> getTextIndexes() {
        return textIndexes;
    }

    public void setTextIndexes(Map<String, String> textIndexes) {
        this.textIndexes = textIndexes;
    }

    public Long getValueDate() {
		return valueDate;
	}

	public void setValueDate(Long valueDate) {
		this.valueDate = valueDate;
	}

	public Long getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(Long openingDate) {
		this.openingDate = openingDate;
	}

	public Long getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Long closingDate) {
		this.closingDate = closingDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Set<String> getInvoicingCodes() {
		return invoicingCodes;
	}

	public void setInvoicingCodes(Set<String> invoicingCodes) {
		this.invoicingCodes = invoicingCodes;
	}

	public Set<CodeDto> getCodes() {
		return codes;
	}

	public void setCodes(Set<CodeDto> codes) {
		this.codes = codes;
	}

	public Set<CodeDto> getTags() {
		return tags;
	}

	public void setTags(Set<CodeDto> tags) {
		this.tags = tags;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getModified() {
		return modified;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

	public Long getEndOfLife() {
		return endOfLife;
	}

	public void setEndOfLife(Long endOfLife) {
		this.endOfLife = endOfLife;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}
}
