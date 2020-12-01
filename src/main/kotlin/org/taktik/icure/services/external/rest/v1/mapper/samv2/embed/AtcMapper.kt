package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Atc
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AtcDto
@Mapper(componentModel = "spring")
interface AtcMapper {
	fun map(atcDto: AtcDto):Atc
	fun map(atc: Atc):AtcDto
}
