/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.PharmaceuticalForm
import org.taktik.icure.services.external.rest.v1.dto.samv2.PharmaceuticalFormDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.SamTextMapper

@Mapper(componentModel = "spring", uses = [SamTextMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PharmaceuticalFormMapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(pharmaceuticalFormDto: PharmaceuticalFormDto): PharmaceuticalForm
	fun map(pharmaceuticalForm: PharmaceuticalForm): PharmaceuticalFormDto
}
