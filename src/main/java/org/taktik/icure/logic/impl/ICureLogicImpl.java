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

package org.taktik.icure.logic.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.dao.ICureDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.logic.ICureLogic;

@Service
public class ICureLogicImpl implements ICureLogic {
	ICureDAO iCureDAO;

	@Override
	public Map<String,Number> getIndexingStatus() {
		return iCureDAO.getIndexingStatus();
	}

	@Autowired
	public void setiCureDAO(ICureDAO iCureDAO) {
		this.iCureDAO = iCureDAO;
	}

}
