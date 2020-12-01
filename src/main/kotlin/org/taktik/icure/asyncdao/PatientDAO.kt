package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import java.net.URI

interface PatientDAO : GenericDAO<Patient> {

    fun listIdsByHcPartyAndName(name: String, healthcarePartyId: String): Flow<String>
    fun listIdsOfHcPartyAndName(name: String, healthcarePartyId: String): Flow<String>
    fun listIdsByHcPartyAndSsin(ssin: String, healthcarePartyId: String): Flow<String>
    fun listIdsOfHcPartyAndSsin(ssin: String, healthcarePartyId: String): Flow<String>
    fun listIdsByActive(active: Boolean, healthcarePartyId: String): Flow<String>
    fun listOfMergesAfter(date: Long?): Flow<Patient>
    suspend fun countByHcParty(healthcarePartyId: String): Int
    suspend fun countOfHcParty(healthcarePartyId: String): Int
    fun listIdsByHcParty(healthcarePartyId: String): Flow<String>
    fun listIdsByHcPartyAndDateOfBirth(date: Int?, healthcarePartyId: String): Flow<String>

    fun listIdsByHcPartyAndDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String): Flow<String>
    fun listIdsByHcPartyGenderEducationProfession(healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String>
    fun listIdsForHcPartyDateOfBirth(date: Int?, healthcarePartyId: String): Flow<String>
    fun listIdsByHcPartyAndNameContainsFuzzy(searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String>
    fun listIdsOfHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String>

    fun listIdsByHcPartyAndExternalId(externalId: String?, healthcarePartyId: String): Flow<String>

    fun findIdsByHcParty(healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>
    fun findPatientsByHcPartyAndName(name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsOfHcPartyAndName(name: String?, healthcarePartyId: String, offset: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsByHcPartyAndSsin(ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsOfHcPartyAndSsin(ssin: String?, healthcarePartyId: String, offset: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findPatientsByHcPartyModificationDate(startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findPatientsOfHcPartyModificationDate(startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findPatientsByHcPartyDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsOfHcPartyDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>


    suspend fun findPatientsByUserId(id: String): Patient?
    fun get(patIds: Collection<String>): Flow<Patient>

    suspend fun getByExternalId(externalId: String): Patient?

    fun findDeletedPatientsByDeleteDate(start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

    fun findDeletedPatientsByNames(firstName: String?, lastName: String?): Flow<Patient>

    fun listConflicts(): Flow<Patient>

    fun listOfPatientsModifiedAfter(date: Long, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

    fun listIdsByHcPartyAndSsins(ssins: Collection<String>, healthcarePartyId: String): Flow<String>

    fun listByHcPartyName(searchString: String?, healthcarePartyId: String): Flow<String>

    suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

    fun getDuplicatePatientsBySsin(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    fun getDuplicatePatientsByName(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    fun getForPagination(ids: Collection<String>):  Flow<ViewQueryResultEvent>

    fun getForPagination(ids: Flow<String>):  Flow<ViewQueryResultEvent>

}
