package org.taktik.icure.asynclogic

import org.taktik.icure.entities.Article

interface ArticleLogic : EntityPersister<Article, String> {
    suspend fun createArticle(article: Article): Article?
    suspend fun deleteArticles(ids: List<String>): List<String>

    suspend fun getArticle(articleId: String): Article?

    suspend fun modifyArticle(article: Article): Article?
}
