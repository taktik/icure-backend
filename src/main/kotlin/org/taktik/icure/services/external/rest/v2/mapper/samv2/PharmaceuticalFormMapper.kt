/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v2.mapper.samv2

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.PharmaceuticalForm
import org.taktik.icure.services.external.rest.v2.dto.samv2.PharmaceuticalFormDto
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.samv2.embed.SamTextV2Mapper

@Mapper(componentModel = "spring", uses = [SamTextV2Mapper::class, CodeStubV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PharmaceuticalFormV2Mapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(pharmaceuticalFormDto: PharmaceuticalFormDto): PharmaceuticalForm
	fun map(pharmaceuticalForm: PharmaceuticalForm): PharmaceuticalFormDto
}
