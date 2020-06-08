package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoiceTypeDto
@Mapper(componentModel = "spring")
interface InvoiceTypeMapper {
	fun map(invoiceTypeDto: InvoiceTypeDto):InvoiceType
	fun map(invoiceType: InvoiceType):InvoiceTypeDto
}
