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
import java.util.jar.Manifest;

import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.applications.utils.JarUtils;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.dao.ICureDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.logic.ICureLogic;
import org.taktik.icure.logic.PropertyLogic;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Service
public class ICureLogicImpl implements ICureLogic {
	ICureDAO iCureDAO;
	private PropertyLogic propertyLogic;
	List<CouchDbRepositorySupport> allDaos;

	@Override
	public Map<String,Number> getIndexingStatus() {
		return iCureDAO.getIndexingStatus();
	}

	@Override
	public void updateDesignDoc(String daoEntityName) {
		allDaos.stream()
				.filter(dao -> dao.getClass().getSimpleName().startsWith(daoEntityName+"DAO"))
				.findFirst()
				.ifPresent(CouchDbRepositorySupport::forceInitStandardDesignDocument);
	}

	@Override
	public Response getVersion() {
		Manifest manifest = JarUtils.getManifest();
		if (manifest != null) {
			return Response.ok(manifest.getMainAttributes().getValue("Build-revision")).build();
		} else {
			return Response.ok(propertyLogic.getSystemPropertyValue(PropertyTypes.System.VERSION.getIdentifier())).build();
		}
	}

	@Autowired
	public void setiCureDAO(ICureDAO iCureDAO) {
		this.iCureDAO = iCureDAO;
	}

	@Autowired
	public void setAllDaos(List<CouchDbRepositorySupport> allDaos) {
		this.allDaos = allDaos;
	}

	@Context
	public void setPropertyLogic(PropertyLogic propertyLogic) {
		this.propertyLogic = propertyLogic;
	}


}
