package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Insurability
import org.taktik.icure.services.external.rest.v1.dto.embed.InsurabilityDto
@Mapper(componentModel = "spring")
interface InsurabilityMapper {
	fun map(insurabilityDto: InsurabilityDto):Insurability
	fun map(insurability: Insurability):InsurabilityDto
}
