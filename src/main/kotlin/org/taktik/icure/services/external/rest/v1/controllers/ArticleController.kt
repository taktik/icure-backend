//package org.taktik.icure.services.external.rest.v1.controllers
//
//import io.swagger.annotations.Api
//import io.swagger.annotations.ApiOperation
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.reactive.asFlow
//import kotlinx.coroutines.reactive.awaitSingle
//import kotlinx.coroutines.reactor.asCoroutineContext
//import kotlinx.coroutines.reactor.asFlux
//import ma.glasnost.orika.MapperFacade
//import org.springframework.http.HttpStatus
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.context.ReactiveSecurityContextHolder
//import org.springframework.security.core.context.SecurityContext
//import org.springframework.security.core.userdetails.User
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.server.ResponseStatusException
//import org.taktik.icure.asynclogic.impl.ArticleLogic
//import org.taktik.icure.asynclogic.impl.AsyncSessionLogic
//import org.taktik.icure.entities.Article
//import org.taktik.icure.security.database.DatabaseUserDetails
//import org.taktik.icure.services.external.rest.v1.dto.ArticleDto
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import java.net.URI
//import kotlin.coroutines.coroutineContext
//
//@RestController
//@RequestMapping("/rest/v1/article")
//@Api(tags = ["article"])
//class ArticleController(private val mapper: MapperFacade,
//                        private val articleLogic: ArticleLogic,
//                        private val sessionLogic: AsyncSessionLogic) {
//
//    @ApiOperation(nickname = "createArticle", value = "Creates a article")
//    @PostMapping
//    suspend fun createArticle(@RequestBody articleDto: ArticleDto): ArticleDto {
//        val article = articleLogic.createArticle(mapper.map(articleDto, Article::class.java))
//                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Article creation failed")
//
//        return mapper.map(article, ArticleDto::class.java)
//    }
//
//    @ApiOperation(nickname = "deleteArticle", value = "Deletes an article")
//    @DeleteMapping("/{articleIds}")
//    suspend fun deleteArticle(@PathVariable articleIds: String): List<String> {
//        return articleLogic.deleteArticles(articleIds.split(','))
//    }
//
//    @ApiOperation(nickname = "getArticle", value = "Gets an article")
//    @GetMapping("/{articleId}")
//    suspend fun getArticle(@PathVariable articleId: String): ArticleDto {
//        val article = articleLogic.getArticle(articleId)
//                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article fetching failed")
//
//        return mapper.map(article, ArticleDto::class.java)
//    }
//
//    @ApiOperation(nickname = "modifyArticle", value = "Modifies an article")
//    @PutMapping
//    suspend fun modifyArticle(@RequestBody articleDto: ArticleDto): ArticleDto {
//        val article = articleLogic.modifyArticle(mapper.map(articleDto, Article::class.java))
//                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog modification failed")
//        return mapper.map(article, ArticleDto::class.java)
//    }
//
//    @ApiOperation(nickname = "getArticles", value = "Gets all articles")
//    @GetMapping
//    suspend fun getArticles(): Flux<ArticleDto> { //Flow<ArticleDto> {
//        return articleLogic.getAllEntities().map { a -> mapper.map(a, ArticleDto::class.java) }
//    }
//
//    @GetMapping("/flow")
//    fun flow() = injectReactorContext(
//            flow {
//                val ctx = sessionLogic.getCurrentSessionContext().awaitSingle()
//                val ctx2 = sessionLogic.getCurrentSessionContext().awaitSingle()
//                val uri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri()!! }.awaitSingle()
//                val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId()!! }.awaitSingle()
//                emit(mapOf("groupId" to ((ctx?.getAuthentication()?.principal as? DatabaseUserDetails)?.groupId ?: "<NONE>"),
//                        "groupId2" to ((ctx2?.getAuthentication()?.principal as? DatabaseUserDetails)?.groupId ?: "<NONE>"),
//                        "groupId3" to (groupId ?: "<NONE>"),
//                        "uri" to (uri ?: "<NONE>")))
//            }
//    )
//
//    @GetMapping("/test0")
//    suspend fun getArticles2(): Flux<String>? {
//        return sessionLogic.getCurrentSessionContext().map {
//            val group = it.getGroupId()!!
//            flow { //Flow<ArticleDto> {
//                emit(group)
//            }.asFlux()
//        }.block()
//    }
//
//
//    @GetMapping("/test1")
//    suspend fun getArticlesTest1(): String { //Flow<ArticleDto> {
//        val url = sessionLogic.getCurrentSessionContext().map { it.getGroupId()!! }.flux().asFlow().single() // OK
//        return url
//    }
//
//    @GetMapping("/test2")
//    suspend fun getArticlesTest2(): String { //Flow<ArticleDto> {
//        val url = sessionLogic.getCurrentSessionContext().map { it.getGroupId()!! }.block() ?: "it was null" // it was null
//        return url
//    }
//
//    @GetMapping("/test3")
//    suspend fun getArticlesTest3(): Flux<String> { //Flow<ArticleDto> {
//        val url = sessionLogic.getCurrentSessionContext().map { it.getGroupId()!! }.flux() // OK
//        return url
//    }
//
//    @GetMapping("/test4")
//    suspend fun getArticlesTest4(): List<String> { //Flow<ArticleDto> {
//        val url = sessionLogic.getCurrentSessionContext().map { it.getGroupId()!! }.flux().asFlow().toList() // OK
//        return url
//    }
//
//    @GetMapping("/test5")
//    suspend fun getArticlesTest5(): Mono<SecurityContext> { //Flow<ArticleDto> {
//        return ReactiveSecurityContextHolder.getContext() // not acceptable
//    }
//
//    @GetMapping("/test6")
//    suspend fun getArticlesTest6(): SecurityContext { //Flow<ArticleDto> {
//        return ReactiveSecurityContextHolder.getContext().flux().asFlow().single() // OK
//    }
//}
