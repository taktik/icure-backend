package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.ContainsAlcohol
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.ContainsAlcoholDto
@Mapper(componentModel = "spring")
interface ContainsAlcoholMapper {
	fun map(containsAlcoholDto: ContainsAlcoholDto):ContainsAlcohol
	fun map(containsAlcohol: ContainsAlcohol):ContainsAlcoholDto
}
