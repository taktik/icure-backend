package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.Article
import org.taktik.icure.logic.ArticleLogic
import org.taktik.icure.services.external.rest.v1.dto.ArticleDto

@RestController
@RequestMapping("/rest/v1/article")
@Api(tags = ["article"])
class ArticleController(private val mapper: MapperFacade,
                        private val articleLogic: ArticleLogic) {

    @ApiOperation(nickname = "createArticle", value = "Creates a article")
    @PostMapping
    fun createArticle(@RequestBody articleDto: ArticleDto): ArticleDto {
        val article = articleLogic.createArticle(mapper.map(articleDto, Article::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Article creation failed")

        return mapper.map(article, ArticleDto::class.java)
    }

    @ApiOperation(nickname = "deleteArticle", value = "Deletes an article")
    @DeleteMapping("/{articleIds}")
    fun deleteArticle(@PathVariable articleIds: String): List<String> {
        return articleLogic.deleteArticles(articleIds.split(','))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Article deletion failed")
    }

    @ApiOperation(nickname = "getArticle", value = "Gets an article")
    @GetMapping("/{articleId}")
    fun getArticle(@PathVariable articleId: String): ArticleDto {
        val article = articleLogic.getArticle(articleId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article fetching failed")

        return mapper.map(article, ArticleDto::class.java)
    }

    @ApiOperation(nickname = "modifyArticle", value = "Modifies an article")
    @PutMapping
    fun modifyArticle(@RequestBody articleDto: ArticleDto): ArticleDto {
        val article = articleLogic.modifyArticle(mapper.map(articleDto, Article::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
        return mapper.map(article, ArticleDto::class.java)
    }

    @ApiOperation(nickname = "getArticles", value = "Gets all articles")
    @GetMapping
    fun getArticles(): List<ArticleDto> {
        val articles = articleLogic.allEntities
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ArticleTypes fetching failed")
        return articles.map { a -> mapper.map(a, ArticleDto::class.java) }
    }
}
