package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Visibility
import org.taktik.icure.services.external.rest.v1.dto.embed.VisibilityDto
@Mapper(componentModel = "spring")
interface VisibilityMapper {
	fun map(visibilityDto: VisibilityDto):Visibility
	fun map(visibility: Visibility):VisibilityDto
}
