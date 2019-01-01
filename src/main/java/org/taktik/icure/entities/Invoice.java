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
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.InvoiceInterventionType;
import org.taktik.icure.entities.embed.InvoiceType;
import org.taktik.icure.entities.embed.InvoicingCode;
import org.taktik.icure.entities.embed.MediumType;
import org.taktik.icure.entities.embed.PaymentType;
import org.taktik.icure.entities.utils.MergeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice extends StoredICureDocument {
	private Long invoiceDate; // yyyyMMdd
	private Long sentDate;
	private Long printedDate;

	private List<InvoicingCode> invoicingCodes = new ArrayList<>();
	private Map<String, String> receipts = new HashMap<>();

	private String recipientType; // org.taktik.icure.entities.HealthcareParty,
	// org.taktik.icure.entities.Insurance, org.taktik.icure.entities.Patient
	private String recipientId; // for hcps and insurance, patient link happens through secretForeignKeys

	private String invoiceReference;
	private String thirdPartyReference;

	private String thirdPartyPaymentJustification;
	private String thirdPartyPaymentReason;

	private InvoiceType invoiceType;
	private MediumType sentMediumType;
	private InvoiceInterventionType interventionType;

	private String groupId;

	private PaymentType paymentType;
	private Double paid;
	private List<Payment> payments;

	private String gnotionNihii;
	private String gnotionSsin;
	private String gnotionLastName;
	private String gnotionFirstName;
	private String gnotionCdHcParty;
	private Integer invoicePeriod;
	private String careProviderType;
	private String internshipNihii;
	private String internshipSsin;
	private String internshipLastName;
	private String internshipFirstName;
	private String internshipCdHcParty;
	private String internshipCbe;
	private String supervisorNihii;
	private String supervisorSsin;
	private String supervisorLastName;
	private String supervisorFirstName;
	private String supervisorCdHcParty;
	private String supervisorCbe;

	private String error;

	private String encounterLocationName;
	private String encounterLocationNihii;
	private Integer encounterLocationNorm;

	private Integer longDelayJustification;

	private String correctiveInvoiceId;
	private String correctedInvoiceId;

	private Boolean creditNote;
	private String creditNoteRelatedInvoiceId;

	private IdentityDocumentReader idDocument;

	public Invoice solveConflictWith(Invoice other) {
		super.solveConflictsWith(other);

		this.invoiceDate = other.invoiceDate == null ? this.invoiceDate
				: this.invoiceDate == null ? other.invoiceDate
				: Long.valueOf(Math.max(this.invoiceDate, other.invoiceDate));
		this.sentDate = other.sentDate == null ? this.sentDate
				: this.sentDate == null ? other.sentDate : Long.valueOf(Math.max(this.sentDate, other.sentDate));
		this.printedDate = other.printedDate == null ? this.printedDate
				: this.printedDate == null ? other.printedDate : Long.valueOf(Math.max(this.printedDate, other.printedDate));
		this.paid = other.paid == null ? this.paid
				: this.paid == null ? other.paid : Double.valueOf(Math.max(this.paid, other.paid));

		this.invoiceReference = this.invoiceReference == null ? other.invoiceReference : this.invoiceReference;
		this.invoiceType = this.invoiceType == null ? other.invoiceType : this.invoiceType;
		this.sentMediumType = this.sentMediumType == null ? other.sentMediumType : this.sentMediumType;
		this.recipientType = this.recipientType == null ? other.recipientType : this.recipientType;
		this.interventionType = this.interventionType == null ? other.interventionType : this.interventionType;
		this.recipientId = this.recipientId == null ? other.recipientId : this.recipientId;
		this.groupId = this.groupId == null ? other.groupId : this.groupId;

		this.longDelayJustification = this.longDelayJustification == null ? other.longDelayJustification : this.longDelayJustification;
		this.creditNote = this.creditNote == null ? other.creditNote : this.creditNote;
		this.creditNoteRelatedInvoiceId = this.creditNoteRelatedInvoiceId == null ? other.creditNoteRelatedInvoiceId: this.creditNoteRelatedInvoiceId;

		this.gnotionNihii = this.gnotionNihii == null ? other.gnotionNihii : this.gnotionNihii;
		this.gnotionSsin = this.gnotionSsin == null ? other.gnotionSsin : this.gnotionSsin;
		this.gnotionLastName = this.gnotionLastName == null ? other.gnotionLastName : this.gnotionLastName;
		this.gnotionFirstName = this.gnotionFirstName == null ? other.gnotionFirstName : this.gnotionFirstName;
		this.gnotionCdHcParty = this.gnotionCdHcParty == null ? other.gnotionCdHcParty : this.gnotionCdHcParty;
		this.invoicePeriod = this.invoicePeriod == null ? other.invoicePeriod : this.invoicePeriod;
		this.careProviderType = this.careProviderType == null ? other.careProviderType : this.careProviderType;
		this.internshipNihii = this.internshipNihii == null ? other.internshipNihii : this.internshipNihii;
		this.internshipSsin = this.internshipSsin == null ? other.internshipSsin : this.internshipSsin;
		this.internshipLastName = this.internshipLastName == null ? other.internshipLastName : this.internshipLastName;
		this.internshipFirstName = this.internshipFirstName == null ? other.internshipFirstName : this.internshipFirstName;
		this.internshipCdHcParty = this.internshipCdHcParty == null ? other.internshipCdHcParty : this.internshipCdHcParty;
		this.internshipCbe = this.internshipCbe == null ? other.internshipCbe : this.internshipCbe;
		this.supervisorNihii = this.supervisorNihii == null ? other.supervisorNihii : this.supervisorNihii;
		this.supervisorSsin = this.supervisorSsin == null ? other.supervisorSsin : this.supervisorSsin;
		this.supervisorLastName = this.supervisorLastName == null ? other.supervisorLastName : this.supervisorLastName;
		this.supervisorFirstName = this.supervisorFirstName == null ? other.supervisorFirstName : this.supervisorFirstName;
		this.supervisorCdHcParty = this.supervisorCdHcParty == null ? other.supervisorCdHcParty : this.supervisorCdHcParty;
		this.supervisorCbe = this.supervisorCbe == null ? other.supervisorCbe : this.supervisorCbe;

		this.invoicingCodes = this.invoicingCodes == null ? other.invoicingCodes
				: MergeUtil.mergeListsDistinct(this.invoicingCodes, other.invoicingCodes,
				(a, b) -> Objects.equals(a != null ? a.getId() : null, b != null ? b.getId() : null),
				(a, b) -> a == null ? b : b == null ? a : a.solveConflictWith(b));
		if (this.receipts != null && other.receipts != null) {
			other.receipts.putAll(this.receipts);
		}
		if (other.receipts != null) {
			this.receipts = other.receipts;
		}

		return this;
	}

	public static Invoice reassignationInvoiceFromOtherInvoice(Invoice i, UUIDGenerator uuidGenerator) {
		return Invoice.reassignationInvoiceFromOtherInvoice(i, i.invoicingCodes, uuidGenerator);
	}

	private static Invoice reassignationInvoiceFromOtherInvoice(Invoice i, List<InvoicingCode> codes,
	                                                            UUIDGenerator uuidGenerator) {
		Invoice ni = new Invoice();

		ni.invoiceDate = i.invoiceDate;
		ni.recipientType = i.recipientType;
		ni.recipientId = i.recipientId;
		ni.invoiceType = i.invoiceType;
		ni.sentMediumType = i.sentMediumType;
		ni.interventionType = i.interventionType;
		ni.secretForeignKeys = i.secretForeignKeys; // The new invoice is linked to the same patient
		ni.cryptedForeignKeys = i.cryptedForeignKeys; // The new invoice is linked to the same patient
		ni.paid = i.paid;
		ni.author = i.author;
		ni.responsible = i.responsible;

		ni.created = System.currentTimeMillis();
		ni.modified = ni.created;

		ni.gnotionNihii = i.gnotionNihii;
		ni.gnotionSsin = i.gnotionSsin;
		ni.gnotionLastName = i.gnotionLastName;
		ni.gnotionFirstName = i.gnotionFirstName;
		ni.gnotionCdHcParty = i.gnotionCdHcParty;
		ni.invoicePeriod = i.invoicePeriod;
		ni.careProviderType = i.careProviderType;
		ni.internshipNihii = i.internshipNihii;
		ni.internshipSsin = i.internshipSsin;
		ni.internshipLastName = i.internshipLastName;
		ni.internshipFirstName = i.internshipFirstName;
		ni.internshipCdHcParty = i.internshipCdHcParty;
		ni.internshipCbe = i.internshipCbe;
		ni.supervisorNihii = i.supervisorNihii;
		ni.supervisorSsin = i.supervisorSsin;
		ni.supervisorLastName = i.supervisorLastName;
		ni.supervisorFirstName = i.supervisorFirstName;
		ni.supervisorCdHcParty = i.supervisorCdHcParty;
		ni.supervisorCbe = i.supervisorCbe;

		ni.invoicingCodes = codes.stream().map(ic -> {
			InvoicingCode invoicingCode = new InvoicingCode(ic);
			invoicingCode.setId(uuidGenerator.newGUID().toString());

			invoicingCode.setResent(true);
			invoicingCode.setCanceled(false);
			invoicingCode.setPending(false);
			invoicingCode.setAccepted(false);

			return invoicingCode;
		}).collect(Collectors.toList());

		return ni;
	}

	public Long getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Long invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public List<InvoicingCode> getInvoicingCodes() {
		return invoicingCodes;
	}

	public void setInvoicingCodes(List<InvoicingCode> invoicingCodes) {
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


	public String getInvoiceReference() {
		return invoiceReference;
	}

	public void setInvoiceReference(String invoiceReference) {
		this.invoiceReference = invoiceReference;
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}

	public Long getSentDate() {
		return sentDate;
	}

	public void setSentDate(Long sentDate) {
		this.sentDate = sentDate;
	}

	public Long getPrintedDate() {
		return printedDate;
	}

	public void setPrintedDate(Long printedDate) {
		this.printedDate = printedDate;
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

	public MediumType getSentMediumType() {
		return sentMediumType;
	}

	public void setSentMediumType(MediumType sentMediumType) {
		this.sentMediumType = sentMediumType;
	}

	public InvoiceInterventionType getInterventionType() {
		return interventionType;
	}

	public void setInterventionType(InvoiceInterventionType interventionType) {
		this.interventionType = interventionType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGnotionNihii() {
		return gnotionNihii;
	}

	public void setGnotionNihii(String gnotionNihii) {
		this.gnotionNihii = gnotionNihii;
	}

	public String getGnotionSsin() {
		return gnotionSsin;
	}

	public void setGnotionSsin(String gnotionSsin) {
		this.gnotionSsin = gnotionSsin;
	}

	public String getGnotionLastName() {
		return gnotionLastName;
	}

	public void setGnotionLastName(String gnotionLastName) {
		this.gnotionLastName = gnotionLastName;
	}

	public String getGnotionFirstName() {
		return gnotionFirstName;
	}

	public void setGnotionFirstName(String gnotionFirstName) {
		this.gnotionFirstName = gnotionFirstName;
	}

	public String getGnotionCdHcParty() {
		return gnotionCdHcParty;
	}

	public void setGnotionCdHcParty(String gnotionCdHcParty) {
		this.gnotionCdHcParty = gnotionCdHcParty;
	}

	public Integer getInvoicePeriod() {
		return invoicePeriod;
	}

	public void setInvoicePeriod(Integer invoicePeriod) {
		this.invoicePeriod = invoicePeriod;
	}

	public String getInternshipNihii() {
		return internshipNihii;
	}

	public void setInternshipNihii(String internshipNihii) {
		this.internshipNihii = internshipNihii;
	}

	public String getInternshipSsin() {
		return internshipSsin;
	}

	public void setInternshipSsin(String internshipSsin) {
		this.internshipSsin = internshipSsin;
	}

	public String getInternshipLastName() {
		return internshipLastName;
	}

	public void setInternshipLastName(String internshipLastName) {
		this.internshipLastName = internshipLastName;
	}

	public String getInternshipFirstName() {
		return internshipFirstName;
	}

	public void setInternshipFirstName(String internshipFirstName) {
		this.internshipFirstName = internshipFirstName;
	}

	public String getInternshipCdHcParty() {
		return internshipCdHcParty;
	}

	public void setInternshipCdHcParty(String internshipCdHcParty) {
		this.internshipCdHcParty = internshipCdHcParty;
	}

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

	public void setSupervisorCbe(String supervisorCbe) { this.supervisorCbe = supervisorCbe; }

	public String getSupervisorCbe() { return supervisorCbe; }

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

	public void setSupervisorFirstName(String supervisorFirstName) {
		this.supervisorFirstName = supervisorFirstName;
	}

	public String getSupervisorCdHcParty() {
		return supervisorCdHcParty;
	}

	public void setSupervisorCdHcParty(String supervisorCdHcParty) {
		this.supervisorCdHcParty = supervisorCdHcParty;
	}

	public String getError() { return error; }

	public void setError(String error) { this.error = error; }

	public Integer getLongDelayJustification() {
		return longDelayJustification;
	}

	public void setLongDelayJustification(Integer longDelayJustification) {
		this.longDelayJustification = longDelayJustification;
	}

	public Boolean getCreditNote() {
		return creditNote;
	}

	public void setCreditNote(Boolean creditNote) {
		this.creditNote = creditNote;
	}

	public String getCreditNoteRelatedInvoiceId() { return creditNoteRelatedInvoiceId; }

	public void setCreditNoteRelatedInvoiceId(String creditNoteRelatedInvoiceId) { this.creditNoteRelatedInvoiceId = creditNoteRelatedInvoiceId; }

	public String getCareProviderType() {
		return careProviderType;
	}

	public void setCareProviderType(String careProviderType) {
		this.careProviderType = careProviderType;
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		Invoice invoice = (Invoice) o;
		return Objects.equals(invoiceDate, invoice.invoiceDate) && Objects.equals(sentDate, invoice.sentDate)
				&& Objects.equals(paid, invoice.paid) && Objects.equals(invoicingCodes, invoice.invoicingCodes)
				&& Objects.equals(recipientType, invoice.recipientType)
				&& Objects.equals(sentMediumType, invoice.sentMediumType)
				&& Objects.equals(recipientId, invoice.recipientId)
				&& Objects.equals(invoiceReference, invoice.invoiceReference)
				&& invoiceType == invoice.invoiceType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), invoiceDate, sentDate, invoicingCodes, recipientType, recipientId,
				invoiceReference, invoiceType);
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


	public IdentityDocumentReader getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(IdentityDocumentReader idDocument) {
		this.idDocument = idDocument;
	}
}
