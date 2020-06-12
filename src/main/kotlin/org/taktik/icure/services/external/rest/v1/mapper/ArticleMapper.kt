package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Article
import org.taktik.icure.services.external.rest.v1.dto.ArticleDto
@Mapper(componentModel = "spring", uses = [ContentMapper::class, CodeStubMapper::class, DelegationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ArticleMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true),
            Mapping(target = "set_type", ignore = true)
            )
	fun map(articleDto: ArticleDto):Article
	fun map(article: Article):ArticleDto
}
