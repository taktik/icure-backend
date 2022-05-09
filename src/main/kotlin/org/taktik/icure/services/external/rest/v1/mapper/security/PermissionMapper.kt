/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.mapper.security

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.security.Permission
import org.taktik.icure.services.external.rest.v1.dto.security.PermissionDto

@Mapper(componentModel = "spring", uses = [PermissionItemMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PermissionMapper {
	fun map(alwaysPermissionDto: PermissionDto): Permission
	fun map(permission: Permission): PermissionDto
}
