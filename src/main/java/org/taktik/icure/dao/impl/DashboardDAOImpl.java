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

import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.DashboardDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.entities.Dashboard;

import java.util.List;

/**
 * Created by aduchate on 19/07/13, 16:23
 */
@Repository("dashboardDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Dashboard' && !doc.deleted) emit( doc._id, null)}")
class DashboardDAOImpl extends GenericDAOImpl<Dashboard> implements DashboardDAO {
    @Autowired
    public DashboardDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(Dashboard.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Dashboard' && !doc.deleted && doc.guid != null) emit(doc.guid, null )}")
    public Dashboard findByGuid(String guid) {
        List<Dashboard> result = queryView("by_guid", guid);
        if (result.size() == 0) return null;
        return result.get(0);
    }
}
