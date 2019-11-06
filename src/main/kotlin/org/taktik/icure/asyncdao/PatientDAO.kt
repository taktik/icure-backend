package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.support.View
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import java.net.URI

interface PatientDAO {

    fun listIdsByHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String): Flow<String>
    fun listIdsOfHcPartyAndName(dbInstanceUrl: URI, groupId: String, name: String, healthcarePartyId: String): Flow<String>
    fun listIdsByHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String): Flow<String>
    fun listIdsOfHcPartyAndSsin(dbInstanceUrl: URI, groupId: String, ssin: String, healthcarePartyId: String): Flow<String>
    fun listIdsByActive(dbInstanceUrl: URI, groupId: String, active: Boolean, healthcarePartyId: String): Flow<String>
    fun listOfMergesAfter(dbInstanceUrl: URI, groupId: String, date: Long?): Flow<Patient>
    suspend fun countByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Int
    suspend fun countOfHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Int
    fun listIdsByHcParty(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String): Flow<String>
    fun listIdsByHcPartyAndDateOfBirth(dbInstanceUrl: URI, groupId: String, date: Int?, healthcarePartyId: String): Flow<String>
    fun listIdsByHcPartyGenderEducationProfession(dbInstanceUrl: URI, groupId: String, healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String>
}
