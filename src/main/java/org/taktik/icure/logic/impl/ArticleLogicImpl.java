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

package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.ArticleDAO;
import org.taktik.icure.entities.Article;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.ArticleLogic;
import org.taktik.icure.logic.ICureSessionLogic;

import java.util.List;

@Service
public class ArticleLogicImpl extends GenericLogicImpl<Article, ArticleDAO> implements ArticleLogic {

    private ArticleDAO articleDAO;
    private ICureSessionLogic sessionLogic;

    @Override
    public Article createArticle(Article article) {
        return articleDAO.create(article);
    }

    @Override
    public List<String> deleteArticles(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
    public Article getArticle(String articleId) {
        return articleDAO.get(articleId);
    }

    @Override
    public Article modifyArticle(Article article) {
        return articleDAO.save(article);
    }

    @Autowired
    public void setArticleDAO(ArticleDAO articleDAO) {
        this.articleDAO = articleDAO;
    }

    @Autowired
    public void setSessionLogic(ICureSessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Override
    protected ArticleDAO getGenericDAO() {
        return articleDAO;
    }
}
