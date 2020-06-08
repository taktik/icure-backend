package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Principal
import org.taktik.icure.services.external.rest.v1.dto.base.PrincipalDto
@Mapper(componentModel = "spring")
interface PrincipalMapper {
	fun map(principalDto: PrincipalDto):Principal
	fun map(principal: Principal):PrincipalDto
}
