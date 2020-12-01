package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.VmpComponent
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VmpComponentDto
@Mapper(componentModel = "spring", uses = [RouteOfAdministrationMapper::class, SamTextMapper::class, VirtualIngredientMapper::class, VirtualFormMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface VmpComponentMapper {
	fun map(vmpComponentDto: VmpComponentDto):VmpComponent
	fun map(vmpComponent: VmpComponent):VmpComponentDto
}
