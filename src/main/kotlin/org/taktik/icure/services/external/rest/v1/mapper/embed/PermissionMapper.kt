package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Permission
import org.taktik.icure.services.external.rest.v1.dto.embed.PermissionDto
@Mapper(componentModel = "spring", uses = [PermissionCriterionMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PermissionMapper {
	fun map(permissionDto: PermissionDto):Permission
	fun map(permission: Permission):PermissionDto
}
