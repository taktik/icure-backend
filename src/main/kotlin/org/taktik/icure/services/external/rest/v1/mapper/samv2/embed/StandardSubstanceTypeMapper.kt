package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.StandardSubstanceType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.StandardSubstanceTypeDto
@Mapper(componentModel = "spring")
interface StandardSubstanceTypeMapper {
	fun map(standardSubstanceTypeDto: StandardSubstanceTypeDto):StandardSubstanceType
	fun map(standardSubstanceType: StandardSubstanceType):StandardSubstanceTypeDto
}
