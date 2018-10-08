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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.entities.base.ICureDocument;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;
import org.taktik.icure.validation.ValidCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Services are created in the course a contact. Information like temperature, blood pressure and so on.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service implements ICureDocument, Serializable, Comparable<Service> {
	@NotNull(autoFix = AutoFix.UUID)
	protected String id; //Two version of the same service in two separate contacts have the same id
	@JsonIgnore
	private String contactId; //Only used when the Service is emitted outside of its contact
	@JsonIgnore
	private Set<String> subContactIds; //Only used when the Service is emitted outside of its contact
	@JsonIgnore
	private Set<String> plansOfActionIds; //Only used when the Service is emitted outside of its contact
	@JsonIgnore
	private Set<String> healthElementsIds; //Only used when the Service is emitted outside of its contact
	@JsonIgnore
	private Set<String> secretForeignKeys = new HashSet<>(); //Only used when the Service is emitted outside of its contact
	@JsonIgnore
	private Map<String,Set<Delegation>> cryptedForeignKeys = new HashMap<>(); //Only used when the Service is emitted outside of its contact
	@JsonIgnore
	private Map<String, Set<Delegation>> delegations = new HashMap<>(); //Only used when the Service is emitted outside of its contact
	@JsonIgnore
	private Map<String, Set<Delegation>> encryptionKeys = new HashMap<>(); //Only used when the Service is emitted outside of its contact

	@NotNull
	protected String label;
	protected String dataClassName;
	protected Long index; //Used for sorting

	protected Map<String, Content> content = new HashMap<>(); //Localized, in the case when the service contains a document, the document id is the SerializableValue

	protected String encryptedContent; //Crypted (AES+base64) version of the above, deprecated, use encryptedSelf instead
	protected Map<String, String> textIndexes = new HashMap<>(); //Same structure as content but used for full text indexation

	@NotNull(autoFix = AutoFix.FUZZYNOW)
	protected Long valueDate;   // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.

	@NotNull(autoFix = AutoFix.FUZZYNOW)
	protected Long openingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
	protected Long closingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.

	protected String formId; //Used to group logically related services

	@NotNull(autoFix = AutoFix.NOW)
	protected Long created;
	@NotNull(autoFix = AutoFix.NOW)
	protected Long modified;
	protected Long endOfLife;

	@NotNull(autoFix = AutoFix.CURRENTUSERID)
	protected String author; //userId
	@NotNull(autoFix = AutoFix.CURRENTHCPID)
	protected String responsible; //healthcarePartyId

	protected String comment;

	protected Integer status; //bit 0: active/inactive, bit 1: relevant/irrelevant, bit2 : present/absent, ex: 0 = active,relevant and present

	protected Set<String> invoicingCodes = new HashSet<>();

	//For the content of the Service
	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected Set<CodeStub> codes = new HashSet<>(); //stub object of the Code

	//For the type of the Service
	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected Set<CodeStub> tags = new HashSet<>(); //stub object of the tag

	public Service solveConflictWith(Service other) {
		this.created = other.created==null?this.created:this.created==null?other.created:Long.valueOf(Math.min(this.created,other.created));
		this.modified = other.modified==null?this.modified:this.modified==null?other.modified:Long.valueOf(Math.max(this.modified,other.modified));

		this.openingDate = other.openingDate==null?this.openingDate:this.openingDate==null?other.openingDate:Long.valueOf(Math.min(this.openingDate,other.openingDate));
		this.closingDate = other.closingDate==null?this.closingDate:this.closingDate==null?other.closingDate:Long.valueOf(Math.max(this.closingDate,other.closingDate));
		this.valueDate = other.valueDate==null?this.valueDate:this.valueDate==null?other.valueDate:Long.valueOf(Math.max(this.valueDate,other.valueDate));

		this.codes.addAll(other.codes);
		this.tags.addAll(other.tags);
		this.invoicingCodes.addAll(other.invoicingCodes);

		this.formId = this.formId == null ? other.formId : this.formId;

		return this;
	}

	public @Nullable String getDataClassName() {
		return dataClassName;
	}

	public void setDataClassName(String dataClassName) {
		this.dataClassName = dataClassName;
	}

	public @Nullable Long getValueDate() {
		return valueDate;
	}

	public @Nullable String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public @Nullable Integer getStatus() {
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

	@Override
	public @Nullable String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public @Nullable String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public @Nullable Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public java.util.Set<CodeStub> getCodes() {
		return codes;
	}

	public void setCodes(java.util.Set<CodeStub> codes) {
		this.codes = codes;
	}

	public java.util.Set<CodeStub> getTags() {
		return tags;
	}

	public void setTags(java.util.Set<CodeStub> tags) {
		this.tags = tags;
	}

	public Map<String, Set<Delegation>> getCryptedForeignKeys() {
		return cryptedForeignKeys;
	}

	public void setCryptedForeignKeys(Map<String, Set<Delegation>> cryptedForeignKeys) {
		this.cryptedForeignKeys = cryptedForeignKeys;
	}

	public Set<String> getSecretForeignKeys() {
		return secretForeignKeys;
	}

	public void setSecretForeignKeys(Set<String> secretForeignKeys) {
		this.secretForeignKeys = secretForeignKeys;
	}

	public void setDelegations(Map<String, Set<Delegation>> delegations) {
		this.delegations = delegations;
	}

	public Map<String, Set<Delegation>> getDelegations() {
		return delegations;
	}

	public Map<String, Set<Delegation>> getEncryptionKeys() {
		return encryptionKeys;
	}

	public void setEncryptionKeys(Map<String, Set<Delegation>> encryptionKeys) {
		this.encryptionKeys = encryptionKeys;
	}

	public @Nullable String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

    public Map<String, Content> getContent() {
        return content;
    }

    public void setContent(Map<String, Content> content) {
        this.content = content;
    }

	public @Nullable String getEncryptedContent() {
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

	public void setValueDate(Long valueDate) {
		this.valueDate = valueDate;
	}

	public @Nullable Long getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(Long openingDate) {
		this.openingDate = openingDate;
	}

	public @Nullable Long getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Long closingDate) {
		this.closingDate = closingDate;
	}

	public @Nullable String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	@Override
	public @Nullable Long getCreated() {
		return created;
	}

	@Override
	public void setCreated(Long created) {
		this.created = created;
	}

	@Override
	public @Nullable Long getModified() {
		return modified;
	}

	@Override
	public void setModified(Long modified) {
		this.modified = modified;
	}

	@Override
	public @Nullable Long getEndOfLife() {
		return endOfLife;
	}

	@Override
	public void setEndOfLife(Long endOfLife) {
		this.endOfLife = endOfLife;
	}

	@Override
	public @Nullable String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public @Nullable String getResponsible() {
		return responsible;
	}

	@Override
	public void setResponsible(String responsible) {
		this.responsible = responsible;
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

	@Override
	public String toString() {
		return "Service{" +
				"id='" + id + '\'' +
				", contactId='" + contactId + '\'' +
				", label='" + label + '\'' +
				", dataClassName='" + dataClassName + '\'' +
				", index=" + index +
				", content=" + content +
				", textIndexes=" + textIndexes +
				", valueDate=" + valueDate +
				", openingDate=" + openingDate +
				", closingDate=" + closingDate +
				", comment='" + comment + '\'' +
				", status=" + status +
				", invoicingCodes=" + invoicingCodes +
				", codes=" + codes +
				", tags=" + tags +
				'}';
	}

	@Override
	public int compareTo(@NotNull Service other) {
		if (this.equals(other)) { return 0; }

		int idx = (this.index != null && other.index != null) ? this.index.compareTo(other.index) : 0;
		if (idx!=0) return idx;

		idx = this.id.compareTo(other.id);
		if (idx!=0) return idx;

		return 1;
	}

}
