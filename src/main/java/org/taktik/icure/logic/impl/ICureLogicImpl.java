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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.applications.utils.JarUtils;
import org.taktik.icure.constants.PropertyTypes;
import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.dao.ICureDAO;
import org.taktik.icure.entities.Group;
import org.taktik.icure.logic.GroupLogic;
import org.taktik.icure.logic.ICureLogic;
import org.taktik.icure.logic.PropertyLogic;

import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

@Service
public class ICureLogicImpl implements ICureLogic {
	ICureDAO iCureDAO;
	GroupLogic groupLogic;
    private PropertyLogic propertyLogic;
	List<GenericDAO> allDaos;

	@Override
	public Map<String,Number> getIndexingStatus(String groupId) {
		return iCureDAO.getIndexingStatus(groupId);
	}

	@Override
	public void updateDesignDoc(String groupId, String daoEntityName) {
		Group group = groupLogic.findGroup(groupId);

		if (group == null) { throw new IllegalArgumentException("Cannot load group "+groupId); }
		allDaos.stream()
				.filter(dao -> dao.getClass().getSimpleName().startsWith(daoEntityName+"DAO"))
				.findFirst()
				.ifPresent(dao -> dao.forceInitStandardDesignDocument(group));
	}

	@Autowired
	public void setGroupLogic(GroupLogic groupLogic) {
		this.groupLogic = groupLogic;
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
	public void setAllDaos(List<GenericDAO> allDaos) {
		this.allDaos = allDaos;
	}

	@Autowired
	public void setPropertyLogic(PropertyLogic propertyLogic) {
		this.propertyLogic = propertyLogic;
	}


}
