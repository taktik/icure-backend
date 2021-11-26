/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asyncdao


import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.CalendarItem

interface CalendarItemDAO : GenericDAO<CalendarItem> {

    fun listCalendarItemByStartDateAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    fun listCalendarItemByStartDateAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem>

    fun listCalendarItemByEndDateAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    fun listCalendarItemByEndDateAndAgendaId(startDate: Long?, endDate: Long?, agenda: String): Flow<CalendarItem>

    fun listCalendarItemByPeriodAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem>

    fun listCalendarItemByPeriodAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem>

    fun listAccessLogsByHcPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<CalendarItem>

    fun listCalendarItemsByRecurrenceId (recurrenceId:String): Flow<CalendarItem>
}
