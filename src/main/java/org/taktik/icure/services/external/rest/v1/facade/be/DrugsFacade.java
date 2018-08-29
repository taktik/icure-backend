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

package org.taktik.icure.services.external.rest.v1.facade.be;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.drugs.dto.DocId;
import org.taktik.icure.be.drugs.dto.DocPreview;
import org.taktik.icure.be.drugs.dto.FullTextSearchResult;
import org.taktik.icure.be.drugs.dto.IamFullInfos;
import org.taktik.icure.be.drugs.dto.MpExtendedInfos;
import org.taktik.icure.be.drugs.dto.MpFullInfos;
import org.taktik.icure.be.drugs.dto.MpId;
import org.taktik.icure.be.drugs.dto.MpPreview;
import org.taktik.icure.be.drugs.dto.MppId;
import org.taktik.icure.be.drugs.dto.MppInfos;
import org.taktik.icure.be.drugs.dto.MppPreview;
import org.taktik.icure.be.drugs.logic.DrugsLogic;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;

@Component
@Path("/be_drugs")
@Api(tags = { "be_drugs" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class DrugsFacade implements OpenApiFacade {
    protected final Log log = LogFactory.getLog(getClass());

    public DrugsFacade() {
        log.info("Drugs initialised");
    }

    protected DrugsLogic drugsLogic;

    @ApiOperation(
            value = "get Medical Product Packages",
            response = MppPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mpp/find/{searchString}/{lang}")
    @GET
    public List<MppPreview> getMedecinePackages(@PathParam("searchString") String searchString,
                                                @PathParam("lang") String lang,
                                                @ApiParam(value = "Restrict to types", required = false) @QueryParam("types") String types,
                                                @ApiParam(value = "Oddset", required = false) @QueryParam("first") Integer first,
                                                @ApiParam(value = "Page size", required = false) @QueryParam("count") Integer count) {
        return drugsLogic.getMedecinePackages(searchString, lang, types == null ? new ArrayList<>() : Arrays.asList(types.split(",")), first==null?0:first, count==null?1000:count);
    }

    @ApiOperation(
            value = "Get Inn clusters formatted as MppPreview for specified search parameters",
            response = MppPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/inn/find/{searchString}/{lang}")
    @GET
    public List<MppPreview> getInnClusters(@PathParam("searchString") String searchString,
                                           @PathParam("lang") String lang,
                                           @ApiParam(value = "Restrict to types", required = false) @QueryParam("types") String types,
                                           @ApiParam(value = "Oddset", required = false) @QueryParam("first") Integer first,
                                           @ApiParam(value = "Page size", required = false) @QueryParam("count") Integer count) {
        return drugsLogic.getInnClusters("be", searchString, lang, types == null ? new ArrayList<>() : Arrays.asList(types.split(",")), first==null?0:first, count==null?1000:count);
    }

    @ApiOperation(
            value = "get Medical Product Packages by searching in the ingredients",
            response = MppPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mpp/find/byIngredients/{searchString}/{lang}")
    @GET
    public List<MppPreview> getMedecinePackagesFromIngredients(@PathParam("searchString") String searchString,
                                                               @PathParam("lang") String lang,
                                                               @ApiParam(value = "Restrict to types", required = false) @QueryParam("types") String types,
                                                               @ApiParam(value = "Oddset", required = false) @QueryParam("first") Integer first,
                                                               @ApiParam(value = "Page size", required = false) @QueryParam("count") Integer count) {
        return drugsLogic.getMedecinePackagesFromIngredients(searchString, lang, types == null ? new ArrayList<>():Arrays.asList(types.split(",")), first==null?0:first, count==null?1000:count);
    }

    @ApiOperation(
            value = "get Cheap MPP alternatives for a provided MPP",
            response = MppPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/atc/{medecinePackageId}/{lang}/cheapmpps")
    @GET
    public List<MppPreview> getCheapAlternativesBasedOnAtc(@PathParam("medecinePackageId") String medecinePackageId,  @PathParam("lang") String lang) {
        return drugsLogic.getCheapAlternativesBasedOnAtc(new MppId(medecinePackageId, lang));
    }

    @ApiOperation(
            value = "get MPP interactions for a provided MPP and other MPPs",
            response = IamFullInfos.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mpp/{medecinePackageId}/{lang}/interactwith/{otherCnks}")
    @GET
    public List<IamFullInfos> getInteractions(@PathParam("medecinePackageId") String medecinePackageId,  @PathParam("lang") String lang, @PathParam("otherCnks") String otherCnks) {
        return drugsLogic.getInteractions(new MppId(medecinePackageId, lang), otherCnks == null ? new ArrayList<>():Arrays.asList(otherCnks.split(",")));
    }

    @ApiOperation(
            value = "get Cheap MPP alternatives for a provided InnCluster",
            response = MppPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/inn/{innClusterId}/{lang}/cheapmpps")
    @GET
    public List<MppPreview> getCheapAlternativesBasedOnInn(@PathParam("innClusterId") String innClusterId,  @PathParam("lang") String lang) {
        return drugsLogic.getCheapAlternativesBasedOnInn(innClusterId, lang);
    }

    @ApiOperation(
            value = "get MPP infos",
            response = MppInfos.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mpp/{medecinePackageId}/{lang}")
    @GET
    public MppInfos getMppInfos(@PathParam("medecinePackageId") String medecinePackageId,  @PathParam("lang") String lang) {
        return drugsLogic.getInfos(new MppId(medecinePackageId, lang));
    }

    @ApiOperation(
            value = "get MP extended infos",
            response = MpExtendedInfos.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mp/xt/{medecinePackageId}/{lang}")
    @GET
    public MpExtendedInfos getExtentedMpInfosWithPackage(@PathParam("medecinePackageId") String medecinePackageId,  @PathParam("lang") String lang) {
        return drugsLogic.getExtendedMpInfos(new MppId(medecinePackageId, lang));
    }

    @ApiOperation(
            value = "get MP full infos",
            response = MpFullInfos.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mp/full/{medecinePackageId}/{lang}")
    @GET
    public MpFullInfos getFullMpInfosWithPackage(@PathParam("medecinePackageId") String medecinePackageId,  @PathParam("lang") String lang) {
        return drugsLogic.getFullMpInfos(new MppId(medecinePackageId, lang));
    }

    @ApiOperation(
            value = "Full text search on documentation",
            response = FullTextSearchResult.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mpp/find/fullText/{searchString}/{lang}")
    @GET
    public List<FullTextSearchResult> fullTextSearch(@PathParam("searchString") String searchString,
                                                     @PathParam("lang") String lang,
                                                     @ApiParam(value = "Restrict to classes", required = false) @QueryParam("classes") String classes,
                                                     @ApiParam(value = "Restrict to types", required = false) @QueryParam("types") String types,
                                                     @ApiParam(value = "Oddset", required = false) @QueryParam("first") Integer first,
                                                     @ApiParam(value = "Page size", required = false) @QueryParam("count") Integer count) throws IOException {
        return drugsLogic.fullTextSearch(searchString, lang, classes == null ? new ArrayList<>():Arrays.asList(classes.split(",")), types == null ? new ArrayList<>():Arrays.asList(types.split(",")), first==null?0:first, count==null?1000:count);
    }

    @ApiOperation(
            value = "get Children chapters of doc",
            response = DocPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/doc/childrenof/{docId}/{lang}")
    @GET
    public List<DocPreview> getChildrenDocs(@PathParam("docId") String docId,  @PathParam("lang") String lang) {
        return drugsLogic.getChildrenDocs(new DocId(docId,lang));
    }

    @ApiOperation(
            value = "get MPs linked to doc",
            response = MpPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mp/childrenof/{docId}/{lang}")
    @GET
    public List<MpPreview> getChildrenMps(@PathParam("docId") String docId,  @PathParam("lang") String lang) {
        return drugsLogic.getChildrenMps(new DocId(docId,lang));
    }

    @ApiOperation(
            value = "get Doc preview",
            response = DocPreview.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/doc/{docId}/{lang}")
    @GET
    public DocPreview getDocPreview(@PathParam("docId") String docId,  @PathParam("lang") String lang) {
        return drugsLogic.getDocPreview(docId, lang);
    }

    @ApiOperation(
            value = "get Doc preview",
            response = MpPreview.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/mpp/{medecinePackageId}/{lang}/mp")
    @GET
    public MpPreview getMpFromMpp(@PathParam("medecinePackageId") String medecinePackageId,  @PathParam("lang") String lang) {
        return drugsLogic.getMpFromMpp(medecinePackageId, lang);
    }

    @ApiOperation(
            value = "get root chapters of doc",
            response = DocPreview.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/doc/{lang}")
    @GET
    public List<DocPreview> getRootDocs(@PathParam("lang") String lang) {
        return drugsLogic.getRootDocs(lang);
    }

    @ApiOperation(
            value = "get Doc for MP",
            response = DocPreview.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/doc/formp/{medecineId}/{lang}")
    @GET
    public DocPreview getDocOfMp(@PathParam("medecineId") String medecineId,  @PathParam("lang") String lang) {
        return drugsLogic.getDocOfMp(new MpId(medecineId,lang));
    }

    @ApiOperation(
            value = "get parent of doc",
            response = DocPreview.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/doc/parentof/{docId}/{lang}")
    @GET
    public DocPreview getParentDoc(@PathParam("docId") String docId,  @PathParam("lang") String lang) {
        return drugsLogic.getParentDoc(new DocId(docId,lang));
    }

    @Context
    public void setDrugsLogic(DrugsLogic drugsLogic) {
        this.drugsLogic = drugsLogic;
    }

}
