package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.services.external.rest.v1.dto.embed.GenderDto
@Mapper(componentModel = "spring")
interface GenderMapper {
	fun map(genderDto: GenderDto):Gender
	fun map(gender: Gender):GenderDto
}
