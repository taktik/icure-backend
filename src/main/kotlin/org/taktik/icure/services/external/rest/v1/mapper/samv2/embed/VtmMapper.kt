package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Vtm
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VtmDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface VtmMapper {
	fun map(vtmDto: VtmDto):Vtm
	fun map(vtm: Vtm):VtmDto
}
