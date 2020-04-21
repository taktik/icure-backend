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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.CalendarItemTypeDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CalendarItemTypeLogic
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.exceptions.DeletionException

@ExperimentalCoroutinesApi
@Service
class CalendarItemTypeLogicImpl(private val calendarItemTypeDAO: CalendarItemTypeDAO,
                                private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<CalendarItemType, CalendarItemTypeDAO>(sessionLogic), CalendarItemTypeLogic {

    override suspend fun createCalendarItemType(calendarItemType: CalendarItemType) = fix(calendarItemType) { calendarItemType ->
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        calendarItemTypeDAO.create(dbInstanceUri, groupId, calendarItemType)
    }

    override fun deleteCalendarItemTypes(ids: List<String>): Flow<DocIdentifier> {
        try {
            return deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getCalendarItemType(calendarItemTypeId: String): CalendarItemType? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return calendarItemTypeDAO.get(dbInstanceUri, groupId, calendarItemTypeId)
    }

    override suspend fun modifyCalendarTypeItem(calendarItemType: CalendarItemType)= fix(calendarItemType) { calendarItemType ->
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        calendarItemTypeDAO.save(dbInstanceUri, groupId, calendarItemType)
    }

    override fun getAllEntitiesIncludeDelete(): Flow<CalendarItemType> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(calendarItemTypeDAO.getAllEntitiesIncludeDelete(dbInstanceUri, groupId))
    }

    override fun getGenericDAO(): CalendarItemTypeDAO {
        return calendarItemTypeDAO
    }
}
