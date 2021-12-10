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

import kotlin.collections.set
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
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.distinct
import java.util.*

@ExperimentalCoroutinesApi
@FlowPreview
@Repository("patientDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) emit(null, doc._id)}")
class PatientDAOImpl(couchDbProperties: CouchDbProperties,
                     @Qualifier("patientCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : GenericIcureDAOImpl<Patient>(Patient::class.java, couchDbProperties, couchDbDispatcher, idGenerator), PatientDAO {

    @View(name = "by_hcparty_name", map = "classpath:js/patient/By_hcparty_name_map.js", reduce = "_count")
    override fun listPatientIdsByHcPartyAndName(name: String, healthcarePartyId: String): Flow<String> {
        return listPatientIdsForName(name, healthcarePartyId, "by_hcparty_name")
    }

    @View(name = "of_hcparty_name", map = "classpath:js/patient/Of_hcparty_name_map.js")
    override fun listPatientIdsOfHcPartyAndName(name: String, healthcarePartyId: String): Flow<String> {
        return listPatientIdsForName(name, healthcarePartyId, "of_hcparty_name")
    }

    @View(name = "by_hcparty_ssin", map = "classpath:js/patient/By_hcparty_ssin_map.js", reduce = "_count")
    override fun listPatientIdsByHcPartyAndSsin(ssin: String, healthcarePartyId: String): Flow<String> {
        return listPatientIdsForSsin(ssin, healthcarePartyId, "by_hcparty_ssin")
    }

    @View(name = "of_hcparty_ssin", map = "classpath:js/patient/Of_hcparty_ssin_map.js", reduce = "_count")
    override fun listPatientIdsOfHcPartyAndSsin(ssin: String, healthcarePartyId: String): Flow<String> {
        return listPatientIdsForSsin(ssin, healthcarePartyId, "of_hcparty_ssin")
    }

    @View(name = "by_hcparty_active", map = "classpath:js/patient/By_hcparty_active.js", reduce = "_count")
    override fun listPatientIdsByActive(active: Boolean, healthcarePartyId: String): Flow<String> {
        return listPatientIdsForActive(active, healthcarePartyId, "by_hcparty_active")
    }

    @View(name = "merged_by_date", map = "classpath:js/patient/Merged_by_date.js")
    override fun listOfMergesAfter(date: Long?): Flow<Patient> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "merged_by_date").startKey(date).includeDocs(true)
        emitAll(client.queryViewIncludeDocs<Long, String, Patient>(viewQuery).map { it.doc })
    }

    override suspend fun countByHcParty(healthcarePartyId: String): Int {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "by_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return try {
            client.queryView<Array<String>, Int>(viewQuery).first().value ?: 0
        } catch (e: NoSuchElementException) {
            return 0
        }
    }

    override suspend fun countOfHcParty(healthcarePartyId: String): Int {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "of_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return try {
            client.queryView<Array<String>, Int>(viewQuery).first().value ?: 0
        } catch (e: NoSuchElementException) {
            return 0
        }
    }

    override fun listPatientIdsByHcParty(healthcarePartyId: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }


    @View(name = "by_hcparty_date_of_birth", map = "classpath:js/patient/By_hcparty_date_of_birth_map.js")
    override fun listPatientIdsByHcPartyAndDateOfBirth(date: Int?, healthcarePartyId: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "by_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    override fun listPatientIdsByHcPartyAndDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, startDate)).endKey(ComplexKey.of(healthcarePartyId, endDate)).includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_gender_education_profession", map = "classpath:js/patient/By_hcparty_gender_education_profession_map.js")
    override fun listPatientIdsByHcPartyGenderEducationProfession(healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "by_hcparty_gender_education_profession")
                .startKey(ComplexKey.of(healthcarePartyId, gender?.name, education, profession))
                .endKey(ComplexKey.of(healthcarePartyId, if (gender == null) ComplexKey.emptyObject() else gender.name, education
                        ?: ComplexKey.emptyObject(), profession ?: ComplexKey.emptyObject())).includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "of_hcparty_date_of_birth", map = "classpath:js/patient/Of_hcparty_date_of_birth_map.js")
    override fun listPatientIdsForHcPartyDateOfBirth(date: Int?, healthcarePartyId: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, "of_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_contains_name", map = "classpath:js/patient/By_hcparty_contains_name_map.js")
    override fun listPatientIdsByHcPartyAndNameContainsFuzzy(searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery(client, "by_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).also { q -> limit?.let { q.limit(it)} ?: q }.includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }.distinct())
    }

    @View(name = "of_hcparty_contains_name", map = "classpath:js/patient/Of_hcparty_contains_name_map.js")
    override fun listPatientIdsOfHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery(client, "of_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).also { q -> limit?.let { q.limit(it)} ?: q }.includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value }.distinct())
    }

    private fun listPatientIdsForName(name: String?, healthcarePartyId: String, viewName: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
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

        val viewQuery = createQuery(client, viewName).startKey(startKey).endKey(endKey).includeDocs(false)

        emitAll(client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value })
    }

    private fun listPatientIdsForSsin(ssin: String?, healthcarePartyId: String, viewName: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
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

        val viewQuery = createQuery(client, viewName).reduce(false).startKey(startKey).endKey(endKey).includeDocs(false)

        emitAll(client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value })
    }

    private fun listPatientIdsForSsins(ssins: Collection<String>, healthcarePartyId: String, viewName: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = createQuery(client, viewName).reduce(false).keys(ssins.map { ssin -> ComplexKey.of(healthcarePartyId, ssin) }).includeDocs(false)
        emitAll(client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value })
    }

    private fun listPatientIdsForActive(active: Boolean, healthcarePartyId: String, viewName: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val onlyKey = ComplexKey.of(healthcarePartyId, if (active) 1 else 0)
        val viewQuery = createQuery(client, viewName).reduce(false).startKey(onlyKey).endKey(onlyKey).includeDocs(false)
        emitAll(client.queryView<ComplexKey, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_externalid", map = "classpath:js/patient/By_hcparty_externalid_map.js")
    override fun listPatientIdsByHcPartyAndExternalId(externalId: String?, healthcarePartyId: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
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

        val viewQuery = createQuery(client, "by_hcparty_externalid").startKey(startKey).endKey(endKey).includeDocs(false)

        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    override fun findPatientIdsByHcParty(healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQueryOfIds<Patient, ComplexKey>(client, "by_hcparty_name", pagination.startKey ?: ComplexKey.of(healthcarePartyId, null), ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject()), pagination)
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Any::class.java))
    }

    override fun findPatientsByHcPartyAndName(name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsByName(name, healthcarePartyId, pagination, descending, "by_hcparty_name")
    }

    override fun findPatientsOfHcPartyAndName(name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsByName(name, healthcarePartyId, pagination, descending, "of_hcparty_name")
    }

    override fun findPatientsByHcPartyAndSsin(ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsBySsin(ssin, healthcarePartyId, pagination, descending, "by_hcparty_ssin")
    }

    override fun findPatientsOfHcPartyAndSsin(ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsBySsin(ssin, healthcarePartyId, pagination, descending, "of_hcparty_ssin")
    }

    @View(name = "by_hcparty_modification_date", map = "classpath:js/patient/By_hcparty_modification_date_map.js")
    override fun findPatientsByHcPartyModificationDate(startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsByModificationDate(startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_modification_date")
    }

    @View(name = "of_hcparty_modification_date", map = "classpath:js/patient/Of_hcparty_modification_date_map.js")
    override fun findPatientsOfHcPartyModificationDate(startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsByModificationDate(startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_modification_date")
    }

    override fun findPatientsByHcPartyDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsByDateOfBirth(startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_date_of_birth")
    }

    override fun findPatientsOfHcPartyDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return findPatientsByDateOfBirth(startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_date_of_birth")
    }

    private fun findPatientsByName(name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        var name = name
        val startKeyNameKeySuffix = if (descending) "\ufff0" else "\u0000"
        val endKeyNameKeySuffix = if (descending) "\u0000" else "\ufff0"
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val startKey: ComplexKey
        val endKey: ComplexKey
        if (name == null) {
            startKey = ComplexKey.of(healthcarePartyId, smallestKey)
            endKey = ComplexKey.of(healthcarePartyId, largestKey)
        } else {
            name = StringUtils.sanitizeString(name)
            startKey = ComplexKey.of(healthcarePartyId, name!! + startKeyNameKeySuffix)
            endKey = ComplexKey.of(healthcarePartyId, name!! + endKeyNameKeySuffix)
        }

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(client, viewName, startKey, endKey, pagination, descending)
        emitAll(client.queryView(viewQuery, Array<String>::class.java, String::class.java, Patient::class.java))
    }

    private fun findPatientsBySsin(ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKeyNameKeySuffix = if (descending) "\ufff0" else "\u0000"
        val endKeyNameKeySuffix = if (descending) "\u0000" else "\ufff0"
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val startKey: ComplexKey
        val endKey: ComplexKey
        if (ssin == null) {
            startKey = ComplexKey.of(healthcarePartyId, smallestKey)
            endKey = ComplexKey.of(healthcarePartyId, largestKey)
        } else {
            val ssinSearchString = ssin.replace(" ".toRegex(), "").replace("\\W".toRegex(), "")
            startKey = ComplexKey.of(healthcarePartyId, ssinSearchString + startKeyNameKeySuffix)
            endKey = ComplexKey.of(healthcarePartyId, ssinSearchString + endKeyNameKeySuffix)
        }

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(client, viewName, startKey, endKey, pagination, descending)
        emitAll(client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Patient::class.java))
    }

    private fun findPatientsByDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKeyStartDate = if (descending) endDate else startDate
        val endKeyEndDate = if (descending) startDate else endDate
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val from = ComplexKey.of(healthcarePartyId, if (startKeyStartDate == null && endKeyEndDate == null) null else startKeyStartDate ?: smallestKey)
        val to = ComplexKey.of(healthcarePartyId, if (startKeyStartDate == null && endKeyEndDate == null) null else endKeyEndDate ?: largestKey)

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(client, viewName, from, to, pagination, descending)
        emitAll(client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Patient::class.java))
    }

    private fun findPatientsByModificationDate(startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean, viewName: String): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val startKeyStartDate = if (descending) endDate else startDate
        val endKeyEndDate = if (descending) startDate else endDate
        val smallestKey = if (descending) ComplexKey.emptyObject() else null
        val largestKey = if (descending) null else ComplexKey.emptyObject()

        val from = ComplexKey.of(healthcarePartyId, startKeyStartDate ?: smallestKey)
        val to = ComplexKey.of(healthcarePartyId, endKeyEndDate ?: largestKey)

        val viewQuery = pagedViewQuery<Patient, ComplexKey>(client, viewName, from, to, pagination, descending)
        emitAll(client.queryView(viewQuery, ComplexKey::class.java, String::class.java, Patient::class.java))
    }

    @View(name = "by_user_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.userId) emit( doc.userId, doc._id )}")
    override suspend fun findPatientsByUserId(id: String): Patient? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val queryView = createQuery(client, "by_user_id").includeDocs(true).key(id)
        return client.queryViewIncludeDocs<String, String, Patient>(queryView).firstOrNull()?.doc
    }

    override fun getPatients(patIds: Collection<String>): Flow<Patient> {
        return getEntities(patIds)
    }

    @View(name = "by_external_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.externalId) emit( doc.externalId, doc._id )}")
    override suspend fun getPatientByExternalId(externalId: String): Patient? {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val queryView = createQuery(client, "by_external_id").includeDocs(true).key(externalId)
        return client.queryViewIncludeDocs<String, String, Patient>(queryView).firstOrNull()?.doc
    }

    @View(name = "deleted_by_delete_date", map = "function(doc) {\n" +
            "    if (doc.java_type == 'org.taktik.icure.entities.Patient' && doc.deleted){\n" +
            "      emit(doc.deleted)\n" +
            "    }\n" +
            "}")
    override fun findDeletedPatientsByDeleteDate(start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQuery<Patient, Long>(client, "deleted_by_delete_date", start, end, paginationOffset, descending)
        emitAll(client.queryView(viewQuery, Long::class.java, Any::class.java, Patient::class.java))
    }

    @View(name = "deleted_by_names", map = "classpath:js/patient/Deleted_by_names.js")
    override fun findDeletedPatientsByNames(_firstName: String?, _lastName: String?): Flow<Patient> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

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

        val queryView = createQuery(client, "deleted_by_names").startKey(startKey).endKey(endKey).includeDocs(true)
        val deleted_by_names = client.queryViewIncludeDocsNoValue<Array<String>, Patient>(queryView).map { it.doc }

        emitAll(
                if (firstName == null || lastName == null) {
                    deleted_by_names
                } else {
                    // filter firstName because main filtering is done on lastName
                    deleted_by_names
                            .filter { p -> p.firstName != null && StringUtils.sanitizeString(p.firstName)?.startsWith(firstName) == true }
                }
        )
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(): Flow<Patient> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.queryViewIncludeDocsNoValue<String, Patient>(createQuery(client, "conflicts").includeDocs(true)).map { it.doc })
    }

    @View(name = "by_modification_date", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient'  && doc.modified) emit(doc.modified)}  ") // TODO mbo revert
    override fun findPatientsModifiedAfter(date: Long, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val viewQuery = pagedViewQuery<Patient, Long>(client, "by_modification_date", date, java.lang.Long.MAX_VALUE, paginationOffset, false)
        emitAll(client.queryView(viewQuery, Long::class.java, Any::class.java, Patient::class.java))
    }

    override fun listPatientIdsByHcPartyAndSsins(ssins: Collection<String>, healthcarePartyId: String): Flow<String> {
        return listPatientIdsForSsins(ssins, healthcarePartyId, "by_hcparty_ssin")
    }

    @View(name = "by_hcparty_contains_name_delegate", map = "classpath:js/patient/By_hcparty_contains_name_delegate.js")
    override fun listPatientsByHcPartyName(searchString: String?, healthcarePartyId: String): Flow<String> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery(client, "by_hcparty_contains_name_delegate")
                .startKey(ComplexKey.of(healthcarePartyId, name))
                .endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).includeDocs(false)
        emitAll(client.queryView<Array<String>, String>(viewQuery).mapNotNull { it.value })
    }

    @View(name = "by_hcparty_delegate_keys", map = "classpath:js/patient/By_hcparty_delegate_keys_map.js")
    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
        //Not transactional aware
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val result = client.queryView<String, List<String>>(createQuery(client, "by_hcparty_delegate_keys")
                .includeDocs(false)
                .key(healthcarePartyId))

        val resultMap = HashMap<String, String>()
        result.collect {
            it.value?.let {
                resultMap[it[0]] = it[1]
            }
        }

        return resultMap
    }

    override fun listPatientByHealthcarepartyAndIdentifier(healthcarePartyId: String, system: String, id: String) = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        val queryView = createQuery(client, "by_hcparty_identifier").includeDocs(true).key(ComplexKey.of(healthcarePartyId, system, id))
        emitAll(client.queryViewIncludeDocs<ComplexKey, String, Patient>(queryView).map { it.doc })
    }

    override fun getDuplicatePatientsBySsin(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        return this.getDuplicatesFromView("by_hcparty_ssin", healthcarePartyId, paginationOffset)
    }

    override fun getDuplicatePatientsByName(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> {
        return this.getDuplicatesFromView("by_hcparty_name", healthcarePartyId, paginationOffset)
    }

    override fun findPatients(ids: Collection<String>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.getForPagination(ids, Patient::class.java))
    }

    override fun findPatients(ids: Flow<String>): Flow<ViewQueryResultEvent> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)
        emitAll(client.getForPagination(ids, Patient::class.java))
    }

    @View(name = "by_hcparty_identifier", map = "classpath:js/patient/By_hcparty_identifier_map.js")
    override fun listPatientByHealthcarepartyAndIdentifiers(healthcarePartyId: String, identifiers: List<Identifier>): Flow<Pair<Identifier, String>> = flow {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val queryView = createQuery(client, "by_hcparty_identifier")
                .keys(identifiers.map {
                    ComplexKey.of(healthcarePartyId, it.system, it.value)
                })

        emitAll(client.queryView<Array<ComplexKey>, String>(queryView)
            .mapNotNull {
                if (it.key.isNullOrEmpty() || it.key!![0].components.size < 3 || it.value == null)
                    null
                else
                    Identifier(system = it.key!![0].components[1] as String,
                        value = it.key!![0].components[2] as String) to it.value!!
            })
    }

    private fun getDuplicatesFromView(viewName: String, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val client = couchDbDispatcher.getClient(dbInstanceUrl)

        val from = if (paginationOffset.startKey == null) ComplexKey.of(healthcarePartyId, "") else ComplexKey.of(*paginationOffset.startKey as Array<Any>)
        val to = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())
        val viewQuery = createQuery(client, viewName)
                .startKey(from)
                .endKey(to)
                .reduce(true)
                .group(true)

        val viewResult = client.queryView<ComplexKey, String>(viewQuery)

        val keysWithDuplicates = viewResult.filter { it.value?.toIntOrNull()?.let { it > 1 } == true }.map { it.key }.toList()

        // TODO MB no reified
        val duplicatePatients = client.queryViewIncludeDocs<ComplexKey, String, Patient>(createQuery(client, viewName).keys(keysWithDuplicates).reduce(false).includeDocs(true))
                .filter { it.doc.active }
                .distinct()
        emitAll(duplicatePatients)
        //duplicatePatients.onEach { emit(it) }.collect()
    }
}

