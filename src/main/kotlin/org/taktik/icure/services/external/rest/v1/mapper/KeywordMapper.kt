package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Keyword
import org.taktik.icure.services.external.rest.v1.dto.KeywordDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.KeywordSubwordMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class, KeywordSubwordMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface KeywordMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(keywordDto: KeywordDto):Keyword
	fun map(keyword: Keyword):KeywordDto
}
