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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI


@FlowPreview
@Repository("messageDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted) emit( null, doc._id )}")
class MessageDAOImpl(couchDbProperties: CouchDbProperties,
                     @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Message>(Message::class.java, couchDbProperties, couchDbDispatcher, idGenerator), MessageDAO {

    @View(name = "by_hcparty_from_address_actor", map = "classpath:js/message/By_hcparty_from_address_actor_map.js")
    override fun findByFromAddressActor(partyId: String, fromAddress: String, actorKeys: List<String>?): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return actorKeys?.takeIf { it.isNotEmpty() }?.let {
            val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, fromAddress, k) }
            client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery<Message>("by_from_address_actor").includeDocs(true).keys(keys)).map { it.doc }
        } ?: emptyFlow()
    }

    @View(name = "by_hcparty_to_address_actor", map = "classpath:js/message/By_hcparty_to_address_actor_map.js")
    override fun findByToAddressActor(partyId: String, toAddress: String, actorKeys: List<String>?): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return actorKeys?.takeIf { it.isNotEmpty() }?.let {
            val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, toAddress, k) }
            client.queryViewIncludeDocs<Array<ComplexKey>, String, Message>(createQuery<Message>("by_hcparty_to_address_actor").includeDocs(true).keys(keys)).map { it.doc }
        } ?: emptyFlow()
    }

    @View(name = "by_hcparty_transport_guid_actor", map = "classpath:js/message/By_hcparty_transport_guid_actor_map.js")
    override fun findByTransportGuidActor(partyId: String, transportGuid: String, actorKeys: List<String>?): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return actorKeys?.takeIf { it.isNotEmpty() }?.let {
            val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, transportGuid, k) }
            client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery<Message>("by_hcparty_transport_guid_actor").includeDocs(true).keys(keys)).map { it.doc }
        } ?: emptyFlow()
    }

    @View(name = "by_hcparty_from_address", map = "classpath:js/message/By_hcparty_from_address_map.js")
    override fun findByFromAddress(partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val startKey = ComplexKey.of(partyId, fromAddress, null)
        val endKey: ComplexKey = ComplexKey.of(startKey.components[0], startKey.components[1], ComplexKey.emptyObject())

        val viewQuery = pagedViewQuery<Message, ComplexKey>("by_hcparty_from_address", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_to_address", map = "classpath:js/message/By_hcparty_to_address_map.js")
    override fun findByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<Any>>, reverse: Boolean?): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val reverse = reverse ?: false
        val startKey = ComplexKey.of(partyId, toAddress, null)
        val endKey = ComplexKey.of(partyId, toAddress, if (reverse) null else ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery<Message, ComplexKey>("by_hcparty_to_address", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
    override fun findByTransportGuid(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix, null)
        } ?: ComplexKey.of(partyId, transportGuid, null)

        val endKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject())
        } ?: ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery<Message, ComplexKey>("by_hcparty_transport_guid_received", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_transport_guid_received", map = "classpath:js/message/By_hcparty_transport_guid_received_map.js")
    override fun findByTransportGuidReceived(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix, null)
        } ?: ComplexKey.of(partyId, transportGuid, null)
        val endKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject())
        } ?: ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery<Message, ComplexKey>("by_hcparty_transport_guid_received", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_transport_guid_sent_date", map = "classpath:js/message/By_hcparty_transport_guid_sent_date.js")
    override fun findByTransportGuidSentDate(partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val startKey = ComplexKey.of(partyId, transportGuid, fromDate)
        val endKey = ComplexKey.of(partyId, transportGuid, toDate)
        val viewQuery = pagedViewQuery<Message, ComplexKey>("by_hcparty_transport_guid_sent_date", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty", map = "classpath:js/message/By_hcparty_map.js")
    override fun findByHcParty(partyId: String, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val startKey: ComplexKey = ComplexKey.of(partyId, null)
        val endKey: ComplexKey = ComplexKey.of(partyId, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery<Message, ComplexKey>("by_hcparty", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java)
    }

    @View(name = "by_hcparty_patientfk", map = "classpath:js/message/By_hcparty_patientfk_map.js")
    override fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val keys = secretPatientKeys.map { fk: String -> ComplexKey.of(hcPartyId, fk) }
        return client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery<Message>("by_hcparty_patientfk").includeDocs(true).keys(keys)).distinctUntilChangedBy { it.id }.map { it.doc }
    }

    @View(name = "by_parent_id", map = "classpath:js/message/By_parent_id_map.js")
    override fun getChildren(messageId: String): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.queryViewIncludeDocs<String, Int, Message>(createQuery<Message>("by_parent_id").includeDocs(true).key(messageId)).map { it.doc }
    }

    override fun getChildren(parentIds: List<String>)= flow<List<Message>> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val byParentId = client.queryViewIncludeDocs<String, Int, Message>(createQuery<Message>("by_parent_id").includeDocs(true).keys(parentIds)).map { it.doc }.toList()
        emitAll( parentIds.asFlow().map { parentId -> byParentId.filter { message -> message.id == parentId } })
    }

    @View(name = "by_invoice_id", map = "classpath:js/message/By_invoice_id_map.js")
    override fun getByInvoiceIds(invoiceIds: Set<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.queryViewIncludeDocs<String, Int, Message>(createQuery<Message>("by_invoice_id").includeDocs(true).keys(invoiceIds)).map { it.doc }
    }

    @View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
    override fun getByTransportGuids(hcPartyId: String, transportGuids: Collection<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery<Message>("by_hcparty_transport_guid").includeDocs(true).keys(HashSet(transportGuids).map { ComplexKey.of(hcPartyId, it) })).map { it.doc }
    }

    @View(name = "by_external_ref", map = "classpath:js/message/By_hcparty_external_ref_map.js")
    override fun getByExternalRefs(hcPartyId: String, externalRefs: Set<String>): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery<Message>("by_hcparty_transport_guid").includeDocs(true).keys(HashSet(externalRefs).map { ComplexKey.of(hcPartyId, it) })).map{it.doc}
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(): Flow<Message> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.queryViewIncludeDocsNoValue<String, Message>(createQuery<Message>("conflicts").includeDocs(true).key(Message::class.java)).map{it.doc}
    }
}
