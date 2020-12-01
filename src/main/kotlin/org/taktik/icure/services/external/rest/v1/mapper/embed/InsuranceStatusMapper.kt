package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.InsuranceStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.InsuranceStatusDto
@Mapper(componentModel = "spring")
interface InsuranceStatusMapper {
	fun map(insuranceStatusDto: InsuranceStatusDto):InsuranceStatus
	fun map(insuranceStatus: InsuranceStatus):InsuranceStatusDto
}
