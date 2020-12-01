package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.InsuranceParameter
import org.taktik.icure.services.external.rest.v1.dto.embed.InsuranceParameterDto
@Mapper(componentModel = "spring")
interface InsuranceParameterMapper {
	fun map(insuranceParameterDto: InsuranceParameterDto):InsuranceParameter
	fun map(insuranceParameter: InsuranceParameter):InsuranceParameterDto
}
