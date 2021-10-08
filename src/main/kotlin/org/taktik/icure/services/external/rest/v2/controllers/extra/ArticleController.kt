/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ArticleLogic
import org.taktik.icure.services.external.rest.v2.dto.ArticleDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.ArticleV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController("articleControllerV2")
@RequestMapping("/rest/v2/article")
@Tag(name = "article")
class ArticleController(
        private val articleLogic: ArticleLogic,
        private val articleV2Mapper: ArticleV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Creates a article")
    @PostMapping
    fun createArticle(@RequestBody articleDto: ArticleDto) = mono {
        val article = articleLogic.createArticle(articleV2Mapper.map(articleDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Article creation failed")

        articleV2Mapper.map(article)
    }

    @Operation(summary = "Deletes articles")
    @PostMapping("/delete/batch")
    fun deleteArticles(@RequestBody articleIds: ListOfIdsDto): Flux<DocIdentifier> {
        return articleIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        articleLogic.deleteEntities(HashSet(ids)).injectReactorContext()
                    } catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Gets an article")
    @GetMapping("/{articleId}")
    fun getArticle(@PathVariable articleId: String) = mono {
        val article = articleLogic.getArticle(articleId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article fetching failed")

        articleV2Mapper.map(article)
    }

    @Operation(summary = "Modifies an article")
    @PutMapping
    fun modifyArticle(@RequestBody articleDto: ArticleDto) = mono {
        val article = articleLogic.modifyArticle(articleV2Mapper.map(articleDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
        articleV2Mapper.map(article)
    }

    @Operation(summary = "Gets all articles")
    @GetMapping
    fun getArticles(): Flux<ArticleDto> {
        return articleLogic.getEntities().map { a -> articleV2Mapper.map(a) }.injectReactorContext()
    }
}
