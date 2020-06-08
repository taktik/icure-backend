package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.HasCodes
import org.taktik.icure.services.external.rest.v1.dto.base.HasCodesDto
@Mapper(componentModel = "spring")
interface HasCodesMapper {
	fun map(hasCodesDto: HasCodesDto):HasCodes
	fun map(hasCodes: HasCodes):HasCodesDto
}
