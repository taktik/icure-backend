package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.CalendarItemType
import java.net.URI

interface CalendarItemTypeDAO : GenericDAO<CalendarItemType> {
    fun getAllEntitiesIncludeDelete(): Flow<CalendarItemType>
}
