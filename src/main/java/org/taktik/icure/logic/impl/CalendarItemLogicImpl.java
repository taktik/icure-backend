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
import org.taktik.icure.dao.CalendarItemDAO;
import org.taktik.icure.entities.CalendarItem;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.CalendarItemLogic;
import org.taktik.icure.logic.ICureSessionLogic;

import java.util.List;

@Service
public class CalendarItemLogicImpl extends GenericLogicImpl<CalendarItem, CalendarItemDAO> implements CalendarItemLogic {

	private CalendarItemDAO calendarItemDAO;
	private ICureSessionLogic sessionLogic;

	@Override
	public CalendarItem createCalendarItem(CalendarItem calendarItem) {
		return calendarItemDAO.create(calendarItem);
	}

    @Override
    public List<String> deleteCalendarItems(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
	public CalendarItem getCalendarItem(String calendarItemId) {
		return calendarItemDAO.get(calendarItemId);
	}

	@Override
	public List<CalendarItem> getCalendarItemByPeriodAndHcPartyId(Long startDate, Long endDate, String hcPartyId){
		return calendarItemDAO.listCalendarItemByPeriodAndHcPartyId(startDate,endDate,hcPartyId);
	}

    @Override
    public List<CalendarItem> getCalendarItemByPeriodAndAgendaId(Long startDate, Long endDate, String agendaId){
        return calendarItemDAO.listCalendarItemByPeriodAndAgendaId(startDate,endDate,agendaId);
    }

	@Override
	public CalendarItem modifyCalendarItem(CalendarItem calendarItem) {
		return calendarItemDAO.save(calendarItem);
	}

	@Autowired
  public void setCalendarItemDAO(CalendarItemDAO calendarItemDAO) {
		this.calendarItemDAO = calendarItemDAO;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	protected CalendarItemDAO getGenericDAO() {
		return calendarItemDAO;
	}
}
