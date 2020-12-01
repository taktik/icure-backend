package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.InvoiceInterventionType
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoiceInterventionTypeDto
@Mapper(componentModel = "spring")
interface InvoiceInterventionTypeMapper {
	fun map(invoiceInterventionTypeDto: InvoiceInterventionTypeDto):InvoiceInterventionType
	fun map(invoiceInterventionType: InvoiceInterventionType):InvoiceInterventionTypeDto
}
