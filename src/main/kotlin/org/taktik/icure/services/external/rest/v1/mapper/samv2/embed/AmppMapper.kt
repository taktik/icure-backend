package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Ampp
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.AmppDto
@Mapper(componentModel = "spring", uses = [CompanyMapper::class, SupplyProblemMapper::class, AmpStatusMapper::class, DmppMapper::class, SamTextMapper::class, AtcMapper::class, CommercializationMapper::class, AmppComponentMapper::class, QuantityMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AmppMapper {
	fun map(amppDto: AmppDto):Ampp
	fun map(ampp: Ampp):AmppDto
}
