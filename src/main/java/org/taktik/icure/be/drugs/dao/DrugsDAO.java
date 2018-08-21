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


package org.taktik.icure.be.drugs.dao;

import java.io.IOException;
import java.util.List;

import org.taktik.icure.be.drugs.*;
import org.taktik.icure.be.drugs.Atc;
import org.taktik.icure.be.drugs.civics.*;
import org.taktik.icure.be.drugs.dto.DocId;
import org.taktik.icure.be.drugs.dto.FullTextSearchResult;
import org.taktik.icure.be.drugs.dto.MpId;
import org.taktik.icure.be.drugs.dto.MppId;

/**
 * DAO interface of the Drugs database system
 * @author abaudoux
 *
 */
public interface DrugsDAO {
	/**
	 * Opens a session to the datastore
	 */
	public void openDataStoreSession();
	
	/**
	 * Closes a session to the datastore
	 */
	public void closeDataStoreSession();
	
	/**
	 * Retrieve a list of MPP's by name.
	 * 
	 * 
	 * @param searchString the first letters of the mpp
	 * @param lang The language of the Search
	 * @param types The types of the dbs to search in. Specify null or empty list 
	 * To search in all dbs.
	 * @param first the first result to return
	 * @param count the number of results to return
	 * @return The list of found Mpp in preview type.
	 */
	public List<Mpp> getMedecinePackages(String searchString,String lang,List<String>types,int first,int count);

	/**
	 * Retrieve a list of MPP's by name or ingredients' name.
	 * 
	 * 
	 * @param searchString the first letters of the mpp or its ingredients
	 * @param lang The language of the Search
	 * @param types The types of the dbs to search in. Specify null or empty list 
	 * To search in all dbs.
	 * @param first the first result to return
	 * @param count the number of results to return
	 * @return The list of found Mpp in preview type.
	 */
	public List<Mpp> getMedecinePackagesFromIngredients(String searchString,
			String lang, List<String> types, int first, int count);

	/**
	 * Load a Mpp and pre-fetch some of its relations : Gal and MP
	 * @param medecinePackageID The id of the Mp
	 * @return The loaded Mpp
	 */
	public Mpp getInfos(MppId medecinePackageID);

	/**
	 * Load a Mp and pre-fetch some of its relations
	 * @param medecineID The id of the Medecine
	 * @return The loaded Mp
	 */
	public Mp getExtendedInfos(MpId medecineID);
	
	/**
	 * Load a documentation 
	 * @param docID
	 * @return
	 */
	public Doc getExtendedInfos(DocId docID);
	
	/**
	 * Load a documentation without prefetching anything
	 * @param docID
	 * @return
	 */
	public Doc getDoc(DocId docID);

	/**
	 * Load the root documentation nodes.
	 */
	public List<Doc> getRootDocs(String lang);

	/**
	 * Retrieve full infos about a Mp
	 */
	public Mp getFullMpInfos(MpId mpId);
	
	/**
	 * Perform a full-text search through the Drugs database.
	 * @return
	 * @throws IOException 
	 */
	public List<FullTextSearchResult> fullTextSearch(String search,String lang,List<String> classes,List<String> types,int from, int count) throws IOException;

	/**
	 * Retrieve a mpp without prefetching anything
	 */
	public Mpp getMpp(MppId mppId);

	/**
	 * Installs a new Drugs database from path
	 * @param path
	 */
	public void installNewDrugsDatabase(String path);

	/**
	 * Is there any drug database available?
	 * @return
	 */
	public boolean isDataBasePresent();

	/**
	 * Initializes the drugs database systems
	 */
	public void initDrugsDatabase();
	
	/**
	 * Release any connections to the drugs database.
	 */
	public void stopDrugsDatabase();

	/**
	 * Retrieve a mp without prefetching anything
	 */
	public Mp getMp(MpId mpId);


    public Atc getAtc(MppId medecinePackageID);

    public List<Mp> getMpsWithAtc(Atc atc);

	public List<Mpp> getMppsWithAtc(Atc atc);

	public List<Mpp> getCheapMppsWithInn(String inn,String lang);

    public List<Mpp> getMppsWithInn(String inn,String lang);

    public List<Iam> getIams(String id, String lang);

    public Ampp getAmpp(MppId mppId);

    public Therapy getTherapy(Long therapyId);

    public Paragraph getParagraph(Therapy therapy);

    Verse getHeaderVerse(Paragraph paragraph);

    List<Verse> getChildrenVerses(Verse verse);

    List<Paragraph> findParagraphs(String searchString, String language);

    Paragraph getParagraph(String chapterName, String paragraphName);

    List<AddedDocument> getAddedDocuments(String chapterName, String paragraphName);

    String getShortText(Long nameId, String fr);

    List<Paragraph> findParagraphsWithCnk(Long cnk, String language);
}