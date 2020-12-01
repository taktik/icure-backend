package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.CareTeamMembership
import org.taktik.icure.services.external.rest.v1.dto.embed.CareTeamMembershipDto
@Mapper(componentModel = "spring", uses = [MembershipTypeMapper::class, CareTeamMemberTypeMapper::class, ConfidentialityMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CareTeamMembershipMapper {
	fun map(careTeamMembershipDto: CareTeamMembershipDto):CareTeamMembership
	fun map(careTeamMembership: CareTeamMembership):CareTeamMembershipDto
}
