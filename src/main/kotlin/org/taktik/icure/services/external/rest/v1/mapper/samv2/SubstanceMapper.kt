/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.Substance
import org.taktik.icure.services.external.rest.v1.dto.samv2.SubstanceDto
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.SamTextMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.StandardSubstanceMapper

@Mapper(componentModel = "spring", uses = [StandardSubstanceMapper::class, SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SubstanceMapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(substanceDto: SubstanceDto): Substance
	fun map(substance: Substance): SubstanceDto
}
