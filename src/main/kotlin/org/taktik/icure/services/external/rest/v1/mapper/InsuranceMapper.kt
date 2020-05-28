package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Insurance
import org.taktik.icure.services.external.rest.v1.dto.InsuranceDto
@Mapper
interface InsuranceMapper {
	fun map(insuranceDto: InsuranceDto):Insurance
	fun map(insurance: Insurance):InsuranceDto
}
