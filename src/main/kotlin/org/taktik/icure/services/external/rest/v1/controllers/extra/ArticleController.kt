package org.taktik.icure.services.external.rest.v1.controllers.extra

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ArticleLogic
import org.taktik.icure.entities.Article
import org.taktik.icure.services.external.rest.v1.dto.ArticleDto
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/article")
@Tag(name = "article")
class ArticleController(private val mapper: MapperFacade,
                        private val articleLogic: ArticleLogic) {

    @Operation(summary = "Creates a article")
    @PostMapping
    fun createArticle(@RequestBody articleDto: ArticleDto) = mono {
        val article = articleLogic.createArticle(mapper.map(articleDto, Article::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Article creation failed")

        mapper.map(article, ArticleDto::class.java)
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

        mapper.map(article, ArticleDto::class.java)
    }

    @Operation(summary = "Modifies an article")
    @PutMapping
    fun modifyArticle(@RequestBody articleDto: ArticleDto) = mono {
        val article = articleLogic.modifyArticle(mapper.map(articleDto, Article::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
        mapper.map(article, ArticleDto::class.java)
    }

    @Operation(summary = "Gets all articles")
    @GetMapping
    fun getArticles(): Flux<ArticleDto> {
        return articleLogic.getAllEntities().map { a -> mapper.map(a, ArticleDto::class.java) }.injectReactorContext()
    }
}
