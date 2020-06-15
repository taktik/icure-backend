package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.PharmaceuticalForm
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.PharmaceuticalFormDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [SamTextMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PharmaceuticalFormMapper {
	fun map(pharmaceuticalFormDto: PharmaceuticalFormDto):PharmaceuticalForm
	fun map(pharmaceuticalForm: PharmaceuticalForm):PharmaceuticalFormDto
}
