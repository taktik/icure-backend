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

package org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing;

import java.util.List;

import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicingTreatmentReasonCode;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class EfactInvoiceDto {
    PatientDto patient;
    List<InvoiceItemDto> items;
    InvoicingTreatmentReasonCode reason;
    String invoiceRef;
    long invoiceNumber;

    public PatientDto getPatient() {
        return patient;
    }

    public void setPatient(PatientDto patient) {
        this.patient = patient;
    }

    public List<InvoiceItemDto> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDto> items) {
        this.items = items;
    }

    public InvoicingTreatmentReasonCode getReason() {
        return reason;
    }

    public void setReason(InvoicingTreatmentReasonCode reason) {
        this.reason = reason;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
