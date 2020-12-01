package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.CareTeamMemberMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.EpisodeMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.LateralityMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PlanOfActionMapper

@Mapper(componentModel = "spring", uses = [LateralityMapper::class, PlanOfActionMapper::class, EpisodeMapper::class, CodeStubMapper::class, DelegationMapper::class, CareTeamMemberMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface HealthElementMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(healthElementDto: HealthElementDto):HealthElement
	fun map(healthElement: HealthElement):HealthElementDto
}
