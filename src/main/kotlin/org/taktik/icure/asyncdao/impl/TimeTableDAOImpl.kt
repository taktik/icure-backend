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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.TimeTableDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.distinctById
import java.net.URI

@Repository("timeTableDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.TimeTable' && !doc.deleted) emit( null, doc._id )}")
class TimeTableDAOImpl (couchDbProperties: CouchDbProperties,
                        @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<TimeTable>(couchDbProperties, TimeTable::class.java, couchDbDispatcher, idGenerator), TimeTableDAO {

	@View(name = "by_agenda", map = "classpath:js/timeTable/by_agenda.js")
	override fun listTimeTableByAgendaId(agendaId: String): Flow<TimeTable> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery<TimeTable>("by_agenda")
				.startKey(agendaId)
				.endKey(agendaId)
				.includeDocs(true)
        return client.queryViewIncludeDocs<String, String, TimeTable>(viewQuery).map{it.doc}
	}

	@View(name = "by_agenda_and_startdate", map = "classpath:js/timeTable/by_agenda_and_startdate.js")
	override fun listTimeTableByStartDateAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val from = ComplexKey.of(
				agendaId,
				startDate
		)
		val to = ComplexKey.of(
				agendaId,
				endDate ?: ComplexKey.emptyObject()
		)
		val viewQuery = createQuery<TimeTable>("by_agenda_and_startdate")
				.startKey(from)
				.endKey(to)
				.includeDocs(true)
        return client.queryViewIncludeDocs<Array<String>, ComplexKey, TimeTable>(viewQuery).map{it.doc}
	}

	@View(name = "by_agenda_and_enddate", map = "classpath:js/timeTable/by_agenda_and_enddate.js")
	override fun listTimeTableByEndDateAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val from = ComplexKey.of(
				agendaId,
				startDate
		)
		val to = ComplexKey.of(
				agendaId,
				endDate ?: ComplexKey.emptyObject()
		)
		val viewQuery = createQuery<TimeTable>("by_agenda_and_enddate")
				.startKey(from)
				.endKey(to)
				.includeDocs(true)
        return client.queryViewIncludeDocs<Array<String>, ComplexKey, TimeTable>(viewQuery).map{it.doc}
	}

	override fun listTimeTableByPeriodAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable> = flow {
        val timeTablesStart = listTimeTableByStartDateAndAgendaId(startDate, endDate, agendaId)
		val timeTablesEnd = listTimeTableByEndDateAndAgendaId(startDate, endDate, agendaId)
		/* Special case : timeTableStart < research.start < rechearch.end < timetableEnd*/
		val timeTableStartBefore = listTimeTableByStartDateAndAgendaId(0L, startDate, agendaId)
		val timeTableEndAfter = listTimeTableByEndDateAndAgendaId(endDate, 999999999999999L, agendaId).toList()

        flowOf(timeTableStartBefore.filterNot { timeTableEndAfter.contains(it) }, timeTablesStart, timeTablesEnd).flattenConcat().distinctById()
	}
}
