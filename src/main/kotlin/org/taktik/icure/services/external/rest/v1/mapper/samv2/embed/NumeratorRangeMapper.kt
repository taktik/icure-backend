package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.NumeratorRange
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.NumeratorRangeDto
@Mapper(componentModel = "spring")
interface NumeratorRangeMapper {
	fun map(numeratorRangeDto: NumeratorRangeDto):NumeratorRange
	fun map(numeratorRange: NumeratorRange):NumeratorRangeDto
}
