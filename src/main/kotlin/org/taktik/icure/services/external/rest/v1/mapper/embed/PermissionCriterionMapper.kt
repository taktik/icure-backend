package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PermissionCriterion
import org.taktik.icure.services.external.rest.v1.dto.embed.PermissionCriterionDto
@Mapper(componentModel = "spring", uses = [UserMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PermissionCriterionMapper {
	fun map(permissionCriterionDto: PermissionCriterionDto):PermissionCriterion
	fun map(permissionCriterion: PermissionCriterion):PermissionCriterionDto
}
