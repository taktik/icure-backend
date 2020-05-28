package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Role
import org.taktik.icure.services.external.rest.v1.dto.RoleDto
@Mapper
interface RoleMapper {
	fun map(roleDto: RoleDto):Role
	fun map(role: Role):RoleDto
}
