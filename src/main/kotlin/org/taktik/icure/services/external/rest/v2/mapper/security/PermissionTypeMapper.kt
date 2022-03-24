package org.taktik.icure.services.external.rest.v2.mapper.security

import org.mapstruct.Mapper
import org.taktik.icure.entities.security.PermissionType
import org.taktik.icure.services.external.rest.v2.dto.security.PermissionTypeDto

import org.mapstruct.InjectionStrategy
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PermissionTypeV2Mapper {
    fun map(permissionsTypeDto: PermissionTypeDto): PermissionType
    fun map(permissionsType: PermissionType): PermissionTypeDto
}
