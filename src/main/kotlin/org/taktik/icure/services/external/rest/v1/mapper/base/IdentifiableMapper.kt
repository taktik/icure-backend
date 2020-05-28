package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifiableDto
@Mapper
interface IdentifiableMapper {
	fun map(identifiableDto: IdentifiableDto):Identifiable
	fun map(identifiable: Identifiable):IdentifiableDto
}
