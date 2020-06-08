package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Article
import org.taktik.icure.services.external.rest.v1.dto.ArticleDto
@Mapper(componentModel = "spring")
interface ArticleMapper {
	fun map(articleDto: ArticleDto):Article
	fun map(article: Article):ArticleDto
}
