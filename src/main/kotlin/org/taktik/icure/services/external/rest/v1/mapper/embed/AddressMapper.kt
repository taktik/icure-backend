package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
@Mapper(componentModel = "spring")
interface AddressMapper {
	fun map(addressDto: AddressDto):Address
    fun map(address: org.taktik.icure.be.mikrono.dto.kmehr.Address):Address
	fun map(address: Address):AddressDto
    fun mapToMikrono(address: Address):org.taktik.icure.be.mikrono.dto.kmehr.Address
}
