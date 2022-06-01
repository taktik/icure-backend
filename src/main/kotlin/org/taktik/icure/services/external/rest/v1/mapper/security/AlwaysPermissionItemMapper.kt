package org.taktik.icure.services.external.rest.v1.mapper.security

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.security.AlwaysPermissionItem
import org.taktik.icure.services.external.rest.v1.dto.security.AlwaysPermissionItemDto

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AlwaysPermissionItemMapper {
	fun map(alwaysPermissionItemDto: AlwaysPermissionItemDto): AlwaysPermissionItem
	fun map(alwaysPermissionItem: AlwaysPermissionItem): AlwaysPermissionItemDto
}
