package org.taktik.icure.services.external.rest.v1.mapper.security

import org.mapstruct.Mapper
import org.taktik.icure.entities.security.PermissionType
import org.taktik.icure.services.external.rest.v1.dto.security.PermissionTypeDto

@Mapper(componentModel = "spring")
interface PermissionTypeMapper {
    fun map(permissionsTypeDto: PermissionTypeDto): PermissionType
    fun map(permissionsType: PermissionType): PermissionTypeDto
}
