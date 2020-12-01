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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.ContactDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.ContactIdServiceId
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.createQuery
import org.taktik.icure.utils.distinct
import org.taktik.icure.utils.pagedViewQuery
import java.net.URI

/**
 * Created by aduchate on 18/07/13, 13:36
 */
@ExperimentalCoroutinesApi
@FlowPreview
@Repository("contactDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Contact' && !doc.deleted) emit( null, doc._id )}")
class ContactDAOImpl(couchDbProperties: CouchDbProperties,
                     @Qualifier("healthdataCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericDAOImpl<Contact>(couchDbProperties, Contact::class.java, couchDbDispatcher, idGenerator), ContactDAO {

    override suspend fun getContact(id: String): Contact? {
        return get(id)
    }

    override fun get(contactIds: Flow<String>): Flow<Contact> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.get(contactIds, Contact::class.java)
    }

    override fun get(contactIds: Collection<String>): Flow<Contact> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.get(contactIds, Contact::class.java)
    }

    @View(name = "by_hcparty_openingdate", map = "classpath:js/contact/By_hcparty_openingdate.js")
    override fun listContactsByOpeningDate(hcPartyId: String, startOpeningDate: Long?, endOpeningDate: Long?, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = ComplexKey.of(hcPartyId, startOpeningDate)
        val endKey = ComplexKey.of(hcPartyId, endOpeningDate)
        val viewQuery = pagedViewQuery<Contact, ComplexKey>("by_hcparty_openingdate", startKey, endKey, pagination, false)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Contact::class.java)
    }

    @View(name = "by_hcparty", map = "classpath:js/contact/By_hcparty.js")
    override fun listContacts(hcPartyId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQuery<Contact,String>("by_hcparty", hcPartyId, hcPartyId, pagination, false)
        return client.queryView(viewQuery, String::class.java, String::class.java, Contact::class.java)
    }

    override fun getPaginatedContacts(contactIds: Flow<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.getForPagination(contactIds, Contact::class.java)
    }

    override fun getPaginatedContacts(contactIds: Collection<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        return client.getForPagination(contactIds, Contact::class.java)
    }

    override fun listContactIds(hcPartyId: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery<Contact>("by_hcparty")
                .startKey(hcPartyId)
                .endKey(hcPartyId)
                .includeDocs(false)

        return client.queryView<String, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "by_hcparty_patientfk", map = "classpath:js/contact/By_hcparty_patientfk_map.js")
    override fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val viewQuery = createQuery<Contact>("by_hcparty_patientfk").keys(keys).includeDocs(true)

        return client.queryViewIncludeDocs<Array<String>, String, Contact>(viewQuery).distinctUntilChangedBy { it.id }.map { it.doc }
    }

    @View(name = "by_hcparty_formid", map = "classpath:js/contact/By_hcparty_formid_map.js")
    override fun findByHcPartyFormId(hcPartyId: String, formId: String): Flow<Contact> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery<Contact>("by_hcparty_formid").key(ComplexKey.of(hcPartyId, formId)).includeDocs(true)
        val result = client.queryViewIncludeDocs<Array<String>, String, Contact>(viewQuery).map { it.doc }
        return relink(result)
    }

    override fun findByHcPartyFormIds(hcPartyId: String, ids: List<String>): Flow<Contact> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery<Contact>("by_hcparty_formid")
                .includeDocs(false)
                .keys(ids.map { k -> ComplexKey.of(hcPartyId, k) })
        val result = client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }.distinct()

        return relink(get(result))
    }


    @ExperimentalCoroutinesApi
    @FlowPreview
    @View(name = "service_by_linked_id", map = "classpath:js/contact/Service_by_linked_id.js")
    override fun findServiceIdsByIdQualifiedLink(ids: List<String>, linkType: String?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery<Contact>("service_by_linked_id")
                .keys(ids)
                .includeDocs(false)
        val res = client.queryView<String, Array<String>>(viewQuery)
        return (linkType?.let { lt ->
            res.filter { it.value!![0] == lt}
        } ?: res).map { it.value!![1] }
    }

    @View(name = "service_by_hcparty_tag", map = "classpath:js/contact/Service_by_hcparty_tag.js")
    override fun listServiceIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
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
                tagType,
                tagCode,
                startValueDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                tagType ?: ComplexKey.emptyObject(),
                tagCode ?: ComplexKey.emptyObject(),
                endValueDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<Contact>("service_by_hcparty_tag")
                .startKey(from)
                .endKey(to)
                .includeDocs(false)

        return client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "service_by_hcparty_patient_tag", map = "classpath:js/contact/Service_by_hcparty_patient_tag.js")
    override fun listServiceIdsByPatientTag(hcPartyId: String, patientSecretForeignKeys: List<String>, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        var startValueDate = startValueDate
        var endValueDate = endValueDate
        if (startValueDate != null && startValueDate < 99999999) {
            startValueDate = startValueDate * 1000000
        }
        if (endValueDate != null && endValueDate < 99999999) {
            endValueDate = endValueDate * 1000000
        }
        val idFlows = mutableListOf<Flow<String>>()
        for (patientSecretForeignKey in patientSecretForeignKeys) {
            val from = ComplexKey.of(
                    hcPartyId,
                    patientSecretForeignKey,
                    tagType,
                    tagCode,
                    startValueDate
            )
            val to = ComplexKey.of(
                    hcPartyId,
                    patientSecretForeignKey,
                    tagType ?: ComplexKey.emptyObject(),
                    tagCode ?: ComplexKey.emptyObject(),
                    endValueDate ?: ComplexKey.emptyObject()
            )

            val viewQuery = createQuery<Contact>("service_by_hcparty_patient_tag")
                    .startKey(from)
                    .endKey(to)
                    .includeDocs(false)

            idFlows.add(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
        }
        return idFlows.asFlow().flattenConcat().distinct()
    }

    @View(name = "service_by_hcparty_code", map = "classpath:js/contact/Service_by_hcparty_code.js", reduce = "_count")
    override fun listServiceIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
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
                codeType,
                codeCode,
                startValueDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                codeType ?: ComplexKey.emptyObject(),
                codeCode ?: ComplexKey.emptyObject(),
                endValueDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<Contact>("service_by_hcparty_code")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)

        return client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "by_hcparty_tag", map = "classpath:js/contact/By_hcparty_tag.js", reduce = "_count")
    override fun listContactIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
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
                tagType,
                tagCode,
                startValueDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                tagType ?: ComplexKey.emptyObject(),
                tagCode ?: ComplexKey.emptyObject(),
                endValueDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<Contact>("by_hcparty_tag")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)

        return client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "by_hcparty_code", map = "classpath:js/contact/By_hcparty_code.js", reduce = "_count")
    override fun listContactIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
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
                codeType,
                codeCode,
                startValueDate
        )
        val to = ComplexKey.of(
                hcPartyId,
                codeType ?: ComplexKey.emptyObject(),
                codeCode ?: ComplexKey.emptyObject(),
                endValueDate ?: ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<Contact>("by_hcparty_code")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)

        return client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }
    }

    override fun listCodesFrequencies(hcPartyId: String, codeType: String): Flow<Pair<ComplexKey,Long?>> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = ComplexKey.of(
                hcPartyId,
                codeType,
                null
        )
        val to = ComplexKey.of(
                hcPartyId,
                codeType,
                ComplexKey.emptyObject()
        )

        val viewQuery = createQuery<Contact>("service_by_hcparty_code").startKey(from).endKey(to).includeDocs(false).reduce(true).group(true).groupLevel(3)

        return client.queryView<Array<String>, Long>(viewQuery).map { Pair(ComplexKey.of(it.key), it.value) }
    }


    @View(name = "service_by_hcparty_patient_code", map = "classpath:js/contact/Service_by_hcparty_patient_code.js")
    override fun findServicesByForeignKeys(hcPartyId: String, patientSecretForeignKeys: List<String>, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        var startValueDate = startValueDate
        var endValueDate = endValueDate
        if (startValueDate != null && startValueDate < 99999999) {
            startValueDate = startValueDate * 1000000
        }
        if (endValueDate != null && endValueDate < 99999999) {
            endValueDate = endValueDate * 1000000
        }
        val ids = mutableListOf<Flow<String>>()
        for (patientSecretForeignKey in patientSecretForeignKeys) {
            val from = ComplexKey.of(
                    hcPartyId,
                    patientSecretForeignKey,
                    codeType,
                    codeCode,
                    startValueDate
            )
            val to = ComplexKey.of(
                    hcPartyId,
                    patientSecretForeignKey,
                    codeType ?: ComplexKey.emptyObject(),
                    codeCode ?: ComplexKey.emptyObject(),
                    endValueDate ?: ComplexKey.emptyObject()
            )

            val viewQuery = createQuery<Contact>("service_by_hcparty_patient_code")
                    .startKey(from)
                    .endKey(to)
                    .includeDocs(false)
            val result = client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }
            ids += result
        }
        return ids.asFlow().flattenConcat().distinct()
    }

    override fun findServicesByForeignKeys(hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String> {
        return this.findByHcPartyPatient(hcPartyId, patientSecretForeignKeys.toList()).mapNotNull { it.services?.mapNotNull { it.id }?.asFlow() }.flattenConcat() // no distinct ?
    }

    @View(name = "by_service", map = "classpath:js/contact/By_service.js")
    fun legacy() {}

    @View(name = "by_service_emit_modified", map = "classpath:js/contact/By_service_emit_modified.js")
    override fun listIdsByServices(services: Collection<String>): Flow<ContactIdServiceId> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery<Contact>("by_service_emit_modified").keys(services).includeDocs(false)
        return client.queryView<String, ContactIdServiceId>(viewQuery).mapNotNull { it.value }
    }

    override fun listByServices(services: Collection<String>): Flow<Contact> {
        return get(this.listIdsByServices(services).map { it.contactId })
    }

    override fun relink(cs: Flow<Contact>): Flow<Contact> {
        return cs.map { c ->
            val services = mutableMapOf<String, Service?>()
            c.services.forEach { s -> s.id.let { services[it] = s } }
            c.subContacts.forEach { ss ->
                ss.services.forEach { s ->
                    val ssvc = services[s.serviceId]
                    //If it is null, leave it null...
                    s.service = ssvc
                }
            }
            c
        }
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Contact' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(): Flow<Contact> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery<Contact>("conflicts").includeDocs(true)
        return client.queryViewIncludeDocsNoValue<String, Contact>(viewQuery).map { it.doc }
    }
}
