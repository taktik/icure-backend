package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.services.external.rest.v1.dto.ClassificationTemplateDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ClassificationTemplateMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(classificationTemplateDto: ClassificationTemplateDto):ClassificationTemplate
	fun map(classificationTemplate: ClassificationTemplate):ClassificationTemplateDto
}
