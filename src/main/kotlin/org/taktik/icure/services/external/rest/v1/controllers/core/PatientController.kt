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

package org.taktik.icure.services.external.rest.v1.controllers.core

import com.google.common.base.Splitter
import com.google.gson.Gson
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.Sorting
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.services.external.rest.v1.dto.*
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.utils.injectReactorContext
import org.taktik.icure.utils.paginatedList
import reactor.core.publisher.Flux
import java.time.Instant
import java.util.*
import javax.security.auth.login.LoginException

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/patient")
@Tag(name = "patient")
class PatientController(
        private val sessionLogic: AsyncSessionLogic,
        private val accessLogLogic: AccessLogLogic,
        private val mapper: MapperFacade,
        private val filters: Filters,
        private val patientLogic: PatientLogic,
        private val healthcarePartyLogic: HealthcarePartyLogic) {

    @Operation(summary = "Find patients for the current user (HcParty) ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/byNameBirthSsinAuto")
    fun findByNameBirthSsinAuto(
            @ApiParam(value = "HealthcareParty Id, if unset will user user's hcpId") @RequestParam(required = false) healthcarePartyId: String?,
            @ApiParam(value = "Optional value for filtering results") @RequestParam(required = false) filterValue: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
            @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.") @RequestParam(required = false, defaultValue = "asc") sortDirection: String
    ) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { Gson().fromJson(it, Array<String>::class.java).toList() }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        sessionLogic.getCurrentHealthcarePartyId().let { currentHcpId ->
            val hcp = healthcarePartyLogic.getHealthcareParty(currentHcpId)
            (hcp?.parentId?.let { if (it.isNotEmpty()) it else null } ?: hcp?.id)?.let { hcpId ->
                patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(
                    hcpId,
                    paginationOffset,
                    filterValue,
                    Sorting(null, sortDirection))
                    .let { PatientPaginatedList(it.paginatedList<Patient, PatientDto>(mapper, realLimit)) }
        } ?: PatientPaginatedList() }
    }

    private fun buildPaginatedListResponse(patients: PaginatedList<Patient>): PatientPaginatedList =
            PatientPaginatedList(patients.pageSize, patients.totalSize, patients.rows.map { mapper.map(it, PatientDto::class.java) }, mapper.map(patients.nextKeyPair, PaginatedDocumentKeyIdPair::class.java))

    @Operation(summary = "List patients of a specific HcParty or of the current HcParty ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/ofHcParty/{hcPartyId}")
    fun listPatientsOfHcParty(@PathVariable hcPartyId: String,
                              @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String?,
                              @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
                              @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                              @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                              @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.") @RequestParam(required = false, defaultValue = "asc") sortDirection: String) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { Gson().fromJson(it, Array<String>::class.java).toList() }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)
        PatientPaginatedList(patientLogic.findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(hcPartyId, paginationOffset, null, Sorting(sortField, sortDirection)).paginatedList<Patient, PatientDto>(mapper, realLimit))
    }

    @Operation(summary = "List patients that have been merged towards another patient ", description = "Returns a list of patients that have been merged after the provided date")
    @GetMapping("/merges/{date}")
    fun listOfMergesAfter(@PathVariable date: Long) =
            patientLogic.listOfMergesAfter(date).map { p -> mapper.map(p, PatientDto::class.java) }.injectReactorContext()

    @Operation(summary = "List patients that have been modified after the provided date", description = "Returns a list of patients that have been modified after the provided date")
    @GetMapping("/modifiedAfter/{date}")
    fun listOfPatientsModifiedAfter(@PathVariable date: Long,
                                    @ApiParam(value = "The start key for pagination the date of the first element of the new page") @RequestParam(required = false) startKey: Long?,
                                    @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                    @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {
        PatientPaginatedList(patientLogic.listOfPatientsModifiedAfter(date, startKey, startDocumentId, (limit
                ?: DEFAULT_LIMIT) + 1).paginatedList<Patient, PatientDto>(mapper, limit ?: DEFAULT_LIMIT))
    }

    @Operation(summary = "List patients for a specific HcParty or for the current HcParty ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/hcParty/{hcPartyId}")
    fun listPatientsByHcParty(@PathVariable hcPartyId: String,
                              @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String,
                              @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String,
                              @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String,
                              @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                              @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.", defaultValue = "asc") @RequestParam(required = false) sortDirection: String) =
            listPatients(hcPartyId, sortField, startKey, startDocumentId, limit, sortDirection)

    @Operation(summary = "Get the HcParty encrypted AES keys indexed by owner", description = "(key, value) of the map is as follows: (ID of the owner of the encrypted AES key, encrypted AES key)")
    @GetMapping("/{healthcarePartyId}/keys")
    fun getHcPartyKeysForDelegate(@PathVariable healthcarePartyId: String) = mono {
        patientLogic.getHcPartyKeysForDelegate(healthcarePartyId)
    }

    @Operation(summary = "Get count of patients for a specific HcParty or for the current HcParty ", description = "Returns the count of patients")
    @GetMapping("/hcParty/{hcPartyId}/count")
    fun countOfPatients(@ApiParam(value = "Healthcare party id") @PathVariable hcPartyId: String) = mono {
        ContentDto.fromNumberValue(patientLogic.countByHcParty(hcPartyId))
    }

    @Operation(summary = "List patients for a specific HcParty", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping
    fun listPatients(@ApiParam(value = "Healthcare party id") @RequestParam(required = false) hcPartyId: String?,
                     @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String?,
                     @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
                     @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                     @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                     @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.") @RequestParam(required = false, defaultValue = "asc") sortDirection: String) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let {  Gson().fromJson(it, Array<String>::class.java).toList() }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)
        sessionLogic.getCurrentHealthcarePartyId().let { currentHcpId ->
            val hcp = healthcarePartyLogic.getHealthcareParty(currentHcpId)
            (hcp?.parentId ?: hcp?.id)?.let { hcpId ->
                patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(
                        hcpId,
                        paginationOffset,
                        null,
                        Sorting(sortField, sortDirection))
                        .let { PatientPaginatedList(it.paginatedList<Patient, PatientDto>(mapper, realLimit)) }
            } ?: PatientPaginatedList()
        }
    }

    @Operation(summary = "List patients by pages for a specific HcParty", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/idsPages")
    fun listPatientsIds(@ApiParam(value = "Healthcare party id")@RequestParam hcPartyId: String,
                        @ApiParam(value = "The page first id") @RequestParam(required = false) startKey: String?,
                        @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                        @ApiParam(value = "Page size") @RequestParam(required = false) limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { Gson().fromJson(it, Array<String>::class.java).toList() }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)
        val collected = patientLogic.findByHcPartyIdsOnly(hcPartyId, paginationOffset).toList()
        println("test")
        patientLogic.findByHcPartyIdsOnly(hcPartyId, paginationOffset).paginatedList<Identifiable<String>, String>(mapper, realLimit)
    }

    @Operation(summary = "Get Paginated List of Patients sorted by Access logs descending")
    @GetMapping("/byExternalId/{externalId}")
    fun findByExternalId(@PathVariable("externalId")
                         @ApiParam(value = "A external ID", required = true) externalId: String) = mono {
        mapper.map(patientLogic.getByExternalId(externalId), PatientDto::class.java)
    }

    @Operation(summary = "Get Paginated List of Patients sorted by Access logs descending")
    @GetMapping("/byAccess/{userId}")
    fun findByAccessLogUserAfterDate(@ApiParam(value = "A User ID", required = true) @PathVariable userId: String,
                                     @ApiParam(value = "The type of access (COMPUTER or USER)") @RequestParam(required = false) accessType: String?,
                                     @ApiParam(value = "The start search epoch") @RequestParam(required = false) startDate: Long?,
                                     @ApiParam(value = "The start key for pagination") @RequestParam(required = false) startKey: String?,
                                     @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                     @ApiParam(value = "Number of rows") @RequestParam(defaultValue = DEFAULT_LIMIT.toString()) limit: Int) = mono {

        val startKeyElements = startKey?.let { Gson().fromJson(it, Array<String>::class.java).toList() }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)
        accessLogLogic.findByUserAfterDate(userId, accessType, startDate?.let { Instant.ofEpochMilli(it) }, paginationOffset, true).paginatedList<AccessLog>(limit)
                .let {
                    val patientsDtos = PatientPaginatedList()

                    patientsDtos.nextKeyPair = mapper.map(it.nextKeyPair, PaginatedDocumentKeyIdPair::class.java)
                    patientsDtos.pageSize = it.pageSize
                    patientsDtos.totalSize = it.totalSize

                    val patientIds = it.rows.filterNotNull().sortedBy { accessLog -> accessLog.date }.map { it.patientId }.distinct()

                    patientsDtos.rows = patientLogic.getPatients(patientIds).filter { it.deletionDate == null }.map { p ->
                        val pdto = PatientDto()
                        pdto.id = p.id
                        pdto.lastName = p.lastName
                        pdto.firstName = p.firstName
                        pdto.partnerName = p.partnerName
                        pdto.maidenName = p.maidenName
                        pdto.dateOfBirth = p.dateOfBirth
                        pdto.ssin = p.ssin
                        pdto.externalId = p.externalId
                        pdto.patientHealthCareParties = p.patientHealthCareParties.map { phcp -> mapper.map(phcp, PatientHealthCarePartyDto::class.java) }
                        pdto.addresses = p.addresses.map { mapper.map(it, AddressDto::class.java) }
                        pdto
                    }.toList()

                    patientsDtos
                }
    }

    @Operation(summary = "Filter patients for the current user (HcParty) ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterBy(
            @ApiParam(value = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
            @ApiParam(value = "Skip rows") @RequestParam(required = false) skip: Int?,
            @ApiParam(value = "Sort key") @RequestParam(required = false) sort: String?,
            @ApiParam(value = "Descending") @RequestParam(required = false) desc: Boolean?,
            @RequestBody filterChain: FilterChain) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { ArrayList(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it)) }
        val paginationOffset = PaginationOffset<List<String>>(startKeyList, startDocumentId, skip, realLimit + 1)

        try {
            //(Filter<String,O> filter, Predicate predicate)
            val patients = filterChain?.let {
                patientLogic.listPatients(paginationOffset, org.taktik.icure.dto.filter.chain.FilterChain<Patient>(it.filter as org.taktik.icure.dto.filter.Filter<String, Patient>, mapper.map(it.predicate, Predicate::class.java)), sort, desc)
            }
                    ?: patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(sessionLogic.getCurrentHealthcarePartyId(), paginationOffset, null, Sorting(null, "asc"))

            log.info("Filter patients in " + (System.currentTimeMillis() - System.currentTimeMillis()) + " ms.")

            patients.paginatedList<Patient, PatientDto>(mapper, realLimit)
        } catch (e: LoginException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Get ids of patients matching the provided filter for the current user (HcParty) ")
    @PostMapping("/match")
    fun matchBy(@RequestBody filter: FilterDto<*>): Flux<String> = filters.resolve(filter).injectReactorContext()

    @Operation(summary = "Filter patients for the current user (HcParty) ", description = "Returns a list of patients")
    @GetMapping("/fuzzy")
    fun fuzzySearch(
            @ApiParam(value = "The first name") @RequestParam(required = false) firstName: String,
            @ApiParam(value = "The last name") @RequestParam(required = false) lastName: String,
            @ApiParam(value = "The date of birth") @RequestParam(required = false) dateOfBirth: Int?): Flux<PatientDto> {

        return try {
            patientLogic.fuzzySearchPatients(mapper, firstName, lastName, dateOfBirth)
                    .map { p -> mapper.map(p, PatientDto::class.java) }
                    .injectReactorContext()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Create a patient", description = "Name, last name, date of birth, and gender are required. After creation of the patient and obtaining the ID, you need to create an initial delegation.")
    @PostMapping
    fun createPatient(@RequestBody p: PatientDto) = mono {
        val patient = try {
            patientLogic.createPatient(mapper.map(p, Patient::class.java))
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        patient?.let { mapper.map(it, PatientDto::class.java) } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Patient creation failed.")
    }

    @Operation(summary = "Delete patients.", description = "Response is an array containing the ID of deleted patient..")
    @DeleteMapping("/{patientIds}")
    fun deletePatient(@PathVariable patientIds: String): Flux<DocIdentifier> {
        val ids = patientIds.split(',')
        if (ids.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        return patientLogic.deletePatients(HashSet(ids)).injectReactorContext()
    }

    @Operation(summary = "Find deleted patients", description = "Returns a list of deleted patients, within the specified time period, if any.")
    @GetMapping("/deleted/by_date")
    fun listDeletedPatients(
            @ApiParam(value = "Filter deletions after this date (unix epoch), included") @RequestParam(required = false) startDate: Long,
            @ApiParam(value = "Filter deletions before this date (unix epoch), included") @RequestParam(required = false) endDate: Long?,
            @ApiParam(value = "Descending") @RequestParam(required = false) desc: Boolean?,
            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startDate, startDocumentId, null, realLimit + 1) // TODO works with descending=true?
        patientLogic.findDeletedPatientsByDeleteDate(startDate, endDate, desc ?: false, paginationOffset).paginatedList<Patient, PatientDto>(mapper, realLimit)
    }

    @Operation(summary = "Find deleted patients", description = "Returns a list of deleted patients, by name and/or firstname prefix, if any.")
    @GetMapping("/deleted/by_name")
    fun listDeletedPatientsByName(
            @ApiParam(value = "First name prefix") @RequestParam(required = false) firstName: String?,
            @ApiParam(value = "Last name prefix") @RequestParam(required = false) lastName: String?) =
            try {
                patientLogic.findDeletedPatientsByNames(firstName, lastName).map { p -> mapper.map(p, PatientDto::class.java) }.injectReactorContext()
            } catch (e: Exception) {
                log.warn(e.message, e)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
            }

    @Operation(summary = "undelete previously deleted patients", description = "Response is an array containing the ID of undeleted patient..")
    @PutMapping("/undelete/{patientIds}")
    fun undeletePatient(@PathVariable patientIds: String): Flux<DocIdentifier> {
        val ids = patientIds.split(',')
        if (ids.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        return patientLogic.undeletePatients(HashSet(ids))
                .injectReactorContext()
    }


    @Operation(summary = "Delegates a patients to a healthcare party", description = "It delegates a patient to a healthcare party (By current healthcare party). A modified patient with new delegation gets returned.")
    @PostMapping("/{patientId}/delegate")
    fun newDelegations(@PathVariable patientId: String,
                       @RequestBody ds: List<DelegationDto>) = mono {
        try {
            patientLogic.addDelegations(patientId, ds.map { d -> mapper.map(d, Delegation::class.java) })
            val patientWithDelegations = patientLogic.getPatient(patientId)

            patientWithDelegations?.takeIf { it.delegations.isNotEmpty() }?.let { mapper.map(it, PatientDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred in creation of the delegation.")
        } catch (e: Exception) {
            log.error(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Get patients by id", description = "It gets patient administrative data.")
    @PostMapping("/byIds")
    fun getPatients(@RequestBody patientIds: ListOfIdsDto): Flux<PatientDto> {
        return patientLogic.getPatients(patientIds.ids)
                .map { p -> mapper.map(p, PatientDto::class.java) }
                .injectReactorContext()
    }

    @Operation(summary = "Get patient", description = "It gets patient administrative data.")
    @GetMapping("/{patientId}")
    fun getPatient(@PathVariable patientId: String) = mono {
        patientLogic.getPatient(patientId)?.let { mapper.map(it, PatientDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.")
    }

    @Operation(summary = "Modify a patient", description = "Returns the id and _rev of created patients")
    @PostMapping("/bulk")
    fun bulkUpdatePatients(@RequestBody patientDtos: List<PatientDto>) = mono {
        try {
            val patients = patientLogic.updateEntities(patientDtos.map { p -> mapper.map(p, Patient::class.java) }.toList())
            patients.map { p -> mapper.map(p, IdWithRevDto::class.java) }.toList()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "Modify a patient", description = "No particular return value. It's just a message.")
    @PutMapping
    fun modifyPatient(@RequestBody patientDto: PatientDto) = mono {
        patientLogic.modifyPatient(mapper.map(patientDto, Patient::class.java)).let { mapper.map(it, PatientDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.").also { log.error(it.message) }
    }

    @Operation(summary = "Set a patient referral doctor")
    @PutMapping("/{patientId}/referral/{referralId}")
    fun modifyPatientReferral(@PathVariable patientId: String,
                              @ApiParam(value = "The referal id. Accepts 'none' for referral removal.") @PathVariable referralId: String,
                              @ApiParam(value = "Optional value for start of referral") @RequestParam(required = false) start: Long?,
                              @ApiParam(value = "Optional value for end of referral") @RequestParam(required = false) end: Long?) = mono {
        patientLogic.getPatient(patientId)
                ?.let {
                    mapper.map(patientLogic.modifyPatientReferral(it, if (referralId == "none") null else referralId, if (start == null) null else Instant.ofEpochMilli(start), if (end == null) null else Instant.ofEpochMilli(end)), PatientDto::class.java)
                }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find patient with ID $patientId in the database").also { log.error(it.message) }
    }

    @Operation(summary = "Merge a series of patients into another patient")
    @PutMapping("/mergeInto/{toId}/from/{fromIds}")
    fun mergeInto(@PathVariable("toId") patientId: String, @PathVariable fromIds: String) = mono {
        val patient = patientLogic.getPatient(patientId)
        patient?.let {
            val patientsFrom = fromIds
                    .split(',')
                    .mapNotNull { patientLogic.getPatient(it) }
                    .toList()
            val mergedPatient = patientLogic.mergePatient(patient, patientsFrom)
            mapper.map(mergedPatient, PatientDto::class.java)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find patient with ID $patientId in the database").also { log.error(it.message) }
    }

    // TODO MB add missing methods like findDuplicatesBySsin or findDuplicatesByName  (compare this controller with the master branch)

    companion object {
        private val log = LoggerFactory.getLogger(javaClass)
        private const val DEFAULT_LIMIT = 1000
    }
}
