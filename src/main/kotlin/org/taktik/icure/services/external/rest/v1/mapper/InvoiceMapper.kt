package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Invoice
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.InvoiceDto
@Mapper(componentModel = "spring")
interface InvoiceMapper {
	fun map(invoiceDto: InvoiceDto):Invoice
	fun map(invoice: Invoice):InvoiceDto

    fun mapToStub(invoice: Invoice):IcureStubDto
    fun mapFromStub(invoice: IcureStubDto):Invoice
}
