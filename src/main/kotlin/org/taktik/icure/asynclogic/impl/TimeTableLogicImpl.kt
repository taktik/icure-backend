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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.TimeTableDAO
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.utils.reEmit

interface TimeTableLogic {
    suspend fun createTimeTable(timeTable: TimeTable): TimeTable?
    suspend fun deleteTimeTables(ids: List<String>)
    suspend fun getTimeTable(timeTableId: String): TimeTable?
    fun getTimeTablesByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<TimeTable>
    fun getTimeTablesByAgendaId(agendaId: String): Flow<TimeTable>
    suspend fun modifyTimeTable(timeTable: TimeTable): TimeTable?
}

@Service
class TimeTableLogicImpl(private val timeTableDAO: TimeTableDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<TimeTable, TimeTableDAO>(sessionLogic), TimeTableLogic {
    override suspend fun createTimeTable(timeTable: TimeTable): TimeTable? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return timeTableDAO.create(dbInstanceUri, groupId, timeTable)
    }

    override suspend fun deleteTimeTables(ids: List<String>) {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        deleteByIds(dbInstanceUri, groupId, ids)
    }

    override suspend fun getTimeTable(timeTableId: String): TimeTable? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return timeTableDAO.get(dbInstanceUri, groupId, timeTableId)
    }

    override fun getTimeTablesByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<TimeTable> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        timeTableDAO.listTimeTableByPeriodAndAgendaId(dbInstanceUri, groupId, startDate, endDate, agendaId).collect { emit(it) }
    }

    override fun getTimeTablesByAgendaId(agendaId: String): Flow<TimeTable> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        timeTableDAO.listTimeTableByAgendaId(dbInstanceUri, groupId, agendaId).collect { emit(it) }
    }

    override suspend fun modifyTimeTable(timeTable: TimeTable): TimeTable? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return timeTableDAO.save(dbInstanceUri, groupId, timeTable)
    }

    override fun getGenericDAO(): TimeTableDAO {
        return timeTableDAO
    }
}
