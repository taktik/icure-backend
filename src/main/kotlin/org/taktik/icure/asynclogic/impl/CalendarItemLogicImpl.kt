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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.CalendarItemDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CalendarItemLogic
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.exceptions.DeletionException

@ExperimentalCoroutinesApi
@Service
class CalendarItemLogicImpl(private val calendarItemDAO: CalendarItemDAO,
                            private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<CalendarItem, CalendarItemDAO>(sessionLogic), CalendarItemLogic {

    override suspend fun createCalendarItem(calendarItem: CalendarItem): CalendarItem? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return calendarItemDAO.create(dbInstanceUri, groupId, calendarItem)
    }

    override fun deleteCalendarItems(ids: List<String>): Flow<DocIdentifier> {
        try {
            return deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getCalendarItem(calendarItemId: String): CalendarItem? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return calendarItemDAO.get(dbInstanceUri, groupId, calendarItemId)
    }

    override fun getCalendarItemByPeriodAndHcPartyId(startDate: Long, endDate: Long, hcPartyId: String): Flow<CalendarItem> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(calendarItemDAO.listCalendarItemByPeriodAndHcPartyId(dbInstanceUri, groupId, startDate, endDate, hcPartyId))
    }

    override fun getCalendarItemByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<CalendarItem> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(calendarItemDAO.listCalendarItemByPeriodAndAgendaId(dbInstanceUri, groupId, startDate, endDate, agendaId))
    }

    override fun getCalendarItemByIds(ids: List<String>): Flow<CalendarItem> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        calendarItemDAO.getList(dbInstanceUri, groupId, ids).collect { emit(it) }
    }


    override suspend fun modifyCalendarItem(calendarItem: CalendarItem): CalendarItem? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return calendarItemDAO.save(dbInstanceUri, groupId, calendarItem)
    }

    override fun getGenericDAO(): CalendarItemDAO {
        return calendarItemDAO
    }
}
