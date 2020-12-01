package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.RegimenItem
import org.taktik.icure.services.external.rest.v1.dto.embed.RegimenItemDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface RegimenItemMapper {
	fun map(regimenItemDto: RegimenItemDto):RegimenItem
	fun map(regimenItem: RegimenItem):RegimenItemDto
}
