package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.DmppCodeType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.DmppCodeTypeDto
@Mapper(componentModel = "spring")
interface DmppCodeTypeMapper {
	fun map(dmppCodeTypeDto: DmppCodeTypeDto):DmppCodeType
	fun map(dmppCodeType: DmppCodeType):DmppCodeTypeDto
}
