package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.PackagingType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.PackagingTypeDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PackagingTypeMapper {
	fun map(packagingTypeDto: PackagingTypeDto):PackagingType
	fun map(packagingType: PackagingType):PackagingTypeDto
}
