package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Crushable
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.CrushableDto
@Mapper(componentModel = "spring")
interface CrushableMapper {
	fun map(crushableDto: CrushableDto):Crushable
	fun map(crushable: Crushable):CrushableDto
}
