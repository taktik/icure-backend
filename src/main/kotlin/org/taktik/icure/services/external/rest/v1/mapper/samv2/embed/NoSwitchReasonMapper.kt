package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.NoSwitchReason
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.NoSwitchReasonDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface NoSwitchReasonMapper {
	fun map(noSwitchReasonDto: NoSwitchReasonDto):NoSwitchReason
	fun map(noSwitchReason: NoSwitchReason):NoSwitchReasonDto
}
