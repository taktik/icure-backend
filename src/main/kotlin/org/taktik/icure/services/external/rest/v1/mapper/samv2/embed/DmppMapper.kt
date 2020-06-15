package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Dmpp
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.DmppDto
@Mapper(componentModel = "spring", uses = [DmppCodeTypeMapper::class, ReimbursementMapper::class, DeliveryEnvironmentMapper::class, DmppCodeTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DmppMapper {
	fun map(dmppDto: DmppDto):Dmpp
	fun map(dmpp: Dmpp):DmppDto
}
