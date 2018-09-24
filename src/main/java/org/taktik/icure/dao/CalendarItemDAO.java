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

package org.taktik.icure.dao;


import org.ektorp.support.View;
import org.taktik.icure.entities.CalendarItem;

import java.time.Instant;
import java.util.List;

public interface CalendarItemDAO extends GenericDAO<CalendarItem> {

    List<CalendarItem> listCalendarItemByStartDateAndHcPartyId(Long startDate, Long endDate, String hcPartyId);

    @View(name = "by_agenda_and_startdate", map = "classpath:js/calendarItem/by_agenda_and_startdate.js")
    List<CalendarItem> listCalendarItemByStartDateAndAgendaId(Long startDate, Long endDate, String agendaId);

    List<CalendarItem> listCalendarItemByEndDateAndHcPartyId(Long startDate, Long endDate, String hcPartyId);

    @View(name = "by_agenda_and_enddate", map = "classpath:js/calendarItem/by_agenda_and_enddate.js")
    List<CalendarItem> listCalendarItemByEndDateAndAgendaId(Long startDate, Long endDate, String agenda);

    List<CalendarItem> listCalendarItemByPeriodAndHcPartyId(Long startDate, Long endDate, String hcPartyId);

    List<CalendarItem> listCalendarItemByPeriodAndAgendaId(Long startDate, Long endDate, String agendaId);
}
