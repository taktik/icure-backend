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
import org.taktik.icure.dao.TimeTableDAO;
import org.taktik.icure.entities.TimeTable;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.TimeTableLogic;
import org.taktik.icure.logic.ICureSessionLogic;

import java.util.List;

@Service
public class TimeTableLogicImpl extends GenericLogicImpl<TimeTable, TimeTableDAO> implements TimeTableLogic {

	private TimeTableDAO timeTableDAO;
	private ICureSessionLogic sessionLogic;

	@Override
	public TimeTable createTimeTable(TimeTable timeTable) {
		return timeTableDAO.create(timeTable);
	}

    @Override
    public List<String> deleteTimeTables(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
	public TimeTable getTimeTable(String timeTableId) {
		return timeTableDAO.get(timeTableId);
	}

	@Override
	public List<TimeTable> getTimeTablesByPeriodAndAgendaId(Long startDate, Long endDate, String agendaId){
		return timeTableDAO.listTimeTableByPeriodAndAgendaId(startDate,endDate,agendaId);
	}

	@Override
	public List<TimeTable> getTimeTablesByAgendaId(String agendaId){
		return timeTableDAO.listTimeTableByAgendaId(agendaId);
	}
	@Override
	public TimeTable modifyTimeTable(TimeTable timeTable) {
		return timeTableDAO.save(timeTable);
	}

	@Autowired
  public void setTimeTableDAO(TimeTableDAO timeTableDAO) {
		this.timeTableDAO = timeTableDAO;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	protected TimeTableDAO getGenericDAO() {
		return timeTableDAO;
	}
}
