package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.CareTeamMember
import org.taktik.icure.services.external.rest.v1.dto.embed.CareTeamMemberDto
@Mapper(componentModel = "spring")
interface CareTeamMemberMapper {
	fun map(careTeamMemberDto: CareTeamMemberDto):CareTeamMember
	fun map(careTeamMember: CareTeamMember):CareTeamMemberDto
}
