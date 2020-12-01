package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.AmpStatus
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AmpStatusDto
@Mapper(componentModel = "spring")
interface AmpStatusMapper {
	fun map(ampStatusDto: AmpStatusDto):AmpStatus
	fun map(ampStatus: AmpStatus):AmpStatusDto
}
