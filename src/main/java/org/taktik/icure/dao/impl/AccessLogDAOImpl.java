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

package org.taktik.icure.dao.impl;

import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.AccessLogDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.AccessLog;

import java.time.Instant;

@Repository("accessLogDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.AccessLog' && !doc.deleted) emit( null, doc._id )}")
public class AccessLogDAOImpl extends GenericDAOImpl<AccessLog> implements AccessLogDAO {

	@Autowired
	public AccessLogDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbPatient") CouchDbICureConnector db, IDGenerator idGenerator) {
		super(AccessLog.class, db, idGenerator);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "all_by_date", map = "classpath:js/accesslog/all_by_date_map.js")
	public PaginatedList<AccessLog> list(PaginationOffset pagination) {
		String key = pagination.getStartKey() == null ? null : (String) pagination.getStartKey();
		return pagedQueryView("all_by_date", key, null, pagination, false);
	}

    @Override
    @View(name = "all_by_user_date", map = "classpath:js/accesslog/all_by_user_type_and_date_map.js")
    public PaginatedList<AccessLog> findByUserAfterDate(String userId, String accessType, Instant startDate, PaginationOffset pagination, boolean descending) {
        if (startDate == null) {
            ComplexKey key = pagination.getStartKey() == null ? ComplexKey.of(userId, accessType, 0l) : (ComplexKey) pagination.getStartKey();
            return pagedQueryView("all_by_user_date", key, null, pagination, descending);
        } else {
            ComplexKey startKey = pagination.getStartKey() == null ? ComplexKey.of(userId, accessType, startDate.toEpochMilli()) : (ComplexKey) pagination.getStartKey();
            ComplexKey endKey = ComplexKey.of(userId, accessType, Long.MAX_VALUE);
            return pagedQueryView("all_by_user_date", descending?endKey:startKey, descending?startKey:endKey, pagination, descending);
        }
    }
}
