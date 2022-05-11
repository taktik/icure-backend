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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowNoDoc
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.InvoiceDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.distinct

@Repository("invoiceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted) emit( null, doc._id )}")
class InvoiceDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericIcureDAOImpl<Invoice>(Invoice::class.java, couchDbProperties, couchDbDispatcher, idGenerator), InvoiceDAO {

	@View(name = "by_hcparty_date", map = "classpath:js/invoice/By_hcparty_date_map.js")
	override fun findInvoicesByHcParty(hcParty: String, fromDate: Long?, toDate: Long?, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val startKey = ComplexKey.of(hcParty, fromDate)
		val endKey = ComplexKey.of(hcParty, toDate ?: ComplexKey.emptyObject())

		val viewQuery = pagedViewQuery<Invoice, ComplexKey>(client, "by_hcparty_date", startKey, endKey, paginationOffset, false)
		emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Invoice::class.java))
	}

	@View(name = "by_hcparty_contact", map = "classpath:js/invoice/By_hcparty_contact_map.js")
	override fun listInvoicesByHcPartyAndContacts(hcParty: String, contactId: Set<String>): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val invoiceIds = client.queryView<Array<String>, String>(createQuery(client, "by_hcparty_contact").keys(contactId.map { ComplexKey.of(hcParty, it) }).includeDocs(false)).mapNotNull { it.value }
		emitAll(getEntities(invoiceIds.distinct()))
	}

	@View(name = "by_hcparty_reference", map = "classpath:js/invoice/By_hcparty_reference_map.js")
	override fun listInvoicesByHcPartyAndReferences(hcParty: String, invoiceReferences: Set<String>?): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_reference").includeDocs(true).let { if (invoiceReferences != null) it.keys(invoiceReferences.map { ComplexKey.of(hcParty, it) }) else it }).map { it.doc })
	}

	@View(name = "by_hcparty_reference", map = "classpath:js/invoice/By_hcparty_reference_map.js")
	override fun listInvoicesByHcPartyAndReferences(hcParty: String, from: String?, to: String?, descending: Boolean, limit: Int): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val startKey = ComplexKey.of(hcParty, if (descending && from == null) ComplexKey.emptyObject() else from)
		val endKey = ComplexKey.of(hcParty, if (!descending && to == null) ComplexKey.emptyObject() else to)

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_reference").includeDocs(true).startKey(startKey).endKey(endKey).descending(descending).limit(limit)).map { it.doc })
	}

	@View(name = "by_hcparty_groupid", map = "classpath:js/invoice/By_hcparty_groupid_map.js")
	override fun listInvoicesByHcPartyAndGroupId(inputGroupId: String, hcParty: String): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val startKey = ComplexKey.of(hcParty, inputGroupId)
		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_groupid").includeDocs(true).startKey(startKey).endKey(startKey)).map { it.doc })
	}

	@View(name = "by_hcparty_recipient", map = "classpath:js/invoice/By_hcparty_recipient_map.js")
	override fun listInvoicesByHcPartyAndRecipientIds(hcParty: String, recipientIds: Set<String?>): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_recipient").includeDocs(true).keys(recipientIds.map { id -> ComplexKey.of(hcParty, id) })).map { it.doc })
	}

	@View(name = "by_hcparty_patientfk", map = "classpath:js/invoice/By_hcparty_patientfk_map.js")
	override fun listInvoicesByHcPartyAndPatientFk(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcParty, fk) }
		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_patientfk").includeDocs(true).keys(keys)).map { it.doc })
	}

	@View(name = "by_hcparty_recipient_unsent", map = "classpath:js/invoice/By_hcparty_recipient_unsent_map.js")
	override fun listInvoicesByHcPartyAndRecipientIdsUnsent(hcParty: String, recipientIds: Set<String?>): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_recipient_unsent").includeDocs(true).keys(recipientIds.map { id -> ComplexKey.of(hcParty, id) })).map { it.doc })
	}

	@View(name = "by_hcparty_patientfk_unsent", map = "classpath:js/invoice/By_hcparty_patientfk_unsent_map.js")
	override fun listInvoicesByHcPartyAndPatientFkUnsent(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcParty, fk) }

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_patientfk_unsent").includeDocs(true).keys(keys)).map { it.doc })
	}

	@View(name = "by_hcparty_sentmediumtype_invoicetype_sent_date", map = "classpath:js/invoice/By_hcparty_sentmediumtype_invoicetype_sent_date.js")
	override fun listInvoicesByHcPartySentMediumTypeInvoiceTypeSentDate(hcParty: String, sentMediumType: MediumType, invoiceType: InvoiceType, sent: Boolean, fromDate: Long?, toDate: Long?): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		var startKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent)
		var endKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, "{}")
		if (fromDate != null) {
			startKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, fromDate)
			if (toDate != null) {
				endKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, toDate)
			}
		}

		emitAll(client.queryViewIncludeDocs<Array<Any>, String, Invoice>(createQuery(client, "by_hcparty_sentmediumtype_invoicetype_sent_date").includeDocs(true).startKey(startKey).endKey(endKey)).map { it.doc })
	}

	@View(name = "by_hcparty_sending_mode_status_date", map = "classpath:js/invoice/By_hcparty_sending_mode_status_date.js")
	override fun listInvoicesByHcPartySendingModeStatus(hcParty: String, sendingMode: String?, status: String?, fromDate: Long?, toDate: Long?): Flow<Invoice> = flow {
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

		emitAll(client.queryViewIncludeDocs<Array<String>, String, Invoice>(createQuery(client, "by_hcparty_sending_mode_status_date").includeDocs(true).startKey(startKey).endKey(endKey)).map { it.doc })
	}

	@View(name = "by_serviceid", map = "classpath:js/invoice/By_serviceid_map.js")
	override fun listInvoicesByServiceIds(serviceIds: Set<String>): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocs<String, String, Invoice>(createQuery(client, "by_serviceid").includeDocs(true).keys(serviceIds)).map { it.doc })
	}

	@View(name = "by_status_hcps_sentdate", map = "classpath:js/invoice/By_status_hcps_sentdate_map.js")
	override fun listInvoicesHcpsByStatus(status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val result = hcpIds.map {
			client.queryView<Array<String>, String>(
				createQuery(client, "by_status_hcps_sentdate").includeDocs(false)
					.startKey(ComplexKey.of(status, it, from))
					.endKey(ComplexKey.of(status, it, to ?: ComplexKey.emptyObject()))
			).mapNotNull { it.value }
		}.asFlow().flattenConcat().distinct()

		emitAll(getEntities(result))
	}

	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	override fun listConflicts(): Flow<Invoice> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		emitAll(client.queryViewIncludeDocsNoValue<String, Invoice>(createQuery(client, "conflicts").includeDocs(true)).mapNotNull { it.doc })
	}

	@View(name = "tarification_by_hcparty_code", map = "classpath:js/invoice/Tarification_by_hcparty_code.js", reduce = "_count")
	override fun listInvoiceIdsByTarificationsAndCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

		emitAll(client.queryView<Array<String>, String>(createQuery(client, "tarification_by_hcparty_code").includeDocs(false).startKey(from).endKey(to).reduce(false)).mapNotNull { it.value })
	}

	override fun listInvoiceIdsByTarificationsByCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

		emitAll(client.queryView<Array<String>, String>(createQuery(client, "tarification_by_hcparty_code").includeDocs(false).startKey(from).endKey(to).reduce(false)).mapNotNull { it.id })
	}

	override fun listTarificationsFrequencies(hcPartyId: String): Flow<ViewRowNoDoc<ComplexKey, Long>> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val from = ComplexKey.of(
			hcPartyId, null
		)
		val to = ComplexKey.of(
			hcPartyId,
			ComplexKey.emptyObject()
		)

		emitAll(client.queryView<ComplexKey, Long>(createQuery(client, "tarification_by_hcparty_code").startKey(from).endKey(to).includeDocs(false).reduce(true).group(true).groupLevel(2)))
	}
}
