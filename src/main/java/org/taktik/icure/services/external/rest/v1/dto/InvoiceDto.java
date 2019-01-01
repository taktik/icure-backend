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

import org.taktik.icure.entities.IdentityDocumentReader;
import org.taktik.icure.entities.Payment;
import org.taktik.icure.services.external.rest.v1.dto.embed.PaymentType;
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoicingCodeDto;

public class InvoiceDto extends IcureDto {
	private Long invoiceDate; //yyyyMMdd
	private Long sentDate;
	private Long printedDate;

	private Double paid;
	private PaymentType paymentType;
	private List<Payment> payments;

	private List<InvoicingCodeDto> invoicingCodes;
	private String invoiceType;
	private String sentMediumType;
	private String interventionType;

	private String groupId;

	private String correctiveInvoiceId;
	private String correctedInvoiceId;

	private String recipientType; //org.taktik.icure.entities.HealthcareParty, org.taktik.icure.entities.Insurance, org.taktik.icure.entities.Patient
	private String recipientId; //for hcps and insurance, patient link happens through secretForeignKeys

	private String invoiceReference;
	private String thirdPartyReference;

	private String thirdPartyPaymentJustification;
	protected String thirdPartyPaymentReason;

	protected String gnotionNihii;
	protected String gnotionSsin;
	protected String gnotionLastName;
	protected String gnotionFirstName;
	protected String gnotionCdHcParty;

	protected Integer invoicePeriod;

	protected String internshipNihii;
	protected String internshipSsin;
	protected String internshipLastName;
	protected String internshipFirstName;
	protected String internshipCdHcParty;
	protected String internshipCbe;

	protected String supervisorNihii;
	protected String supervisorSsin;
	protected String supervisorLastName;
	protected String supervisorFirstName;
	protected String supervisorCdHcParty;
	protected String supervisorCbe;

	protected Integer longDelayJustification;

	protected Boolean creditNote;
	protected String creditNoteRelatedInvoiceId;

	protected String careProviderType;

	protected String error;

	protected String encounterLocationName;
	protected String encounterLocationNihii;
	protected Integer encounterLocationNorm;


	protected Map<String,String> receipts = new HashMap<>();

	protected IdentityDocumentReader idDocument;

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

	public String getCorrectiveInvoiceId() {
		return correctiveInvoiceId;
	}

	public void setCorrectiveInvoiceId(String correctiveInvoiceId) {
		this.correctiveInvoiceId = correctiveInvoiceId;
	}

	public String getCorrectedInvoiceId() {
		return correctedInvoiceId;
	}

	public void setCorrectedInvoiceId(String correctedInvoiceId) {
		this.correctedInvoiceId = correctedInvoiceId;
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

	public String getGnotionNihii() { return gnotionNihii; }

	public void setGnotionNihii(String gnotionNihii) { this.gnotionNihii = gnotionNihii; }

	public String getGnotionSsin() { return gnotionSsin; }

	public void setGnotionSsin(String gnotionSsin) { this.gnotionSsin = gnotionSsin; }

	public String getGnotionLastName() { return gnotionLastName; }

	public void setGnotionLastName(String gnotionLastName) { this.gnotionLastName = gnotionLastName; }

	public String getGnotionFirstName() { return gnotionFirstName; }

	public void setGnotionFirstName(String gnotionFirstName) { this.gnotionFirstName = gnotionFirstName; }

	public String getGnotionCdHcParty() { return gnotionCdHcParty; }

	public void setGnotionCdHcParty(String gnotionCdHcParty) { this.gnotionCdHcParty = gnotionCdHcParty; }

	public Integer getInvoicePeriod() { return invoicePeriod;}

	public void setInvoicePeriod(Integer invoicePeriod) { this.invoicePeriod = invoicePeriod; }

	public String getInternshipNihii() { return internshipNihii; }

	public void setInternshipNihii(String internshipNihii) { this.internshipNihii = internshipNihii; }

	public String getInternshipSsin() { return internshipSsin; }

	public void setInternshipSsin(String internshipSsin) { this.internshipSsin = internshipSsin; }

	public String getInternshipLastName() { return internshipLastName; }

	public void setInternshipLastName(String internshipLastName) { this.internshipLastName = internshipLastName; }

	public String getInternshipFirstName() { return internshipFirstName; }

	public void setInternshipFirstName(String internshipFirstName) { this.internshipFirstName = internshipFirstName; }

	public String getInternshipCdHcParty() { return internshipCdHcParty; }

	public void setInternshipCdHcParty(String internshipCdHcParty) { this.internshipCdHcParty = internshipCdHcParty; }

	public String getInternshipCbe() { return internshipCbe; }

	public void setInternshipCbe(String internshipCbe) { this.internshipCbe = internshipCbe; }

	public String getSupervisorNihii() {
		return supervisorNihii;
	}

	public void setSupervisorNihii(String supervisorNihii) {
		this.supervisorNihii = supervisorNihii;
	}

	public String getSupervisorSsin() {
		return supervisorSsin;
	}

	public void setSupervisorSsin(String supervisorSsin) {
		this.supervisorSsin = supervisorSsin;
	}

	public String getSupervisorLastName() {
		return supervisorLastName;
	}

	public void setSupervisorLastName(String supervisorLastName) {
		this.supervisorLastName = supervisorLastName;
	}

	public String getSupervisorFirstName() {
		return supervisorFirstName;
	}

	public void setSupervisorFirstName(String supervisorFirstName) {
		this.supervisorFirstName = supervisorFirstName;
	}

	public String getSupervisorCdHcParty() {
		return supervisorCdHcParty;
	}

	public void setSupervisorCdHcParty(String supervisorCdHcParty) {
		this.supervisorCdHcParty = supervisorCdHcParty;
	}

	public void setSupervisorCbe(String supervisorCbe) { this.supervisorCbe = supervisorCbe; }

	public String getSupervisorCbe() { return supervisorCbe; }

	public Integer getLongDelayJustification() {
		return longDelayJustification;
	}

	public void setLongDelayJustification(Integer longDelayJustification) { this.longDelayJustification = longDelayJustification; }

	public Boolean getCreditNote() { return creditNote; }

	public void setCreditNote(Boolean creditNote) { this.creditNote = creditNote; }

	public String getCreditNoteRelatedInvoiceId() { return creditNoteRelatedInvoiceId; }

	public void setCreditNoteRelatedInvoiceId(String creditNoteRelatedInvoiceId) { this.creditNoteRelatedInvoiceId = creditNoteRelatedInvoiceId; }

	public String getCareProviderType() { return careProviderType; }

	public void setCareProviderType(String careProviderType) { this.careProviderType = careProviderType; }

	public String getError() { return error; }

	public void setError(String error) { this.error = error; }

	public String getEncounterLocationName() { return encounterLocationName; }

	public void setEncounterLocationName(String encounterLocationName) { this.encounterLocationName = encounterLocationName; }

	public String getEncounterLocationNihii() { return encounterLocationNihii; }

	public void setEncounterLocationNihii(String encounterLocationNihii) { this.encounterLocationNihii = encounterLocationNihii; }

	public Integer getEncounterLocationNorm() {
		return encounterLocationNorm;
	}

	public void setEncounterLocationNorm(Integer encounterLocationNorm) {
		this.encounterLocationNorm = encounterLocationNorm;
	}


	public String getThirdPartyPaymentJustification() {
		return thirdPartyPaymentJustification;
	}

	public void setThirdPartyPaymentJustification(String thirdPartyPaymentJustification) {
		this.thirdPartyPaymentJustification = thirdPartyPaymentJustification;
	}

	public String getThirdPartyPaymentReason() {
		return thirdPartyPaymentReason;
	}

	public void setThirdPartyPaymentReason(String thirdPartyPaymentReason) {
		this.thirdPartyPaymentReason = thirdPartyPaymentReason;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public IdentityDocumentReader getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(IdentityDocumentReader idDocument) {
		this.idDocument = idDocument;
	}
	
}
