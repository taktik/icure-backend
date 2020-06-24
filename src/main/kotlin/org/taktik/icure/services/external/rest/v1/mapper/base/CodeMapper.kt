package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.base.Code
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper

@Mapper(componentModel = "spring", uses = [DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CodeMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
    )
	fun map(codeDto: CodeDto):Code
	fun map(code: Code):CodeDto
}
