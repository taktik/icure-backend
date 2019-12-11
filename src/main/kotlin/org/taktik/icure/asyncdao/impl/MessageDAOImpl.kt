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
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import java.net.URI
import kotlin.collections.HashSet

@FlowPreview
@Repository("messageDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted) emit( null, doc._id )}")
class MessageDAOImpl(@Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Message>(Message::class.java, couchDbDispatcher, idGenerator), MessageDAO {

    @View(name = "by_hcparty_from_address_actor", map = "classpath:js/message/By_hcparty_from_address_actor_map.js")
    override fun findByFromAddressActor(dbInstanceUrl: URI, groupId: String, partyId: String, fromAddress: String, actorKeys: List<String>?): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return actorKeys?.takeIf { it.isNotEmpty() }?.let {
            val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, fromAddress, k) }
            client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery("by_from_address_actor").includeDocs(true).keys(keys)).map { it.doc }
        } ?: emptyFlow()
    }

    @View(name = "by_hcparty_to_address_actor", map = "classpath:js/message/By_hcparty_to_address_actor_map.js")
    override fun findByToAddressActor(dbInstanceUrl: URI, groupId: String, partyId: String, toAddress: String, actorKeys: List<String>?): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return actorKeys?.takeIf { it.isNotEmpty() }?.let {
            val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, toAddress, k) }
            client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery("by_hcparty_to_address_actor").includeDocs(true).keys(keys)).map { it.doc }
        } ?: emptyFlow()
    }

    @View(name = "by_hcparty_transport_guid_actor", map = "classpath:js/message/By_hcparty_transport_guid_actor_map.js")
    override fun findByTransportGuidActor(dbInstanceUrl: URI, groupId: String, partyId: String, transportGuid: String, actorKeys: List<String>?): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return actorKeys?.takeIf { it.isNotEmpty() }?.let {
            val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, transportGuid, k) }
            client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery("by_hcparty_transport_guid_actor").includeDocs(true).keys(keys)).map { it.doc }
        } ?: emptyFlow()
    }

    @View(name = "by_hcparty_from_address", map = "classpath:js/message/By_hcparty_from_address_map.js")
    override fun findByFromAddress(dbInstanceUrl: URI, groupId: String, partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val startKey = paginationOffset.startKey?.let { ComplexKey.of(it) } ?: ComplexKey.of(partyId, fromAddress, null)
        val endKey: ComplexKey = ComplexKey.of(startKey.components[0], startKey.components[1], ComplexKey.emptyObject())

        val viewQuery = pagedViewQuery("by_hcparty_from_address", startKey, endKey, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_to_address", map = "classpath:js/message/By_hcparty_to_address_map.js")
    override fun findByToAddress(dbInstanceUrl: URI, groupId: String, partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<Any>>, reverse: Boolean?): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val reverse = reverse ?: false
        val startKey = paginationOffset.startKey?.let { ComplexKey.of(it) } ?: ComplexKey.of(partyId, toAddress, null)
        val endKey = ComplexKey.of(partyId, toAddress, if (reverse) null else ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery("by_hcparty_to_address", startKey, endKey, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
    override fun findByTransportGuid(dbInstanceUrl: URI, groupId: String, partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val startKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            paginationOffset?.let { ComplexKey.of(paginationOffset.startKey) } ?: ComplexKey.of(partyId, prefix, null)
        } ?: paginationOffset?.startKey?.let { ComplexKey.of(it) } ?: ComplexKey.of(partyId, transportGuid, null)

        val endKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject())
        } ?: ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery("by_hcparty_transport_guid_received", startKey, endKey, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_transport_guid_received", map = "classpath:js/message/By_hcparty_transport_guid_received_map.js")
    override fun findByTransportGuidReceived(dbInstanceUrl: URI, groupId: String, partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val startKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            paginationOffset.startKey?.let { ComplexKey.of(it) } ?: ComplexKey.of(partyId, prefix, null)
        }
                ?: if (paginationOffset.startKey == null) ComplexKey.of(partyId, transportGuid, null) else ComplexKey.of(*paginationOffset.startKey.toTypedArray())
        val endKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject())
        } ?: ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery("by_hcparty_transport_guid_received", startKey, endKey, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_transport_guid_sent_date", map = "classpath:js/message/By_hcparty_transport_guid_sent_date.js")
    override fun findByTransportGuidSentDate(dbInstanceUrl: URI, groupId: String, partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val startKey = paginationOffset?.startKey?.let { ComplexKey.of(it) }
                ?: ComplexKey.of(partyId, transportGuid, fromDate)
        val endKey = ComplexKey.of(partyId, transportGuid, toDate)
        val viewQuery = pagedViewQuery("by_hcparty_transport_guid_received", startKey, endKey, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty", map = "classpath:js/message/By_hcparty_map.js")
    override fun findByHcParty(dbInstanceUrl: URI, groupId: String, partyId: String, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val startKey: ComplexKey = paginationOffset.startKey?.let { ComplexKey.of(it) } ?: ComplexKey.of(partyId, null)
        val endKey: ComplexKey = ComplexKey.of(startKey.components[0], ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery("by_hcparty", startKey, endKey, PaginationOffset(paginationOffset.limit, paginationOffset.startDocumentId), false)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_patientfk", map = "classpath:js/message/By_hcparty_patientfk_map.js")
    override fun findByHcPartyPatient(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val keys = secretPatientKeys.map { fk: String -> ComplexKey.of(hcPartyId, fk) }
        return client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery("by_hcparty_patientfk").includeDocs(true).keys(keys)).distinctUntilChangedBy { it.id }.map { it.doc }
    }

    @View(name = "by_parent_id", map = "classpath:js/message/By_parent_id_map.js")
    override fun getChildren(dbInstanceUrl: URI, groupId: String, messageId: String): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<String, Int, Message>(createQuery("by_parent_id").includeDocs(true).key(messageId)).map { it.doc }
    }

    override suspend fun getChildren(dbInstanceUrl: URI, groupId: String, parentIds: List<String>): Flow<List<Message>> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val byParentId = client.queryViewIncludeDocs<String, Int, Message>(createQuery("by_parent_id").includeDocs(true).keys(parentIds)).map { it.doc }.toList()
        return parentIds.asFlow().map { parentId -> byParentId.filter { message -> message.id == parentId } }
    }

    @View(name = "by_invoice_id", map = "classpath:js/message/By_invoice_id_map.js")
    override fun getByInvoiceIds(dbInstanceUrl: URI, groupId: String, invoiceIds: Set<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<String, Int, Message>(createQuery("by_invoice_id").includeDocs(true).keys(invoiceIds)).map { it.doc }
    }

    @View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
    override fun getByTransportGuids(dbInstanceUrl: URI, groupId: String, hcPartyId: String, transportGuids: Collection<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery("by_hcparty_transport_guid").includeDocs(true).keys(HashSet(transportGuids).map { ComplexKey.of(hcPartyId, it) })).map { it.doc }
    }

    @View(name = "by_external_ref", map = "classpath:js/message/By_hcparty_external_ref_map.js")
    override fun getByExternalRefs(dbInstanceUrl: URI, groupId: String, hcPartyId: String, externalRefs: HashSet<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery("by_hcparty_transport_guid").includeDocs(true).keys(HashSet(externalRefs).map { ComplexKey.of(hcPartyId, it) })).map{it.doc}
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.queryViewIncludeDocsNoValue<String, Message>(createQuery("conflicts").includeDocs(true).key(Message::class.java)).map{it.doc}
    }
}
