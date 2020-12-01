package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.CareTeamMemberType
import org.taktik.icure.services.external.rest.v1.dto.embed.CareTeamMemberTypeDto
@Mapper(componentModel = "spring")
interface CareTeamMemberTypeMapper {
	fun map(careTeamMemberTypeDto: CareTeamMemberTypeDto):CareTeamMemberType
	fun map(careTeamMemberType: CareTeamMemberType):CareTeamMemberTypeDto
}
