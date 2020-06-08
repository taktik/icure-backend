package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PartnershipStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipStatusDto
@Mapper(componentModel = "spring")
interface PartnershipStatusMapper {
	fun map(partnershipStatusDto: PartnershipStatusDto):PartnershipStatus
	fun map(partnershipStatus: PartnershipStatus):PartnershipStatusDto
}
