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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Objects;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.utils.MergeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message extends StoredICureDocument implements Serializable {

	public final static int STATUS_LABO_RESULT 				= 1 << 0;
	public final static int STATUS_UNREAD 					= 1 << 1;
	public final static int STATUS_IMPORTANT 				= 1 << 2;
	public final static int STATUS_ENCRYPTED 				= 1 << 3;
	public final static int STATUS_HAS_ANNEX 				= 1 << 4;
	public final static int STATUS_HAS_FREE_INFORMATION 	= 1 << 5;
	public final static int STATUS_EFACT 					= 1 << 6;

	public final static int STATUS_SENT  	     			= 1 << 7;
	public final static int STATUS_SUBMITTED 				= 1 << 8; //tack
	public final static int STATUS_RECEIVED 				= 1 << 9; //tack
	public final static int STATUS_ACCEPTED_FOR_TREATMENT 	= 1 << 10; //931000
	public final static int STATUS_ACCEPTED 				= 1 << 11; //920098
	public final static int STATUS_REJECTED 				= 1 << 12; //920999

	public final static int STATUS_TACK 					= 1 << 13;
	public final static int STATUS_MASKED 					= 1 << 14;

	public final static int STATUS_SUCCESS 					= 1 << 15; //920900 920098
	public final static int STATUS_WARNING 					= 1 << 16; //920900
	public final static int STATUS_ERROR 					= 1 << 17; //920099

	public final static int STATUS_ANALYZED 				= 1 << 18;
	public final static int STATUS_DELETED_ON_SERVER 		= 1 << 19;
	public final static int STATUS_SHOULD_BE_DELETED_ON_SERVER 	= 1 << 20;

	public final static int STATUS_ARCHIVED					= 1 << 21;

	private String fromAddress;
	private String fromHealthcarePartyId;
	private String formId;
	private Integer status;
	private String recipientsType;

	private Set<String> recipients = new HashSet<>(); //The id of the hcp whose the message is addressed to
	private Set<String> toAddresses = new HashSet<>(); //The address of the recipient of the message. Format is of an email address with extra domains defined for mycarenet and ehealth: (efact.mycarenet.be/eattest.mycarenet.be/chapter4.mycarenet.be/ehbox.ehealth.fgov.be)

	private Long received;
	private Long sent;

	private Map<String, String> metas = new HashMap<>();

	/*
		CHAP4:IN:   ${Mycarenet message ref}
		CHAP4:OUT:  ${Mycarenet message ref}
		EFACT:BATCH:${iCure batch ref}
		EFACT:IN:   ${Mycarenet message ref}
		EFACT:OUT:  ${Mycarenet message ref}
		GMD:IN:     ${Mycarenet message ref}
		INBOX:      ${Ehealth box message ref}
		SENTBOX:    ${Ehealth box message ref}
		BININBOX:   ${Ehealth box message ref}
		BINSENTBOX: ${Ehealth box message ref}
		REPORT:IN:  ${iCure ref}
		REPORT:OUT: ${iCure ref}
	 */

	private String transportGuid; //Each message should have a transportGuid: see above for formats
	private String remark;
	private String conversationGuid;

	private String subject;

	private List<String> invoiceIds = new ArrayList<>();
	private String parentId; //ID of parent in a message conversation
	private String externalRef;

	private Set<String> unassignedResults; //refs
	private Map<String, String> assignedResults; //ContactId -> ref

	private Map<String,String> senderReferences;

	public Message solveConflictWith(Message other) {
		super.solveConflictsWith(other);

		this.fromAddress = this.fromAddress == null ? other.fromAddress : this.fromAddress;
		this.fromHealthcarePartyId = this.fromHealthcarePartyId == null ? other.fromHealthcarePartyId : this.fromHealthcarePartyId ;
		this.formId = this.formId == null ? other.formId : this.formId;
		this.recipients.addAll(other.recipients);
		this.toAddresses.addAll(other.toAddresses);

		this.received = other.received==null?this.received:this.received==null?other.received:Long.valueOf(Math.min(this.received,other.received));
		this.sent = other.sent==null?this.sent:this.sent==null?other.sent:Long.valueOf(Math.min(this.sent,other.sent));

		this.remark = this.remark == null ? other.remark : this.remark;
		this.transportGuid = this.transportGuid == null ? other.transportGuid : this.transportGuid ;
		this.conversationGuid = this.conversationGuid == null ? other.conversationGuid : this.conversationGuid;

		this.subject = this.subject == null ? other.subject : this.subject;
		this.parentId = this.parentId == null ? other.parentId : this.parentId ;
		this.externalRef = this.externalRef == null ? other.externalRef : this.externalRef;

		this.invoiceIds = MergeUtil.mergeListsDistinct(this.invoiceIds,other.invoiceIds, Objects::equal, (a, b)->a);
		other.metas.forEach((k,v)->this.metas.putIfAbsent(k,v));

		return this;
	}

	public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Set<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(Set<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String getFromHealthcarePartyId() {
        return fromHealthcarePartyId;
    }

    public void setFromHealthcarePartyId(String fromHealthcarePartyId) {
        this.fromHealthcarePartyId = fromHealthcarePartyId;
    }

    public Long getReceived() {
        return received;
    }

    public void setReceived(Long received) {
        this.received = received;
    }

    public Long getSent() {
        return sent;
    }

    public void setSent(Long sent) {
        this.sent = sent;
    }

    public Map<String, String> getMetas() {
        return metas;
    }

    public void setMetas(Map<String, String> metas) {
        this.metas = metas;
    }

    public String getTransportGuid() {
        return transportGuid;
    }

    public void setTransportGuid(String transportGuid) {
        this.transportGuid = transportGuid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getConversationGuid() {
        return conversationGuid;
    }

    public void setConversationGuid(String conversationGuid) {
        this.conversationGuid = conversationGuid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonIgnore
    public Set<String> getSecretContactKeys() {
        return super.getSecretForeignKeys();
    }

    @JsonIgnore
    public void setSecretContactKeys(Set<String> secretContactKeys) {
        super.setSecretForeignKeys(secretContactKeys);
    }

    @JsonIgnore
    public Map<String, Set<Delegation>> getCryptedContactIds() {
        return super.getCryptedForeignKeys();
    }

    @JsonIgnore
    public void setCryptedContactIds(Map<String, Set<Delegation>> cryptedContactIds) {
        super.setCryptedForeignKeys(cryptedContactIds);
    }

	public Set<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}

	public List<String> getInvoiceIds() {
		return invoiceIds;
	}

	public void setInvoiceIds(List<String> invoiceIds) {
		this.invoiceIds = invoiceIds;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setRecipientsType(String recipientsType) {
		this.recipientsType = recipientsType;
	}

	public String getRecipientsType() {
		return recipientsType;
	}

	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	public String getExternalRef() {
		return externalRef;
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

	public Map<String, String> getSenderReferences() {
		return senderReferences;
	}

	public void setSenderReferences(Map<String, String> senderReferences) {
		this.senderReferences = senderReferences;
	}

	public Set<String> getUnassignedResults() {
		return unassignedResults;
	}

	public void setUnassignedResults(Set<String> unassignedResults) {
		this.unassignedResults = unassignedResults;
	}

	public Map<String, String> getAssignedResults() {
		return assignedResults;
	}

	public void setAssignedResults(Map<String, String> assignedResults) {
		this.assignedResults = assignedResults;
	}
}
