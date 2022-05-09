/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.asynclogic.impl.filter.invoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.invoice.InvoiceByHcPartyCodeDateFilter
import org.taktik.icure.entities.Invoice

@Service
class InvoiceByHcPartyCodeDateFilter(
	private val invoiceLogic: InvoiceLogic,
	private val healthcarePartyLogic: HealthcarePartyLogic
) : Filter<String, Invoice, InvoiceByHcPartyCodeDateFilter> {


	override fun resolve(filter: InvoiceByHcPartyCodeDateFilter, context: Filters): Flow<String> {
		return if (filter.healthcarePartyId != null) invoiceLogic.listInvoiceIdsByTarificationsByCode(filter.healthcarePartyId!!, filter.code, filter.startInvoiceDate, filter.endInvoiceDate)
		else healthcarePartyLogic.getEntityIds().flatMapConcat { hcpId -> invoiceLogic.listInvoiceIdsByTarificationsByCode(hcpId, filter.code, filter.startInvoiceDate, filter.endInvoiceDate) }
	}
}
