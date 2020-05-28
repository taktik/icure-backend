package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Partnership
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipDto
@Mapper
interface PartnershipMapper {
	fun map(partnershipDto: PartnershipDto):Partnership
	fun map(partnership: Partnership):PartnershipDto
}
