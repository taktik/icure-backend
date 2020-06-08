package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Named
import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
@Mapper(componentModel = "spring")
interface NamedMapper {
	fun map(namedDto: NamedDto):Named
	fun map(named: Named):NamedDto
}
