package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.HealthcarePartyStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.HealthcarePartyStatusDto
@Mapper(componentModel = "spring")
interface HealthcarePartyStatusMapper {
	fun map(healthcarePartyStatusDto: HealthcarePartyStatusDto):HealthcarePartyStatus
	fun map(healthcarePartyStatus: HealthcarePartyStatus):HealthcarePartyStatusDto
}
