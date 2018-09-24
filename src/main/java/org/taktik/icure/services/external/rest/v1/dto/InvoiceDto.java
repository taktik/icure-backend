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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.taktik.icure.services.external.rest.v1.dto.embed.InvoicingCodeDto;

public class InvoiceDto extends IcureDto {
	private Long invoiceDate; //yyyyMMdd
	private Long sentDate;
	private Long printedDate;

	private Double paid;

	private List<InvoicingCodeDto> invoicingCodes;
	private String invoiceType;
	private String sentMediumType;
	private String interventionType;

	private String groupId;

	private String recipientType; //org.taktik.icure.entities.HealthcareParty, org.taktik.icure.entities.Insurance, org.taktik.icure.entities.Patient
	private String recipientId; //for hcps and insurance, patient link happens through secretForeignKeys

	private String invoiceReference;
	private String thirdPartyReference;

	private Map<String,String> receipts = new HashMap<>();

	public Long getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Long invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Long getPrintedDate() {
		return printedDate;
	}

	public void setPrintedDate(Long printedDate) {
		this.printedDate = printedDate;
	}

	public List<InvoicingCodeDto> getInvoicingCodes() {
		return invoicingCodes;
	}

	public void setInvoicingCodes(List<InvoicingCodeDto> invoicingCodes) {
		this.invoicingCodes = invoicingCodes;
	}

	public String getRecipientType() {
		return recipientType;
	}

	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	public String getInvoiceReference() {
		return invoiceReference;
	}

	public void setInvoiceReference(String invoiceReference) {
		this.invoiceReference = invoiceReference;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getSentMediumType() { return sentMediumType; }

	public void setSentMediumType(String sentMediumType) { this.sentMediumType = sentMediumType; }

	public String getInterventionType() { return interventionType; }

	public void setInterventionType(String interventionType) { this.interventionType = interventionType; }

	public String getGroupId() { return groupId; }

	public void setGroupId(String groupId) { this.groupId = groupId; }

	public Long getSentDate() {
		return sentDate;
	}

	public void setSentDate(Long sentDate) {
		this.sentDate = sentDate;
	}

	public String getThirdPartyReference() {
		return thirdPartyReference;
	}

	public void setThirdPartyReference(String thirdPartyReference) {
		this.thirdPartyReference = thirdPartyReference;
	}

	public Map<String, String> getReceipts() {
		return receipts;
	}

	public void setReceipts(Map<String, String> receipts) {
		this.receipts = receipts;
	}

	public Double getPaid() {
		return paid;
	}

	public void setPaid(Double paid) {
		this.paid = paid;
	}

	private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}
}
