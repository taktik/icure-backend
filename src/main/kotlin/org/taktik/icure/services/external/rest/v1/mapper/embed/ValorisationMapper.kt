package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Valorisation
import org.taktik.icure.services.external.rest.v1.dto.embed.ValorisationDto
@Mapper(componentModel = "spring")
interface ValorisationMapper {
	fun map(valorisationDto: ValorisationDto):Valorisation
	fun map(valorisation: Valorisation):ValorisationDto
}
