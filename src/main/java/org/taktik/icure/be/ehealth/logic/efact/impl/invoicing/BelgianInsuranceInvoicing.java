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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.Acknowledgment;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.Bordereau95;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.ErrorDetail;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.IdentificationFlux;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceBordereau;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.ErrorCount;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class BelgianInsuranceInvoicing {

	private Acknowledgment acknowledgment;
	private List<Bordereau95> bordereaus95 = new ArrayList<>();
	private ErrorCount errorCount;
	private List<ErrorDetail> errorDetails = new ArrayList<>();
	private IdentificationFlux identificationFlux;
	private InvoiceBordereau invoiceBordereau;

	public Acknowledgment getAcknowledgment() {
		return acknowledgment;
	}

	public void setAcknowledgment(Acknowledgment acknowledgment) {
		this.acknowledgment = acknowledgment;
	}

	public List<Bordereau95> getBordereaus95() {
		return bordereaus95;
	}

	public void setBordereaus95(List<Bordereau95> bordereaus95) {
		this.bordereaus95 = bordereaus95;
	}

	public ErrorCount getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(ErrorCount errorCount) {
		this.errorCount = errorCount;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public IdentificationFlux getIdentificationFlux() {
		return identificationFlux;
	}

	public void setIdentificationFlux(IdentificationFlux identificationFlux) {
		this.identificationFlux = identificationFlux;
	}

	public InvoiceBordereau getInvoiceBordereau() {
		return invoiceBordereau;
	}

	public void setInvoiceBordereau(InvoiceBordereau invoiceBordereau) {
		this.invoiceBordereau = invoiceBordereau;
	}
}
