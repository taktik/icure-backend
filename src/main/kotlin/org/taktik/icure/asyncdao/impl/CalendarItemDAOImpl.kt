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

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.taktik.couchdb.annotation.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CalendarItemDAO
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.properties.CouchDbProperties

import org.taktik.icure.utils.distinctById

@FlowPreview
@Repository("calendarItemDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItem' && !doc.deleted) emit( null, doc._id )}")
class CalendarItemDAOImpl(couchDbProperties: CouchDbProperties,
                          @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<CalendarItem>(couchDbProperties, CalendarItem::class.java, couchDbDispatcher, idGenerator), CalendarItemDAO {

    @View(name = "by_hcparty_and_startdate", map = "classpath:js/calendarItem/by_hcparty_and_startdate.js")
    override fun listCalendarItemByStartDateAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = ComplexKey.of(
                hcPartyId,
                startDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery("by_hcparty_and_startdate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<Array<String>, CalendarItem>(viewQuery).map { it.doc }
    }

    @View(name = "by_hcparty_and_enddate", map = "classpath:js/calendarItem/by_hcparty_and_enddate.js")
    override fun listCalendarItemByEndDateAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = ComplexKey.of(
                hcPartyId,
                startDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery("by_hcparty_and_enddate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<Array<String>, CalendarItem>(viewQuery).map { it.doc }
    }

    override fun listCalendarItemByPeriodAndHcPartyId(startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem> {
        val calendarItems = this.listCalendarItemByStartDateAndHcPartyId(startDate, endDate, hcPartyId)
        val calendarItemsEnd = this.listCalendarItemByEndDateAndHcPartyId(startDate, endDate, hcPartyId)

        return flowOf(calendarItems, calendarItemsEnd).flattenConcat().distinctById()
    }

    @View(name = "by_agenda_and_startdate", map = "classpath:js/calendarItem/by_agenda_and_startdate.js")
    override fun listCalendarItemByStartDateAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = ComplexKey.of(
                agendaId,
                startDate
        )
        val to = ComplexKey.of(
                agendaId,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery("by_agenda_and_startdate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<ComplexKey, CalendarItem>(viewQuery).map { it.doc }
    }

    @View(name = "by_agenda_and_enddate", map = "classpath:js/calendarItem/by_agenda_and_enddate.js")
    override fun listCalendarItemByEndDateAndAgendaId(startDate: Long?, endDate: Long?, agenda: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = ComplexKey.of(
                agenda,
                startDate
        )
        val to = ComplexKey.of(
                agenda,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery("by_agenda_and_enddate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<ComplexKey, CalendarItem>(viewQuery).map { it.doc }
    }

    override fun listCalendarItemByPeriodAndAgendaId(startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem> {
        val calendarItems = this.listCalendarItemByStartDateAndAgendaId(startDate, endDate, agendaId)
        val calendarItemsEnd = this.listCalendarItemByEndDateAndAgendaId(startDate, endDate, agendaId)

        return flowOf(calendarItems, calendarItemsEnd).flattenConcat().distinctById()
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/calendarItem/by_hcparty_patient_map.js")
    override fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }
        val viewQuery = createQuery("by_hcparty_patient").keys(keys).includeDocs(true)
        return client.queryViewIncludeDocs<Array<String>, String, CalendarItem>(viewQuery).distinctUntilChangedBy { it.id }.map { it.doc }
    }
}
