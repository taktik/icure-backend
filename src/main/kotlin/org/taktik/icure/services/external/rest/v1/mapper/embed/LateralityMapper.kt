package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Laterality
import org.taktik.icure.services.external.rest.v1.dto.embed.LateralityDto
@Mapper(componentModel = "spring")
interface LateralityMapper {
	fun map(lateralityDto: LateralityDto):Laterality
	fun map(laterality: Laterality):LateralityDto
}
