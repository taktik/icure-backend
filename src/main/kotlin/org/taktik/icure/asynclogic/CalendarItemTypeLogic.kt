package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.CalendarItemTypeDAO
import org.taktik.icure.entities.CalendarItemType

interface CalendarItemTypeLogic : EntityPersister<CalendarItemType, String> {
    suspend fun createCalendarItemType(calendarItemType: CalendarItemType): CalendarItemType?
    fun deleteCalendarItemTypes(ids: List<String>): Flow<DocIdentifier>

    suspend fun getCalendarItemType(calendarItemTypeId: String): CalendarItemType?

    suspend fun modifyCalendarTypeItem(calendarItemType: CalendarItemType): CalendarItemType?
    fun getAllEntitiesIncludeDelete(): Flow<CalendarItemType>
    fun getGenericDAO(): CalendarItemTypeDAO
}
