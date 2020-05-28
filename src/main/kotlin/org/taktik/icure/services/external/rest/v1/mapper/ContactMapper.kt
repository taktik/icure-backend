package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Contact
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
@Mapper
interface ContactMapper {
	fun map(contactDto: ContactDto):Contact
	fun map(contact: Contact):ContactDto
}
