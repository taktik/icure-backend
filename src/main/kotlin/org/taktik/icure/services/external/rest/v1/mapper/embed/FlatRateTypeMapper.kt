package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FlatRateType
import org.taktik.icure.services.external.rest.v1.dto.embed.FlatRateTypeDto
@Mapper(componentModel = "spring")
interface FlatRateTypeMapper {
	fun map(flatRateTypeDto: FlatRateTypeDto):FlatRateType
	fun map(flatRateType: FlatRateType):FlatRateTypeDto
}
