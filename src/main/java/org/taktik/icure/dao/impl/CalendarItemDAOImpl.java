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
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.CalendarItemDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.CalendarItem;

import java.util.List;


@Repository("calendarItemDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItem' && !doc.deleted) emit( null, doc._id )}")
public class CalendarItemDAOImpl extends GenericDAOImpl<CalendarItem> implements CalendarItemDAO {

    @Autowired
    public CalendarItemDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
        super(CalendarItem.class, db, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_hcparty_and_startdate", map = "classpath:js/calendarItem/by_hcparty_and_startdate.js")
    public List<CalendarItem> listCalendarItemByStartDateAndHcPartyId(Long startDate, Long endDate, String hcPartyId) {
        ComplexKey from = ComplexKey.of(
                hcPartyId,
                startDate
        );
        ComplexKey to = ComplexKey.of(
                hcPartyId,
                endDate == null ? ComplexKey.emptyObject() : endDate
        );

        ViewQuery viewQuery = createQuery("by_hcparty_and_startdate")
                .startKey(from)
                .endKey(to)
                .includeDocs(false);

        List<CalendarItem> calendarItems = db.queryView(viewQuery, CalendarItem.class);

        return calendarItems;
    }

    @Override
    @View(name = "by_hcparty_and_enddate", map = "classpath:js/calendarItem/by_hcparty_and_enddate.js")
    public List<CalendarItem> listCalendarItemByEndDateAndHcPartyId(Long startDate, Long endDate, String hcPartyId) {
        ComplexKey from = ComplexKey.of(
                hcPartyId,
                startDate
        );
        ComplexKey to = ComplexKey.of(
                hcPartyId,
                endDate == null ? ComplexKey.emptyObject() : endDate
        );

        ViewQuery viewQuery = createQuery("by_hcparty_and_enddate")
                .startKey(from)
                .endKey(to)
                .includeDocs(false);

        List<CalendarItem> calendarItems = db.queryView(viewQuery, CalendarItem.class);

        return calendarItems;
    }

    @Override
    public List<CalendarItem> listCalendarItemByPeriodAndHcPartyId(Long startDate, Long endDate, String hcPartyId) {
        List<CalendarItem> calendarItems = this.listCalendarItemByStartDateAndHcPartyId(startDate, endDate, hcPartyId);
        List<CalendarItem> calendarItemsEnd = this.listCalendarItemByEndDateAndHcPartyId(startDate, endDate, hcPartyId);

        if (calendarItems == null && calendarItemsEnd != null) {
            return calendarItemsEnd;
        }
        if (calendarItems != null && calendarItemsEnd == null) {
            return calendarItems;
        }
        if (!calendarItemsEnd.isEmpty()) {
            for (CalendarItem item : calendarItemsEnd) {
                Boolean toAdd = true;
                for (CalendarItem itemTest : calendarItems) {
                    if (itemTest.getId().equals(item.getId())) {
                        toAdd = false;
                    }
                }
                if (toAdd) {
                    calendarItems.add(item);
                }
            }
        }
        return calendarItems;
    }

    @Override
    @View(name = "by_agenda_and_startdate", map = "classpath:js/calendarItem/by_agenda_and_startdate.js")
    public List<CalendarItem> listCalendarItemByStartDateAndAgendaId(Long startDate, Long endDate, String agendaId) {
        ComplexKey from = ComplexKey.of(
            agendaId,
            startDate
        );
        ComplexKey to = ComplexKey.of(
            agendaId,
            endDate == null ? ComplexKey.emptyObject() : endDate
        );

        ViewQuery viewQuery = createQuery("by_agenda_and_startdate")
            .startKey(from)
            .endKey(to)
            .includeDocs(false);

        return db.queryView(viewQuery, CalendarItem.class);
    }

    @Override
    @View(name = "by_agenda_and_enddate", map = "classpath:js/calendarItem/by_agenda_and_enddate.js")
    public List<CalendarItem> listCalendarItemByEndDateAndAgendaId(Long startDate, Long endDate, String agenda) {
        ComplexKey from = ComplexKey.of(
            agenda,
            startDate
        );
        ComplexKey to = ComplexKey.of(
            agenda,
            endDate == null ? ComplexKey.emptyObject() : endDate
        );

        ViewQuery viewQuery = createQuery("by_agenda_and_enddate")
            .startKey(from)
            .endKey(to)
            .includeDocs(false);

        return db.queryView(viewQuery, CalendarItem.class);
    }

    @Override
    public List<CalendarItem> listCalendarItemByPeriodAndAgendaId(Long startDate, Long endDate, String agendaId) {
        List<CalendarItem> calendarItems = this.listCalendarItemByStartDateAndAgendaId(startDate, endDate, agendaId);
        List<CalendarItem> calendarItemsEnd = this.listCalendarItemByEndDateAndAgendaId(startDate, endDate, agendaId);

        if (calendarItems == null && calendarItemsEnd != null) {
            return calendarItemsEnd;
        }
        if (calendarItems != null && calendarItemsEnd == null) {
            return calendarItems;
        }
        if (!calendarItemsEnd.isEmpty()) {
            for (CalendarItem item : calendarItemsEnd) {
                Boolean toAdd = true;
                for (CalendarItem itemTest : calendarItems) {
                    if (itemTest.getId().equals(item.getId())) {
                        toAdd = false;
                    }
                }
                if (toAdd) {
                    calendarItems.add(item);
                }
            }
        }
        return calendarItems;
    }
}
