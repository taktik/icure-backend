package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.CareTeamMember
import org.taktik.icure.services.external.rest.v1.dto.embed.CareTeamMemberDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, CareTeamMemberTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CareTeamMemberMapper {
	fun map(careTeamMemberDto: CareTeamMemberDto):CareTeamMember
	fun map(careTeamMember: CareTeamMember):CareTeamMemberDto
}
