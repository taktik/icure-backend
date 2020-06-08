package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Measure
import org.taktik.icure.services.external.rest.v1.dto.embed.MeasureDto
@Mapper(componentModel = "spring")
interface MeasureMapper {
	fun map(measureDto: MeasureDto):Measure
	fun map(measure: Measure):MeasureDto
}
