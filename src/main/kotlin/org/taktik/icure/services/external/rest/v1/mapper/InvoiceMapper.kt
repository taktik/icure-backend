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

package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Invoice
import org.taktik.icure.services.external.rest.v1.dto.InvoiceDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.IdentityDocumentReaderMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.InvoiceInterventionTypeMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.InvoiceTypeMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.InvoicingCodeMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.MediumTypeMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PaymentMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PaymentTypeMapper

@Mapper(componentModel = "spring", uses = [InvoiceTypeMapper::class, PaymentTypeMapper::class, InvoicingCodeMapper::class, IdentityDocumentReaderMapper::class, MediumTypeMapper::class, CodeStubMapper::class, PaymentMapper::class, DelegationMapper::class, InvoiceInterventionTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface InvoiceMapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(invoiceDto: InvoiceDto): Invoice
	fun map(invoice: Invoice): InvoiceDto
}
