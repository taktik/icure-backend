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

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.CalendarItemDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.entities.Contact
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.distinctById
import java.net.URI

@FlowPreview
@Repository("calendarItemDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.CalendarItem' && !doc.deleted) emit( null, doc._id )}")
class CalendarItemDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<CalendarItem>(CalendarItem::class.java, couchDbDispatcher, idGenerator), CalendarItemDAO {

    @View(name = "by_hcparty_and_startdate", map = "classpath:js/calendarItem/by_hcparty_and_startdate.js")
    override fun listCalendarItemByStartDateAndHcPartyId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val from = ComplexKey.of(
                hcPartyId,
                startDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<CalendarItem>("by_hcparty_and_startdate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<Array<String>, CalendarItem>(viewQuery).map { it.doc }
    }

    @View(name = "by_hcparty_and_enddate", map = "classpath:js/calendarItem/by_hcparty_and_enddate.js")
    override fun listCalendarItemByEndDateAndHcPartyId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val from = ComplexKey.of(
                hcPartyId,
                startDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<CalendarItem>("by_hcparty_and_enddate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<Array<String>, CalendarItem>(viewQuery).map { it.doc }
    }

    override fun listCalendarItemByPeriodAndHcPartyId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, hcPartyId: String): Flow<CalendarItem> {
        val calendarItems = this.listCalendarItemByStartDateAndHcPartyId(dbInstanceUrl, groupId, startDate, endDate, hcPartyId)
        val calendarItemsEnd = this.listCalendarItemByEndDateAndHcPartyId(dbInstanceUrl, groupId, startDate, endDate, hcPartyId)

        return flowOf(calendarItems, calendarItemsEnd).flattenConcat().distinctById()
    }

    @View(name = "by_agenda_and_startdate", map = "classpath:js/calendarItem/by_agenda_and_startdate.js")
    override fun listCalendarItemByStartDateAndAgendaId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val from = ComplexKey.of(
                agendaId,
                startDate
        )
        val to = ComplexKey.of(
                agendaId,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<CalendarItem>("by_agenda_and_startdate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<ComplexKey, CalendarItem>(viewQuery).map { it.doc }
    }

    @View(name = "by_agenda_and_enddate", map = "classpath:js/calendarItem/by_agenda_and_enddate.js")
    override fun listCalendarItemByEndDateAndAgendaId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, agenda: String): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val from = ComplexKey.of(
                agenda,
                startDate
        )
        val to = ComplexKey.of(
                agenda,
                endDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<CalendarItem>("by_agenda_and_enddate")
                .startKey(from)
                .endKey(to)
                .includeDocs(true)

        return client.queryViewIncludeDocsNoValue<ComplexKey, CalendarItem>(viewQuery).map { it.doc }
    }

    override fun listCalendarItemByPeriodAndAgendaId(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, agendaId: String): Flow<CalendarItem> {
        val calendarItems = this.listCalendarItemByStartDateAndAgendaId(dbInstanceUrl, groupId, startDate, endDate, agendaId)
        val calendarItemsEnd = this.listCalendarItemByEndDateAndAgendaId(dbInstanceUrl, groupId, startDate, endDate, agendaId)

        return flowOf(calendarItems, calendarItemsEnd).flattenConcat().distinctById()
    }

    @View(name = "by_hcparty_patient", map = "classpath:js/calendarItem/by_hcparty_patient_map.js")
    override fun findByHcPartyPatient(dbInstanceUri: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<CalendarItem> {
        val client = couchDbDispatcher.getClient(dbInstanceUri, groupId)
        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }
        val viewQuery = createQuery<CalendarItem>("by_hcparty_patient").keys(keys).includeDocs(true)
        return client.queryViewIncludeDocs<Array<String>, String, CalendarItem>(viewQuery).distinctUntilChangedBy { it.id }.map { it.doc }
    }
}
