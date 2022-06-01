package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.HealthcarePartyHistoryStatus
import org.taktik.icure.services.external.rest.v2.dto.embed.HealthcarePartyHistoryStatusDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface HealthcarePartyHistoryStatusV2Mapper {
	fun map(healthcarePartyHistoryStatus: HealthcarePartyHistoryStatus): HealthcarePartyHistoryStatusDto
	fun map(healthcarePartyHistoryStatus: HealthcarePartyHistoryStatusDto): HealthcarePartyHistoryStatus
}
