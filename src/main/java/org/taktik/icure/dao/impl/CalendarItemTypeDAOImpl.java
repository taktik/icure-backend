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

import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.CalendarItemTypeDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.CalendarItemType;

import java.util.List;

@Repository("calendarItemTypeDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItemType' && !doc.deleted) emit( null, doc._id )}")
public class CalendarItemTypeDAOImpl extends GenericDAOImpl<CalendarItemType> implements CalendarItemTypeDAO {

    @Autowired
    public CalendarItemTypeDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
        super(CalendarItemType.class, db, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "all_and_deleted", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItemType') emit( doc._id , doc )}")
    public List<CalendarItemType> getAllEntitiesIncludeDelete() {

        ViewQuery viewQuery = createQuery("all_and_deleted").includeDocs(false);
        List<CalendarItemType> result = db.queryView(viewQuery, CalendarItemType.class);
        result.forEach(this::postLoad);

        return result;
    }
}
