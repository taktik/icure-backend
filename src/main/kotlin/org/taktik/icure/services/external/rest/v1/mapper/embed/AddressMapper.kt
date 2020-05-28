package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
@Mapper
interface AddressMapper {
	fun map(addressDto: AddressDto):Address
	fun map(address: Address):AddressDto
}
