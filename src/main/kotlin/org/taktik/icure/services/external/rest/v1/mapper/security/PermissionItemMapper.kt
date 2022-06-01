package org.taktik.icure.services.external.rest.v1.mapper.security

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.security.AlwaysPermissionItem
import org.taktik.icure.entities.security.PermissionItem
import org.taktik.icure.services.external.rest.v1.dto.security.AlwaysPermissionItemDto
import org.taktik.icure.services.external.rest.v1.dto.security.PermissionItemDto

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class PermissionItemMapper {
	abstract fun map(alwaysPermissionItemDto: AlwaysPermissionItemDto): AlwaysPermissionItem
	abstract fun map(alwaysPermissionItem: AlwaysPermissionItem): AlwaysPermissionItemDto

	fun map(permissionItemDto: PermissionItemDto): PermissionItem {
		return when (permissionItemDto) {
			is AlwaysPermissionItemDto -> map(permissionItemDto)
			else -> throw IllegalArgumentException("Unsupported permission class")
		}
	}

	fun map(permissionItem: PermissionItem): PermissionItemDto {
		return when (permissionItem) {
			is AlwaysPermissionItem -> map(permissionItem)
			else -> throw IllegalArgumentException("Unsupported filter class")
		}
	}
}
