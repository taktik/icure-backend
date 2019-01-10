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

import java.util.List;
import java.util.Map;

import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.dao.ICureDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.logic.ICureLogic;

@Service
public class ICureLogicImpl implements ICureLogic {
	ICureDAO iCureDAO;
	List<CouchDbRepositorySupport> allDaos;

	@Override
	public Map<String,Number> getIndexingStatus(String groupId) {
		return iCureDAO.getIndexingStatus(groupId);
	}

	@Override
	public void updateDesignDoc(String groupId, String daoEntityName) {
		allDaos.stream()
				.filter(dao -> dao.getClass().getSimpleName().startsWith(daoEntityName+"DAO"))
				.findFirst()
				.ifPresent(couchDbRepositorySupport -> couchDbRepositorySupport.forceInitStandardDesignDocument(groupId));
	}

	@Autowired
	public void setiCureDAO(ICureDAO iCureDAO) {
		this.iCureDAO = iCureDAO;
	}

	@Autowired
	public void setAllDaos(List<CouchDbRepositorySupport> allDaos) {
		this.allDaos = allDaos;
	}

}
