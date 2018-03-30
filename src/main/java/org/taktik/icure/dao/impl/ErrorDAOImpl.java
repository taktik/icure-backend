/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.dao.impl;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.ErrorDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.Error;

@Repository("errorDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Error' && !doc.deleted) emit( null, doc._id )}")
public class ErrorDAOImpl extends GenericDAOImpl<Error> implements ErrorDAO {
	@Autowired
	public ErrorDAOImpl(@Qualifier("couchdbConfig") CouchDbICureConnector db, IDGenerator idGenerator) {
		super(Error.class, db, idGenerator);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "all_by_user_domain", map = "classpath:js/error/all_by_user_domain_map.js")
	public List<Error> listErrorsWithDomain(String userId, String domain) {
		return queryView("all_by_user_domain", ComplexKey.of(userId, domain));
	}
}
