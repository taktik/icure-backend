package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Substanceproduct
import org.taktik.icure.services.external.rest.v1.dto.embed.SubstanceproductDto
@Mapper
interface SubstanceproductMapper {
	fun map(substanceproductDto: SubstanceproductDto):Substanceproduct
	fun map(substanceproduct: Substanceproduct):SubstanceproductDto
}
