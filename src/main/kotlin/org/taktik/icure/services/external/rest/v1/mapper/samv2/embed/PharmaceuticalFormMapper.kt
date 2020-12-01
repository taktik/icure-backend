package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.embed.PharmaceuticalForm
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.PharmaceuticalFormDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [SamTextMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PharmaceuticalFormMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
    )
	fun map(pharmaceuticalFormDto: PharmaceuticalFormDto):PharmaceuticalForm
	fun map(pharmaceuticalForm: PharmaceuticalForm):PharmaceuticalFormDto
}
