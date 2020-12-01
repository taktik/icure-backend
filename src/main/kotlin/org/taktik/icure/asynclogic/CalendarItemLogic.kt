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
    fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<CalendarItem>

    suspend fun modifyCalendarItem(calendarItem: CalendarItem): CalendarItem?
    fun getGenericDAO(): CalendarItemDAO
    fun getCalendarItemByIds(ids: List<String>): Flow<CalendarItem>
}
