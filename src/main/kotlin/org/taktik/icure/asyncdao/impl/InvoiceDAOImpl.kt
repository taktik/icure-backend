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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowNoDoc
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.InvoiceDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.distinct
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI

@Repository("invoiceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted) emit( null, doc._id )}")
class InvoiceDAOImpl(couchDbProperties: CouchDbProperties,
                     @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Invoice>(Invoice::class.java, couchDbProperties, couchDbDispatcher, idGenerator), InvoiceDAO {

    @View(name = "by_hcparty_date", map = "classpath:js/invoice/By_hcparty_date_map.js")
    override fun findByHcParty(hcParty: String, fromDate: Long?, toDate: Long?, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = ComplexKey.of(hcParty, fromDate)
        val endKey = ComplexKey.of(hcParty, toDate ?: ComplexKey.emptyObject())

        val viewQuery = pagedViewQuery<Invoice, ComplexKey>("by_hcparty_date", startKey, endKey, paginationOffset, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Invoice::class.java)
    }

    @View(name = "by_hcparty_contact", map = "classpath:js/invoice/By_hcparty_contact_map.js")
    override fun listByHcPartyContacts(hcParty: String, contactId: Set<String>): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val invoiceIds = client.queryView<Array<String>, String>(createQuery<Invoice>("by_hcparty_contact").keys(contactId.map { ComplexKey.of(hcParty, it) }).includeDocs(false)).mapNotNull { it.value }
        return getList(invoiceIds.distinct())
    }

    @View(name = "by_hcparty_reference", map = "classpath:js/invoice/By_hcparty_reference_map.js")
    override fun listByHcPartyReferences(hcParty: String, invoiceReferences: Set<String>?): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_reference").includeDocs(true).let { if (invoiceReferences != null) it.keys(invoiceReferences.map { ComplexKey.of(hcParty, it) }) else it }).map { it.doc }
    }

    @View(name = "by_hcparty_reference", map = "classpath:js/invoice/By_hcparty_reference_map.js")
    override fun listByHcPartyReferences(hcParty: String, from: String?, to: String?, descending: Boolean, limit: Int): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = ComplexKey.of(hcParty, if (descending && from == null) ComplexKey.emptyObject() else from)
        val endKey = ComplexKey.of(hcParty, if (!descending && to == null) ComplexKey.emptyObject() else to)

        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_reference").includeDocs(true).startKey(startKey).endKey(endKey).descending(descending).limit(limit)).map { it.doc }
    }

    @View(name = "by_hcparty_groupid", map = "classpath:js/invoice/By_hcparty_groupid_map.js")
    override fun listByHcPartyGroupId(inputGroupId:String, hcParty: String): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = ComplexKey.of(hcParty, inputGroupId)
        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_groupid").includeDocs(true).startKey(startKey).endKey(startKey)).map { it.doc }
    }

    @View(name = "by_hcparty_recipient", map = "classpath:js/invoice/By_hcparty_recipient_map.js")
    override fun listByHcPartyRecipientIds(hcParty: String, recipientIds: Set<String?>): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_recipient").includeDocs(true).keys(recipientIds.map { id -> ComplexKey.of(hcParty, id) })).map { it.doc }
    }

    @View(name = "by_hcparty_patientfk", map = "classpath:js/invoice/By_hcparty_patientfk_map.js")
    override fun listByHcPartyPatientFk(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcParty, fk) }
        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_patientfk").includeDocs(true).keys(keys)).map { it.doc }
    }

    @View(name = "by_hcparty_recipient_unsent", map = "classpath:js/invoice/By_hcparty_recipient_unsent_map.js")
    override fun listByHcPartyRecipientIdsUnsent(hcParty: String, recipientIds: Set<String?>): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_recipient_unsent").includeDocs(true).keys(recipientIds.map { id -> ComplexKey.of(hcParty, id) })).map { it.doc }
    }

    @View(name = "by_hcparty_patientfk_unsent", map = "classpath:js/invoice/By_hcparty_patientfk_unsent_map.js")
    override fun listByHcPartyPatientFkUnsent(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcParty, fk) }

        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_patientfk_unsent").includeDocs(true).keys(keys)).map { it.doc }
    }

    @View(name = "by_hcparty_sentmediumtype_invoicetype_sent_date", map = "classpath:js/invoice/By_hcparty_sentmediumtype_invoicetype_sent_date.js")
    override fun listByHcPartySentMediumTypeInvoiceTypeSentDate(hcParty: String, sentMediumType: MediumType, invoiceType: InvoiceType, sent: Boolean, fromDate: Long?, toDate: Long?): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        var startKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent)
        var endKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, "{}")
        if (fromDate != null) {
            startKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, fromDate)
            if (toDate != null) {
                endKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, toDate)
            }
        }

        return client.queryViewIncludeDocs<Array<Any>, String, Invoice>(createQuery<Invoice>("by_hcparty_sentmediumtype_invoicetype_sent_date").includeDocs(true).startKey(startKey).endKey(endKey)).map { it.doc }
    }

    @View(name = "by_hcparty_sending_mode_status_date", map = "classpath:js/invoice/By_hcparty_sending_mode_status_date.js")
    override fun listByHcPartySendingModeStatus(hcParty: String, sendingMode: String?, status: String?, fromDate: Long?, toDate: Long?): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        var startKey = ComplexKey.of(hcParty)
        var endKey = ComplexKey.of(hcParty, ComplexKey.emptyObject(), ComplexKey.emptyObject(), ComplexKey.emptyObject())
        if (fromDate != null && toDate != null) { // The full key is given
            startKey = ComplexKey.of(hcParty, sendingMode, status, fromDate)
            endKey = ComplexKey.of(hcParty, sendingMode, status, toDate)
        } else if (status != null) {
            startKey = ComplexKey.of(hcParty, sendingMode, status)
            endKey = ComplexKey.of(hcParty, sendingMode, status, ComplexKey.emptyObject())
        } else if (sendingMode != null) {
            startKey = ComplexKey.of(hcParty, sendingMode)
            endKey = ComplexKey.of(hcParty, sendingMode, ComplexKey.emptyObject(), ComplexKey.emptyObject())
        }

        return client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery<Invoice>("by_hcparty_sending_mode_status_date").includeDocs(true).startKey(startKey).endKey(endKey)).map { it.doc }
    }

    @View(name = "by_serviceid", map = "classpath:js/invoice/By_serviceid_map.js")
    override fun listByServiceIds(serviceIds: Set<String>): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocs<String, String, Invoice>(createQuery<Invoice>("by_serviceid").includeDocs(true).keys(serviceIds)).map { it.doc }
    }

    @View(name = "by_status_hcps_sentdate", map = "classpath:js/invoice/By_status_hcps_sentdate_map.js")
    override fun listAllHcpsByStatus(status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val result = hcpIds.map {
            client.queryView<Array<String>, String>(createQuery<Invoice>("by_status_hcps_sentdate").includeDocs(false)
                    .startKey(ComplexKey.of(status, it, from))
                    .endKey(ComplexKey.of(status, it, to ?: ComplexKey.emptyObject()))).mapNotNull { it.value }
        }.asFlow().flattenConcat().distinct()

        return getList(result)
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(): Flow<Invoice> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        return client.queryViewIncludeDocsNoValue<String, Invoice>(createQuery<Invoice>("conflicts").includeDocs(true)).mapNotNull { it.doc }
    }


    @View(name = "tarification_by_hcparty_code", map = "classpath:js/invoice/Tarification_by_hcparty_code.js", reduce = "_count")
    override fun listIdsByTarificationsByCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        var startValueDate = startValueDate
        var endValueDate = endValueDate
        if (startValueDate != null && startValueDate < 99999999) {
            startValueDate = startValueDate * 1000000
        }
        if (endValueDate != null && endValueDate < 99999999) {
            endValueDate = endValueDate * 1000000
        }
        val from = ComplexKey.of(
                hcPartyId,
                codeCode,
                startValueDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                codeCode ?: ComplexKey.emptyObject(),
                endValueDate ?: ComplexKey.emptyObject()
        )

        return client.queryView<Array<String>, String>(createQuery<Invoice>("tarification_by_hcparty_code").includeDocs(false).startKey(from).endKey(to).reduce(false)).mapNotNull { it.value }
    }

    override fun listInvoiceIdsByTarificationsByCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        var startValueDate = startValueDate
        var endValueDate = endValueDate
        if (startValueDate != null && startValueDate < 99999999) {
            startValueDate = startValueDate * 1000000
        }
        if (endValueDate != null && endValueDate < 99999999) {
            endValueDate = endValueDate * 1000000
        }
        val from = ComplexKey.of(
                hcPartyId,
                codeCode,
                startValueDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                codeCode ?: ComplexKey.emptyObject(),
                endValueDate ?: ComplexKey.emptyObject()
        )

        return client.queryView<Array<String>, String>(createQuery<Invoice>("tarification_by_hcparty_code").includeDocs(false).startKey(from).endKey(to).reduce(false)).mapNotNull { it.id }
    }

    override fun listTarificationsFrequencies(hcPartyId: String): Flow<ViewRowNoDoc<ComplexKey, Long>> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = ComplexKey.of(
                hcPartyId, null
        )
        val to = ComplexKey.of(
                hcPartyId,
                ComplexKey.emptyObject()
        )

        return client.queryView<ComplexKey, Long>(createQuery<Invoice>("tarification_by_hcparty_code").startKey(from).endKey(to).includeDocs(false).reduce(true).group(true).groupLevel(2))
    }
}
