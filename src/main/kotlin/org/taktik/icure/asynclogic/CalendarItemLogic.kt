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

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.CalendarItemDAO
import org.taktik.icure.entities.CalendarItem

interface CalendarItemLogic : EntityPersister<CalendarItem, String> {
    suspend fun createCalendarItem(calendarItem: CalendarItem): CalendarItem?
    fun deleteCalendarItems(ids: List<String>): Flow<DocIdentifier>
    suspend fun getCalendarItem(calendarItemId: String): CalendarItem?
    fun getCalendarItemByPeriodAndHcPartyId(startDate: Long, endDate: Long, hcPartyId: String): Flow<CalendarItem>
    fun getCalendarItemByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<CalendarItem>
    fun listCalendarItemsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<CalendarItem>

    suspend fun modifyCalendarItem(calendarItem: CalendarItem): CalendarItem?
    fun getGenericDAO(): CalendarItemDAO
    fun getCalendarItems(ids: List<String>): Flow<CalendarItem>
}
