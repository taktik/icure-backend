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


package org.taktik.icure.be.drugs.logic.impl;

import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.be.drugs.dto.DocExtendedInfos;
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
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.be.drugs.*;
import org.taktik.icure.be.drugs.Atc;
import org.taktik.icure.be.drugs.civics.*;
import org.taktik.icure.be.drugs.dao.DrugsDAO;
import org.taktik.icure.be.drugs.logic.DrugsLogic;
import org.taktik.icure.logic.CodeLogic;

import java.io.IOException;
import java.util.*;

@Service
@HibernateDatasource
public class DrugsLogicImpl implements DrugsLogic {

    protected final Log log = LogFactory.getLog(getClass());

    protected DrugsDAO drugsDAO;
    private MapperFacade mapper;
    protected CodeLogic codeLogic;

    private final static List<String> availableLanguages = Arrays.asList("fr", "nl");
    private final static String defaultLanguage = "fr";

    protected final Transformer<Therapy, TherapyInfos> THERAPY_TO_THERAPYINFOS = new Transformer<Therapy, TherapyInfos>() {
        public TherapyInfos transform(Therapy therapy) {
            return mapper.map(therapy, TherapyInfos.class);
        }
    };

    protected final Transformer<Verse, VerseInfos> VERSE_TO_VERSEINFOS = new Transformer<Verse, VerseInfos>() {
        @Override
        public VerseInfos transform(Verse verse) {
            VerseInfos verseInfos = mapper.map(verse, VerseInfos.class);
            verseInfos.setVerses(new TreeSet<>(CollectionUtils.collect(drugsDAO.getChildrenVerses(verse), VERSE_TO_VERSEINFOS)));

            return verseInfos;
        }
    };

    protected final Transformer<Paragraph, ParagraphInfos> PARAGRAPH_TO_PARAGRAPHINFOS = new Transformer<Paragraph, ParagraphInfos>() {
        public ParagraphInfos transform(Paragraph paragraph) {
            ParagraphInfos paragraphInfos = mapper.map(paragraph, ParagraphInfos.class);

            paragraphInfos.setHeaderVerse(VERSE_TO_VERSEINFOS.transform(drugsDAO.getHeaderVerse(paragraph)));

            return paragraphInfos;
        }
    };

    protected final Transformer<AddedDocument, AddedDocumentPreview> ADDDOC_TO_ADDDOCPREVIEW = new Transformer<AddedDocument, AddedDocumentPreview>() {
        public AddedDocumentPreview transform(AddedDocument addedDocument) {
            AddedDocumentPreview documentPreview = mapper.map(addedDocument, AddedDocumentPreview.class);

            documentPreview.setDescrFr(drugsDAO.getShortText(addedDocument.getAppendixType().getNameId(), "FR"));
            documentPreview.setDescrNl(drugsDAO.getShortText(addedDocument.getAppendixType().getNameId(), "NL"));

            return documentPreview;
        }
    };

    protected final Transformer<Paragraph, ParagraphPreview> PARAGRAPH_TO_PARAGRAPHPREVIEW = new Transformer<Paragraph, ParagraphPreview>() {
        public ParagraphPreview transform(Paragraph paragraph) {
            return mapper.map(paragraph, ParagraphPreview.class);
        }
    };

    protected final Transformer<Code, MppPreview> INN_TO_MPPPREVIEW_FR = inn -> {
		MppPreview mppPreview = new MppPreview();

		mppPreview.setName(inn.getDescrFR());
		mppPreview.setInncluster(inn.getCode());
		return mppPreview;
	};

    protected final Transformer<Code, MppPreview> INN_TO_MPPPREVIEW_NL = inn -> {
		MppPreview mppPreview = new MppPreview();

		mppPreview.setName(inn.getDescrNL());
		mppPreview.setInncluster(inn.getCode());
		return mppPreview;
	};
    protected final Transformer<Mpp, MppPreview> MPP_TO_MPPPREVIEW = new Transformer<Mpp, MppPreview>() {
        public MppPreview transform(Mpp mpp) {
            return mapper.map(mpp, MppPreview.class);
        }
    };

    protected final Transformer<Iam, IamFullInfos> IAM_TO_IAMFULL = new Transformer<Iam, IamFullInfos>() {
        public IamFullInfos transform(Iam iam) {
            return mapper.map(iam, IamFullInfos.class);
        }
    };

    protected final Transformer<Mpp, MppInfos> MPP_TO_MPPINFOS = new Transformer<Mpp, MppInfos>() {
        public MppInfos transform(Mpp mpp) {
            return mapper.map(mpp, MppInfos.class);
        }
    };

    protected final Transformer<Mp, MpExtendedInfos> MP_TO_MPEXTENDEDINFOS = new Transformer<Mp, MpExtendedInfos>() {
        public MpExtendedInfos transform(Mp mp) {
            return mapper.map(mp, MpExtendedInfos.class);
        }
    };

    protected final Transformer<Mp, MpFullInfos> MP_TO_MPFULLINFOS = new Transformer<Mp, MpFullInfos>() {
        public MpFullInfos transform(Mp mp) {
            return mapper.map(mp, MpFullInfos.class);
        }
    };

    protected final Transformer<Mp, MpPreview> MP_TO_MPPREVIEW = new Transformer<Mp, MpPreview>() {
        public MpPreview transform(Mp mp) {
            return mapper.map(mp, MpPreview.class);
        }
    };

    protected final Transformer<Equivalence, MpPreview> EQUIV_TO_MPPREVIEW = new Transformer<Equivalence, MpPreview>() {
        public MpPreview transform(Equivalence eq) {
            return mapper.map(eq.getMpByTargetequivalence(), MpPreview.class);
        }
    };

    protected final Transformer<Doc, DocExtendedInfos> DOC_TO_DOCEXTENDEDINFOS = new Transformer<Doc, DocExtendedInfos>() {
        public DocExtendedInfos transform(Doc doc) {
            return mapper.map(doc, DocExtendedInfos.class);
        }
    };

    protected final Transformer<Doc, DocPreview> DOC_TO_DOCPREVIEW = new Transformer<Doc, DocPreview>() {
        public DocPreview transform(Doc doc) {
            return mapper.map(doc, DocPreview.class);
        }
    };

    public List<MppPreview> getMedecinePackages(String searchString, String lang, List<String> types, int first, int count) {
        try {
            drugsDAO.openDataStoreSession();
            Validate.noNullElements(new Object[]{searchString, lang});
            log.debug("Asked language : " + lang);
            lang = getAvailableLanguage(lang);
            log.debug("Final language : " + lang);
            List<Mpp> packages = drugsDAO.getMedecinePackages(searchString, lang, types, first, count);
            return (List<MppPreview>) CollectionUtils.collect(packages, MPP_TO_MPPPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }


    public List<MppPreview> getMedecinePackagesFromIngredients(String searchString, String lang, List<String> types, int first, int count) {
        try {
            drugsDAO.openDataStoreSession();
            Validate.noNullElements(new Object[]{searchString, lang});
            log.debug("Asked language : " + lang);
            lang = getAvailableLanguage(lang);
            log.debug("Final language : " + lang);
            List<Mpp> packages = drugsDAO.getMedecinePackagesFromIngredients(searchString, lang, types, first, count);
            return (List<MppPreview>) CollectionUtils.collect(packages, MPP_TO_MPPPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    @Override
    public List<MppPreview> getMedecinePackagesFromInn(String inn, String lang) {
        try {
            drugsDAO.openDataStoreSession();
            lang = getAvailableLanguage(lang);

            return (List<MppPreview>) CollectionUtils.collect(drugsDAO.getMppsWithInn(inn, lang), MPP_TO_MPPPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }


    public MpExtendedInfos getExtendedMpInfos(MppId medecinePackageID) {
        try {
            drugsDAO.openDataStoreSession();
            return MP_TO_MPEXTENDEDINFOS.transform(drugsDAO.getExtendedInfos(drugsDAO.getInfos(medecinePackageID).getMp().getId()));
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public MpFullInfos getFullMpInfos(MppId medecinePackageID) {
        try {
            drugsDAO.openDataStoreSession();
            Mpp mpp = drugsDAO.getInfos(medecinePackageID);
            if (mpp == null) {
                return null;
            }
            return getFullMpInfos(mpp.getMp().getId());
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public List<MppPreview> getCheapAlternativesBasedOnAtc(MppId medecinePackageID) {
        try {
            drugsDAO.openDataStoreSession();
            try {
                Atc atc = drugsDAO.getAtc(medecinePackageID);

                return (List<MppPreview>) CollectionUtils.collect(drugsDAO.getMppsWithAtc(atc), MPP_TO_MPPPREVIEW);
            } catch (Exception ignored) {
            }
            return new ArrayList<>();
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public List<MppPreview> getCheapAlternativesBasedOnInn(String innCluster, String lang) {
        try {
            drugsDAO.openDataStoreSession();
            try {
                lang = getAvailableLanguage(lang);
                return (List<MppPreview>) CollectionUtils.collect(drugsDAO.getCheapMppsWithInn(innCluster, lang), MPP_TO_MPPPREVIEW);
            } catch (Exception ignored) {
            }
            return new ArrayList<>();
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    @Override
    public List<ParagraphPreview> findParagraphs(String searchString, String lang) {
        try {
            drugsDAO.openDataStoreSession();
            lang = getAvailableLanguage(lang);
            return (List<ParagraphPreview>) CollectionUtils.collect(drugsDAO.findParagraphs(searchString, lang), PARAGRAPH_TO_PARAGRAPHPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    @Override
    public List<ParagraphPreview> findParagraphsWithCnk(Long cnk, String lang) {
        try {
            drugsDAO.openDataStoreSession();
            lang = getAvailableLanguage(lang);
            return (List<ParagraphPreview>) CollectionUtils.collect(drugsDAO.findParagraphsWithCnk(cnk, lang), PARAGRAPH_TO_PARAGRAPHPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public MpExtendedInfos getExtendedMpInfos(MpId medecineID) {
        try {
            drugsDAO.openDataStoreSession();
            Mp mp = drugsDAO.getExtendedInfos(medecineID);
            return MP_TO_MPEXTENDEDINFOS.transform(mp);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public MppInfos getInfos(MppId medecinePackageID) {
        try {
            drugsDAO.openDataStoreSession();
            Mpp mpp = drugsDAO.getInfos(medecinePackageID);
            if (mpp==null) {
                return null;
            }
            MppInfos mppInfos = MPP_TO_MPPINFOS.transform(mpp);

            Atc atc = drugsDAO.getAtc(medecinePackageID);

            if (atc != null) {
                mppInfos.setAtcCode(atc.getCode());
            }

            return mppInfos;
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public String getAvailableLanguage(String proposedLanguage) {
        if (proposedLanguage==null) { return defaultLanguage; }
        if (!availableLanguages.contains(proposedLanguage) && proposedLanguage.length()>=2) {
            proposedLanguage = proposedLanguage.substring(0,2).toLowerCase();
        }
        if (!availableLanguages.contains(proposedLanguage)) {
            return defaultLanguage;
        }
        return proposedLanguage;
    }

    public DocExtendedInfos getExtendedDocInfos(DocId docID) {
        try {
            drugsDAO.openDataStoreSession();
            Doc doc = drugsDAO.getExtendedInfos(docID);
            DocExtendedInfos result = DOC_TO_DOCEXTENDEDINFOS.transform(doc);
            result.setNext(getNextDoc(docID));
            result.setPrevious(getPreviousDoc(docID));
            if ((doc.getChildren().size() > 0) && (doc.getChildren().get(0).getMpgrp())) {
                result.setMpGroups((List<DocExtendedInfos>) CollectionUtils.collect(doc.getChildren(), DOC_TO_DOCEXTENDEDINFOS));
            }
            return result;
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public DocPreview getNextDoc(DocId docID) {
        try {
            drugsDAO.openDataStoreSession();
            Doc doc = drugsDAO.getDoc(docID);
            Doc result;
            DocPreview resultPreview;
            log.info("children size = " + doc.getChildren().size());
            if ((doc.getChildren().size() > 0) && (!doc.getChildren().get(0).getMpgrp())) {
                result = doc.getChildren().get(0);
            } else {
                result = doc;
                boolean finished = false;
                do {
                    Doc previous = result;
                    result = result.getParent();
                    if (result != null) {
                        int index = result.getChildren().indexOf(previous);
                        if ((index + 1) < result.getChildren().size()) {
                            finished = true;
                            result = result.getChildren().get(index + 1);
                        }
                    } else {
                        int index = previous.getDocindex() - 1;
                        List<Doc> roots = drugsDAO.getRootDocs(docID.getLang());
                        if ((index + 1) < roots.size()) {
                            result = roots.get(index + 1);
                        }
                    }
                } while ((result != null) && !finished);
            }
            if (result == null) {
                resultPreview = null;
            } else {
                resultPreview = DOC_TO_DOCPREVIEW.transform(result);
            }
            return resultPreview;
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }


    public DocPreview getPreviousDoc(DocId docID) {
        try {
            drugsDAO.openDataStoreSession();
            Doc doc = drugsDAO.getDoc(docID);
            Doc result;
            DocPreview resultPreview;
            if (doc.getDocindex() > 1) {
                if (doc.getParent() == null) {
                    result = drugsDAO.getRootDocs(docID.getLang()).get(doc.getDocindex() - 2);
                } else {
                    result = doc.getParent().getChildren().get(doc.getDocindex() - 2);
                }
            } else {
                result = doc.getParent();
            }
            if (result == null) {
                resultPreview = null;
            } else {
                resultPreview = DOC_TO_DOCPREVIEW.transform(result);
            }
            return resultPreview;
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public DrugsDAO getDrugsDAO() {
        return drugsDAO;
    }

    public MpFullInfos getFullMpInfos(MpId mpId) {
        try {
            drugsDAO.openDataStoreSession();
            Mp fullMp = drugsDAO.getFullMpInfos(mpId);
            MpFullInfos result = MP_TO_MPFULLINFOS.transform(fullMp);
            if ((fullMp.getDoc() != null) && (fullMp.getDoc().getParent() != null)) {
                result.setRelatedDoc(DOC_TO_DOCPREVIEW.transform(fullMp.getDoc().getParent()));
            }
            Set<Equivalence> mps = fullMp.getEquivalencesForSourceequivalence();
            CollectionUtils.collect(mps, EQUIV_TO_MPPREVIEW, result.getEquivalences());

            return result;
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public List<IamFullInfos> getInteractions(MppId newMppId, List<String> otherMpps) {
        try {
            drugsDAO.openDataStoreSession();
            String language = newMppId.getLang();
            Atc atc = drugsDAO.getAtc(newMppId);

            Map<String, List<MppId>> otherMeds = new HashMap<>();
            for (String o : otherMpps) {
                MppId medecinePackageID = new MppId(o, language);
                Atc oAtc = drugsDAO.getAtc(medecinePackageID);
                if (oAtc!=null) {
                List<MppId> medsForAtc = otherMeds.get(oAtc.getCode());
                if (medsForAtc == null) {
                    otherMeds.put(oAtc.getCode(), medsForAtc = new ArrayList<>());
                }

                medsForAtc.add(medecinePackageID);}
            }


            List<IamFullInfos> result = new ArrayList<>();

            if (atc != null) {
                List<Iam> iams = drugsDAO.getIams(atc.getCode(), language);
                for (Iam iam : iams) {
                    List<MppId> mppIds = otherMeds.get(iam.getAtc2());
                    if (mppIds != null) {
                        for (MppId id : mppIds) {
                            IamFullInfos transform = IAM_TO_IAMFULL.transform(iam);
                            transform.setMppInfos(MPP_TO_MPPINFOS.transform(drugsDAO.getMpp(id)));
                            result.add(transform);
                        }
                    }
                }
            }

            return result;
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public List<FullTextSearchResult> fullTextSearch(String search, String lang, List<String> classes, List<String> types, int from, int count) throws IOException {
        lang = getAvailableLanguage(lang);
        return drugsDAO.fullTextSearch(search, lang, classes, types, from, count);
    }


    public List<DocPreview> getRootDocs(String lang) {
        try {
            drugsDAO.openDataStoreSession();
            lang = getAvailableLanguage(lang);
            return (List<DocPreview>) CollectionUtils.collect(drugsDAO.getRootDocs(lang), DOC_TO_DOCPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public DocPreview getDocPreview(String id, String lang) {
        try {
            drugsDAO.openDataStoreSession();
            lang = getAvailableLanguage(lang);
            return DOC_TO_DOCPREVIEW.transform(drugsDAO.getDoc(new DocId(id, lang)));
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }


    public List<DocPreview> getChildrenDocs(DocId docID) {
        try {
            drugsDAO.openDataStoreSession();
            Doc aDoc = drugsDAO.getDoc(docID);
            List<Doc> childrenDocs = aDoc.getChildren();
            if (childrenDocs.size() > 0) {
                if (childrenDocs.get(0).getMpgrp()) {
                    childrenDocs = new ArrayList<>();
                }
            }
            return (List<DocPreview>) CollectionUtils.collect(childrenDocs, DOC_TO_DOCPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public List<MpPreview> getChildrenMps(DocId docID) {
        try {
            drugsDAO.openDataStoreSession();
            Doc aDoc = drugsDAO.getDoc(docID);
            SortedSet<Mp> childrenMps = aDoc.getMps();
            List<Mp> resultMps = new ArrayList<>();
            resultMps.addAll(childrenMps);
            List<Doc> childrenDocs = aDoc.getChildren();
            if (childrenDocs.size() > 0) {
                if (childrenDocs.get(0).getMpgrp()) {
                    for (Doc child : childrenDocs) {
                        resultMps.addAll(child.getMps());
                    }
                }
            }
            return (List<MpPreview>) CollectionUtils.collect(resultMps, MP_TO_MPPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public List<TherapyInfos> getTherapiesInfos(MppId mppId) {
        try {
            drugsDAO.openDataStoreSession();
            Ampp ampp = drugsDAO.getAmpp(mppId);
            return (List<TherapyInfos>) CollectionUtils.collect(ampp.getAmp().getAtm().getTherapies(), THERAPY_TO_THERAPYINFOS);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    public ParagraphInfos getParagraphInfos(Long therapyId) {
        try {
            drugsDAO.openDataStoreSession();
            Therapy therapy = drugsDAO.getTherapy(therapyId);
            Paragraph paragraph = drugsDAO.getParagraph(therapy);

            return PARAGRAPH_TO_PARAGRAPHINFOS.transform(paragraph);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    @Override
    public ParagraphInfos getParagraphInfos(String chapterName, String paragraphName) {
        try {
            drugsDAO.openDataStoreSession();
            Paragraph paragraph = drugsDAO.getParagraph(chapterName, paragraphName);

            return paragraph == null ? null : PARAGRAPH_TO_PARAGRAPHINFOS.transform(paragraph);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    @Override
    public List<AddedDocumentPreview> getAddedDocuments(String chapterName, String paragraphName) {
        try {
            drugsDAO.openDataStoreSession();
            List<AddedDocument> docs = drugsDAO.getAddedDocuments(chapterName, paragraphName);
            return (List<AddedDocumentPreview>) CollectionUtils.collect(docs, ADDDOC_TO_ADDDOCPREVIEW);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    @Override
    public List<MppPreview> getInnClusters(String region, String searchString, String lang, List<String> types, int first, int count) {
        try {
            drugsDAO.openDataStoreSession();
            Validate.noNullElements(new Object[]{searchString, lang});
            log.debug("Asked language : " + lang);
            lang = getAvailableLanguage(lang);
            log.debug("Final language : " + lang);
            PaginatedList<Code> inns = codeLogic.findCodesByLabel(region, lang, "CD-INNCLUSTER", searchString, new PaginationOffset(first + count));
            return (List<MppPreview>) CollectionUtils.collect(inns.getRows().subList(first, inns.getRows().size()), lang != null && lang.equals("nl") ? INN_TO_MPPPREVIEW_NL : INN_TO_MPPPREVIEW_FR);
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }


    public MpPreview getMpFromMpp(String mppId, String lang) {
        try {
            drugsDAO.openDataStoreSession();
            lang = getAvailableLanguage(lang);
            Mpp mpp = drugsDAO.getMpp(new MppId(mppId, lang));
            return MP_TO_MPPREVIEW.transform(mpp.getMp());
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }


    public void installNewDrugsDatabase(String path) {
        drugsDAO.installNewDrugsDatabase(path);
    }


    public boolean isDataBasePresent() {
        return drugsDAO.isDataBasePresent();
    }


    public void initDrugsDatabase() {
        drugsDAO.initDrugsDatabase();
    }

    public void stopDrugsDatabase() {
        drugsDAO.stopDrugsDatabase();
    }


    public DocPreview getDocOfMp(MpId mpId) {
        try {
            drugsDAO.openDataStoreSession();
            Mp mp = drugsDAO.getMp(mpId);
            Doc mpGrp = mp.getDoc();
            if (mpGrp == null) {
                return null;
            }
            if (mpGrp.getParent() == null) {
                return null;
            }
            return DOC_TO_DOCPREVIEW.transform(mpGrp.getParent());
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }


    public DocPreview getParentDoc(DocId docID) {
        try {
            drugsDAO.openDataStoreSession();
            Doc doc = drugsDAO.getDoc(docID);
            if (doc.getParent() == null) {
                return null;
            }
            return DOC_TO_DOCPREVIEW.transform(doc.getParent());
        } finally {
            drugsDAO.closeDataStoreSession();
        }
    }

    @Autowired
    public void setDrugsDAO(DrugsDAO drugsDAO) {
        this.drugsDAO = drugsDAO;
    }

    @Autowired
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Autowired
    public void setCodeLogic(CodeLogic codeLogic) {
        this.codeLogic = codeLogic;
    }
}
