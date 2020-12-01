/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.ArticleDAO
import org.taktik.icure.asynclogic.ArticleLogic
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.entities.Article
import org.taktik.icure.exceptions.DeletionException

@Service
class ArticleLogicImpl(private val articleDAO: ArticleDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Article, ArticleDAO>(sessionLogic), ArticleLogic {

    override suspend fun createArticle(article: Article) = fix(article) { article ->
        articleDAO.create(article)
    }

    override fun deleteArticles(ids: List<String>): Flow<DocIdentifier> {
        try {
            return deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getArticle(articleId: String): Article? {
        return articleDAO.get(articleId)
    }

    override suspend fun modifyArticle(article: Article) = fix(article) { article ->
        articleDAO.save(article)
    }

    override fun getGenericDAO() = articleDAO
}
