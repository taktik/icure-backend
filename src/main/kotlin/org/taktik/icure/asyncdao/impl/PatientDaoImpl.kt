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

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.JsonNode
import com.google.common.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.ektorp.ComplexKey
import org.ektorp.ViewQuery
import org.ektorp.ViewResult
import org.ektorp.support.View
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.Client
import org.taktik.couchdb.queryView
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.dao.impl.GenericIcureDAOImpl
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.IDGenerator
import org.taktik.icure.db.PaginatedDocumentKeyIdPair
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import java.net.URI

import java.util.ArrayList
import java.util.HashMap
import java.util.TreeSet
import java.util.stream.Collectors

@ExperimentalCoroutinesApi
@Repository("patientDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) emit(doc._id )}")
class PatientDAOImpl(@Qualifier("patientCouchDbDispatcher") val patientCouchDbDispatcher: CouchDbDispatcher, val idGenerator: IDGenerator) : GenericIcureDAOImpl<Patient>(Patient::class.java, couchdb, idGenerator), PatientDAO {

    @View(name = "by_hcparty_name", map = "classpath:js/patient/By_hcparty_name_map.js", reduce = "_count")
    override fun listIdsByHcPartyAndName(dbInstanceUrl:URI, groupId:String, name: String, healthcarePartyId: String): Flow<String> {
        return listIdsForName(dbInstanceUrl, groupId, name, healthcarePartyId, "by_hcparty_name")
    }

    @View(name = "of_hcparty_name", map = "classpath:js/patient/Of_hcparty_name_map.js")
    override fun listIdsOfHcPartyAndName(dbInstanceUrl:URI, groupId:String, name: String, healthcarePartyId: String): Flow<String> {
        return listIdsForName(dbInstanceUrl, groupId, name, healthcarePartyId, "of_hcparty_name")
    }

    @View(name = "by_hcparty_ssin", map = "classpath:js/patient/By_hcparty_ssin_map.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun listIdsByHcPartyAndSsin(dbInstanceUrl:URI, groupId:String, ssin: String, healthcarePartyId: String): Flow<String> {
        return listIdsForSsin(dbInstanceUrl, groupId, ssin, healthcarePartyId, "by_hcparty_ssin")
    }

    @View(name = "of_hcparty_ssin", map = "classpath:js/patient/Of_hcparty_ssin_map.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun listIdsOfHcPartyAndSsin(dbInstanceUrl:URI, groupId:String, ssin: String, healthcarePartyId: String): Flow<String> {
        return listIdsForSsin(dbInstanceUrl, groupId, ssin, healthcarePartyId, "of_hcparty_ssin")
    }

    @View(name = "by_hcparty_active", map = "classpath:js/patient/By_hcparty_active.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
    override fun listIdsByActive(dbInstanceUrl:URI, groupId:String, active: Boolean, healthcarePartyId: String): Flow<String> {
        return listIdsForActive(dbInstanceUrl, groupId, active, healthcarePartyId, "by_hcparty_active")
    }

    @View(name = "merged_by_date", map = "classpath:js/patient/Merged_by_date.js")
    override fun listOfMergesAfter(dbInstanceUrl:URI, groupId:String, date: Long?): Flow<Patient> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery("merged_by_date").startKey(date).includeDocs(true)
        return client.queryViewIncludeDocs<Long, String, Patient>(viewQuery).map { it.doc }
    }

    override suspend fun countByHcParty(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String): Int {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery("by_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return try { client.queryView<ComplexKey,Int>(viewQuery).first().value ?: 0 } catch(e:NoSuchElementException) { return 0 }
    }

    override suspend fun countOfHcParty(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String): Int {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery("of_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return try { client.queryView<ComplexKey,Int>(viewQuery).first().value ?: 0 } catch(e:NoSuchElementException) { return 0 }
    }

    override fun listIdsByHcParty(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String): Flow<String> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery("by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }


    @View(name = "by_hcparty_date_of_birth", map = "classpath:js/patient/By_hcparty_date_of_birth_map.js")
    override fun listIdsByHcPartyAndDateOfBirth(dbInstanceUrl:URI, groupId:String, date: Int?, healthcarePartyId: String): Flow<String> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery("by_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

    @View(name = "by_hcparty_gender_education_profession", map = "classpath:js/patient/By_hcparty_gender_education_profession_map.js")
    override fun listIdsByHcPartyGenderEducationProfession(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery("by_hcparty_gender_education_profession")
                .startKey(ComplexKey.of(healthcarePartyId, gender?.getName(), education, profession))
                .endKey(ComplexKey.of(healthcarePartyId, if (gender == null) ComplexKey.emptyObject() else gender.getName(), education
                        ?: ComplexKey.emptyObject(), profession ?: ComplexKey.emptyObject())).includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

    override fun listIdsByHcPartyAndDateOfBirth(dbInstanceUrl:URI, groupId:String, startDate: Int?, endDate: Int?, healthcarePartyId: String): List<String> {
        val viewQuery = createQuery("by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, startDate)).endKey(ComplexKey.of(healthcarePartyId, endDate)).includeDocs(false)
        return db.queryView(viewQuery, String::class.java)
    }

    @View(name = "of_hcparty_date_of_birth", map = "classpath:js/patient/Of_hcparty_date_of_birth_map.js")
    override fun listIdsForHcPartyDateOfBirth(dbInstanceUrl:URI, groupId:String, date: Int?, healthcarePartyId: String): List<String> {
        val viewQuery = createQuery("of_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false)
        return db.queryView(viewQuery, String::class.java)
    }

    @View(name = "by_hcparty_contains_name", map = "classpath:js/patient/By_hcparty_contains_name_map.js")
    override fun listIdsByHcPartyAndNameContainsFuzzy(dbInstanceUrl:URI, groupId:String, searchString: String?, healthcarePartyId: String, limit: Int?): List<String> {
        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery("by_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).limit(limit
                ?: 10000).includeDocs(false)
        return ArrayList(TreeSet(db.queryView(viewQuery, String::class.java)))
    }

    @View(name = "of_hcparty_contains_name", map = "classpath:js/patient/Of_hcparty_contains_name_map.js")
    override fun listIdsOfHcPartyNameContainsFuzzy(dbInstanceUrl:URI, groupId:String, searchString: String?, healthcarePartyId: String, limit: Int?): List<String> {
        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery("of_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).limit(limit
                ?: 10000).includeDocs(false)
        return ArrayList(TreeSet(db.queryView(viewQuery, String::class.java)))
    }

    private fun listIdsForName(dbInstanceUrl:URI, groupId:String, name: String?, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
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

        val viewQuery = createQuery(viewName).startKey(startKey).endKey(endKey).includeDocs(false)

        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

    private fun listIdsForSsin(dbInstanceUrl:URI, groupId:String, ssin: String?, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        var ssin = ssin
        val startKey: ComplexKey
        val endKey: ComplexKey

        //Not transactional aware
        if (ssin != null) {
            ssin = ssin.replace(" ".toRegex(), "").replace("\\W".toRegex(), "")
            startKey = ComplexKey.of(healthcarePartyId, ssin)
            endKey = ComplexKey.of(healthcarePartyId, ssin + "\ufff0")
        } else {
            startKey = ComplexKey.of(healthcarePartyId, null)
            endKey = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())
        }

        val viewQuery = createQuery(viewName).reduce(false).startKey(startKey).endKey(endKey).includeDocs(false)

        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

    private fun listIdsForSsins(dbInstanceUrl:URI, groupId:String, ssins: Collection<String>, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val viewQuery = createQuery(viewName).reduce(false).keys(ssins.stream().map { ssin -> ComplexKey.of(healthcarePartyId, ssin) }.collect<List<ComplexKey>, Any>(Collectors.toList())).includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

    private fun listIdsForActive(dbInstanceUrl:URI, groupId:String, active: Boolean, healthcarePartyId: String, viewName: String): Flow<String> {
        val client = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId)
        val onlyKey = ComplexKey.of(healthcarePartyId, if (active) 1 else 0)
        val viewQuery = createQuery(viewName).reduce(false).startKey(onlyKey).endKey(onlyKey).includeDocs(false)
        return client.queryView<ComplexKey,String>(viewQuery).map { it.id }
    }

    @View(name = "by_hcparty_externalid", map = "classpath:js/patient/By_hcparty_externalid_map.js")
    override fun listIdsByHcPartyAndExternalId(dbInstanceUrl:URI, groupId:String, externalId: String?, healthcarePartyId: String): List<String> {
        var externalId = externalId
        val startKey: ComplexKey
        val endKey: ComplexKey

        //Not transactional aware
        if (externalId != null) {
            externalId = externalId.replace(" ".toRegex(), "").replace("\\W".toRegex(), "")
            startKey = ComplexKey.of(healthcarePartyId, externalId)
            endKey = ComplexKey.of(healthcarePartyId, externalId + "\ufff0")
        } else {
            startKey = ComplexKey.of(healthcarePartyId, null)
            endKey = ComplexKey.of(healthcarePartyId, "\ufff0")
        }

        val viewQuery = createQuery("by_hcparty_externalid").startKey(startKey).endKey(endKey).includeDocs(false)

        return db.queryView(viewQuery, String::class.java)
    }

    override fun findIdsByHcParty(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String, pagination: PaginationOffset<*>): PaginatedList<String> {
        return pagedQueryViewOfIds("by_hcparty_name", ComplexKey.of(healthcarePartyId, null), ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject()), pagination)
    }

    override fun findPatientsByHcPartyAndName(dbInstanceUrl:URI, groupId:String, name: String, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findByName(name, healthcarePartyId, pagination, descending, "by_hcparty_name")
    }

    override fun findPatientsOfHcPartyAndName(dbInstanceUrl:URI, groupId:String, name: String, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findByName(name, healthcarePartyId, pagination, descending, "of_hcparty_name")
    }

    override fun findPatientsByHcPartyAndSsin(dbInstanceUrl:URI, groupId:String, ssin: String, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findBySsin(ssin, healthcarePartyId, pagination, descending, "by_hcparty_ssin")
    }

    override fun findPatientsOfHcPartyAndSsin(dbInstanceUrl:URI, groupId:String, ssin: String, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findBySsin(ssin, healthcarePartyId, pagination, descending, "of_hcparty_ssin")
    }

    @View(name = "by_hcparty_modification_date", map = "classpath:js/patient/By_hcparty_modification_date_map.js")
    override fun findPatientsByHcPartyModificationDate(dbInstanceUrl:URI, groupId:String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findByModificationDate(startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_modification_date")
    }

    @View(name = "of_hcparty_modification_date", map = "classpath:js/patient/Of_hcparty_modification_date_map.js")
    override fun findPatientsOfHcPartyModificationDate(dbInstanceUrl:URI, groupId:String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findByModificationDate(startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_modification_date")
    }

    override fun findPatientsByHcPartyDateOfBirth(dbInstanceUrl:URI, groupId:String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findByDateOfBirth(startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_date_of_birth")
    }

    override fun findPatientsOfHcPartyDateOfBirth(dbInstanceUrl:URI, groupId:String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<Patient> {
        return findByDateOfBirth(startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_date_of_birth")
    }


    private fun findByName(dbInstanceUrl:URI, groupId:String, name: String?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean, viewName: String): PaginatedList<Patient> {
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
        return pagedQueryView(viewName, startKey, endKey, pagination, descending)
    }

    private fun findBySsin(dbInstanceUrl:URI, groupId:String, ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean, viewName: String): PaginatedList<Patient> {
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
        return pagedQueryView(viewName, startKey, endKey, pagination, descending)
    }

    private fun findByDateOfBirth(dbInstanceUrl:URI, groupId:String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean, viewName: String): PaginatedList<Patient> {
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

        return pagedQueryView(viewName, from, to, pagination, descending)
    }

    private fun findByModificationDate(dbInstanceUrl:URI, groupId:String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<*>, descending: Boolean, viewName: String): PaginatedList<Patient> {
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

        return pagedQueryView(viewName, from, to, pagination, descending)
    }

    @View(name = "by_user_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.userId) emit( doc.userId, doc._id )}")
    override fun findPatientsByUserId(dbInstanceUrl:URI, groupId:String, id: String): Patient? {
        val patients = queryView("by_user_id", id)
        return if (patients.size > 0) patients[0] else null
    }

    override fun get(patIds: Collection<String>): List<Patient> {
        return getList(patIds)
    }

    @View(name = "by_external_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.externalId) emit( doc.externalId, doc._id )}")
    override fun getByExternalId(dbInstanceUrl:URI, groupId:String, externalId: String): Patient? {
        val patients = queryView("by_external_id", externalId)
        return if (patients.size > 0) patients[0] else null
    }

    @View(name = "deleted_by_delete_date", map = "function(doc) {\n" +
            "    if (doc.java_type == 'org.taktik.icure.entities.Patient' && doc.deleted){\n" +
            "      emit(doc.deleted)\n" +
            "    }\n" +
            "}")
    override fun findDeletedPatientsByDeleteDate(dbInstanceUrl:URI, groupId:String, start: Long?, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<*>): PaginatedList<Patient> {
        return pagedQueryView("deleted_by_delete_date", start, end, paginationOffset, descending)
    }

    @View(name = "deleted_by_names", map = "classpath:js/patient/Deleted_by_names.js")
    override fun findDeletedPatientsByNames(dbInstanceUrl:URI, groupId:String, _firstName: String?, _lastName: String?): List<Patient> {
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
        val deleted_by_names = queryView("deleted_by_names", startKey, endKey)
        return if (firstName == null || lastName == null) {
            deleted_by_names
        } else {
            // filter firstName because main filtering is done on lastName
            deleted_by_names.stream()
                    .filter { p: Patient -> p.firstName != null && StringUtils.sanitizeString(p.firstName).startsWith(firstName) }
                    .collect<List<Patient>, Any>(Collectors.toList())
        }
    }

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    override fun listConflicts(dbInstanceUrl:URI, groupId:String): List<Patient> {
        return queryView("conflicts")
    }

    @View(name = "by_modification_date", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && doc.modified) emit(doc.modified)}")
    override fun listOfPatientsModifiedAfter(dbInstanceUrl:URI, groupId:String, date: Long?, paginationOffset: PaginationOffset<Long>): PaginatedList<Patient> {
        return pagedQueryView("by_modification_date", date, java.lang.Long.MAX_VALUE, paginationOffset, false)
    }

    override fun listIdsByHcPartyAndSsins(dbInstanceUrl:URI, groupId:String, ssins: Collection<String>, healthcarePartyId: String): List<String> {
        return listIdsForSsins(ssins, healthcarePartyId, "by_hcparty_ssin")
    }

    @View(name = "by_hcparty_contains_name_delegate", map = "classpath:js/patient/By_hcparty_contains_name_delegate.js")
    override fun listByHcPartyName(dbInstanceUrl:URI, groupId:String, searchString: String?, healthcarePartyId: String): List<String> {
        val name = if (searchString != null) StringUtils.sanitizeString(searchString) else null
        val viewQuery = createQuery("by_hcparty_contains_name_delegate")
                .startKey(ComplexKey.of(healthcarePartyId, name))
                .endKey(ComplexKey.of(healthcarePartyId, if (name == null) ComplexKey.emptyObject() else name + "\ufff0")).includeDocs(false)
        return ArrayList(TreeSet(db.queryView(viewQuery, String::class.java)))
    }

    @View(name = "by_hcparty_delegate_keys", map = "classpath:js/patient/By_hcparty_delegate_keys_map.js")
    override suspend fun getHcPartyKeysForDelegate(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String): Map<String, String> {
        //Not transactional aware
        val result = patientCouchDbDispatcher.getClient(dbInstanceUrl, groupId).queryView<String,List<String>>(createQuery("by_hcparty_delegate_keys")
                .includeDocs(false)
                .key(healthcarePartyId))

        val resultMap = HashMap<String, String>()
        result.collect {  }
        for (row in result.rows) {
            val valueNode = row.valueAsNode
            resultMap[valueNode.get(0).asText()] = valueNode.get(1).asText()
        }

        return resultMap
    }

    @Throws(JsonProcessingException::class)
    override fun getDuplicatePatientsBySsin(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Patient> {
        return this.getDuplicatesFromView("by_hcparty_ssin", healthcarePartyId, paginationOffset)
    }

    @Throws(JsonProcessingException::class)
    override fun getDuplicatePatientsByName(dbInstanceUrl:URI, groupId:String, healthcarePartyId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Patient> {
        return this.getDuplicatesFromView("by_hcparty_name", healthcarePartyId, paginationOffset)
    }

    @Throws(JsonProcessingException::class)
    private fun getDuplicatesFromView(dbInstanceUrl:URI, groupId:String, viewName: String, healthcarePartyId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Patient> {
        val keysWithDuplicates = ArrayList<JsonNode>()
        val from = if (paginationOffset.startKey == null) ComplexKey.of(healthcarePartyId, "") else ComplexKey.of(*paginationOffset.startKey as Array<Any>)
        val to = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())
        val viewQuery = createQuery(viewName)
                .startKey(from)
                .endKey(to)
                .reduce(true)
                .group(true)
        val viewResult = db.queryView(viewQuery)

        var nextKey: PaginatedDocumentKeyIdPair? = null
        for (row in viewResult.rows) {
            if (keysWithDuplicates.size >= paginationOffset.limit) {
                nextKey = PaginatedDocumentKeyIdPair(CouchDbICureRepositorySupport.MAPPER.treeToValue(row.keyAsNode, List<*>::class.java), row.key)
                break
            }

            if (row.valueAsInt > 1) {
                keysWithDuplicates.add(row.keyAsNode)
            }
        }

        val duplicatePatients = db.queryView(createQuery(viewName).keys(keysWithDuplicates).reduce(false).includeDocs(true), Patient::class.java)
                .stream()
                .filter { patient -> patient.active == true }
                .distinct()
                .collect<List<Patient>, Any>(Collectors.toList())


        return PaginatedList<Patient?>(
                paginationOffset.limit!!,
                0,
                duplicatePatients,
                nextKey)
    }

    companion object {
        private val log = LoggerFactory.getLogger(PatientDAOImpl::class.java)
    }

}
