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

package org.taktik.icure.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.InsuranceDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.StringUtils;
import org.taktik.icure.entities.Insurance;

@Repository("insuranceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Insurance' && !doc.deleted) emit( null, doc._id )}")
public class InsuranceDAOImpl extends GenericDAOImpl<Insurance> implements InsuranceDAO {

	@Autowired
	public InsuranceDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector db, IDGenerator idGenerator) {
		super(Insurance.class, db, idGenerator);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "all_by_code", map = "classpath:js/insurance/all_by_code_map.js")
	public List<Insurance> listByCode(String code) {
		return queryView("all_by_code", code);
	}

	@Override
	@View(name = "all_by_name", map = "classpath:js/insurance/all_by_name_map.js")
	public List<Insurance> listByName(String name) {
		name = StringUtils.sanitizeString(name);
		ViewQuery viewQuery = createQuery("all_by_name").startKey(ComplexKey.of(name)).endKey(ComplexKey.of(name+ "\uFFF0")).includeDocs(false);
		Set<String> ids = new HashSet<>(db.queryView(viewQuery, String.class));
		return getList(ids);
	}
}
