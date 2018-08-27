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
import org.taktik.icure.dao.TimeTableDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.entities.TimeTable;

import java.util.ArrayList;
import java.util.List;

@Repository("timeTableDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.TimeTable' && !doc.deleted) emit( null, doc._id )}")
public class TimeTableDAOImpl extends GenericDAOImpl<TimeTable> implements TimeTableDAO {

    @Autowired
    public TimeTableDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
        super(TimeTable.class, db, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_agenda", map = "classpath:js/timeTable/by_agenda.js")
    public List<TimeTable> listTimeTableByAgendaId(String agendaId) {
        ViewQuery viewQuery = createQuery("by_agenda")
                .startKey(agendaId)
                .endKey(agendaId)
                .includeDocs(false);

        List<TimeTable> timeTables = db.queryView(viewQuery, TimeTable.class);

        return timeTables;
    }

    @Override
    @View(name = "by_agenda_and_startdate", map = "classpath:js/timeTable/by_agenda_and_startdate.js")
    public List<TimeTable> listTimeTableByStartDateAndAgendaId(Long startDate, Long endDate, String agendaId) {
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

        List<TimeTable> timeTables = db.queryView(viewQuery, TimeTable.class);

        return timeTables;
    }

    @Override
    @View(name = "by_agenda_and_enddate", map = "classpath:js/timeTable/by_agenda_and_enddate.js")
    public List<TimeTable> listTimeTableByEndDateAndAgendaId(Long startDate, Long endDate, String agendaId) {
        ComplexKey from = ComplexKey.of(
                agendaId,
                startDate
        );
        ComplexKey to = ComplexKey.of(
                agendaId,
                endDate == null ? ComplexKey.emptyObject() : endDate
        );

        ViewQuery viewQuery = createQuery("by_agenda_and_enddate")
                .startKey(from)
                .endKey(to)
                .includeDocs(false);

        List<TimeTable> timeTables = db.queryView(viewQuery, TimeTable.class);

        return timeTables;
    }

    @Override
    public List<TimeTable> listTimeTableByPeriodAndAgendaId(Long startDate, Long endDate, String agendaId) {
        List<TimeTable> timeTablesStart = this.listTimeTableByStartDateAndAgendaId(startDate, endDate, agendaId);
        List<TimeTable> timeTablesEnd = this.listTimeTableByEndDateAndAgendaId(startDate, endDate, agendaId);
        /* Special case : timeTableStart < research.start < rechearch.end < timetableEnd*/
        List<TimeTable> timeTableStartBefore = this.listTimeTableByStartDateAndAgendaId(0l,startDate,agendaId);
        List<TimeTable> timeTableEndAfter = this.listTimeTableByEndDateAndAgendaId(endDate,999999999999999l,agendaId);
        List<TimeTable> timeTableMerged = new ArrayList<>();

        /* Add in merged TimeTable that are in both timeTableStartBefore AND timeTableEndAfter, avoiding duplicates*/
        for(TimeTable elem : timeTableStartBefore){
            if(listContains(timeTableEndAfter,elem) && !listContains(timeTableMerged,elem)){
                timeTableMerged.add(elem);
            }
        }
        /* Add in merged elem that are in timeTablesStart, avoiding duplicate */
        for(TimeTable elem : timeTablesStart){
            if(!listContains(timeTableMerged,elem)){
                timeTableMerged.add(elem);
            }
        }

        /* Add in merged elem that are in timeTablesEnd, avoiding duplicate */
        for(TimeTable elem : timeTablesEnd){
            if(!listContains(timeTableMerged,elem)){
                timeTableMerged.add(elem);
            }
        }
        return timeTableMerged;
    }

    private boolean listContains(List<TimeTable> list, TimeTable obj){
        for(TimeTable elem : list){
            if(elem.getId().equals(obj.getId())){
                return true;
            }
        }
        return false;
    }
}
