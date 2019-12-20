package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.TimeTable

interface TimeTableLogic : EntityPersister<TimeTable, String> {
    suspend fun createTimeTable(timeTable: TimeTable): TimeTable?
    fun deleteTimeTables(ids: List<String>): Flow<DocIdentifier>
    suspend fun getTimeTable(timeTableId: String): TimeTable?
    fun getTimeTablesByPeriodAndAgendaId(startDate: Long, endDate: Long, agendaId: String): Flow<TimeTable>
    fun getTimeTablesByAgendaId(agendaId: String): Flow<TimeTable>
    suspend fun modifyTimeTable(timeTable: TimeTable): TimeTable?
}
