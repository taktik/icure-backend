package org.taktik.icure.services.external.rest.v2.mapper.security

import org.mapstruct.Mapper
import org.taktik.icure.entities.security.PermissionType
import org.taktik.icure.services.external.rest.v2.dto.security.PermissionTypeDto

@Mapper(componentModel = "spring")
interface PermissionTypeV2Mapper {
	fun map(permissionsTypeDto: PermissionTypeDto): PermissionType
	fun map(permissionsType: PermissionType): PermissionTypeDto
}
