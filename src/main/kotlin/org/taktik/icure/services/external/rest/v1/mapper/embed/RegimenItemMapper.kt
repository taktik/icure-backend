package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.RegimenItem
import org.taktik.icure.services.external.rest.v1.dto.embed.RegimenItemDto
@Mapper(componentModel = "spring", uses = [CodeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface RegimenItemMapper {
	fun map(regimenItemDto: RegimenItemDto):RegimenItem
	fun map(regimenItem: RegimenItem):RegimenItemDto
}
