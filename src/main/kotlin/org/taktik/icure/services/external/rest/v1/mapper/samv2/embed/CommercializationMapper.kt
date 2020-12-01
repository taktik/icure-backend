package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Commercialization
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.CommercializationDto
@Mapper(componentModel = "spring")
interface CommercializationMapper {
	fun map(commercializationDto: CommercializationDto):Commercialization
	fun map(commercialization: Commercialization):CommercializationDto
}
