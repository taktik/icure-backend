package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Role
import org.taktik.icure.services.external.rest.v1.dto.RoleDto
import org.taktik.icure.services.external.rest.v1.mapper.base.PropertyStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PermissionMapper

@Mapper(componentModel = "spring", uses = [PropertyStubMapper::class, PermissionMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface RoleMapper {
    @Mappings(
            Mapping(target = "parents", ignore = true),
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(roleDto: RoleDto):Role
	fun map(role: Role):RoleDto
}
