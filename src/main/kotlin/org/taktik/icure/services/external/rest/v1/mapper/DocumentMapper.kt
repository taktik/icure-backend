package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Document
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentLocationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentStatusMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentTypeMapper

@Mapper(componentModel = "spring", uses = [DocumentTypeMapper::class, DocumentLocationMapper::class, CodeStubMapper::class, DelegationMapper::class, DocumentStatusMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DocumentMapper {
    @Mappings(
            Mapping(target = "isAttachmentDirty", ignore = true),
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(documentDto: DocumentDto):Document
	fun map(document: Document):DocumentDto
}
