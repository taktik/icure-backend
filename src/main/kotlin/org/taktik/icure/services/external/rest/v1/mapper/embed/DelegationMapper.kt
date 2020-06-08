package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
@Mapper(componentModel = "spring")
interface DelegationMapper {
	fun map(delegationDto: DelegationDto):Delegation
	fun map(delegation: Delegation):DelegationDto
}
