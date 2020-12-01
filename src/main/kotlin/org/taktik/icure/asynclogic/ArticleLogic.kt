package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.Article

interface ArticleLogic : EntityPersister<Article, String> {
    suspend fun createArticle(article: Article): Article?
    fun deleteArticles(ids: List<String>): Flow<DocIdentifier>

    suspend fun getArticle(articleId: String): Article?

    suspend fun modifyArticle(article: Article): Article?
}
