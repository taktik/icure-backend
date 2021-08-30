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

package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.flow.*
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.TimeTableDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.FuzzyValues

import org.taktik.icure.utils.distinctById
import java.time.temporal.ChronoUnit

@Repository("timeTableDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.TimeTable' && !doc.deleted) emit( null, doc._id )}")
class TimeTableDAOImpl (couchDbProperties: CouchDbProperties,
                        @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<TimeTable>(couchDbProperties, TimeTable::class.java, couchDbDispatcher, idGenerator), TimeTableDAO {

	@View(name = "by_agenda", map = "classpath:js/timeTable/by_agenda.js")
	override fun listTimeTableByAgendaId(agendaId: String): Flow<TimeTable> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "by_agenda")
				.startKey(agendaId)
				.endKey(agendaId)
				.includeDocs(true)
        emitAll(client.queryViewIncludeDocsNoValue<String, TimeTable>(viewQuery).map{it.doc})
	}

	@View(name = "by_agenda_and_startdate", map = "classpath:js/timeTable/by_agenda_and_startdate.js")
	override fun listTimeTableByStartDateAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val from = ComplexKey.of(
				agendaId,
				startDate
		)
		val to = ComplexKey.of(
				agendaId,
				endDate ?: ComplexKey.emptyObject()
		)
		val viewQuery = createQuery(client, "by_agenda_and_startdate")
				.startKey(from)
				.endKey(to)
				.includeDocs(true)
        emitAll(client.queryViewIncludeDocsNoValue<Array<String>, TimeTable>(viewQuery).map{it.doc})
	}

	@View(name = "by_agenda_and_enddate", map = "classpath:js/timeTable/by_agenda_and_enddate.js")
	override fun listTimeTableByEndDateAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val from = ComplexKey.of(
				agendaId,
				startDate
		)
		val to = ComplexKey.of(
				agendaId,
				endDate ?: ComplexKey.emptyObject()
		)
		val viewQuery = createQuery(client, "by_agenda_and_enddate")
				.startKey(from)
				.endKey(to)
				.includeDocs(true)
        emitAll(client.queryViewIncludeDocsNoValue<Array<String>, TimeTable>(viewQuery).map{it.doc})
	}

	override fun listTimeTableByPeriodAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<TimeTable> =
	        listTimeTableByStartDateAndAgendaId(
                    startDate?.let {
                        /* 1 day in the past to catch long lasting events that could bracket the search period */
                        FuzzyValues.getFuzzyDateTime(FuzzyValues.getDateTime(it).minusDays(1), ChronoUnit.SECONDS)
                    },
                    endDate,
                    agendaId
            ).filter {
                it.endTime?.let { et -> et > (startDate ?: 0) } ?: true
            }
}
