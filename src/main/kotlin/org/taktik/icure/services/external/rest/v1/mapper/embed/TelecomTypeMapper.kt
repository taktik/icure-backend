package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.services.external.rest.v1.dto.embed.TelecomTypeDto
@Mapper(componentModel = "spring")
interface TelecomTypeMapper {
	fun map(telecomTypeDto: TelecomTypeDto):TelecomType
	fun map(telecomType: TelecomType):TelecomTypeDto
}
