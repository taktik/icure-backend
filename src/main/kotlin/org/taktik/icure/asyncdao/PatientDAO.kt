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

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Identifier

interface PatientDAO : GenericDAO<Patient> {

	fun listPatientIdsByHcPartyAndName(name: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsOfHcPartyAndName(name: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndSsin(ssin: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsOfHcPartyAndSsin(ssin: String, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByActive(active: Boolean, healthcarePartyId: String): Flow<String>
	fun listOfMergesAfter(date: Long?): Flow<Patient>
	suspend fun countByHcParty(healthcarePartyId: String): Int
	suspend fun countOfHcParty(healthcarePartyId: String): Int
	fun listPatientIdsByHcParty(healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndDateOfBirth(date: Int?, healthcarePartyId: String): Flow<String>

	fun listPatientIdsByHcPartyAndDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyGenderEducationProfession(healthcarePartyId: String, gender: Gender?, education: String?, profession: String?): Flow<String>
	fun listPatientIdsForHcPartyDateOfBirth(date: Int?, healthcarePartyId: String): Flow<String>
	fun listPatientIdsByHcPartyAndNameContainsFuzzy(searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String>
	fun listPatientIdsOfHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String, limit: Int?): Flow<String>

	fun listPatientIdsByHcPartyAndExternalId(externalId: String?, healthcarePartyId: String): Flow<String>

	fun findPatientIdsByHcParty(healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>
	fun findPatientsByHcPartyAndName(name: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsOfHcPartyAndName(name: String?, healthcarePartyId: String, offset: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsByHcPartyAndSsin(ssin: String?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsOfHcPartyAndSsin(ssin: String?, healthcarePartyId: String, offset: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	fun findPatientsByHcPartyModificationDate(startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	fun findPatientsOfHcPartyModificationDate(startDate: Long?, endDate: Long?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	fun findPatientsByHcPartyDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>
	fun findPatientsOfHcPartyDateOfBirth(startDate: Int?, endDate: Int?, healthcarePartyId: String, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

	suspend fun findPatientsByUserId(id: String): Patient?
	fun getPatients(patIds: Collection<String>): Flow<Patient>

	suspend fun getPatientByExternalId(externalId: String): Patient?

	fun findDeletedPatientsByDeleteDate(start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

	fun findDeletedPatientsByNames(firstName: String?, lastName: String?): Flow<Patient>

	fun listConflicts(): Flow<Patient>

	fun findPatientsModifiedAfter(date: Long, paginationOffset: PaginationOffset<Long>): Flow<ViewQueryResultEvent>

	fun listPatientIdsByHcPartyAndSsins(ssins: Collection<String>, healthcarePartyId: String): Flow<String>

	fun listPatientsByHcPartyName(searchString: String?, healthcarePartyId: String): Flow<String>

	suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String>

	fun getDuplicatePatientsBySsin(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

	fun getDuplicatePatientsByName(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

	fun findPatients(ids: Collection<String>): Flow<ViewQueryResultEvent>

	fun findPatients(ids: Flow<String>): Flow<ViewQueryResultEvent>

	fun listPatientIdsByHcPartyAndIdentifiers(healthcarePartyId: String, identifiers: List<Identifier>): Flow<String>

	fun listPatientsByHcPartyAndIdentifier(healthcarePartyId: String, system: String, id: String): Flow<Patient>
}
