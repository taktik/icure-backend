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

package org.taktik.icure.services.external.rest.v1.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MessageDto extends IcureDto {
	protected String fromAddress;
	protected String fromHealthcarePartyId;
	protected String formId;
	protected Integer status;
	protected String recipientsType;
	protected Set<String> recipients = new HashSet<>();
	protected Set<String> toAddresses = new HashSet<>();

	protected Long received;
	protected Long sent;

	protected Map<String,String> metas;
	protected String transportGuid;
	protected String remark;
	protected String conversationGuid;

	protected String subject;

	protected List<String> invoiceIds;
	protected String parentId; //ID of parent in a message conversation
	private String externalRef;
	private Map<String,String> senderReferences;

	private Set<String> unassignedResults; //refs
	private Map<String, String> assignedResults; //ContactId -> ref

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Set<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
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

	public String getRecipientsType() {
		return recipientsType;
	}

	public void setRecipientsType(String recipientsType) {
		this.recipientsType = recipientsType;
	}

	public String getExternalRef() {
		return externalRef;
	}

	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

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
