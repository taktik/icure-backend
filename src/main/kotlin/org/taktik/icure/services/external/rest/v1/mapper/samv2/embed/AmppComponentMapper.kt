package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.AmppComponent
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AmppComponentDto
@Mapper(componentModel = "spring")
interface AmppComponentMapper {
	fun map(amppComponentDto: AmppComponentDto):AmppComponent
	fun map(amppComponent: AmppComponent):AmppComponentDto
}
