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

package org.taktik.icure.logic.impl.filter.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.InvoiceLogic;
import org.taktik.icure.logic.impl.filter.Filter;
import org.taktik.icure.logic.impl.filter.Filters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InvoiceByHcPartyCodeDateFilter implements Filter<String, Invoice, org.taktik.icure.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter> {
    InvoiceLogic invoiceLogic;
    HealthcarePartyLogic healthcarePartyLogic;


    @Autowired
    public void setInvoiceLogic(InvoiceLogic invoiceLogic) {
        this.invoiceLogic = invoiceLogic;
    }
    @Autowired
    public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
        this.healthcarePartyLogic = healthcarePartyLogic;
    }

    @Override
    public Set<String> resolve(org.taktik.icure.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter filter, Filters context) {
        return filter.getHealthcarePartyId() != null ? new HashSet<>(invoiceLogic.listIdsByTarificationsByCode(filter.getHealthcarePartyId(), filter.code(), filter.getStartInvoiceDate(), filter.getEndInvoiceDate())) :
                healthcarePartyLogic.getAllEntityIds().parallelStream().map(hcpId -> invoiceLogic.listIdsByTarificationsByCode(hcpId, filter.code(), filter.getStartInvoiceDate(), filter.getEndInvoiceDate()))
                        .flatMap(Collection::stream).collect(Collectors.toSet());
    }
}
