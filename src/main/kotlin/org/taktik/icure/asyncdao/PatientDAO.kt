package org.taktik.icure.asyncdao

import com.fasterxml.jackson.core.JsonProcessingException
import kotlinx.coroutines.flow.Flow
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import java.net.URI

interface PatientDAO {

    fun listIdsByHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String): Flow<String?>
    fun listIdsOfHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String): Flow<String?>
    fun listIdsByHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String): Flow<String?>
    fun listIdsOfHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String): Flow<String?>
    fun listIdsByActive(dbInstanceUrl: URI, groupId: String, active: Boolean, healthcarePartyId: String): Flow<String?>
    fun listOfMergesAfter(dbInstanceUrl: URI, groupId: String, date: Long?): Flow<Patient>
    suspend fun countByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Int
    suspend fun countOfHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Int
    fun listIdsByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Flow<String?>
    fun listIdsByHcPartyAndDateOfBirth(dbInstanceUrl: URI, groupId: String, date: Int?, healthcarePartyId: String): Flow<String?>

    fun listIdsByHcPartyAndDateOfBirth(dbInstanceUrl: URI, groupId: String, startDate: Int?, endDate: Int?, healthcarePartyId: String): Flow<String?>
    fun listIdsByHcPartyGenderEducationProfession(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String?>
    fun listIdsForHcPartyDateOfBirth(dbInstanceUrl: URI, groupId: String, date: Int?, healthcarePartyId: String): Flow<String?>
    fun listIdsByHcPartyAndNameContainsFuzzy(dbInstanceUrl: URI, groupId: String, searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String?>
    fun listIdsOfHcPartyNameContainsFuzzy(dbInstanceUrl: URI, groupId: String, searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String?>

    fun listIdsByHcPartyAndExternalId(dbInstanceUrl: URI, groupId: String, externalId: String?, healthcarePartyId: String): Flow<String?>

    fun findIdsByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>
    fun findPatientsByHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsOfHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String, offset: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsByHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsOfHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String, offset: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findPatientsByHcPartyModificationDate(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findPatientsOfHcPartyModificationDate(dbInstanceUrl: URI, groupId: String, startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findPatientsByHcPartyDateOfBirth(dbInstanceUrl: URI, groupId: String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
    fun findPatientsOfHcPartyDateOfBirth(dbInstanceUrl: URI, groupId: String, startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>


    suspend fun findPatientsByUserId(dbInstanceUrl: URI, groupId: String, id: String): Patient?
    fun get(dbInstanceUrl: URI, groupId: String, patIds: Collection<String>): Flow<Patient>

    suspend fun getByExternalId(dbInstanceUrl: URI, groupId: String, externalId: String): Patient?

    fun findDeletedPatientsByDeleteDate(dbInstanceUrl: URI, groupId: String, start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

    fun findDeletedPatientsByNames(dbInstanceUrl: URI, groupId: String, firstName: String?, lastName: String?): Flow<Patient>

    fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Patient>

    fun listOfPatientsModifiedAfter(dbInstanceUrl: URI, groupId: String, date: Long, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

    fun listIdsByHcPartyAndSsins(dbInstanceUrl: URI, groupId: String, ssins: Collection<String>, healthcarePartyId: String): Flow<String?>

    fun listByHcPartyName(dbInstanceUrl: URI, groupId: String, searchString: String?, healthcarePartyId: String): Flow<String?>

    @View(name = "by_hcparty_delegate_keys", map = "classpath:js/healthcareparty/By_hcparty_delegate_keys_map.js")
    suspend fun getHcPartyKeysForDelegate(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Map<String, String>

    suspend fun getDuplicatePatientsBySsin(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    suspend fun getDuplicatePatientsByName(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

}
