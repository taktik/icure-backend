package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DelegationTag
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationTagDto
@Mapper(componentModel = "spring")
interface DelegationTagMapper {
	fun map(delegationTagDto: DelegationTagDto):DelegationTag
	fun map(delegationTag: DelegationTag):DelegationTagDto
}
