package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.HealthcarePartyHistoryStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.HealthcarePartyHistoryStatusDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface HealthcarePartyHistoryStatusMapper {
	fun map(healthcarePartyHistoryStatus: HealthcarePartyHistoryStatus): HealthcarePartyHistoryStatusDto
	fun map(healthcarePartyHistoryStatus: HealthcarePartyHistoryStatusDto): HealthcarePartyHistoryStatus
}
