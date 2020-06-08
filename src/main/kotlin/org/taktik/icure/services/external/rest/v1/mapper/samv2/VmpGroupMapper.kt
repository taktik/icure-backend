package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.services.external.rest.v1.dto.samv2.VmpGroupDto
@Mapper(componentModel = "spring")
interface VmpGroupMapper {
	fun map(vmpGroupDto: VmpGroupDto):VmpGroup
	fun map(vmpGroup: VmpGroup):VmpGroupDto
}
