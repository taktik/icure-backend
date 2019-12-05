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

import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.ArticleDAO
import org.taktik.icure.asynclogic.EntityPersister
import org.taktik.icure.entities.Article
import org.taktik.icure.exceptions.DeletionException
import java.net.URI

interface ArticleLogic : EntityPersister<Article, String> {
    suspend fun createArticle(dbInstanceUri: URI, groupId: String, article: Article): Article?
    suspend fun deleteArticles(dbInstanceUri: URI, groupId: String, ids: List<String>): List<String>

    suspend fun getArticle(dbInstanceUri: URI, groupId: String, articleId: String): Article?

    suspend fun modifyArticle(dbInstanceUri: URI, groupId: String, article: Article): Article?
}

@Service
class ArticleLogicImpl(private val articleDAO: ArticleDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Article, ArticleDAO>(sessionLogic), ArticleLogic {

    override suspend fun createArticle(dbInstanceUri: URI, groupId: String, article: Article): Article? {
        return articleDAO.create(dbInstanceUri, groupId, article)
    }

    override suspend fun deleteArticles(dbInstanceUri: URI, groupId: String, ids: List<String>): List<String> {
        try {
            deleteByIds(dbInstanceUri, groupId, ids)
            return ids
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getArticle(dbInstanceUri: URI, groupId: String, articleId: String): Article? {
        return articleDAO.get(dbInstanceUri, groupId, articleId)
    }

    override suspend fun modifyArticle(dbInstanceUri: URI, groupId: String, article: Article): Article? {
        return articleDAO.save(dbInstanceUri, groupId, article)
    }

    override fun getGenericDAO() = articleDAO
}
