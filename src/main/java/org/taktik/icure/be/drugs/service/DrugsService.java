/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.drugs.service;

import java.io.IOException;
import java.util.List;

import org.taktik.icure.be.drugs.dto.DocId;
import org.taktik.icure.be.drugs.dto.DocPreview;
import org.taktik.icure.be.drugs.dto.FullTextSearchResult;
import org.taktik.icure.be.drugs.dto.MpExtendedInfos;
import org.taktik.icure.be.drugs.dto.MpFullInfos;
import org.taktik.icure.be.drugs.dto.MpId;
import org.taktik.icure.be.drugs.dto.MpPreview;
import org.taktik.icure.be.drugs.dto.MppId;
import org.taktik.icure.be.drugs.dto.MppInfos;
import org.taktik.icure.be.drugs.dto.MppPreview;

/**
 * Published WebServices for the drugs system.
 * @author abaudoux
 *
 */

public interface DrugsService {
	/**
	 * Retrieve a list of MPP's by name.
	 * 
	 * 
	 * @param searchString the first letters of the mpp
	 * @param first the first result to return
	 * @param count the number of results to return
	 * @return The list of found Mpp in preview type.
	 */
	public List<MppPreview> getMedecinePackages(String searchString,String lang,List<String> types,int first,int count);
	
	/**
	 * Retrieve a list of MPP's by name or ingredients'.
	 * 
	 * 
	 * @param searchString the first letters of the mpp or its ingredients
	 * @param first the first result to return
	 * @param count the number of results to return
	 * @return The list of found Mpp in preview type.
	 */
	public List<MppPreview> getMedecinePackagesFromIngredients(String searchString,String lang,List<String> types, int first, int count);
	
	/**
	 * Retrieve detailed infos about a Mpp
	 * @param medecinePackageID the id of the Mpp
	 * @return The detailed infos
	 */
	public MppInfos getMppInfos(MppId medecinePackageID);
	
	/**
	 * Retrieve extended infos about a Mp
	 * @param medecinePackageID the id of the Mpp
	 * @return The detailed infos
	 */
	public MpExtendedInfos getExtentedMpInfosWithPackage(MppId medecinePackageID);
	
	/**
	 * Perform a full-text search through the Drugs database.
	 * @return
	 */
	public List<FullTextSearchResult> fullTextSearch(String search,String lang,List<String> classes,List<String> types,int first, int count) throws IOException;


	/**
	 * Retrieve a preview of a doc
	 */
	public DocPreview getDocPreview(String id,String lang);
	
	/**
	 * Retrieve combined children Docs and mps of a doc.
	 * 
	 * The first item in the list is children docs
	 * The second item in the list is children mps
	 */
	public List<Object> getChildrenDocsAndMps(DocId docID);
	
	/**
	 * Retrieve the root documentation (main chapters)
	 * @param lang
	 * @return
	 */
	public List<DocPreview> getRootDocs(String lang);
	
	/**
	 * Get a Mp from a mpp
	 * @param mppId
	 * @param lang
	 * @return
	 */
	public MpPreview getMpFromMpp(String mppId, String lang);
	
	/**
	 * Retrieve the parent documentation of a given doc.
	 */
	public DocPreview getParentDoc(DocId docId);

	/**
	 * Retrieve the related documentation of a given mp.
	 */
	public DocPreview getDocOfMp(MpId mpId);

    MpFullInfos getFullMpInfosWithPackage(MppId medecinePackageID);

    List getCheapAlternativesBasedOnAtc(MppId medecinePackageID);

    public List getInteractions(MppId medecinePackageID, List<String> otherCnks);

    List getCheapAlternativesBasedOnInn(String innClusterId, String lang);

    List<MppPreview> getInnClusters(String searchString, String lang, List<String> types, int first, int count);
}
