package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Invoice
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto

@Mapper(componentModel = "spring")
interface ContactMapper {
	fun map(contactDto: ContactDto):Contact
	fun map(contact: Contact):ContactDto

    fun mapToStub(invoice: Contact): IcureStubDto
    fun mapFromStub(invoice: IcureStubDto): Contact
}
