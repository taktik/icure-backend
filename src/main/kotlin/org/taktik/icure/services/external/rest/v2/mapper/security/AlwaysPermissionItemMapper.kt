package org.taktik.icure.services.external.rest.v2.mapper.security

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.security.AlwaysPermissionItem
import org.taktik.icure.services.external.rest.v2.dto.security.AlwaysPermissionItemDto

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AlwaysPermissionItemV2Mapper {
	fun map(alwaysPermissionItemDto: AlwaysPermissionItemDto): AlwaysPermissionItem
	fun map(alwaysPermissionItem: AlwaysPermissionItem): AlwaysPermissionItemDto
}
