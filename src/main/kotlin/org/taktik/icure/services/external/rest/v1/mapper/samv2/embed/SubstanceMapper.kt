package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Substance
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SubstanceDto
@Mapper
interface SubstanceMapper {
	fun map(substanceDto: SubstanceDto):Substance
	fun map(substance: Substance):SubstanceDto
}
