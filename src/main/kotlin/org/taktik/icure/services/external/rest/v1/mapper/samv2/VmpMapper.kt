package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.services.external.rest.v1.dto.samv2.VmpDto
@Mapper(componentModel = "spring")
interface VmpMapper {
	fun map(vmpDto: VmpDto):Vmp
	fun map(vmp: Vmp):VmpDto
}
