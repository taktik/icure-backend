package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Group
import org.taktik.icure.services.external.rest.v1.dto.GroupDto
@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface GroupMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(groupDto: GroupDto):Group
	fun map(group: Group):GroupDto
}
