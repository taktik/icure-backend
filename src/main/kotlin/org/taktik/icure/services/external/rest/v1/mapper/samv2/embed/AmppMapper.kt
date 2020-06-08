package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Ampp
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AmppDto
@Mapper(componentModel = "spring")
interface AmppMapper {
	fun map(amppDto: AmppDto):Ampp
	fun map(ampp: Ampp):AmppDto
}
