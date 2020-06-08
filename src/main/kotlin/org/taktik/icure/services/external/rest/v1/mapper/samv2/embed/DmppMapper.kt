package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Dmpp
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.DmppDto
@Mapper(componentModel = "spring")
interface DmppMapper {
	fun map(dmppDto: DmppDto):Dmpp
	fun map(dmpp: Dmpp):DmppDto
}
