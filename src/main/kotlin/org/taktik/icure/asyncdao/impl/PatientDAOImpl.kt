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

import com.squareup.moshi.Types
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.utils.*
import java.net.URI
import java.util.*
import kotlin.collections.set

@ExperimentalCoroutinesApi
@FlowPreview
@Repository("patientDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) emit(doc._id )}")
class PatientDAOImpl(@Qualifier("patientCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Patient>(Patient::class.java, couchDbDispatcher, idGenerator), PatientDAO {

    @View(name = "by_hcparty_name", map = "classpath:js/patient/By_hcparty_name_map.js", reduce = "_count")
    override fun listIdsByHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String): Flow<String> {
        return listIdsForName(dbInstanceUrl, groupId, name, healthcarePartyId, "by_hcparty_name")
    }

    @View(name = "of_hcparty_name", map = "classpath:js/patient/Of_hcparty_name_map.js")
    override fun listIdsOfHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String): Flow<String> {
        return listIdsForName(dbInstanceUrl, groupId, name, healthcarePartyId, "of_hcparty_name")
    }

    @View(name = "by_hcparty_ssin", map = "classpath:js/patient/By_hcparty_ssin_map.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun listIdsByHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String): Flow<String> {
        return listIdsForSsin(dbInstanceUrl, groupId, ssin, healthcarePartyId, "by_hcparty_ssin")
    }

    @View(name = "of_hcparty_ssin", map = "classpath:js/patient/Of_hcparty_ssin_map.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun listIdsOfHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String): Flow<String> {
        return listIdsForSsin(dbInstanceUrl, groupId, ssin, healthcarePartyId, "of_hcparty_ssin")
    }

    @View(name = "by_hcparty_active", map = "classpath:js/patient/By_hcparty_active.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun listIdsByActive(dbInstanceUrl: URI, groupId: String, active: Boolean, healthcarePartyId: String): Flow<String> {
        return listIdsForActive(dbInstanceUrl, groupId, active, healthcarePartyId, "by_hcparty_active")
    }

    @View(name = "merged_by_date", map = "classpath:js/patient/Merged_by_date.js")
    override fun listOfMergesAfter(dbInstanceUrl: URI, groupId: String, date: Long?): Flow<Patient> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("merged_by_date").startKey(date).includeDocs(true)
        return client.queryViewIncludeDocs<Long, String, Patient>(viewQuery).map { it.doc }
    }

    override suspend fun countByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Int {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("by_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return try {
            client.queryView<ComplexKey, Int>(viewQuery).first().value ?: 0
        } catch (e: NoSuchElementException) {
            return 0
        }
    }

    override suspend fun countOfHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Int {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("of_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return try {
            client.queryView<ComplexKey, Int>(viewQuery).first().value ?: 0
        } catch (e: NoSuchElementException) {
            return 0
        }
    }

    override fun listIdsByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }


    @View(name = "by_hcparty_date_of_birth", map = "classpath:js/patient/By_hcparty_date_of_birth_map.js")
    override fun listIdsByHcPartyAndDateOfBirth(dbInstanceUrl: URI, groupId: String, date: Int?, healthcarePartyId: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("by_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    override fun listIdsByHcPartyAndDateOfBirth(dbInstanceUrl: URI, groupId: String, startDate: Int?, endDate: Int?, healthcarePartyId: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, startDate)).endKey(ComplexKey.of(healthcarePartyId, endDate)).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "by_hcparty_gender_education_profession", map = "classpath:js/patient/By_hcparty_gender_education_profession_map.js")
    override fun listIdsByHcPartyGenderEducationProfession(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("by_hcparty_gender_education_profession")
                .startKey(ComplexKey.of(healthcarePartyId, gender?.getName(), education, profession))
                .endKey(ComplexKey.of(healthcarePartyId, if (gender == null) ComplexKey.emptyObject() else gender.getName(), education
                        ?: ComplexKey.emptyObject(), profession ?: ComplexKey.emptyObject())).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "of_hcparty_date_of_birth", map = "classpath:js/patient/Of_hcparty_date_of_birth_map.js")
    override fun listIdsForHcPartyDateOfBirth(dbInstanceUrl: URI, groupId: String, date: Int?, healthcarePartyId: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("of_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "by_hcparty_contains_name", map = "classpath:js/patient/By_hcparty_contains_name_map.js")
    override fun listIdsByHcPartyAndNameContainsFuzzy(dbInstanceUrl: URI, groupId: String, searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery<Patient>("by_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).limit(limit
                ?: 10000).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }.distinct()
    }

    @View(name = "of_hcparty_contains_name", map = "classpath:js/patient/Of_hcparty_contains_name_map.js")
    override fun listIdsOfHcPartyNameContainsFuzzy(dbInstanceUrl: URI, groupId: String, searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery<Patient>("of_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).limit(limit
                ?: 10000).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }.distinct()
    }

    private fun listIdsForName(dbInstanceUrl: URI, groupId: String, name: String?, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        var name = name
        val startKey: ComplexKey
        val endKey: ComplexKey

        //Not transactional aware
        if (name != null) {
            name = StringUtils.sanitizeString(name)
            startKey = ComplexKey.of(healthcarePartyId, name)
            endKey = ComplexKey.of(healthcarePartyId, name!! + "\ufff0")
        } else {
            startKey = ComplexKey.of(healthcarePartyId, null)
            endKey = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())
        }

        val viewQuery = createQuery<Patient>(viewName).startKey(startKey).endKey(endKey).includeDocs(false)

        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    private fun listIdsForSsin(dbInstanceUrl: URI, groupId: String, ssin: String?, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val startKey: ComplexKey
        val endKey: ComplexKey

        if (ssin != null) {
            val cssin = ssin.replace(" ".toRegex(), "").replace("\\W".toRegex(), "")
            startKey = ComplexKey.of(healthcarePartyId, cssin)
            endKey = ComplexKey.of(healthcarePartyId, cssin + "\ufff0")
        } else {
            startKey = ComplexKey.of(healthcarePartyId, null)
            endKey = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())
        }

        val viewQuery = createQuery<Patient>(viewName).reduce(false).startKey(startKey).endKey(endKey).includeDocs(false)

        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    private fun listIdsForSsins(dbInstanceUrl: URI, groupId: String, ssins: Collection<String>, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>(viewName).reduce(false).keys(ssins.map { ssin -> ComplexKey.of(healthcarePartyId, ssin) }).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    private fun listIdsForActive(dbInstanceUrl: URI, groupId: String, active: Boolean, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val onlyKey = ComplexKey.of(healthcarePartyId, if (active) 1 else 0)
        val viewQuery = createQuery<Patient>(viewName).reduce(false).startKey(onlyKey).endKey(onlyKey).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "by_hcparty_externalid", map = "classpath:js/patient/By_hcparty_externalid_map.js")
    override fun listIdsByHcPartyAndExternalId(dbInstanceUrl: URI, groupId: String, externalId: String?, healthcarePartyId: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val startKey: ComplexKey
        val endKey: ComplexKey

        //Not transactional aware
        if (externalId != null) {
            val cexternalId = externalId.replace(" ".toRegex(), "").replace("\\W".toRegex(), "")
            startKey = ComplexKey.of(healthcarePartyId, cexternalId)
            endKey = ComplexKey.of(healthcarePartyId, cexternalId + "\ufff0")
        } else {
            startKey = ComplexKey.of(healthcarePartyId, null)
            endKey = ComplexKey.of(healthcarePartyId, "\ufff0")
        }

        val viewQuery = createQuery<Patient>("by_hcparty_externalid").startKey(startKey).endKey(endKey).includeDocs(false)

        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    override fun findIdsByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = pagedViewQueryOfIds<Patient, ComplexKey>("by_hcparty_name", ComplexKey.of(healthcarePartyId, null), ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject()), pagination)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Any::class.java)
    }

    override fun findPatientsByHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findByName(dbInstanceUrl, groupId, name, healthcarePartyId, pagination, descending, "by_hcparty_name")
    }

    override fun findPatientsOfHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findByName(dbInstanceUrl, groupId, name, healthcarePartyId, pagination, descending, "of_hcparty_name")
    }

    override fun findPatientsByHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findBySsin(dbInstanceUrl, groupId, ssin, healthcarePartyId, pagination, descending, "by_hcparty_ssin")
    }

    override fun findPatientsOfHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findBySsin(dbInstanceUrl, groupId, ssin, healthcarePartyId, pagination, descending, "of_hcparty_ssin")
    }

    @View(name = "by_hcparty_modification_date", map = "classpath:js/patient/By_hcparty_modification_date_map.js")
    override fun findPatientsByHcPartyModificationDate(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findByModificationDate(dbInstanceUrl, groupId, startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_modification_date")
    }

    @View(name = "of_hcparty_modification_date", map = "classpath:js/patient/Of_hcparty_modification_date_map.js")
    override fun findPatientsOfHcPartyModificationDate(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findByModificationDate(dbInstanceUrl, groupId, startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_modification_date")
    }

    override fun findPatientsByHcPartyDateOfBirth(dbInstanceUrl: URI, groupId: String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findByDateOfBirth(dbInstanceUrl, groupId, startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_date_of_birth")
    }

    override fun findPatientsOfHcPartyDateOfBirth(dbInstanceUrl: URI, groupId: String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findByDateOfBirth(dbInstanceUrl, groupId, startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_date_of_birth")
    }

    private fun findByName(dbInstanceUrl: URI, groupId: String, name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        var name = name
        val startKeyNameKeySuffix = if (descending) "\ufff0" else "\u0000"
        val endKeyNameKeySuffix = if (descending) "\u0000" else "\ufff0"
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val startKey: ComplexKey
        val endKey: ComplexKey
        if (name == null) {
            startKey = if (pagination.startKey == null) ComplexKey.of(healthcarePartyId, smallestKey) else ComplexKey.of(*pagination.startKey as Array<Any>)
            endKey = ComplexKey.of(healthcarePartyId, largestKey)
        } else {
            name = StringUtils.sanitizeString(name)
            startKey = if (pagination.startKey == null) ComplexKey.of(healthcarePartyId, name!! + startKeyNameKeySuffix) else ComplexKey.of(*pagination.startKey as Array<Any>)
            endKey = ComplexKey.of(healthcarePartyId, name!! + endKeyNameKeySuffix)
        }

//        // This is an example of what we'll have to do in the controller in order to get back the Mono<PaginatedList> from the Flow<ViewQueryResultEvent>
//        val result = PaginatedList<Patient>().apply {
//            pageSize = pagination.page
//        }
//        result.rows = client.queryView(createQuery<_root_ide_package_.org.taktik.icure.entities.Patient>(viewName).startKey(startKey).endKey(endKey).includeDocs(true), ComplexKey::class.java, String::class.java, Patient::class.java).map {
//            if (it is TotalCount) {
//                result.totalSize = it.total
//            }
//            it
//        }.filterIsInstance<ViewRow<ComplexKey, String, Patient>>().map { it.doc }.filterNotNull().toList()

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(viewName, startKey, endKey, pagination, descending)
//        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Patient::class.java)
        return client.queryView(viewQuery, Array<String>::class.java, String::class.java, Patient::class.java)
    }

    private fun findBySsin(dbInstanceUrl: URI, groupId: String, ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val startKeyNameKeySuffix = if (descending) "\ufff0" else "\u0000"
        val endKeyNameKeySuffix = if (descending) "\u0000" else "\ufff0"
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val startKey: ComplexKey
        val endKey: ComplexKey
        if (ssin == null) {
            startKey = if (pagination.startKey == null) ComplexKey.of(healthcarePartyId, smallestKey) else ComplexKey.of(*pagination.startKey as Array<Any>)
            endKey = ComplexKey.of(healthcarePartyId, largestKey)
        } else {
            val ssinSearchString = ssin.replace(" ".toRegex(), "").replace("\\W".toRegex(), "")
            startKey = if (pagination.startKey == null) ComplexKey.of(healthcarePartyId, ssinSearchString + startKeyNameKeySuffix) else ComplexKey.of(*pagination.startKey as Array<Any>)
            endKey = ComplexKey.of(healthcarePartyId, ssinSearchString + endKeyNameKeySuffix)
        }

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(viewName, startKey, endKey, pagination, descending)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Patient::class.java)
    }

    private fun findByDateOfBirth(dbInstanceUrl: URI, groupId: String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val startKeyStartDate = if (descending) endDate else startDate
        val endKeyEndDate = if (descending) startDate else endDate
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val from: ComplexKey
        if (pagination.startKey == null) {
            //If both keys are null, search for null
            from = ComplexKey.of(healthcarePartyId, if (startKeyStartDate == null && endKeyEndDate == null) null else startKeyStartDate
                    ?: smallestKey)
        } else {
            from = ComplexKey.of(*pagination.startKey as Array<Any>)
        }

        //If both keys are null, search for null
        val to = ComplexKey.of(healthcarePartyId, if (startKeyStartDate == null && endKeyEndDate == null) null else endKeyEndDate
                ?: largestKey)

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(viewName, from, to, pagination, descending)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Patient::class.java)
    }

    private fun findByModificationDate(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val startKeyStartDate = if (descending) endDate else startDate
        val endKeyEndDate = if (descending) startDate else endDate
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val from: ComplexKey
        if (pagination.startKey == null) {
            from = ComplexKey.of(healthcarePartyId, startKeyStartDate ?: smallestKey)
        } else {
            from = ComplexKey.of(*pagination.startKey as Array<Any>)
        }

        val to = ComplexKey.of(healthcarePartyId, endKeyEndDate ?: largestKey)

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(viewName, from, to, pagination, descending)
        return client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Patient::class.java)
    }

    @View(name = "by_user_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.userId) emit( doc.userId, doc._id )}")
    override suspend fun findPatientsByUserId(dbInstanceUrl: URI, groupId: String, id: String): Patient? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val queryView = createQuery<Patient>("by_user_id").includeDocs(true).key(id)
        return client.queryViewIncludeDocs<String, String, Patient>(queryView).firstOrNull()?.doc
    }

    override fun get(dbInstanceUrl: URI, groupId: String, patIds: Collection<String>): Flow<Patient> {
        return getList(dbInstanceUrl, groupId, patIds)
    }

    @View(name = "by_external_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.externalId) emit( doc.externalId, doc._id )}")
    override suspend fun getByExternalId(dbInstanceUrl: URI, groupId: String, externalId: String): Patient? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val queryView = createQuery<Patient>("by_external_id").includeDocs(true).key(externalId)
        return client.queryViewIncludeDocs<String, String, Patient>(queryView).firstOrNull()?.doc
    }

    @View(name = "deleted_by_delete_date", map = "function(doc) {\n" +
            "    if (doc.java_type == 'org.taktik.icure.entities.Patient' && doc.deleted){\n" +
            "      emit(doc.deleted)\n" +
            "    }\n" +
            "}")
    override fun findDeletedPatientsByDeleteDate(dbInstanceUrl: URI, groupId: String, start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val viewQuery = pagedViewQuery<Patient, Long>("deleted_by_delete_date", start, end, paginationOffset, descending)
        return client.queryView(viewQuery, Long::class.java, Any::class.java, Patient::class.java)
    }

    @View(name = "deleted_by_names", map = "classpath:js/patient/Deleted_by_names.js")
    override fun findDeletedPatientsByNames(dbInstanceUrl: URI, groupId: String, _firstName: String?, _lastName: String?): Flow<Patient> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val firstName = if (_firstName == null) null else StringUtils.sanitizeString(_firstName)
        val lastName = if (_lastName == null) null else StringUtils.sanitizeString(_lastName)

        val startKey: ComplexKey
        val endKey: ComplexKey
        if (lastName == null && firstName == null) {
            startKey = ComplexKey.of(null, null)
            endKey = ComplexKey.of(ComplexKey.of(), ComplexKey.emptyObject())
        } else if (lastName == null) {
            startKey = ComplexKey.of(ComplexKey.emptyObject(), firstName)
            endKey = ComplexKey.of(ComplexKey.emptyObject(), firstName!! + "\ufff0")
        } else if (firstName == null) {
            startKey = ComplexKey.of(lastName)
            endKey = ComplexKey.of(lastName + "\ufff0")
        } else {
            startKey = ComplexKey.of(lastName, firstName)
            endKey = ComplexKey.of(lastName + "\ufff0", firstName + "\ufff0")
        }

        val queryView = createQuery<Patient>("deleted_by_names").startKey(startKey).endKey(endKey).includeDocs(true)
        val deleted_by_names = client.queryViewIncludeDocsNoValue<ComplexKey, Patient>(queryView).map { it.doc }

        return if (firstName == null || lastName == null) {
            deleted_by_names
        } else {
            // filter firstName because main filtering is done on lastName
            deleted_by_names
                    .filter { p -> p.firstName != null && StringUtils.sanitizeString(p.firstName)?.startsWith(firstName) == true }
        }
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Patient> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery<Patient>("conflicts").includeDocs(true)
        return client.queryViewIncludeDocsNoValue<String, Patient>(viewQuery).map { it.doc }
    }

    @View(name = "by_modification_date", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && doc.modified) emit(doc.modified)}")
    override fun listOfPatientsModifiedAfter(dbInstanceUrl: URI, groupId: String, date: Long, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = pagedViewQuery<Patient, Long>("by_modification_date", date, java.lang.Long.MAX_VALUE, paginationOffset, false)
        return client.queryView(viewQuery, Long::class.java, Any::class.java, Patient::class.java)
    }

    override fun listIdsByHcPartyAndSsins(dbInstanceUrl: URI, groupId: String, ssins: Collection<String>, healthcarePartyId: String): Flow<String> {
        return listIdsForSsins(dbInstanceUrl, groupId, ssins, healthcarePartyId, "by_hcparty_ssin")
    }

    @View(name = "by_hcparty_contains_name_delegate", map = "classpath:js/patient/By_hcparty_contains_name_delegate.js")
    override fun listByHcPartyName(dbInstanceUrl: URI, groupId: String, searchString: String?, healthcarePartyId: String): Flow<String> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery<Patient>("by_hcparty_contains_name_delegate")
                .startKey(ComplexKey.of(healthcarePartyId, name))
                .endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).includeDocs(false)
        return client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value }
    }

    @View(name = "by_hcparty_delegate_keys", map = "classpath:js/patient/By_hcparty_delegate_keys_map.js")
    override suspend fun getHcPartyKeysForDelegate(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Map<String, String> {
        //Not transactional aware
        val result = couchDbDispatcher.getClient(dbInstanceUrl, groupId).queryView<String, List<String>>(createQuery<Patient>("by_hcparty_delegate_keys")
                .includeDocs(false)
                .key(healthcarePartyId))

        val resultMap = HashMap<String, String>()
        result.collect {
            if (it.value != null) {
                resultMap[it.value[0]] = it.value[1]
            }
        }

        return resultMap
    }

    override suspend fun getDuplicatePatientsBySsin(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        return this.getDuplicatesFromView(dbInstanceUrl, groupId, "by_hcparty_ssin", healthcarePartyId, paginationOffset)
    }

    override suspend fun getDuplicatePatientsByName(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        return this.getDuplicatesFromView(dbInstanceUrl, groupId, "by_hcparty_name", healthcarePartyId, paginationOffset)
    }

    override fun getForPagination(dbInstanceUrl: URI, groupId: String, ids: Collection<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.getForPagination(ids, Patient::class.java)
    }

    override fun getForPagination(dbInstanceUrl: URI, groupId: String, ids: Flow<String>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)
        return client.getForPagination(ids, Patient::class.java)
    }

    private suspend fun getDuplicatesFromView(dbInstanceUrl: URI, groupId: String, viewName: String, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl, groupId)

        val from = if (paginationOffset.startKey == null) ComplexKey.of(healthcarePartyId, "") else ComplexKey.of(*paginationOffset.startKey as Array<Any>)
        val to = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())
        val viewQuery = createQuery<Patient>(viewName)
                .startKey(from)
                .endKey(to)
                .reduce(true)
                .group(true)

        val viewResult = client.queryView<ComplexKey, String>(viewQuery)

        val keysWithDuplicates = viewResult.filter { it.value?.toIntOrNull()?.let { it > 1 } == true }.map { it.key }.toList()

        // TODO MB no reified
        val duplicatePatients = client.queryViewIncludeDocs<ComplexKey, String, Patient>(createQuery<Patient>(viewName).keys(keysWithDuplicates).reduce(false).includeDocs(true))
                .filter { it.doc.active }
                .distinct()
        return duplicatePatients
        //duplicatePatients.onEach { emit(it) }.collect()
    }
}

