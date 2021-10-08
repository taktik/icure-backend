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

package org.taktik.icure.services.external.rest.v1.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ArticleLogic
import org.taktik.icure.services.external.rest.v1.dto.ArticleDto
import org.taktik.icure.services.external.rest.v1.mapper.ArticleMapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/article")
@Tag(name = "article")
class ArticleController(
        private val articleLogic: ArticleLogic,
        private val articleMapper: ArticleMapper
) {

    @Operation(summary = "Creates a article")
    @PostMapping
    fun createArticle(@RequestBody articleDto: ArticleDto) = mono {
        val article = articleLogic.createArticle(articleMapper.map(articleDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Article creation failed")

        articleMapper.map(article)
    }

    @Operation(summary = "Deletes an article")
    @DeleteMapping("/{articleIds}")
    fun deleteArticle(@PathVariable articleIds: String): Flux<DocIdentifier> {
        return articleLogic.deleteArticles(articleIds.split(',')).injectReactorContext()
    }

    @Operation(summary = "Gets an article")
    @GetMapping("/{articleId}")
    fun getArticle(@PathVariable articleId: String) = mono {
        val article = articleLogic.getArticle(articleId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article fetching failed")

        articleMapper.map(article)
    }

    @Operation(summary = "Modifies an article")
    @PutMapping
    fun modifyArticle(@RequestBody articleDto: ArticleDto) = mono {
        val article = articleLogic.modifyArticle(articleMapper.map(articleDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
        articleMapper.map(article)
    }

    @Operation(summary = "Gets all articles")
    @GetMapping
    fun getArticles(): Flux<ArticleDto> {
        return articleLogic.getEntities().map { a -> articleMapper.map(a) }.injectReactorContext()
    }
}
