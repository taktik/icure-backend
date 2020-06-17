package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.SchoolingInfo
import org.taktik.icure.services.external.rest.v1.dto.embed.SchoolingInfoDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SchoolingInfoMapper {
	fun map(schoolingInfoDto: SchoolingInfoDto):SchoolingInfo
	fun map(schoolingInfo: SchoolingInfo):SchoolingInfoDto
}
