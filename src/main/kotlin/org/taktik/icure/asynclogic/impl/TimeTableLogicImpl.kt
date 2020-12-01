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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.TimeTableDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.TimeTableLogic
import org.taktik.icure.entities.TimeTable

@Service
class TimeTableLogicImpl(private val timeTableDAO: TimeTableDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<TimeTable, TimeTableDAO>(sessionLogic), TimeTableLogic {
    override suspend fun createTimeTable(timeTable: TimeTable) = fix(timeTable) { timeTable ->
        timeTableDAO.create(timeTable)
    }

    override fun deleteTimeTables(ids: List<String>): Flow<DocIdentifier> {
        return deleteByIds(ids)
    }

    override suspend fun getTimeTable(timeTableId: String): TimeTable? {
        return timeTableDAO.get(timeTableId)
    }

    override fun getTimeTablesByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<TimeTable> = flow {
        emitAll(timeTableDAO.listTimeTableByPeriodAndAgendaId(startDate, endDate, agendaId))
    }

    override fun getTimeTablesByAgendaId(agendaId: String): Flow<TimeTable> = flow {
        emitAll(timeTableDAO.listTimeTableByAgendaId(agendaId))
    }

    override suspend fun modifyTimeTable(timeTable: TimeTable) = fix(timeTable) { timeTable ->
        timeTableDAO.save(timeTable)
    }

    override fun getGenericDAO(): TimeTableDAO {
        return timeTableDAO
    }

}
