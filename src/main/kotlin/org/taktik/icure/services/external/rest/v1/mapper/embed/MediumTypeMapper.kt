package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.services.external.rest.v1.dto.embed.MediumTypeDto
@Mapper(componentModel = "spring")
interface MediumTypeMapper {
	fun map(mediumTypeDto: MediumTypeDto):MediumType
	fun map(mediumType: MediumType):MediumTypeDto
}
