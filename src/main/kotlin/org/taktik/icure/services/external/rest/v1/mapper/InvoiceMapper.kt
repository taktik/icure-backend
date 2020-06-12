package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Invoice
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.InvoiceDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper

@Mapper(componentModel = "spring", uses = [InvoiceTypeMapper::class, PaymentTypeMapper::class, InvoicingCodeMapper::class, IdentityDocumentReaderMapper::class, MediumTypeMapper::class, CodeStubMapper::class, PaymentMapper::class, DelegationMapper::class, InvoiceInterventionTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface InvoiceMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true),
            Mapping(target = "set_type", ignore = true)
            )
	fun map(invoiceDto: InvoiceDto):Invoice
	fun map(invoice: Invoice):InvoiceDto
}
