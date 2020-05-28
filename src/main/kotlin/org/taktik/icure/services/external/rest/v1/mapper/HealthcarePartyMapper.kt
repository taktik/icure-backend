package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
@Mapper
interface HealthcarePartyMapper {
	fun map(healthcarePartyDto: HealthcarePartyDto):HealthcareParty
	fun map(healthcareParty: HealthcareParty):HealthcarePartyDto
}
