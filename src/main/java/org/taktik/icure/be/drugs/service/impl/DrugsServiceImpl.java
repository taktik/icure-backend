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


package org.taktik.icure.be.drugs.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.drugs.dto.DocId;
import org.taktik.icure.be.drugs.dto.DocPreview;
import org.taktik.icure.be.drugs.dto.FullTextSearchResult;
import org.taktik.icure.be.drugs.dto.MpId;
import org.taktik.icure.be.drugs.dto.MppId;
import org.taktik.icure.be.drugs.dto.IamFullInfos;
import org.taktik.icure.be.drugs.dto.MpExtendedInfos;
import org.taktik.icure.be.drugs.dto.MpFullInfos;
import org.taktik.icure.be.drugs.dto.MpPreview;
import org.taktik.icure.be.drugs.dto.MppInfos;
import org.taktik.icure.be.drugs.dto.MppPreview;
import org.taktik.icure.be.drugs.logic.DrugsLogic;
import org.taktik.icure.be.drugs.service.DrugsService;

public class DrugsServiceImpl implements DrugsService {
    protected DrugsLogic drugsLogic;

    public List<MppPreview> getMedecinePackages(String searchString, String lang, List<String> types, int first, int count) {
        return drugsLogic.getMedecinePackages(searchString, lang, types, first, count);
    }

    @Override
    public List<MppPreview> getInnClusters(String searchString, String lang, List<String> types, int first, int count) {
        return drugsLogic.getInnClusters("be", searchString, lang, types, first, count);
    }

    public List<MppPreview> getMedecinePackagesFromIngredients(String searchString, String lang, List<String> types, int first, int count) {
        List<MppPreview> medecinePackagesFromIngredients = drugsLogic.getMedecinePackagesFromIngredients(searchString, lang, types, first, count);
        return medecinePackagesFromIngredients;
    }

    public MppInfos getMppInfos(MppId medecinePackageID) {
        return drugsLogic.getInfos(medecinePackageID);
    }

    public MpExtendedInfos getExtentedMpInfosWithPackage(MppId medecinePackageID) {
        return drugsLogic.getExtendedMpInfos(medecinePackageID);
    }

    public List<MppPreview> getCheapAlternativesBasedOnAtc(MppId medecinePackageID) {
        return drugsLogic.getCheapAlternativesBasedOnAtc(medecinePackageID);
    }

    public List<IamFullInfos> getInteractions(MppId medecinePackageID, List<String> otherCnks) {
        return drugsLogic.getInteractions(medecinePackageID, otherCnks);
    }

    public List<MppPreview> getCheapAlternativesBasedOnInn(String innCluster, String lang) {
        return drugsLogic.getCheapAlternativesBasedOnInn(innCluster, lang);
    }

    public MpFullInfos getFullMpInfosWithPackage(MppId medecinePackageID) {
        return drugsLogic.getFullMpInfos(medecinePackageID);
    }

    public List<FullTextSearchResult> fullTextSearch(String search, String lang, List<String> classes, List<String> types, int from, int count) throws IOException {
        return drugsLogic.fullTextSearch(search, lang, classes, types, from, count);
    }

    @SuppressWarnings("unchecked")
    public List getChildrenDocsAndMps(DocId docID) {
        List result = new ArrayList();
        result.add(drugsLogic.getChildrenDocs(docID));
        result.add(drugsLogic.getChildrenMps(docID));
        return result;
    }

    public DocPreview getDocPreview(String id, String lang) {
        return drugsLogic.getDocPreview(id, lang);
    }

    public MpPreview getMpFromMpp(String mppId, String lang) {
        return drugsLogic.getMpFromMpp(mppId, lang);
    }

    public List<DocPreview> getRootDocs(String lang) {
        return drugsLogic.getRootDocs(lang);
    }

    public DocPreview getDocOfMp(MpId mpId) {
        return drugsLogic.getDocOfMp(mpId);
    }

    public DocPreview getParentDoc(DocId docID) {
        return drugsLogic.getParentDoc(docID);
    }

    @Autowired
    public void setDrugsLogic(DrugsLogic drugsLogic) {
        this.drugsLogic = drugsLogic;
    }

}
