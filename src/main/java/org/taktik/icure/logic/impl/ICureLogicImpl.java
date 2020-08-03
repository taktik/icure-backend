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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.applications.utils.JarUtils;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.dao.ICureDAO;
import org.taktik.icure.dao.impl.GenericDAOImpl;
import org.taktik.icure.logic.ICureLogic;
import org.taktik.icure.logic.PropertyLogic;

@Service
public class ICureLogicImpl implements ICureLogic {
	ICureDAO iCureDAO;
	private PropertyLogic propertyLogic;
	List<GenericDAOImpl> allDaos;

	@Override
	public Map<String,Number> getIndexingStatus() {
		return iCureDAO.getIndexingStatus();
	}

	@Override
	public void updateDesignDoc(String daoEntityName, boolean warmup) {
		allDaos.stream()
				.filter(dao -> dao.getClass().getSimpleName().startsWith(daoEntityName+"DAO"))
				.findFirst()
				.ifPresent(dao -> {
				    dao.forceInitStandardDesignDocument();
				    if(warmup) {
				        dao.warmupIndex();
                    }
                });
	}

	@Override
	public String getVersion() {
		Manifest manifest = JarUtils.getManifest();
		if (manifest != null) {
			return manifest.getMainAttributes().getValue("Build-revision").trim();
		} else {
			return propertyLogic.getSystemPropertyValue(PropertyTypes.System.VERSION.getIdentifier()).toString().trim();
		}
	}

	@Autowired
	public void setiCureDAO(ICureDAO iCureDAO) {
		this.iCureDAO = iCureDAO;
	}

	@Autowired
	public void setAllDaos(List<GenericDAOImpl> allDaos) {
		this.allDaos = allDaos;
	}

	@Autowired
	public void setPropertyLogic(PropertyLogic propertyLogic) {
		this.propertyLogic = propertyLogic;
	}
}
