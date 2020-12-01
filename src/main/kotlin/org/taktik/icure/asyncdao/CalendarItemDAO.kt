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
import org.taktik.icure.entities.CalendarItem
import java.net.URI

interface CalendarItemDAO : GenericDAO<CalendarItem> {

    fun listCalendarItemByStartDateAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    fun listCalendarItemByStartDateAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem>

    fun listCalendarItemByEndDateAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    fun listCalendarItemByEndDateAndAgendaId(startDate: Long?, endDate: Long?, agenda: String): Flow<CalendarItem>

    fun listCalendarItemByPeriodAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    fun listCalendarItemByPeriodAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem>

    fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<CalendarItem>
}
