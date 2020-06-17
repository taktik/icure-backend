package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PermissionCriterion
import org.taktik.icure.services.external.rest.v1.dto.embed.PermissionCriterionDto
import org.taktik.icure.services.external.rest.v1.mapper.UserMapper

@Mapper(componentModel = "spring", uses = [UserMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PermissionCriterionMapper {
	fun map(permissionCriterionDto: PermissionCriterionDto):PermissionCriterion
	fun map(permissionCriterion: PermissionCriterion):PermissionCriterionDto
}
