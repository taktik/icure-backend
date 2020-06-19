package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.services.external.rest.v1.dto.embed.ReferralPeriodDto

@Mapper(componentModel = "spring")
interface ReferralPeriodMapper {
	fun map(referralPeriodDto: ReferralPeriodDto):ReferralPeriod
	fun map(referralPeriod: ReferralPeriod):ReferralPeriodDto
}
