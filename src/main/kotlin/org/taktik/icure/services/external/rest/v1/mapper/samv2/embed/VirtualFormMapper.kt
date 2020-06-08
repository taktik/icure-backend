package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.VirtualForm
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VirtualFormDto
@Mapper(componentModel = "spring")
interface VirtualFormMapper {
	fun map(virtualFormDto: VirtualFormDto):VirtualForm
	fun map(virtualForm: VirtualForm):VirtualFormDto
}
