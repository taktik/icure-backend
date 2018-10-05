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
import org.taktik.icure.dao.CalendarItemTypeDAO;
import org.taktik.icure.entities.CalendarItemType;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.CalendarItemTypeLogic;
import org.taktik.icure.logic.ICureSessionLogic;

import java.util.List;

@Service
public class CalendarItemTypeLogicImpl extends GenericLogicImpl<CalendarItemType, CalendarItemTypeDAO> implements CalendarItemTypeLogic {

	private CalendarItemTypeDAO calendarItemTypeDAO;
	private ICureSessionLogic sessionLogic;

	@Override
	public CalendarItemType createCalendarItemType(CalendarItemType calendarItemType) {
		return calendarItemTypeDAO.create(calendarItemType);
	}

    @Override
    public List<String> deleteCalendarItemTypes(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
    public CalendarItemType getCalendarItemType(String calendarItemTypeId) {
        return calendarItemTypeDAO.get(calendarItemTypeId);
    }

    @Override
    public CalendarItemType modifyCalendarTypeItem(CalendarItemType calendarItemType) {
        return calendarItemTypeDAO.save(calendarItemType);
    }

    @Override
    public List<CalendarItemType> getAllEntitiesIncludeDelete() {
        return calendarItemTypeDAO.getAllEntitiesIncludeDelete();
    }

    @Autowired
  public void setCalendarItemDAO(CalendarItemTypeDAO calendarItemTypeDAO) {
		this.calendarItemTypeDAO = calendarItemTypeDAO;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	protected CalendarItemTypeDAO getGenericDAO() {
		return calendarItemTypeDAO;
	}
}
