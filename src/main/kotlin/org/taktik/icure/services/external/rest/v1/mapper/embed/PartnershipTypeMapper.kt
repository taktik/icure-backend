package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PartnershipType
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipTypeDto
@Mapper(componentModel = "spring")
interface PartnershipTypeMapper {
	fun map(partnershipTypeDto: PartnershipTypeDto):PartnershipType
	fun map(partnershipType: PartnershipType):PartnershipTypeDto
}
