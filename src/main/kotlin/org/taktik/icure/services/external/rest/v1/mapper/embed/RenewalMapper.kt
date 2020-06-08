package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Renewal
import org.taktik.icure.services.external.rest.v1.dto.embed.RenewalDto
@Mapper(componentModel = "spring")
interface RenewalMapper {
	fun map(renewalDto: RenewalDto):Renewal
	fun map(renewal: Renewal):RenewalDto
}
