/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class InvoicesBatchDto {
    int invoicingYear;
    int invoicingMonth;
    String batchRef;
    String oaCode;
    List<EfactInvoiceDto> invoices;

    public int getInvoicingYear() {
        return invoicingYear;
    }

    public void setInvoicingYear(int invoicingYear) {
        this.invoicingYear = invoicingYear;
    }

    public int getInvoicingMonth() {
        return invoicingMonth;
    }

    public void setInvoicingMonth(int invoicingMonth) {
        this.invoicingMonth = invoicingMonth;
    }

    public String getBatchRef() {
        return batchRef;
    }

    public void setBatchRef(String batchRef) {
        this.batchRef = batchRef;
    }

    public List<EfactInvoiceDto> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<EfactInvoiceDto> invoices) {
        this.invoices = invoices;
    }

    public String getOaCode() {
        return oaCode;
    }

    public void setOaCode(String oaCode) {
        this.oaCode = oaCode;
    }
}
