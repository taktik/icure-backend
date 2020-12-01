package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Measure
import org.taktik.icure.services.external.rest.v1.dto.embed.MeasureDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MeasureMapper {
	fun map(measureDto: MeasureDto):Measure
	fun map(measure: Measure):MeasureDto
}
