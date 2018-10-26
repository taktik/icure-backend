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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.KeywordDAO;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.Keyword;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.logic.KeywordLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.validation.aspect.Check;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@org.springframework.stereotype.Service
public class KeywordLogicImpl extends GenericLogicImpl<Keyword, KeywordDAO> implements KeywordLogic {
	private static final Logger log = LoggerFactory.getLogger(KeywordLogicImpl.class);


	private KeywordDAO keywordDAO;
	private UUIDGenerator uuidGenerator;
	private ICureSessionLogic sessionLogic;

	@Autowired
	public void setKeywordDAO(KeywordDAO keywordDAO) {
		this.keywordDAO = keywordDAO;
	}
	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}
	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
	@Override
	protected KeywordDAO getGenericDAO() {
		return keywordDAO;
	}

	@Override
	public Keyword createKeyword(@Check @NotNull Keyword keyword) {
		List<Keyword> createdKeywords = new ArrayList<>(1);
		try {
			// Setting Keyword attributes
			keyword.setId(uuidGenerator.newGUID().toString());
			createEntities(Collections.singleton(keyword), createdKeywords);
		} catch (Exception e) {
			log.error("createKeyword: " + e.getMessage());
			throw new IllegalArgumentException("Invalid Keyword", e);
		}
		return createdKeywords.size() == 0 ? null:createdKeywords.get(0);
	}

	@Override
	public Keyword getKeyword(String keywordId) {
		return keywordDAO.getKeyword(keywordId);
	}


	@Override
	public Set<String> deleteKeywords(Set<String> ids) {
		try {
			deleteEntities(ids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return ids;
	}

	@Override
	public Keyword modifyKeyword(@Check @NotNull Keyword keyword) {
		try {
			updateEntities(Collections.singleton(keyword));
			return getKeyword(keyword.getId());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Keyword", e);
		}
	}

	@Override
	public List<Keyword> getKeywordsByUser(String userId) {
		return keywordDAO.getByUserId(userId);
	}

}
