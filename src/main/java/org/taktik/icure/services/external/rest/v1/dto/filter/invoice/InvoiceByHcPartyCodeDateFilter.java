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

package org.taktik.icure.services.external.rest.v1.dto.filter.invoice;

import com.google.common.base.Objects;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

@JsonPolymorphismRoot(Filter.class)
public class InvoiceByHcPartyCodeDateFilter extends Filter<Invoice> implements org.taktik.icure.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter {

    private String healthcarePartyId;
    private String code;
    private Long startInvoiceDate;
    private Long endInvoiceDate;

    @Override
	public boolean matches(Invoice item) {
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
                && (item.getInvoicingCodes().stream().anyMatch(ic -> ic.getTarificationId().contains(code)))
                && (startInvoiceDate == null || startInvoiceDate < item.getInvoiceDate())
                && (endInvoiceDate == null || item.getInvoiceDate() > endInvoiceDate);
	}

    @Override
    public String getHealthcarePartyId() {
        return healthcarePartyId;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public Long getStartInvoiceDate() {
        return startInvoiceDate;
    }

    @Override
    public Long getEndInvoiceDate() {
        return endInvoiceDate;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStartInvoiceDate(Long startInvoiceDate) {
        this.startInvoiceDate = startInvoiceDate;
    }

    public void setEndInvoiceDate(Long endInvoiceDate) {
        this.endInvoiceDate = endInvoiceDate;
    }
}
