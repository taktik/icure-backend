package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.CareTeamMembership
import org.taktik.icure.services.external.rest.v1.dto.embed.CareTeamMembershipDto
@Mapper(componentModel = "spring")
interface CareTeamMembershipMapper {
	fun map(careTeamMembershipDto: CareTeamMembershipDto):CareTeamMembership
	fun map(careTeamMembership: CareTeamMembership):CareTeamMembershipDto
}
