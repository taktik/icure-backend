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
import org.taktik.icure.entities.embed.InvoiceType;
import org.taktik.icure.entities.embed.InvoicingCode;
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
	private Long invoiceDate; //yyyyMMdd
	private Long sentDate;
	private Long printedDate;

	private List<InvoicingCode> invoicingCodes = new ArrayList<>();
	private Map<String, String> receipts = new HashMap<>();

	private String recipientType; //org.taktik.icure.entities.HealthcareParty, org.taktik.icure.entities.Insurance, org.taktik.icure.entities.Patient
	private String recipientId; //for hcps and insurance, patient link happens through secretForeignKeys

	private String invoiceReference;
	private String thirdPartyReference;

	private InvoiceType invoiceType;

	private Double paid;

	public Invoice solveConflictWith(Invoice other) {
		super.solveConflictsWith(other);

		this.invoiceDate = other.invoiceDate == null ? this.invoiceDate : this.invoiceDate == null ? other.invoiceDate : Long.valueOf(Math.max(this.invoiceDate, other.invoiceDate));
		this.sentDate = other.sentDate == null ? this.sentDate : this.sentDate == null ? other.sentDate : Long.valueOf(Math.max(this.sentDate, other.sentDate));
		this.paid = other.paid == null ? this.paid : this.paid == null ? other.paid : Double.valueOf(Math.max(this.paid, other.paid));

		this.invoiceReference = this.invoiceReference == null ? other.invoiceReference : this.invoiceReference;
		this.invoiceType = this.invoiceType == null ? other.invoiceType : this.invoiceType;
		this.recipientType = this.recipientType == null ? other.recipientType : this.recipientType;
		this.recipientId = this.recipientId == null ? other.recipientId : this.recipientId;

		this.invoicingCodes = this.invoicingCodes == null ? other.invoicingCodes :
			MergeUtil.mergeListsDistinct(this.invoicingCodes, other.invoicingCodes,
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

	public static Invoice reassignationInvoiceFromOtherInvoice(Invoice i, List<InvoicingCode> codes, UUIDGenerator uuidGenerator) {
		Invoice ni = new Invoice();

		ni.invoiceDate = i.invoiceDate;
		ni.recipientType = i.recipientType;
		ni.recipientId = i.recipientId;
		ni.invoiceType = i.invoiceType;
		ni.secretForeignKeys = i.secretForeignKeys; //The new invoice is linked to the same patient
		ni.cryptedForeignKeys = i.cryptedForeignKeys; //The new invoice is linked to the same patient
		ni.paid = i.paid;
		ni.author = i.author;
		ni.responsible = i.responsible;

		ni.created = System.currentTimeMillis();
		ni.modified = ni.created;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Invoice invoice = (Invoice) o;
		return Objects.equals(invoiceDate, invoice.invoiceDate) &&
			Objects.equals(sentDate, invoice.sentDate) &&
			Objects.equals(paid, invoice.paid) &&
			Objects.equals(invoicingCodes, invoice.invoicingCodes) &&
			Objects.equals(recipientType, invoice.recipientType) &&
			Objects.equals(recipientId, invoice.recipientId) &&
			Objects.equals(invoiceReference, invoice.invoiceReference) &&
			invoiceType == invoice.invoiceType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), invoiceDate, sentDate, invoicingCodes, recipientType, recipientId, invoiceReference, invoiceType);
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
}
