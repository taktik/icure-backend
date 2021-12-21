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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.ContactDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.ContactIdServiceId
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.distinct


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

    override fun getContacts(contactIds: Flow<String>): Flow<Contact> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.get(contactIds, Contact::class.java))
    }

    override fun getContacts(contactIds: Collection<String>): Flow<Contact> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.get(contactIds, Contact::class.java))
    }

    @View(name = "by_hcparty_openingdate", map = "classpath:js/contact/By_hcparty_openingdate.js")
    override fun listContactsByOpeningDate(hcPartyId: String, startOpeningDate: Long?, endOpeningDate: Long?, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKey = ComplexKey.of(hcPartyId, startOpeningDate)
        val endKey = ComplexKey.of(hcPartyId, endOpeningDate)
        val viewQuery = pagedViewQuery<Contact, ComplexKey>(client, "by_hcparty_openingdate", startKey, endKey, pagination, false)
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Contact::class.java))
    }

    @View(name = "by_hcparty", map = "classpath:js/contact/By_hcparty.js")
    override fun findContactsByHcParty(hcPartyId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQuery<Contact,String>(client, "by_hcparty", hcPartyId, hcPartyId, pagination, false)
        emitAll(client.queryView(viewQuery, String::class.java, String::class.java, Contact::class.java))
    }

    override fun findContactsByIds(contactIds: Flow<String>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.getForPagination(contactIds, Contact::class.java))
    }

    override fun findContactsByIds(contactIds: Collection<String>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.getForPagination(contactIds, Contact::class.java))
    }

    override fun listContactIdsByHealthcareParty(hcPartyId: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "by_hcparty")
                .startKey(hcPartyId)
                .endKey(hcPartyId)
                .includeDocs(false)

        emitAll(client.queryView<String, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_patientfk", map = "classpath:js/contact/By_hcparty_patientfk_map.js")
    override fun listContactsByHcPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val keys = secretPatientKeys.map { fk -> ComplexKey.of(hcPartyId, fk) }

        val viewQuery = createQuery(client, "by_hcparty_patientfk").keys(keys).includeDocs(true)

        emitAll(client.queryViewIncludeDocs<Array<String>, String, Contact>(viewQuery).distinctUntilChangedBy { it.id }.map { it.doc })
    }

    @View(name = "by_hcparty_formid", map = "classpath:js/contact/By_hcparty_formid_map.js")
    override fun listContactsByHcPartyAndFormId(hcPartyId: String, formId: String): Flow<Contact> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "by_hcparty_formid").key(ComplexKey.of(hcPartyId, formId)).includeDocs(true)
        val result = client.queryViewIncludeDocs<Array<String>, String, Contact>(viewQuery).map { it.doc }
        emitAll(relink(result))
    }

    override fun listContactsByHcPartyAndFormIds(hcPartyId: String, ids: List<String>): Flow<Contact> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "by_hcparty_formid")
                .includeDocs(false)
                .keys(ids.map { k -> ComplexKey.of(hcPartyId, k) })
        val result = client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }.distinct()

        emitAll(relink(getContacts(result)))
    }

    @View(name = "by_hcparty_serviceid", map = "classpath:js/contact/By_hcparty_serviceid_map.js")
    override fun findContactsByHcPartyServiceId(hcPartyId: String, serviceId: String) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "by_hcparty_serviceid")
                .key(ComplexKey.of(hcPartyId, serviceId))
                .includeDocs(true)

        val result = client.queryViewIncludeDocs<Array<String>, String, Contact>(viewQuery).map { it.doc }
        emitAll(relink(result))
    }


    @ExperimentalCoroutinesApi
    @FlowPreview
    @View(name = "service_by_linked_id", map = "classpath:js/contact/Service_by_linked_id.js")
    override fun findServiceIdsByIdQualifiedLink(ids: List<String>, linkType: String?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "service_by_linked_id")
                .keys(ids)
                .includeDocs(false)
        val res = client.queryView<String, Array<String>>(viewQuery)
        emitAll(
                (linkType?.let { lt ->
                    res.filter { it.value!![0] == lt }
                } ?: res).map { it.value!![1] }
        )
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    @View(name = "service_by_association_id", map = "classpath:js/contact/Service_by_association_id.js")
    override fun findServiceIdsByAssociationId(associationId: String) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "service_by_association_id")
                .key(associationId)
                .includeDocs(true)

        val res = client.queryViewIncludeDocs<String, String, Contact>(viewQuery)
        emitAll(res.mapNotNull { it.doc }
                .flatMapConcat { it.services.filter { it.qualifiedLinks.values.flatMap { it.keys }.contains(associationId) }.asFlow() })
    }

    @View(name = "service_by_hcparty_tag", map = "classpath:js/contact/Service_by_hcparty_tag.js")
    override fun listServiceIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

        val viewQuery = createQuery(client, "service_by_hcparty_tag")
                .startKey(from)
                .endKey(to)
                .includeDocs(false)

        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "service_by_hcparty_patient_tag", map = "classpath:js/contact/Service_by_hcparty_patient_tag.js")
    override fun listServiceIdsByPatientAndTag(hcPartyId: String, patientSecretForeignKeys: List<String>, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

            val viewQuery = createQuery(client, "service_by_hcparty_patient_tag")
                    .startKey(from)
                    .endKey(to)
                    .includeDocs(false)

            idFlows.add(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
        }
        emitAll(idFlows.asFlow().flattenConcat().distinct())
    }

    @View(name = "service_by_hcparty_code", map = "classpath:js/contact/Service_by_hcparty_code.js", reduce = "_count")
    override fun listServiceIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

        val viewQuery = createQuery(client, "service_by_hcparty_code")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)

        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_tag", map = "classpath:js/contact/By_hcparty_tag.js", reduce = "_count")
    override fun listContactIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

        val viewQuery = createQuery(client, "by_hcparty_tag")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)

        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "service_by_hcparty_identifier", map = "classpath:js/contact/Service_by_hcparty_identifier.js")
    override fun listServiceIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val queryView = createQuery(client, "service_by_hcparty_identifier")
            .keys(identifiers.map {
                ComplexKey.of(hcPartyId, it.system, it.value)
            })

        emitAll(client.queryView<ComplexKey, String>(queryView)
                .mapNotNull {
                    if (it.key == null || it.key!!.components.size < 3) {
                        return@mapNotNull null
                    }
                    return@mapNotNull it.id
                })
    }

    @View(name = "by_hcparty_code", map = "classpath:js/contact/By_hcparty_code.js", reduce = "_count")
    override fun listContactIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

        val viewQuery = createQuery(client, "by_hcparty_code")
                .startKey(from)
                .endKey(to)
                .reduce(false)
                .includeDocs(false)

        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    override fun listCodesFrequencies(hcPartyId: String, codeType: String): Flow<Pair<ComplexKey,Long?>> = flow {
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

        val viewQuery = createQuery(client, "service_by_hcparty_code").startKey(from).endKey(to).includeDocs(false).reduce(true).group(true).groupLevel(3)

        emitAll(client.queryView<Array<String>, Long>(viewQuery).map { Pair(ComplexKey.of(it.key), it.value) })
    }


    @View(name = "service_by_hcparty_patient_code", map = "classpath:js/contact/Service_by_hcparty_patient_code.js")
    override fun listServicesIdsByPatientForeignKeys(hcPartyId: String, patientSecretForeignKeys: List<String>, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
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

            val viewQuery = createQuery(client, "service_by_hcparty_patient_code")
                    .startKey(from)
                    .endKey(to)
                    .includeDocs(false)
            val result = client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }
            ids += result
        }
        emitAll(ids.asFlow().flattenConcat().distinct())
    }

    override fun listServicesIdsByPatientForeignKeys(hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String> {
        return this.listContactsByHcPartyAndPatient(hcPartyId, patientSecretForeignKeys.toList()).mapNotNull { it.services?.mapNotNull { it.id }?.asFlow() }.flattenConcat() // no distinct ?
    }

    @View(name = "by_service", map = "classpath:js/contact/By_service.js")
    fun legacy() {}

    @View(name = "by_service_emit_modified", map = "classpath:js/contact/By_service_emit_modified.js")
    override fun listIdsByServices(services: Collection<String>): Flow<ContactIdServiceId> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "by_service_emit_modified").keys(services).includeDocs(false)
        emitAll(client.queryView<String, ContactIdServiceId>(viewQuery).mapNotNull { it.value })
    }

    override fun listContactsByServices(services: Collection<String>): Flow<Contact> {
        return getContacts(listIdsByServices(services).map { it.contactId })
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

    @View(name = "by_externalid", map = "classpath:js/contact/By_externalid.js")
    override fun findContactsByExternalId(externalId: String) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client,"by_externalid")
                .key(externalId)
                .includeDocs(true)

        emitAll(client.queryViewIncludeDocs<String, String, Contact>(viewQuery).mapNotNull { it.doc })
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Contact' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(): Flow<Contact> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val viewQuery = createQuery(client, "conflicts").includeDocs(true)
        emitAll(client.queryViewIncludeDocsNoValue<String, Contact>(viewQuery).map { it.doc })
    }
}
