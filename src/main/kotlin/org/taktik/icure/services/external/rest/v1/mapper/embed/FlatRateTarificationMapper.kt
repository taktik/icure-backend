package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FlatRateTarification
import org.taktik.icure.services.external.rest.v1.dto.embed.FlatRateTarificationDto
@Mapper
interface FlatRateTarificationMapper {
	fun map(flatRateTarificationDto: FlatRateTarificationDto):FlatRateTarification
	fun map(flatRateTarification: FlatRateTarification):FlatRateTarificationDto
}
