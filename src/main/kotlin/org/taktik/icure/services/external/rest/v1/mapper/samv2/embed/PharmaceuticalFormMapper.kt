package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.PharmaceuticalForm
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.PharmaceuticalFormDto
@Mapper(componentModel = "spring")
interface PharmaceuticalFormMapper {
	fun map(pharmaceuticalFormDto: PharmaceuticalFormDto):PharmaceuticalForm
	fun map(pharmaceuticalForm: PharmaceuticalForm):PharmaceuticalFormDto
}
