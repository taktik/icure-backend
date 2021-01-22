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


package org.taktik.icure.be.drugs.dao.h2impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.taktik.icure.be.drugs.Atc;
import org.taktik.icure.be.drugs.dto.AtcId;
import org.taktik.icure.be.drugs.Doc;
import org.taktik.icure.be.drugs.dto.DocId;
import org.taktik.icure.be.drugs.dto.FullTextSearchResult;
import org.taktik.icure.be.drugs.Iam;
import org.taktik.icure.be.drugs.Mp;
import org.taktik.icure.be.drugs.dto.MpId;
import org.taktik.icure.be.drugs.Mpp;
import org.taktik.icure.be.drugs.dto.MppId;
import org.taktik.icure.be.drugs.civics.AddedDocument;
import org.taktik.icure.be.drugs.civics.Ampp;
import org.taktik.icure.be.drugs.civics.NameTranslation;
import org.taktik.icure.be.drugs.civics.Paragraph;
import org.taktik.icure.be.drugs.civics.Therapy;
import org.taktik.icure.be.drugs.civics.Verse;
import org.taktik.icure.be.drugs.dao.DrugsDAO;
import org.taktik.icure.be.drugs.logic.DrugsDatabaseNotFoundException;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.logic.PropertyLogic;

@Repository("drugsDAO")
public class DrugsDAOImpl implements DrugsDAO {

	protected final Log log = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;
	private IndexSearcher indexSearcher;

	protected Map<String, Analyzer> analyzers;

	private String dbMainFile = "drugs.mv.db";
	private File dbDir;
	private PropertyLogic propertyLogic;

	public DrugsDAOImpl() {
		buildAnalyzerMap();
	}

	@Autowired
	public void setPropertyLogic(PropertyLogic propertyLogic) {
		this.propertyLogic = propertyLogic;
	}

	public File getDbDir() {
		synchronized(this) {
			if (dbDir == null) {
                    String tempDir = propertyLogic.getSystemPropertyValue(PropertyTypes.System.ICURE_PATH_TEMP.getIdentifier());
                    File drugsDir = new File(tempDir, "drugs");
                    File dbFile = new File(drugsDir, dbMainFile);

                    if (drugsDir.exists() && drugsDir.isDirectory() && dbFile.exists()) {
                        //Should Check validity
                        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("be/drugs/drugs.zip");
                        ZipInputStream zis = new ZipInputStream(stream);
                        ZipEntry ze;
                        try {
                            while ((ze = zis.getNextEntry()) != null) {
                                if (dbMainFile.equals(ze.getName())) {
                                    if (ze.getSize() == dbFile.length()) {
                                        dbDir = drugsDir;
                                        return dbDir;
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    if (drugsDir.isFile()) {
                        drugsDir.delete();
                    }

                    boolean mkdirs = drugsDir.mkdirs();
                    if (!mkdirs) {
                        Optional.ofNullable(drugsDir.listFiles()).ifPresent(files->Arrays.asList(files).forEach(File::delete));
                    }
                    if (drugsDir.exists() && drugsDir.isDirectory()) {
                        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("be/drugs/drugs.zip");
                        ZipInputStream zis = new ZipInputStream(stream);
                        ZipEntry ze;
                        byte[] buffer = new byte[10 * 1024];
                        // while there are entries I process them
                        try {
                            while ((ze = zis.getNextEntry()) != null) {
                                String fileName = ze.getName();
                                File newFile = new File(drugsDir, fileName);
                                new File(newFile.getParent()).mkdirs();
                                FileOutputStream fos = new FileOutputStream(newFile);
                                int len;
                                while ((len = zis.read(buffer)) > 0) {
                                    fos.write(buffer, 0, len);
                                }
                                fos.close();
                            }
                            dbDir = drugsDir;
                        } catch (Exception ignored) {
                        } finally {
                            try {
                                zis.close();
                                stream.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }
			return dbDir;
		}
	}

	public void setDbDir(File dbDir) {
		this.dbDir = dbDir;
	}

	@SuppressWarnings("unchecked")
	public List<Mpp> getMedecinePackages(String searchString, String lang, List<String> types, int first, int count) {
		log.debug("Getting medecine packages for " + searchString + " from " + first + ", count=" + count);
		Session sess = getSessionFactory().getCurrentSession();
		Criteria c = sess.createCriteria(Mpp.class);
		addLangRestriction(c, lang);
		addTypesRestriction(c, types);
		c.add(Restrictions.ilike("name", searchString, MatchMode.START));
		c.setFirstResult(first);
		c.setMaxResults(count);
		c.addOrder(Order.asc("name"));
		List<Mpp> result = c.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Mpp> getMedecinePackagesFromIngredients(String searchString, String lang, List<String> types, int first, int count) {
		log.debug("Getting medecine packages from ingredients for " + searchString + " from " + first + ", count=" + count);
		Session sess = getSessionFactory().getCurrentSession();
		Criteria c = sess.createCriteria(Mpp.class);
		addLangRestriction(c, lang);
		addTypesRestriction(c, types);

		c.createAlias("compositions", "comp").createAlias("comp.ingredient", "ingrd");
		c.add(Restrictions.or(Restrictions.ilike("name", searchString, MatchMode.START),
				Restrictions.ilike("ingrd.name", searchString, MatchMode.START)));

		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		c.setFirstResult(first);
		c.setMaxResults(count);
		c.addOrder(Order.asc("name"));
		List<Mpp> result = c.list();
		return result;
	}


	@SuppressWarnings("unchecked")
	public Mpp getInfos(MppId medecinePackageID) {
		log.debug("Getting infos for Mpp " + medecinePackageID);
		Session sess = getSessionFactory().getCurrentSession();
		Criteria c = sess.createCriteria(Mpp.class);
		c.add(Restrictions.eq("id", medecinePackageID));
		c.setFetchMode("mp", FetchMode.JOIN);
		List<Mpp> result = c.list();
		if (result.size() == 0) {
			return null;
		}
		Validate.isTrue(result.size() == 1, "More than One Mpp found!");
		return result.get(0);
	}

	protected HibernateTemplate getHibernateTemplate() {
		return new HibernateTemplate(getSessionFactory());
	}

	private synchronized SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			if (!isDataBasePresent()) {
				throw new DrugsDatabaseNotFoundException();
			}
			try {
				Configuration cfg = new Configuration()
						.addClass(org.taktik.icure.be.drugs.Mpp.class)
						.addClass(org.taktik.icure.be.drugs.Atc.class)
						.addClass(org.taktik.icure.be.drugs.Composition.class)
						.addClass(org.taktik.icure.be.drugs.Doc.class)
						.addClass(org.taktik.icure.be.drugs.Gal.class)
						.addClass(org.taktik.icure.be.drugs.Informationresponsible.class)
						.addClass(org.taktik.icure.be.drugs.Ingredient.class)
						.addClass(org.taktik.icure.be.drugs.Iam.class)
						.addClass(org.taktik.icure.be.drugs.Mp.class)
						.addClass(org.taktik.icure.be.drugs.Mppprop.class)
						.addClass(org.taktik.icure.be.drugs.Prop.class)
						.addClass(org.taktik.icure.be.drugs.Equivalence.class)
						.addClass(org.taktik.icure.be.drugs.civics.ActualIngredientStrength.class)
						.addClass(org.taktik.icure.be.drugs.civics.AddedDocument.class)
						.addClass(org.taktik.icure.be.drugs.civics.AdministrationForm.class)
						.addClass(org.taktik.icure.be.drugs.civics.Amp.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.AmpComb.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.AmpIntPckComb.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.AmpIntermediatePackage.class)
						.addClass(org.taktik.icure.be.drugs.civics.Ampp.class)
						.addClass(org.taktik.icure.be.drugs.civics.AppendixType.class)
						.addClass(org.taktik.icure.be.drugs.civics.Application.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.Atc.class)
						.addClass(org.taktik.icure.be.drugs.civics.Atm.class)
						.addClass(org.taktik.icure.be.drugs.civics.Company.class)
						//.addClass(org.taktik.icure.be.drugs.civics.Copayment.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.Exclusion.class)
						.addClass(org.taktik.icure.be.drugs.civics.FormType.class)
/*                  .addClass(org.taktik.icure.be.drugs.civics.HActualIngredientStrength.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HAddedDocument.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HAmp.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HAmpComb.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HAmpIntPckComb.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HAmpIntermediatePackage.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HAmpp.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HAtm.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HCopayment.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HExclusion.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HNameExplanation.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HNameTranslation.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HParagraph.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HParagraphTrace.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HPrice.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HProfessionalAuthorisation.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HQualificationList.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HReimbursement.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HTherapy.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HVerse.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HVirtualIngredientStrength.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HVmp.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HVmpComb.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HVmpp.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HVtm.class)
                    .addClass(org.taktik.icure.be.drugs.civics.HVtmIngredient.class) */
//                    .addClass(org.taktik.icure.be.drugs.civics.Hyr.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.InnerPackage.class)
						.addClass(org.taktik.icure.be.drugs.civics.NameExplanation.class)
						.addClass(org.taktik.icure.be.drugs.civics.NameTranslation.class)
						.addClass(org.taktik.icure.be.drugs.civics.NameType.class)
						.addClass(org.taktik.icure.be.drugs.civics.Paragraph.class)
						.addClass(org.taktik.icure.be.drugs.civics.ParagraphTrace.class)
						.addClass(org.taktik.icure.be.drugs.civics.PharmaceuticalForm.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.Price.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.ProfessionalAuthorisation.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.ProfessionalCode.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.QualificationList.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.Reimbursement.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.ReimbursementCategory.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.ReimbursementCriteria.class)
						.addClass(org.taktik.icure.be.drugs.civics.RouteOfAdministration.class)
						.addClass(org.taktik.icure.be.drugs.civics.SphereVersion.class)
						.addClass(org.taktik.icure.be.drugs.civics.Substance.class)
						.addClass(org.taktik.icure.be.drugs.civics.Therapy.class)
//                    .addClass(org.taktik.icure.be.drugs.civics.TreatmentDurationCategory.class)
						.addClass(org.taktik.icure.be.drugs.civics.Verse.class)
						.addClass(org.taktik.icure.be.drugs.civics.VirtualIngredientStrength.class)
						.addClass(org.taktik.icure.be.drugs.civics.Vmp.class)
						.addClass(org.taktik.icure.be.drugs.civics.VmpComb.class)
						.addClass(org.taktik.icure.be.drugs.civics.Vmpp.class)
						.addClass(org.taktik.icure.be.drugs.civics.Vtm.class)
						.addClass(org.taktik.icure.be.drugs.civics.VtmIngredient.class)
						.addClass(org.taktik.icure.be.drugs.civics.Wada.class);
				cfg.setProperty("hibernate.current_session_context_class", "thread");
				cfg.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
				cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
				cfg.setProperty("hibernate.connection.password", "");
				cfg.setProperty("hibernate.connection.username", "sa");
				cfg.setProperty("hibernate.default_schema", "PUBLIC");
				cfg.setProperty("hibernate.connection.url", "jdbc:h2:" + getDbDir().getAbsolutePath() + "/drugs");
				sessionFactory = cfg.buildSessionFactory();
			} catch (Exception e) {
				log.error(e);
			}
		}
		return sessionFactory;
	}


	public void closeDataStoreSession() {
		getSessionFactory().getCurrentSession().close();
	}

	public void openDataStoreSession() {
		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			factory.getCurrentSession().beginTransaction();
		}
	}

	private Criterion getLangCriterion(String lang) {
		return Restrictions.eq("id.lang", lang);
	}

	private Criterion getTypesCriterion(List<String> types) {
		return Restrictions.in("type", types);
	}

	private void addTypesRestriction(Criteria c, List<String> types) {
		if ((types != null) && (types.size() > 0)) {
			c.add(getTypesCriterion(types));
		}
	}

	private void addLangRestriction(Criteria c, String lang) {
		c.add(getLangCriterion(lang));
	}

	@SuppressWarnings("unchecked")
	public Mp getExtendedInfos(MpId medecineID) {
		log.debug("Getting infos for Mp " + medecineID);
		Session sess = getSessionFactory().getCurrentSession();
		Criteria c = sess.createCriteria(Mp.class);
		c.add(Restrictions.eq("id", medecineID));
		List<Mp> result = c.list();
		if (result.size() == 0) {
			return null;
		}
		Validate.isTrue(result.size() == 1, "More than One Mp found!");

		return result.get(0);
	}

	@SuppressWarnings("unchecked")
	public Doc getExtendedInfos(DocId docID) {
		log.debug("Getting infos for doc " + docID);
		Session sess = getSessionFactory().getCurrentSession();
		Criteria c = sess.createCriteria(Doc.class);
		c.add(Restrictions.eq("id", docID));
		List<Doc> result = c.list();
		if (result.size() == 0) {
			return null;
		}
		Validate.isTrue(result.size() == 1, "More than One Doc found!");
		return result.get(0);
	}

	public Doc getDoc(DocId docID) {
		Session sess = getSessionFactory().getCurrentSession();
		Doc doc = (Doc) sess.get(Doc.class, docID);
		return doc;
	}

	@SuppressWarnings("unchecked")
	public List<Doc> getRootDocs(String lang) {
		Session sess = getSessionFactory().getCurrentSession();
		Criteria c = sess.createCriteria(Doc.class);
		addLangRestriction(c, lang);
		c.add(Restrictions.sqlRestriction("parent_id is null"));
		c.addOrder(Order.asc("docindex"));
		List<Doc> result = c.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public Mp getFullMpInfos(MpId mpId) {
		log.debug("Getting infos for Mp " + mpId);
		Session sess = getSessionFactory().getCurrentSession();
		Criteria c = sess.createCriteria(Mp.class);
		c.add(Restrictions.eq("id", mpId));
		List<Mp> result = c.list();
		if (result.size() == 0) {
			return null;
		}
		Validate.isTrue(result.size() == 1, "More than One Mp found!");
		return result.get(0);
	}

	private IndexSearcher getIndexSearcher() {
		if (indexSearcher == null) {
			if (!isDataBasePresent()) {
				throw new DrugsDatabaseNotFoundException();
			}
			try {
				indexSearcher = new IndexSearcher(IndexReader.open(FSDirectory.open(getDbDir())));
				log.info("index has " + indexSearcher.getIndexReader().numDocs() + " docs");
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Unable to open Lucene index.", e);
			}
		}
		return indexSearcher;
	}

	protected static Transformer<Document, FullTextSearchResult> DOC_TO_SEARCHRESULT = new Transformer<Document, FullTextSearchResult>() {

		public FullTextSearchResult transform(Document doc) {
			FullTextSearchResult ftsr = new FullTextSearchResult();
			ftsr.setId(doc.get("id"));
			ftsr.setLang(doc.get("lang"));
			ftsr.setResultClass(doc.get("Discriminator"));
			ftsr.setTitle(doc.get("title"));
			return ftsr;
		}

	};

	public List<FullTextSearchResult> fullTextSearch(String search, String lang, List<String> classes, List<String> types, int from, int count) throws IOException {
		Validate.notNull(lang, "Language must be specified");
		Validate.notNull(search, "Search string must not be null");
		// Normalize search string
		search = search.replaceAll("\\*|\\?", "").trim();
		List<FullTextSearchResult> results = new ArrayList<>();
		if (search.length() == 0) {
			return results;
		}

		Analyzer std = getAnalyserForLanguage(lang);
		TokenStream ts = std.tokenStream("content", new StringReader(search));
		CharTermAttribute cattr = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		List<String> searchTerms2 = new ArrayList<>();
		while (ts.incrementToken()) {
			searchTerms2.add(cattr.toString());
		}

		if (searchTerms2.size() == 0) {
			return results;
		}
		// Construct the lucene query
		BooleanQuery.setMaxClauseCount(10000);
		BooleanQuery fullTextQuery = new BooleanQuery();
		fullTextQuery.setMinimumNumberShouldMatch(1);
		//String[] searchTerms = search.toLowerCase().split(" +");
		BooleanQuery titleQuery = new BooleanQuery();
		for (String term : searchTerms2) {
			titleQuery.add(new WildcardQuery(new Term("title", term + "*")), Occur.MUST);
		}
		fullTextQuery.add(titleQuery, Occur.SHOULD);
		BooleanQuery contentQuery = new BooleanQuery();
		for (String term : searchTerms2) {
			contentQuery.add(new WildcardQuery(new Term("content", term + "*")), Occur.MUST);
		}
		fullTextQuery.add(contentQuery, Occur.SHOULD);
		fullTextQuery.add(new TermQuery(new Term("lang", lang)), Occur.MUST);
		if ((types != null) && (types.size() > 0)) {
			BooleanQuery typesQuery = new BooleanQuery();
			typesQuery.setMinimumNumberShouldMatch(1);
			for (String type : types) {
				typesQuery.add(new TermQuery(new Term("type", type)), Occur.SHOULD);
			}
			fullTextQuery.add(typesQuery, Occur.MUST);
		}
		if ((classes != null) && (classes.size() > 0)) {
			BooleanQuery classesQuery = new BooleanQuery();
			classesQuery.setMinimumNumberShouldMatch(1);
			for (String clazz : classes) {
				classesQuery.add(new TermQuery(new Term("Discriminator", clazz)), Occur.SHOULD);
			}
			fullTextQuery.add(classesQuery, Occur.MUST);
		}
		log.info("Full text search from " + from + ", " + count + " results: " + fullTextQuery);
		// Perform the search
		TopDocs hits = getIndexSearcher().search(fullTextQuery, 10000);
		log.info("" + hits.scoreDocs.length + " results");

		int realFrom = Math.min(hits.scoreDocs.length, from);
		int to = Math.min(hits.scoreDocs.length - 1, (realFrom + count) - 1);
		log.info("realfrom=" + realFrom + ",to=" + to);

		for (int i = realFrom; i <= to; i++) {
			Document doc = getIndexSearcher().doc(hits.scoreDocs[i].doc);
			FullTextSearchResult sr = DOC_TO_SEARCHRESULT.transform(doc);
			sr.setScore(hits.scoreDocs[i].score);
			results.add(sr);
		}

		return results;
	}

	public Mpp getMpp(MppId mppId) {
		Session sess = getSessionFactory().getCurrentSession();
		Mpp mpp = (Mpp) sess.get(Mpp.class, mppId);
		return mpp;
	}

	public void stopDrugsDatabase() {
		try {
			if (sessionFactory != null) {
				sessionFactory.close();
				sessionFactory = null;
			}
			if (indexSearcher != null) {
				indexSearcher = null;
			}
		} catch (Exception e) {
			// Ignore any exception at this stage.
		}
	}


	public void installNewDrugsDatabase(String zipFile) {
		try {
			if (sessionFactory != null) {
				sessionFactory.close();
				sessionFactory = null;
			}
			if (indexSearcher != null) {
				indexSearcher = null;
			}
			File fdbDir = getDbDir();
			if (fdbDir.exists()) {
				File oldDb = new File(fdbDir.getParentFile(), "drugs.old");
				if (oldDb.exists()) {
					FileUtils.deleteDirectory(oldDb);
				}
				fdbDir.renameTo(oldDb);
			}
			if (!fdbDir.mkdir()) {
				throw new RuntimeException("Unable to create Drugs dir!");
			}
			Unzip.unzip(new File(zipFile), fdbDir);
		} catch (Exception e) {
			throw new RuntimeException("Unable to install new Drugs database", e);
		}
	}


	public boolean isDataBasePresent() {
		return new File(getDbDir(), dbMainFile).exists();
	}

	public void initDrugsDatabase() {
/*		if (!isDataBasePresent()) {
			log.info("Installing default drugs database");
			installDefaultDatabase();
		}*/
		// Force the connection to the db and the fullText index.
		// Will throw a RuntimeException if something goes wrong.
		getSessionFactory();
		getIndexSearcher();
	}

/*	private void installDefaultDatabase() {
		try {
			File temp = File.createTempFile("drugs", ".tmp");
			InputStream defaultDatabaseFile = this.getClass().getResourceAsStream(DEFAULT_DB_NAME);
			OutputStream tempFile = new FileOutputStream(temp);
			IOUtils.copy(defaultDatabaseFile, tempFile);
			defaultDatabaseFile.close();
			tempFile.close();
			installNewDrugsDatabase(temp.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("Unable to install default Drugs database",e);
		}
	} */

	private void buildAnalyzerMap() {
		analyzers = new HashMap<>();
		analyzers.put("fr", new FrenchAnalyzer(Version.LUCENE_47));
		analyzers.put("nl", new DutchAnalyzer(Version.LUCENE_47));
		analyzers.put("en", new StandardAnalyzer(Version.LUCENE_47));
	}

	private Analyzer getAnalyserForLanguage(String lang) {
		Analyzer result = analyzers.get(lang);
		if (result == null) {
			result = analyzers.get("en");
		}
		return result;
	}


	public Mp getMp(MpId mpId) {
		Session sess = getSessionFactory().getCurrentSession();
		Mp mp = (Mp) sess.get(Mp.class, mpId);
		return mp;
	}

	public Atc getAtc(MppId medecinePackageID) {
		Session sess = getSessionFactory().getCurrentSession();
		Atc atc = (Atc) sess.get(Atc.class, new AtcId(medecinePackageID.getId(), medecinePackageID.getLang()));
		return atc;
	}

	public List<Mp> getMpsWithAtc(Atc atc) {
		Session sess = getSessionFactory().getCurrentSession();

		Set<Mp> mps = new HashSet<>();
		if (atc != null && atc.getCode() != null)
			for (Atc a : (List<Atc>) sess.createCriteria(Atc.class).add(Restrictions.eq("code", atc.getCode())).add(Restrictions.eq("current", true)).list()) {
				if (a.getId().getLang().equals(atc.getId().getLang())) {
					Mpp mpp = (Mpp) sess.get(Mpp.class, new MppId(a.getId().getMppId(), a.getId().getLang()));
					if (mpp != null && mpp.getRrsstate() != null && (mpp.getRrsstate().equals("G") || mpp.getRrsstate().equals("C") || mpp.getRrsstate().equals("B"))) {
						mps.add(mpp.getMp());
					}
				}
			}
		return new ArrayList<>(mps);
	}

	public List<Mpp> getMppsWithAtc(Atc atc) {
		Session sess = getSessionFactory().getCurrentSession();

		Set<Mpp> mpps = new HashSet<>();
		if (atc != null && atc.getCode() != null)
			for (Atc a : (List<Atc>) sess.createCriteria(Atc.class).add(Restrictions.eq("code", atc.getCode())).add(Restrictions.eq("current", true)).list()) {
				if (a.getId().getLang().equals(atc.getId().getLang())) {
					Mpp mpp = (Mpp) sess.get(Mpp.class, new MppId(a.getId().getMppId(), a.getId().getLang()));
					if (mpp != null && mpp.getRrsstate() != null && (mpp.getRrsstate().equals("G") || mpp.getRrsstate().equals("C") || mpp.getRrsstate().equals("B"))) {
						mpps.add(mpp);
					}
				}
			}
		return new ArrayList<>(mpps);
	}

	public List<Mpp> getCheapMppsWithInn(String inn, String lang) {
		if (inn != null && lang != null) {
			Session sess = getSessionFactory().getCurrentSession();
			ArrayList<Mpp> result = new ArrayList<>();
			for (Mpp candidate : (Collection<Mpp>) sess.createCriteria(Mpp.class).add(Restrictions.eq("inncluster", inn)).addOrder(Order.asc("index")).list()) {
				if (candidate.getId().getLang().equals(lang) && candidate.getRrsstate() != null && (candidate.getRrsstate().equals("G") || candidate.getRrsstate().equals("C") || candidate.getRrsstate().equals("B"))) {
					result.add(candidate);
				}
			}
			return result;
		}
		return new ArrayList<>();
	}

	public List<Mpp> getMppsWithInn(String inn, String lang) {
		if (inn != null && lang != null) {
			Session sess = getSessionFactory().getCurrentSession();
			ArrayList<Mpp> result = new ArrayList<>();
			for (Mpp candidate : (Collection<Mpp>) sess.createCriteria(Mpp.class).add(Restrictions.eq("inncluster", inn)).addOrder(Order.asc("index")).list()) {
				if (candidate.getId().getLang().equals(lang)) {
					result.add(candidate);
				}
			}
			return result;
		}
		return new ArrayList<>();
	}

	public List<Iam> getIams(String id, String lang) {
		if (id != null) {
			Session sess = getSessionFactory().getCurrentSession();
			List<Iam> result = new ArrayList<>();
			for (Iam iam : (List<Iam>) sess.createCriteria(Iam.class).add(Restrictions.eq("atc1", id)).list()) {
				if (iam.getIamId().getLang().equals(lang)) {
					result.add(iam);
				}
			}
			return result;
		}
		return new ArrayList<>();
	}

	@Override
	public Ampp getAmpp(MppId mppId) {
		if (mppId != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return (Ampp) sess.get(Ampp.class, mppId.getId());
		}
		return null;
	}

	@Override
	public Therapy getTherapy(Long therapyId) {
		if (therapyId != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return (Therapy) sess.get(Therapy.class, therapyId);
		}
		return null;
	}

	@Override
	public Paragraph getParagraph(Therapy therapy) {
		if (therapy != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return (Paragraph) sess.createCriteria(Paragraph.class)
					.add(Restrictions.eq("chapterName", therapy.getChapterName()))
					.add(Restrictions.eq("paragraphName", therapy.getParagraphName()))
					.uniqueResult();
		}
		return null;
	}

	@Override
	public Verse getHeaderVerse(Paragraph paragraph) {
		if (paragraph != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return (Verse) sess.createCriteria(Verse.class)
					.add(Restrictions.eq("chapterName", paragraph.getChapterName()))
					.add(Restrictions.eq("paragraphName", paragraph.getParagraphName()))
					.add(Restrictions.eq("verseSeqParent", 0l))
					.uniqueResult();
		}
		return null;
	}

	@Override
	public List<Verse> getChildrenVerses(Verse verse) {
		if (verse != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return (List<Verse>) sess.createCriteria(Verse.class)
					.add(Restrictions.eq("chapterName", verse.getChapterName()))
					.add(Restrictions.eq("paragraphName", verse.getParagraphName()))
					.add(Restrictions.eq("verseSeqParent", verse.getVerseSeq()))
					.addOrder(Order.asc("verseSeq"))
					.list();
		}
		return null;
	}

	@Override
	public List<Paragraph> findParagraphsWithCnk(Long cnk, String language) {
		if (cnk != null) {
			Set<Paragraph> result = new HashSet<>();
			Session sess = getSessionFactory().getCurrentSession();

			Map<String, List<String>> chapterParagraphs = new HashMap<>();
			for (Therapy t : (List<Therapy>) sess.createCriteria(Therapy.class, "a_th")
					.createAlias("a_th.atm", "a_atm")
					.createAlias("a_atm.amps", "a_amp")
					.createAlias("a_amp.ampps", "a_ampp")
					.add(Restrictions.eq("a_ampp.id", cnk))
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list()) {
				List<String> pns = chapterParagraphs.get(t.getChapterName());
				if (pns == null) {
					chapterParagraphs.put(t.getChapterName(), pns = new ArrayList<>());
				}
				pns.add(t.getParagraphName());
			}

			List<String> vals = chapterParagraphs.get("IV");
			if (vals != null && vals.size() > 0) {
				//for (Map.Entry<String, List<String>> k : chapterParagraphs.entrySet()) {
				result.addAll(sess.createCriteria(Paragraph.class)
						.add(Restrictions.eq("chapterName", "IV"))
						.add(Restrictions.in("paragraphName", vals))
						.list());
				//}
			}
			return new ArrayList<>(result);
		}
		return new ArrayList<>();
	}

	@Override
	public List<Paragraph> findParagraphs(String searchString, String language) {
		if (searchString != null && searchString.length() >= 2) {
			Set<Paragraph> result = new HashSet<>();
			Session sess = getSessionFactory().getCurrentSession();

			result.addAll(sess.createCriteria(Paragraph.class)
					.add(Restrictions.or(
							Restrictions.ilike("paragraphName", searchString + "%"),
							Restrictions.ilike(language != null && language.startsWith("nl") ? "keyStringNl" : "keyStringFr", "%" + searchString + "%")
					))
					.list());

			List<Long> ids = new LinkedList<>();
			for (Mpp mpp : getMedecinePackages(searchString, language, null, 0, 100)) {
				if (mpp.getId().getId().matches("^[0-9]+$")) {
					ids.add(Long.parseLong(mpp.getId().getId()));
				}
			}


			Map<String, List<String>> chapterParagraphs = new HashMap<>();
			for (Therapy t : (List<Therapy>) sess.createCriteria(Therapy.class, "a_th")
					.createAlias("a_th.atm", "a_atm")
					.createAlias("a_atm.amps", "a_amp")
					.createAlias("a_amp.ampps", "a_ampp")
					.add(Restrictions.in("a_ampp.id", ids))
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list()) {
				List<String> pns = chapterParagraphs.get(t.getChapterName());
				if (pns == null) {
					chapterParagraphs.put(t.getChapterName(), pns = new ArrayList<>());
				}
				pns.add(t.getParagraphName());
			}

			List<String> vals = chapterParagraphs.get("IV");
			if (vals != null && vals.size() > 0) {
				//for (Map.Entry<String, List<String>> k : chapterParagraphs.entrySet()) {
				result.addAll(sess.createCriteria(Paragraph.class)
						.add(Restrictions.eq("chapterName", "IV"))
						.add(Restrictions.in("paragraphName", vals))
						.list());
				//}
			}
			return new ArrayList<>(result);
		}
		return new ArrayList<>();
	}

	@Override
	public Paragraph getParagraph(String chapterName, String paragraphName) {
		if (chapterName != null && paragraphName != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return (Paragraph) sess.createCriteria(Paragraph.class)
					.add(Restrictions.eq("chapterName", chapterName))
					.add(Restrictions.eq("paragraphName", paragraphName))
					.uniqueResult();
		}
		return null;
	}

	@Override
	public List<AddedDocument> getAddedDocuments(String chapterName, String paragraphName) {
		if (chapterName != null && paragraphName != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return (List<AddedDocument>) sess.createCriteria(AddedDocument.class)
					.add(Restrictions.eq("chapterName", chapterName))
					.add(Restrictions.eq("paragraphName", paragraphName))
					.list();
		}
		return new ArrayList<>();
	}

	@Override
	public String getShortText(Long nameId, String lng) {
		if (nameId != null && lng != null) {
			Session sess = getSessionFactory().getCurrentSession();
			return ((NameTranslation) sess.createCriteria(NameTranslation.class)
					.createAlias("name", "n")
					.add(Restrictions.eq("n.id", nameId))
					.add(Restrictions.eq("languageCv", lng))
					.uniqueResult()).getShortText();
		}
		return null;
	}

}
