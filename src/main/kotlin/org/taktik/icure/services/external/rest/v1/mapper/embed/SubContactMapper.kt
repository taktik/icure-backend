package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.services.external.rest.v1.dto.embed.SubContactDto
@Mapper(componentModel = "spring", uses = [DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SubContactMapper {
	fun map(subContactDto: SubContactDto):SubContact
	fun map(subContact: SubContact):SubContactDto
}
