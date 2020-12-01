package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressTypeDto
@Mapper(componentModel = "spring")
interface AddressTypeMapper {
	fun map(addressTypeDto: AddressTypeDto):AddressType
	fun map(addressType: AddressType):AddressTypeDto
}
