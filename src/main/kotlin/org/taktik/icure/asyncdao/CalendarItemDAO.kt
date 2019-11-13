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

package org.taktik.icure.asyncdao


import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.entities.CalendarItem
import java.net.URI

interface CalendarItemDAO : GenericDAO<CalendarItem> {

    fun listCalendarItemByStartDateAndHcPartyId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    @View(name = "by_agenda_and_startdate", map = "classpath:js/calendarItem/by_agenda_and_startdate.js")
    fun listCalendarItemByStartDateAndAgendaId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem>

    fun listCalendarItemByEndDateAndHcPartyId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    @View(name = "by_agenda_and_enddate", map = "classpath:js/calendarItem/by_agenda_and_enddate.js")
    fun listCalendarItemByEndDateAndAgendaId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, agenda: String): Flow<CalendarItem>

    fun listCalendarItemByPeriodAndHcPartyId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    fun listCalendarItemByPeriodAndAgendaId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem>
}