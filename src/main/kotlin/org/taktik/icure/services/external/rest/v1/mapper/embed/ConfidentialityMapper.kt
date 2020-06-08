package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Confidentiality
import org.taktik.icure.services.external.rest.v1.dto.embed.ConfidentialityDto
@Mapper(componentModel = "spring")
interface ConfidentialityMapper {
	fun map(confidentialityDto: ConfidentialityDto):Confidentiality
	fun map(confidentiality: Confidentiality):ConfidentialityDto
}
