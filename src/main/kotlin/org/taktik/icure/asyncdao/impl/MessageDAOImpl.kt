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
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.properties.CouchDbProperties


@FlowPreview
@Repository("messageDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted) emit( null, doc._id )}")
class MessageDAOImpl(couchDbProperties: CouchDbProperties,
                     @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Message>(Message::class.java, couchDbProperties, couchDbDispatcher, idGenerator), MessageDAO {

    @View(name = "by_hcparty_from_address_actor", map = "classpath:js/message/By_hcparty_from_address_actor_map.js")
    override fun listMessagesByFromAddressAndActor(partyId: String, fromAddress: String, actorKeys: List<String>?): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                actorKeys?.takeIf { it.isNotEmpty() }?.let {
                    val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, fromAddress, k) }
                    client.queryViewIncludeDocs<ComplexKey, String, Message>(createQuery(client, "by_from_address_actor").includeDocs(true).keys(keys)).map { it.doc }
                } ?: emptyFlow()
        )
    }

    @View(name = "by_hcparty_to_address_actor", map = "classpath:js/message/By_hcparty_to_address_actor_map.js")
    override fun listMessagesByToAddressAndActor(partyId: String, toAddress: String, actorKeys: List<String>?): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                actorKeys?.takeIf { it.isNotEmpty() }?.let {
                    val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, toAddress, k) }
                    client.queryViewIncludeDocs<Array<ComplexKey>, String, Message>(createQuery(client, "by_hcparty_to_address_actor").includeDocs(true).keys(keys)).map { it.doc }
                } ?: emptyFlow()
        )
    }

    @View(name = "by_hcparty_transport_guid_actor", map = "classpath:js/message/By_hcparty_transport_guid_actor_map.js")
    override fun listMessagesByTransportGuidAndActor(partyId: String, transportGuid: String, actorKeys: List<String>?): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(
                actorKeys?.takeIf { it.isNotEmpty() }?.let {
                    val keys = actorKeys.map { k: String -> ComplexKey.of(partyId, transportGuid, k) }
                    client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery(client, "by_hcparty_transport_guid_actor").includeDocs(true).keys(keys)).map { it.doc }
                } ?: emptyFlow()
        )
    }

    @View(name = "by_hcparty_from_address", map = "classpath:js/message/By_hcparty_from_address_map.js")
    override fun listMessagesByFromAddress(
        partyId: String,
        fromAddress: String,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val startKey = ComplexKey.of(partyId, fromAddress, null)
        val endKey: ComplexKey = ComplexKey.of(startKey.components[0], startKey.components[1], ComplexKey.emptyObject())

        val viewQuery = pagedViewQuery<Message, ComplexKey>(
            client,
            "by_hcparty_from_address",
            startKey,
            endKey,
            paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) },
            false
        )
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java))
    }

    @View(name = "by_hcparty_to_address", map = "classpath:js/message/By_hcparty_to_address_map.js")
    override fun findMessagesByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<*>>, reverse: Boolean?): Flow<ViewQueryResultEvent> =
        flow {
            val client = couchDbDispatcher.getClient(dbInstanceUrl)
            val reverse = reverse ?: false
            val startKey = ComplexKey.of(partyId, toAddress, null)
            val endKey = ComplexKey.of(partyId, toAddress, if (reverse) null else ComplexKey.emptyObject())
            val viewQuery =
                pagedViewQuery<Message, ComplexKey>(client, "by_hcparty_to_address", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
            emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java))
        }

    @View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
    override fun findMessagesByTransportGuid(
        partyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix, null)
        } ?: ComplexKey.of(partyId, transportGuid, null)

        val endKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject())
        } ?: ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery<Message, ComplexKey>(client, "by_hcparty_transport_guid_received", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java))
    }

    @View(
        name = "by_hcparty_transport_guid_received",
        map = "classpath:js/message/By_hcparty_transport_guid_received_map.js"
    )
    override fun findMessagesByTransportGuidReceived(
        partyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix, null)
        } ?: ComplexKey.of(partyId, transportGuid, null)
        val endKey = transportGuid?.takeIf { it.endsWith(":*") }?.let {
            val prefix = transportGuid.substring(0, transportGuid.length - 1)
            ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject())
        } ?: ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery<Message, ComplexKey>(client, "by_hcparty_transport_guid_received", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java))
    }

    @View(name = "by_hcparty_transport_guid_sent_date", map = "classpath:js/message/By_hcparty_transport_guid_sent_date.js")
    override fun findMessagesByTransportGuidAndSentDate(partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<*>>): Flow<ViewQueryResultEvent> =
        flow {
            val client = couchDbDispatcher.getClient(dbInstanceUrl)
            val startKey = ComplexKey.of(partyId, transportGuid, fromDate)
            val endKey = ComplexKey.of(partyId, transportGuid, toDate)
            val viewQuery =
                pagedViewQuery<Message, ComplexKey>(client, "by_hcparty_transport_guid_sent_date", startKey, endKey, paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) }, false)
            emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java))
        }

    @View(name = "by_hcparty", map = "classpath:js/message/By_hcparty_map.js")
    override fun findMessagesByHcParty(
        partyId: String,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val startKey: ComplexKey = ComplexKey.of(partyId, null)
        val endKey: ComplexKey = ComplexKey.of(partyId, ComplexKey.emptyObject())
        val viewQuery = pagedViewQuery<Message, ComplexKey>(
            client,
            "by_hcparty",
            startKey,
            endKey,
            paginationOffset.toPaginationOffset { ComplexKey.of(*it.toTypedArray()) },
            false
        )
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Message::class.java))
    }

    @View(name = "by_hcparty_patientfk", map = "classpath:js/message/By_hcparty_patientfk_map.js")
    override fun listMessagesByHcPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val keys = secretPatientKeys.map { fk: String -> ComplexKey.of(hcPartyId, fk) }
        emitAll(client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery(client, "by_hcparty_patientfk").includeDocs(true).keys(keys)).distinctUntilChangedBy { it.id }.map { it.doc })
    }

    @View(name = "by_parent_id", map = "classpath:js/message/By_parent_id_map.js")
    override fun getChildren(messageId: String): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryViewIncludeDocs<String, Int, Message>(createQuery(client, "by_parent_id").includeDocs(true).key(messageId)).map { it.doc })
    }

    override fun getMessagesChildren(parentIds: List<String>)= flow<List<Message>> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val byParentId = client.queryViewIncludeDocs<String, Int, Message>(createQuery(client, "by_parent_id").includeDocs(true).keys(parentIds)).map { it.doc }.toList()
        emitAll( parentIds.asFlow().map { parentId -> byParentId.filter { message -> message.id == parentId } })
    }

    @View(name = "by_invoice_id", map = "classpath:js/message/By_invoice_id_map.js")
    override fun listMessagesByInvoiceIds(invoiceIds: Set<String>): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryViewIncludeDocs<String, Int, Message>(createQuery(client, "by_invoice_id").includeDocs(true).keys(invoiceIds)).map { it.doc })
    }

    @View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
    override fun getMessagesByTransportGuids(hcPartyId: String, transportGuids: Collection<String>): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery(client, "by_hcparty_transport_guid").includeDocs(true).keys(HashSet(transportGuids).map { ComplexKey.of(hcPartyId, it) })).map { it.doc })
    }

    @View(name = "by_external_ref", map = "classpath:js/message/By_hcparty_external_ref_map.js")
    override fun getMessagesByExternalRefs(hcPartyId: String, externalRefs: Set<String>): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryViewIncludeDocs<Array<String>, String, Message>(createQuery(client, "by_hcparty_transport_guid").includeDocs(true).keys(HashSet(externalRefs).map { ComplexKey.of(hcPartyId, it) })).map{it.doc})
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(): Flow<Message> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryViewIncludeDocsNoValue<String, Message>(createQuery(client, "conflicts").includeDocs(true)).map{ it.doc })
    }
}
