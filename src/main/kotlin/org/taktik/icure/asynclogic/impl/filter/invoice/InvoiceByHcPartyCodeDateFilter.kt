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
package org.taktik.icure.asynclogic.impl.filter.invoice

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.invoice.InvoiceByHcPartyCodeDateFilter
import org.taktik.icure.entities.Invoice

class InvoiceByHcPartyCodeDateFilter(private val invoiceLogic: InvoiceLogic,
                                     private val healthcarePartyLogic: HealthcarePartyLogic) : Filter<String, Invoice, InvoiceByHcPartyCodeDateFilter> {

    @FlowPreview
    override fun resolve(filter: InvoiceByHcPartyCodeDateFilter, context: Filters): Flow<String> {
        return if (filter.healthcarePartyId != null) invoiceLogic.listInvoiceIdsByTarificationsByCode(filter.healthcarePartyId!!, filter.code, filter.startInvoiceDate, filter.endInvoiceDate)
        else healthcarePartyLogic.getAllEntityIds().flatMapConcat { hcpId -> invoiceLogic.listInvoiceIdsByTarificationsByCode(hcpId, filter.code, filter.startInvoiceDate, filter.endInvoiceDate) }
    }
}
