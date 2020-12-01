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

    override suspend fun createCalendarItem(calendarItem: CalendarItem) = fix(calendarItem) { calendarItem ->
        calendarItemDAO.create(calendarItem)
    }

    override fun deleteCalendarItems(ids: List<String>): Flow<DocIdentifier> {
        try {
            return deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override suspend fun getCalendarItem(calendarItemId: String): CalendarItem? {
        return calendarItemDAO.get(calendarItemId)
    }

    override fun getCalendarItemByPeriodAndHcPartyId(startDate: Long, endDate: Long, hcPartyId: String): Flow<CalendarItem> = flow {
        emitAll(calendarItemDAO.listCalendarItemByPeriodAndHcPartyId(startDate, endDate, hcPartyId))
    }

    override fun getCalendarItemByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<CalendarItem> = flow {
        emitAll(calendarItemDAO.listCalendarItemByPeriodAndAgendaId(startDate, endDate, agendaId))
    }

    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>) = flow {
            emitAll(calendarItemDAO.findByHcPartyPatient(hcPartyId, secretPatientKeys))
    }

    override fun getCalendarItemByIds(ids: List<String>): Flow<CalendarItem> = flow {
        calendarItemDAO.getList(ids).collect { emit(it) }
    }


    override suspend fun modifyCalendarItem(calendarItem: CalendarItem) = fix(calendarItem) { calendarItem ->
        calendarItemDAO.save(calendarItem)
    }

    override fun getGenericDAO(): CalendarItemDAO {
        return calendarItemDAO
    }
}
