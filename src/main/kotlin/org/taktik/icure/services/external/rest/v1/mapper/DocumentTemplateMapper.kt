package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.DocumentTemplate
import org.taktik.icure.services.external.rest.v1.dto.DocumentTemplateDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.ReportVersionMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentGroupMapper

@Mapper(componentModel = "spring", uses = [ReportVersionMapper::class, DocumentGroupMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DocumentTemplateMapper {
    @Mappings(
            Mapping(target = "isAttachmentDirty", ignore = true),
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(documentTemplateDto: DocumentTemplateDto):DocumentTemplate
	fun map(documentTemplate: DocumentTemplate):DocumentTemplateDto
}
