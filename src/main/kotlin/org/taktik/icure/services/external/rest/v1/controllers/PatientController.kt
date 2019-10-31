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

package org.taktik.icure.services.external.rest.v1.controllers

import com.google.common.base.Splitter
import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.metadata.TypeBuilder
import org.ektorp.ComplexKey
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.Sorting
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.DocumentNotFoundException
import org.taktik.icure.logic.AccessLogLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.ICureSessionLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.impl.filter.Filters
import org.taktik.icure.services.external.rest.v1.dto.*
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.utils.ResponseUtils
import java.time.Instant
import java.util.*
import javax.security.auth.login.LoginException
import kotlin.streams.toList

@RestController
@RequestMapping("/rest/v1/patient")
@Api(tags = ["patient"])
class PatientController(
        private val sessionLogic: ICureSessionLogic,
        private val accessLogLogic: AccessLogLogic,
        private val mapper: MapperFacade,
        private val filters: Filters,
        private val patientLogic: PatientLogic,
        private val healthcarePartyLogic: HealthcarePartyLogic) {

    @ApiOperation(nickname = "findByNameBirthSsinAuto", value = "Find patients for the current user (HcParty) ", notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/byNameBirthSsinAuto")
    fun findByNameBirthSsinAuto(
            @ApiParam(value = "HealthcareParty Id, if unset will user user's hcpId") @RequestParam(required = false) healthcarePartyId: String?,
            @ApiParam(value = "Optional value for filtering results") @RequestParam(required = false) filterValue: String?,
            @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
            @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.", defaultValue = "asc") @RequestParam(required = false) sortDirection: String?
    ): PatientPaginatedList {
        val startKeyElements = Gson().fromJson(startKey, Array<String>::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)
        val hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentHealthcarePartyId)
        return patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(
                if (hcp.parentId != null) hcp.parentId else hcp.id,
                paginationOffset,
                filterValue,
                Sorting(null, sortDirection))
                ?.let { buildPaginatedListResponse(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing patients failed.")
    }

    private fun buildPaginatedListResponse(patients: PaginatedList<Patient>): PatientPaginatedList {
        patients.rows = patients.rows ?: emptyList()
        val paginatedPatientDtoList = PatientPaginatedList()
        mapper.map(
                patients,
                paginatedPatientDtoList,
                object : TypeBuilder<PaginatedList<Patient>>() {}.build(),
                object : TypeBuilder<PatientPaginatedList>() {}.build())
        return paginatedPatientDtoList
    }


    @ApiOperation(nickname = "listPatientsOfHcParty", value = "List patients of a specific HcParty or of the current HcParty ", notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/ofHcParty/{hcPartyId}")
    fun listPatientsOfHcParty(@PathVariable hcPartyId: String?,
                              @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String?,
                              @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
                              @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                              @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                              @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.", defaultValue = "asc") @RequestParam(required = false) sortDirection: String?) {
        val startKeyElements = Gson().fromJson(startKey, Array<String>::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)
        patientLogic.findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(hcPartyId, paginationOffset, null, Sorting(sortField, sortDirection))
                ?.let { buildPaginatedListResponse(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing patients failed.")
    }

    @ApiOperation(nickname = "listOfMergesAfter", value = "List patients that have been merged towards another patient ", notes = "Returns a list of patients that have been merged after the provided date")
    @GetMapping("/merges/{date}")
    fun listOfMergesAfter(@PathVariable date: Long?) =
            patientLogic.listOfMergesAfter(date).map { p -> mapper.map(p, PatientDto::class.java) }

    @ApiOperation(nickname = "listOfPatientsModifiedAfter", value = "List patients that have been modified after the provided date", notes = "Returns a list of patients that have been modified after the provided date")
    @GetMapping("/modifiedAfter/{date}")
    fun listOfPatientsModifiedAfter(@PathVariable date: Long?,
                                    @ApiParam(value = "The start key for pagination the date of the first element of the new page") @RequestParam(required = false) startKey: Long?,
                                    @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                    @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?) =
            patientLogic.listOfPatientsModifiedAfter(date, startKey, startDocumentId, limit).also { mapper.map(it, PatientPaginatedList::class.java) }

    @ApiOperation(nickname = "listPatientsByHcParty", value = "List patients for a specific HcParty or for the current HcParty ", notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/hcParty/{hcPartyId}")
    fun listPatientsByHcParty(@PathVariable(required = false) hcPartyId: String,
                              @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String,
                              @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String,
                              @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String,
                              @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                              @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.", defaultValue = "asc") @RequestParam(required = false) sortDirection: String) =
            listPatients(hcPartyId, sortField, startKey, startDocumentId, limit, sortDirection)

    @ApiOperation(nickname = "getHcPartyKeysForDelegate", value = "Get the HcParty encrypted AES keys indexed by owner", notes = "(key, value) of the map is as follows: (ID of the owner of the encrypted AES key, encrypted AES key)")
    @GetMapping("/{healthcarePartyId}/keys")
    fun getHcPartyKeysForDelegate(@PathVariable healthcarePartyId: String) =
            patientLogic.getHcPartyKeysForDelegate(healthcarePartyId)
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding fetching keys. Read the app logs.")

    @ApiOperation(nickname = "countOfPatients", value = "Get count of patients for a specific HcParty or for the current HcParty ", response = ContentDto::class, httpMethod = "GetMapping", notes = "Returns the count of patients")
    @GetMapping("/hcParty/{hcPartyId}/count")
    fun countOfPatients(@ApiParam(value = "Healthcare party id") @PathVariable(required = false) hcPartyId: String?) =
            ResponseUtils.ok(ContentDto.fromNumberValue(patientLogic.countByHcParty(hcPartyId)))

    @ApiOperation(nickname = "listPatients", value = "List patients for a specific HcParty", response = org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList::class, httpMethod = "GetMapping", notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping
    fun listPatients(@ApiParam(value = "Healthcare party id") @RequestParam(required = false) hcPartyId: String?,
                     @ApiParam(value = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String?,
                     @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
                     @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                     @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
                     @ApiParam(value = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.", defaultValue = "asc") @RequestParam(required = false) sortDirection: String?): PatientPaginatedList {
        val startKeyElements = Gson().fromJson(startKey, Array<String>::class.java)
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)
        val hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.currentHealthcarePartyId)
        return patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(if (hcp.parentId != null) hcp.parentId else hcp.id, paginationOffset, null, Sorting(sortField, sortDirection))?.let { buildPaginatedListResponse(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing patients failed.")
    }

    @ApiOperation(nickname = "listPatientsIds", value = "List patients by pages for a specific HcParty", notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/idsPages")
    fun listPatientsIds(@ApiParam(value = "Healthcare party id") @RequestParam(required = false) hcPartyId: String?,
                        @ApiParam(value = "The page first id") @RequestParam(required = false) startKey: String?,
                        @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                        @ApiParam(value = "Page size") @RequestParam(required = false) limit: Int?): PaginatedList<String> {
        val startKeyElements = startKey?.let { ComplexKey.of(Gson().fromJson(it, Array<String>::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, limit)
        return patientLogic.findByHcPartyIdsOnly(hcPartyId, paginationOffset)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing patients failed.")
    }

    @ApiOperation(nickname = "findByExternalId", value = "Get Paginated List of Patients sorted by Access logs descending")
    @GetMapping("/byExternalId/{externalId}")
    fun findByExternalId(@PathVariable("externalId")
                         @ApiParam(value = "A external ID", required = true) externalId: String) = mapper.map(patientLogic.getByExternalId(externalId), PatientDto::class.java)

    @ApiOperation(nickname = "findByAccessLogUserAfterDate", value = "Get Paginated List of Patients sorted by Access logs descending")
    @GetMapping("/byAccess/{userId}")
    fun findByAccessLogUserAfterDate(@ApiParam(value = "A User ID", required = true) @PathVariable userId: String,
                                     @ApiParam(value = "The type of access (COMPUTER or USER)") @RequestParam(required = false) accessType: String?,
                                     @ApiParam(value = "The start search epoch") @RequestParam(required = false) startDate: Long?,
                                     @ApiParam(value = "The start key for pagination") @RequestParam(required = false) startKey: String?,
                                     @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                     @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): PatientPaginatedList {

        fun removeDuplicates(patientIds: List<String>): List<String> {
            var patientIds = patientIds
            val patientIdsSet = LinkedHashSet<String>()
            patientIdsSet.addAll(patientIds)
            patientIds = ArrayList(patientIdsSet)
            return patientIds
        }

        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, limit)
        accessLogLogic.findByUserAfterDate(userId, accessType, if (startDate == null) startDate else Instant.ofEpochMilli(startDate), paginationOffset, true)
                ?.let {
                    val patientsDtos = PatientPaginatedList()

                    patientsDtos.nextKeyPair = mapper.map(it.nextKeyPair, PaginatedDocumentKeyIdPair::class.java)
                    patientsDtos.pageSize = it.pageSize
                    patientsDtos.totalSize = it.totalSize

                    val patientIds = removeDuplicates(it.rows.filter { accessLog -> Objects.nonNull(accessLog) }.sortedBy { accessLog -> accessLog.date }.map { accessLog -> accessLog.patientId })

                    patientsDtos.rows = patientLogic.getPatients(patientIds).filter { p -> p != null && p.deletionDate == null }.map { p ->
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
                        pdto.addresses = p.addresses.stream().map { a -> mapper.map(a, AddressDto::class.java) }.toList()

                        pdto
                    }.toList()

                    return patientsDtos
                }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AccessLog based patient listing failed")
    }

    @ApiOperation(nickname = "filterBy", value = "Filter patients for the current user (HcParty) ", notes = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterBy(
            @ApiParam(value = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?,
            @ApiParam(value = "Skip rows") @RequestParam(required = false) skip: Int?,
            @ApiParam(value = "Sort key") @RequestParam(required = false) sort: String,
            @ApiParam(value = "Descending") @RequestParam(required = false) desc: Boolean?,
            @RequestBody filterChain: FilterChain?): PatientPaginatedList {

        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { ArrayList(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it)) }
        val paginationOffset = PaginationOffset(startKeyList, startDocumentId, skip, limit)

        try {
            //(Filter<String,O> filter, Predicate predicate)
            val patients = filterChain?.let {
                patientLogic.listPatients(paginationOffset, org.taktik.icure.dto.filter.chain.FilterChain<Patient>(it.filter as org.taktik.icure.dto.filter.Filter<String, Patient>, mapper.map(it.predicate, Predicate::class.java)), sort, desc)
            }
                    ?: patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(null, paginationOffset, null, Sorting(null, "asc"))

            log.info("Filter patients in " + (System.currentTimeMillis() - System.currentTimeMillis()) + " ms.")

            return patients?.let { buildPaginatedListResponse(it) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing patients failed.")

        } catch (e: LoginException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @ApiOperation(nickname = "matchBy", value = "Get ids of patients matching the provided filter for the current user (HcParty) ")
    @PostMapping("/match")
    fun matchBy(@RequestBody filter: Filter<*>): List<String> = filters.resolve(filter).toList()

    @ApiOperation(nickname = "fuzzySearch", value = "Filter patients for the current user (HcParty) ", notes = "Returns a list of patients")
    @GetMapping("/fuzzy")
    fun fuzzySearch(
            @ApiParam(value = "The first name") @RequestParam(required = false) firstName: String,
            @ApiParam(value = "The last name") @RequestParam(required = false) lastName: String,
            @ApiParam(value = "The date of birth") @RequestParam(required = false) dateOfBirth: Int?) =
            try {
                patientLogic.fuzzySearchPatients(sessionLogic.currentHealthcarePartyId, firstName, lastName, dateOfBirth)
                        ?.let { it.map { p -> mapper.map(p, PatientDto::class.java) } }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fuzzy search failed.")
            } catch (e: Exception) {
                log.warn(e.message, e)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
            }

    @ApiOperation(nickname = "createPatient", value = "Create a patient", notes = "Name, last name, date of birth, and gender are required. After creation of the patient and obtaining the ID, you need to create an initial delegation.")
    @PostMapping
    fun createPatient(@RequestBody p: PatientDto): PatientDto {
        val patient: Patient?
        try {
            patient = patientLogic.createPatient(mapper.map(p, Patient::class.java))
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

        return patient?.let { mapper.map(it, PatientDto::class.java) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Patient creation failed.")
    }

    @ApiOperation(nickname = "deletePatient", value = "Delete patients.", notes = "Response is an array containing the ID of deleted patient..")
    @DeleteMapping("/{patientIds}")
    fun deletePatient(@PathVariable patientIds: String): List<String> {

        val ids = patientIds.split(',')
        if (ids.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")

        val deletedIds: Set<String>?
        try {
            deletedIds = patientLogic.deletePatients(HashSet(ids))
        } catch (e: DocumentNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

        return deletedIds?.toList()
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Patients deletion failed.")
    }

    @ApiOperation(nickname = "listDeletedPatients", value = "Find deleted patients", notes = "Returns a list of deleted patients, within the specified time period, if any.")
    @GetMapping("/deleted/by_date")
    fun listDeletedPatients(
            @ApiParam(value = "Filter deletions after this date (unix epoch), included") @RequestParam(required = false) startDate: Long?,
            @ApiParam(value = "Filter deletions before this date (unix epoch), included") @RequestParam(required = false) endDate: Long?,
            @ApiParam(value = "Descending") @RequestParam(required = false) desc: Boolean?,
            @ApiParam(value = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestParam(required = false) limit: Int?): PatientPaginatedList {

        val paginationOffset = PaginationOffset<Long>(startDate, startDocumentId, null, limit) // TODO works with descending=true?
        return patientLogic.findDeletedPatientsByDeleteDate(startDate, endDate, desc ?: false, paginationOffset)
                ?.let { buildPaginatedListResponse(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Listing deleted patients failed.")
    }

    @ApiOperation(nickname = "listDeletedPatientsByName", value = "Find deleted patients", notes = "Returns a list of deleted patients, by name and/or firstname prefix, if any.")
    @GetMapping("/deleted/by_name")
    fun listDeletedPatientsByName(
            @ApiParam(value = "First name prefix") @RequestParam(required = false) firstName: String?,
            @ApiParam(value = "Last name prefix") @RequestParam(required = false) lastName: String?) =
            try {
                patientLogic.findDeletedPatientsByNames(firstName, lastName).map { p -> mapper.map(p, PatientDto::class.java) }
            } catch (e: Exception) {
                log.warn(e.message, e)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
            }

    @ApiOperation(nickname = "undeletePatient", value = "undelete previously deleted patients", notes = "Response is an array containing the ID of undeleted patient..")
    @PutMapping("/undelete/{patientIds}")
    fun undeletePatient(@PathVariable patientIds: String): List<String> {
        val ids = patientIds.split(',')
        if (ids.isEmpty()) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")

        val deletedIds: Set<String>?
        try {
            deletedIds = patientLogic.undeletePatients(HashSet(ids))
        } catch (e: DocumentNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

        return deletedIds?.toList()
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Patients undeleted failed.")
    }


    @ApiOperation(nickname = "newDelegations", value = "Delegates a patients to a healthcare party", notes = "It delegates a patient to a healthcare party (By current healthcare party). A modified patient with new delegation gets returned.")
    @PostMapping("/{patientId}/delegate")
    fun newDelegations(@PathVariable patientId: String,
                       @RequestBody ds: List<DelegationDto>): PatientDto {
        return try {
            patientLogic.addDelegations(patientId, ds.map { d -> mapper.map(d, Delegation::class.java) })
            val patientWithDelegations = patientLogic.getPatient(patientId)

            patientWithDelegations?.takeIf { it.delegations.isNotEmpty() }?.let { mapper.map(it, PatientDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred in creation of the delegation.")
        } catch (e: Exception) {
            log.error(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @ApiOperation(nickname = "getPatients", value = "Get patients by id", notes = "It gets patient administrative data.")
    @PostMapping("/byIds")
    fun getPatients(@RequestBody patientIds: ListOfIdsDto): List<PatientDto> {

        return patientLogic.getPatients(patientIds.ids)?.let { it.map { p -> mapper.map(p, PatientDto::class.java) }.toList() }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting patients failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.")
    }

    @ApiOperation(nickname = "getPatient", value = "Get patient", notes = "It gets patient administrative data.")
    @GetMapping("/{patientId}")
    fun getPatient(@PathVariable patientId: String) =
            patientLogic.getPatient(patientId)?.let { mapper.map(it, PatientDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.")

    @ApiOperation(nickname = "bulkUpdatePatients", value = "Modify a patient", notes = "Returns the id and _rev of created patients")
    @PostMapping("/bulk")
    fun bulkUpdatePatients(patientDtos: List<PatientDto>) = try {
        val patients = patientLogic.updateEntities(patientDtos.map { p -> mapper.map(p, Patient::class.java) }.toList())
        patients.map { p -> mapper.map(p, IdWithRevDto::class.java) }.toList()
    } catch (e: Exception) {
        log.warn(e.message, e)
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }

    @ApiOperation(nickname = "modifyPatient", value = "Modify a patient", notes = "No particular return value. It's just a message.")
    @PutMapping
    fun modifyPatient(patientDto: PatientDto) =
            patientLogic.modifyPatient(mapper.map(patientDto, Patient::class.java)).let { mapper.map(it, PatientDto::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.")

    @ApiOperation(nickname = "modifyPatientReferral", value = "Set a patient referral doctor")
    @PutMapping("/{patientId}/referral/{referralId}")
    fun modifyPatientReferral(@PathVariable patientId: String,
                              @ApiParam(value = "The referal id. Accepts 'none' for referral removal.") @PathVariable referralId: String,
                              @ApiParam(value = "Optional value for start of referral") @RequestParam(required = false) start: Long?,
                              @ApiParam(value = "Optional value for end of referral") @RequestParam(required = false) end: Long?) {
        patientLogic.getPatient(patientId).let {
            mapper.map(patientLogic.modifyPatientReferral(it, if (referralId == "none") null else referralId, if (start == null) null else Instant.ofEpochMilli(start), if (end == null) null else Instant.ofEpochMilli(end)), PatientDto::class.java)
        }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find patient with ID $patientId in the database")
                        .also { log.error(it.message) }
    }

    @ApiOperation(nickname = "mergeInto", value = "Merge a series of patients into another patient")
    @PutMapping("/mergeInto/{toId}/from/{fromIds}")
    fun mergeInto(@PathVariable("toId") patientId: String?, @PathVariable fromIds: String?) =
            with(patientLogic.getPatient(patientId)) {
                fromIds?.split(',')?.map { patientLogic.getPatient(it) }.also { patientLogic.mergePatient(this, it) }.let { mapper.map(it, PatientDto::class.java) }
                        ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find patient with ID $patientId in the database")
                                .also { log.error(it.message) }

            }

    companion object {
        private val log = LoggerFactory.getLogger(javaClass)
    }

}
