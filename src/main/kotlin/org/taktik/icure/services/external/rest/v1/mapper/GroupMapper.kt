package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Group
import org.taktik.icure.services.external.rest.v1.dto.GroupDto
@Mapper
interface GroupMapper {
	fun map(groupDto: GroupDto):Group
	fun map(group: Group):GroupDto
}
