/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments;

@SuppressWarnings("unused")
public class InvoiceBordereau {
	private String contactPersonLastName;
	private String contactPersonFirstName;
	private String contactPersonPhone;
	private int instructionsVersion;
	private int invoiceCreationDate;
	private String invoiceReference;
	private int invoiceType;
	private int invoicingType;
	private int invoicingYearMonth;
	private String reserve;
	private int sendNumber;
	public InvoiceBordereau() {
	}
	public InvoiceBordereau(String contactPersonFirstName, String contactPersonLastName, String contactPersonPhone,
	                        int instructionsVersion, int invoiceCreationDate, String invoiceReference, int invoiceType,
	                        int invoicingType, int invoicingYearMonth, String reserve, int sendNumber) {
		this.invoicingYearMonth = invoicingYearMonth;
		this.contactPersonFirstName = contactPersonFirstName;
		this.contactPersonLastName = contactPersonLastName;
		this.contactPersonPhone = contactPersonPhone;
		this.instructionsVersion = instructionsVersion;
		this.invoiceCreationDate = invoiceCreationDate;
		this.invoiceReference = invoiceReference;
		this.invoiceType = invoiceType;
		this.invoicingType = invoicingType;
		this.reserve = reserve;
		this.sendNumber = sendNumber;
	}
	public String getContactPersonFirstName() {
		return contactPersonFirstName;
	}
	public void setContactPersonFirstName(String contactPersonFirstName) {
		this.contactPersonFirstName = contactPersonFirstName;
	}
	public String getContactPersonLastName() {
		return contactPersonLastName;
	}
	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}
	public String getContactPersonPhone() {
		return contactPersonPhone;
	}
	public void setContactPersonPhone(String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
	}
	public int getInstructionsVersion() {
		return instructionsVersion;
	}
	public void setInstructionsVersion(int instructionsVersion) {
		this.instructionsVersion = instructionsVersion;
	}
	public int getInvoiceCreationDate() {
		return invoiceCreationDate;
	}
	public void setInvoiceCreationDate(int invoiceCreationDate) {
		this.invoiceCreationDate = invoiceCreationDate;
	}
	public String getInvoiceReference() {
		return invoiceReference;
	}
	public void setInvoiceReference(String invoiceReference) {
		this.invoiceReference = invoiceReference;
	}
	public int getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}
	public int getInvoicingType() {
		return invoicingType;
	}
	public void setInvoicingType(int invoicingType) {
		this.invoicingType = invoicingType;
	}
	public int getInvoicingYearMonth() {
		return invoicingYearMonth;
	}
	public void setInvoicingYearMonth(int invoicingYearMonth) {
		this.invoicingYearMonth = invoicingYearMonth;
	}
	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	public int getSendNumber() {
		return sendNumber;
	}
	public void setSendNumber(int sendNumber) {
		this.sendNumber = sendNumber;
	}
}
