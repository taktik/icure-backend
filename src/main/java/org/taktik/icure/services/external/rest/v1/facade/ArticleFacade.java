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

package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.Article;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.ArticleLogic;
import org.taktik.icure.services.external.rest.v1.dto.ArticleDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/article")
@Api(tags = {"article"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class ArticleFacade implements OpenApiFacade {

    private static Logger logger = LoggerFactory.getLogger(ArticleFacade.class);

    private ArticleLogic articleLogic;
    private MapperFacade mapper;

    @ApiOperation(response = ArticleDto.class, value = "Creates a article")
    @POST
    public Response createArticle(ArticleDto articleDto) {
        Response response;

        if (articleDto == null) {
            response = ResponseUtils.badRequest("Cannot create article: supplied ArticleDto is null");

        } else {
            Article article = articleLogic.createArticle(mapper.map(articleDto, Article.class));
            if (article != null) {
                response = ResponseUtils.ok(mapper.map(article, ArticleDto.class));

            } else {
                response = ResponseUtils.internalServerError("Article creation failed");
            }
        }

        return response;
    }

    @ApiOperation(value = "Deletes an article")
    @DELETE
    @Path("/{articleIds}")
    public Response deleteArticle(@PathParam("articleIds") String articleIds) throws DeletionException {
        Response response;

        if (articleIds == null) {
            response = ResponseUtils.badRequest("Cannot delete access log: supplied articleIds is null");

        } else {
            List<String> deletedArticleIds = articleLogic.deleteArticles(Arrays.asList(articleIds.split(",")));
            if (deletedArticleIds != null) {
                response = Response.ok().entity(deletedArticleIds).build();
            } else {
                return Response.status(500).type("text/plain").entity("Article deletion failed.").build();
            }
        }

        return response;
    }

    @ApiOperation(response = ArticleDto.class, value = "Gets an article")
    @GET
    @Path("/{articleId}")
    public Response getArticle(@PathParam("articleId") String articleId) {
        Response response;

        if (articleId == null) {
            response = ResponseUtils.badRequest("Cannot get access log: supplied articleId is null");

        } else {
            Article article = articleLogic.getArticle(articleId);
            if (article != null) {
                response = ResponseUtils.ok(mapper.map(article, ArticleDto.class));

            } else {
                response = ResponseUtils.internalServerError("Article fetching failed");
            }
        }
        return response;
    }


    @ApiOperation(response = ArticleDto.class, value = "Modifies an article")
    @PUT
    public Response modifyArticle(ArticleDto articleDto) {
        Response response;

        if (articleDto == null) {
            response = ResponseUtils.badRequest("Cannot modify article: supplied articleDto is null");

        } else {
            Article article = articleLogic.modifyArticle(mapper.map(articleDto, Article.class));
            if (article != null) {
                response = ResponseUtils.ok(mapper.map(article, ArticleDto.class));

            } else {
                response = ResponseUtils.internalServerError("Article modification failed");
            }
        }

        return response;
    }

    @Context
    public void setarticleLogic(ArticleLogic articleLogic) {
        this.articleLogic = articleLogic;
    }

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @ExceptionHandler(Exception.class)
    Response exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseUtils.internalServerError(e.getMessage());
    }

    @ApiOperation(
        value = "Gets all articles",
        response = ArticleDto.class,
        responseContainer = "Array",
        httpMethod = "GET",
        notes = ""
    )
    @GET
    public Response getArticles() {
        Response response;
        List<Article> articles = articleLogic.getAllEntities();
        if (articles != null) {
            response = Response.ok().entity(articles.stream().map(c -> mapper.map(c, ArticleDto.class)).collect(Collectors.toList())).build();

        } else {
            response = ResponseUtils.internalServerError("ArticleTypes fetching failed");
        }
        return response;
    }

}
