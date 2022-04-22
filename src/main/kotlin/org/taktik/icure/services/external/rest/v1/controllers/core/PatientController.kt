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

package org.taktik.icure.services.external.rest.v1.controllers.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Splitter
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import javax.security.auth.login.LoginException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.Sorting
import org.taktik.icure.entities.Patient
import org.taktik.icure.services.external.rest.v1.dto.IdWithRevDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v1.mapper.PatientMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PatientHealthCarePartyMapper
import org.taktik.icure.services.external.rest.v1.mapper.filter.FilterChainMapper
import org.taktik.icure.services.external.rest.v1.utils.paginatedList
import org.taktik.icure.services.external.rest.v1.utils.paginatedListOfIds
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/patient")
@Tag(name = "patient")
class PatientController(
    private val sessionLogic: AsyncSessionLogic,
    private val accessLogLogic: AccessLogLogic,
    private val filters: Filters,
    private val patientLogic: PatientLogic,
    private val healthcarePartyLogic: HealthcarePartyLogic,
    private val patientMapper: PatientMapper,
    private val filterChainMapper: FilterChainMapper,
    private val addressMapper: AddressMapper,
    private val patientHealthCarePartyMapper: PatientHealthCarePartyMapper,
    private val delegationMapper: DelegationMapper,
    private val objectMapper: ObjectMapper
) {
    private val patientToPatientDto = { it: Patient -> patientMapper.map(it) }

    @Operation(summary = "Find patients for the current user (HcParty) ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/byNameBirthSsinAuto")
    fun findByNameBirthSsinAuto(
            @Parameter(description = "HealthcareParty Id. If not set, will use user's hcpId") @RequestParam(required = false) healthcarePartyId: String?,
            @Parameter(description = "Optional value for filtering results") @RequestParam(required = false) filterValue: String?,
            @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @Parameter(description = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.") @RequestParam(required = false, defaultValue = "asc") sortDirection: String
    ) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        val currentHealthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()

        currentHealthcarePartyId.let { currentHcpId ->
            val hcp = healthcarePartyLogic.getHealthcareParty(currentHcpId)
            (hcp?.parentId?.takeIf { it.isNotEmpty() } ?: hcp?.id)?.let { hcpId ->
                patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(
                        hcpId,
                        paginationOffset,
                        filterValue,
                        Sorting(null, sortDirection))
                        .let { it.paginatedList(patientToPatientDto, realLimit) }
            } ?: PaginatedList() }
    }

    @Operation(summary = "List patients of a specific HcParty or of the current HcParty ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/ofHcParty/{hcPartyId}")
    fun listPatientsOfHcParty(@PathVariable hcPartyId: String,
                              @Parameter(description = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String?,
                              @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
                              @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                              @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
                              @Parameter(description = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.") @RequestParam(required = false, defaultValue = "asc") sortDirection: String) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit + 1)
        patientLogic.findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(hcPartyId, paginationOffset, null, Sorting(sortField, sortDirection)).paginatedList(patientToPatientDto, realLimit)
    }

    @Operation(summary = "List patients that have been merged towards another patient ", description = "Returns a list of patients that have been merged after the provided date")
    @GetMapping("/merges/{date}")
    fun listOfMergesAfter(@PathVariable date: Long) =
            patientLogic.listOfMergesAfter(date).map {patientMapper.map(it)}.injectReactorContext()

    @Operation(summary = "List patients that have been modified after the provided date", description = "Returns a list of patients that have been modified after the provided date")
    @GetMapping("/modifiedAfter/{date}")
    fun listOfPatientsModifiedAfter(@PathVariable date: Long,
                                    @Parameter(description = "The start key for pagination the date of the first element of the new page") @RequestParam(required = false) startKey: Long?,
                                    @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                    @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {
        patientLogic.listOfPatientsModifiedAfter(date, startKey, startDocumentId, (limit
                ?: DEFAULT_LIMIT) + 1).paginatedList<Patient, PatientDto>(patientToPatientDto, limit ?: DEFAULT_LIMIT)
    }

    @Operation(summary = "List patients for a specific HcParty or for the current HcParty ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/hcParty/{hcPartyId}")
    fun listPatientsByHcParty(@PathVariable hcPartyId: String,
                              @Parameter(description = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String?,
                              @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
                              @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                              @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
                              @Parameter(description = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.") @RequestParam(required = false) sortDirection: String?) =
            listPatients(hcPartyId, sortField, startKey, startDocumentId, limit, sortDirection ?: "asc")

    @Operation(
        summary = "Get the patient (identified by patientId) hcparty keys. Those keys are AES keys (encrypted) used to share information between HCPs and a patient.",
        description = """This endpoint is used to recover all keys that have already been created and that can be used to share information with this patient. It returns a map with the following structure: ID of the owner of the encrypted AES key -> encrypted AES key. The returned encrypted AES keys will have to be decrypted using the patient's private key.

                {
                    "hcparty 1 delegator ID": "AES hcparty key (encrypted using patient public RSA key)"
                    "hcparty 2 delegator ID": "other AES hcparty key (encrypted using patient public RSA key)"
                }
                """, responses = [
            ApiResponse(responseCode = "200", description = "Successful operation"),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized operation: the provided credentials are invalid",
                content = []
            )
        ]
    )
    @GetMapping("/{patientId}/keys")
    fun getPatientHcPartyKeysForDelegate(@Parameter(description = "The patient Id for which information is shared") @PathVariable patientId: String) = mono {
        patientLogic.getHcPartyKeysForDelegate(patientId)
    }

    @Operation(summary = "Get count of patients for a specific HcParty or for the current HcParty ", description = "Returns the count of patients")
    @GetMapping("/hcParty/{hcPartyId}/count")
    fun countOfPatients(@Parameter(description = "Healthcare party id") @PathVariable hcPartyId: String) = mono {
        ContentDto(numberValue = patientLogic.countByHcParty(hcPartyId).toDouble())
    }

    @Operation(summary = "List patients for a specific HcParty", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping
    fun listPatients(@Parameter(description = "Healthcare party id") @RequestParam(required = false) hcPartyId: String?,
                     @Parameter(description = "Optional value for sorting results by a given field ('name', 'ssin', 'dateOfBirth'). " + "Specifying this deactivates filtering") @RequestParam(required = false) sortField: String?,
                     @Parameter(description = "The start key for pagination: a JSON representation of an array containing all the necessary " + "components to form the Complex Key's startKey") @RequestParam(required = false) startKey: String?,
                     @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                     @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
                     @Parameter(description = "Optional value for providing a sorting direction ('asc', 'desc'). Set to 'asc' by default.") @RequestParam(required = false, defaultValue = "asc") sortDirection: String) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let {  objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)
        sessionLogic.getCurrentHealthcarePartyId().let { currentHcpId ->
            val hcp = healthcarePartyLogic.getHealthcareParty(currentHcpId)
            (hcp?.parentId?.takeIf { it.isNotEmpty() } ?: hcp?.id)?.let { hcpId ->
                patientLogic.findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(
                        hcpId,
                        paginationOffset,
                        null,
                        Sorting(sortField, sortDirection)).paginatedList<Patient, PatientDto>(patientToPatientDto, realLimit)
            } ?: PaginatedList()
        }
    }

    @Operation(summary = "List patients by pages for a specific HcParty", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is " + "Null it means that this is the last page.")
    @GetMapping("/idsPages")
    fun listPatientsIds(@Parameter(description = "Healthcare party id") @RequestParam hcPartyId: String,
                        @Parameter(description = "The page first id") @RequestParam(required = false) startKey: String?,
                        @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                        @Parameter(description = "Page size") @RequestParam(required = false) limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)
        patientLogic.findByHcPartyIdsOnly(hcPartyId, paginationOffset).paginatedListOfIds(realLimit)
    }

    @Operation(summary = "Get the patient having the provided externalId")
    @GetMapping("/byExternalId/{externalId}")
    fun findByExternalId(@PathVariable("externalId")
                         @Parameter(description = "A external ID", required = true) externalId: String) = mono {
        patientLogic.getByExternalId(externalId)?.let(patientToPatientDto)
    }



    @Operation(summary = "Get Paginated List of Patients sorted by Access logs descending")
    @GetMapping("/byAccess/{userId}")
    fun findByAccessLogUserAfterDate(@Parameter(description = "A User ID", required = true) @PathVariable userId: String,
                                     @Parameter(description = "The type of access (COMPUTER or USER)") @RequestParam(required = false) accessType: String?,
                                     @Parameter(description = "The start search epoch") @RequestParam(required = false) startDate: Long?,
                                     @Parameter(description = "The start key for pagination") @RequestParam(required = false) startKey: String?,
                                     @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
                                     @Parameter(description = "Number of rows") @RequestParam(defaultValue = DEFAULT_LIMIT.toString()) limit: Int): Mono<PaginatedList<PatientDto>> = mono {
        accessLogLogic.aggregatePatientByAccessLogs(userId, accessType, startDate, startKey, startDocumentId, limit).let { (totalSize, count, patients, dateNextKey, nextDocumentId) ->
            val patients = patients.map { patient ->
                    PatientDto(
                        id = patient.id,
                        lastName = patient.lastName,
                        firstName = patient.firstName,
                        partnerName = patient.partnerName,
                        maidenName = patient.maidenName,
                        dateOfBirth = patient.dateOfBirth,
                        ssin = patient.ssin,
                        externalId = patient.externalId,
                        patientHealthCareParties = patient.patientHealthCareParties.map { phcp ->
                            patientHealthCarePartyMapper.map(
                                phcp
                            )
                        },
                        addresses = patient.addresses.map { addressMapper.map(it) }
                    )
                }

            PaginatedList(
                nextKeyPair = dateNextKey?.let {
                    PaginatedDocumentKeyIdPair(
                        it,
                        nextDocumentId
                    )
                },
                pageSize = limit,
                totalSize = (totalSize * (patients.size / count.toDouble())).toInt(),
                rows = patients.toList()
            )
        }
    }

    @Operation(summary = "Filter patients for the current user (HcParty) ", description = "Returns a list of patients along with next start keys and Document ID. If the nextStartKey is Null it means that this is the last page.")
    @PostMapping("/filter")
    fun filterPatientsBy(
            @Parameter(description = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?,
            @Parameter(description = "Skip rows") @RequestParam(required = false) skip: Int?,
            @Parameter(description = "Sort key") @RequestParam(required = false) sort: String?,
            @Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?,
            @RequestBody filterChain: FilterChain<Patient>) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyList = startKey?.takeIf { it.isNotEmpty() }?.let { ArrayList(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(it)) }
        val paginationOffset = PaginationOffset<List<String>>(startKeyList, startDocumentId, skip, realLimit + 1)

        try {
            val patients = patientLogic.listPatients(paginationOffset, filterChainMapper.map(filterChain), sort, desc)
            log.info("Filter patients in " + (System.currentTimeMillis() - System.currentTimeMillis()) + " ms.")

            patients.paginatedList<Patient, PatientDto>(patientToPatientDto, realLimit)
        } catch (e: LoginException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Get ids of patients matching the provided filter for the current user (HcParty) ")
    @PostMapping("/match")
    fun matchPatientsBy(@RequestBody filter: AbstractFilterDto<Patient>) = mono {
        filters.resolve(filter).toList()
    }

    @Operation(summary = "Filter patients for the current user (HcParty) ", description = "Returns a list of patients")
    @GetMapping("/fuzzy")
    fun fuzzySearch(
            @Parameter(description = "The first name") @RequestParam(required = false) firstName: String,
            @Parameter(description = "The last name") @RequestParam(required = false) lastName: String,
            @Parameter(description = "The date of birth") @RequestParam(required = false) dateOfBirth: Int?): Flux<PatientDto> {

        return try {
            patientLogic.fuzzySearchPatients(firstName, lastName, dateOfBirth)
                    .map { patientMapper.map(it) }
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
            patientLogic.createPatient(patientMapper.map(p))
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        patient?.let(patientToPatientDto) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Patient creation failed.")
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
            @Parameter(description = "Filter deletions after this date (unix epoch), included") @RequestParam(required = false) startDate: Long,
            @Parameter(description = "Filter deletions before this date (unix epoch), included") @RequestParam(required = false) endDate: Long?,
            @Parameter(description = "Descending") @RequestParam(required = false) desc: Boolean?,
            @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?) = mono {

        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startDate, startDocumentId, null, realLimit + 1) // TODO works with descending=true?
        patientLogic.findDeletedPatientsByDeleteDate(startDate, endDate, desc ?: false, paginationOffset).paginatedList<Patient, PatientDto>(patientToPatientDto, realLimit)
    }

    @Operation(summary = "Find deleted patients", description = "Returns a list of deleted patients, by name and/or firstname prefix, if any.")
    @GetMapping("/deleted/by_name")
    fun listDeletedPatientsByName(
            @Parameter(description = "First name prefix") @RequestParam(required = false) firstName: String?,
            @Parameter(description = "Last name prefix") @RequestParam(required = false) lastName: String?) =
            try {
                patientLogic.listDeletedPatientsByNames(firstName, lastName).map { patientMapper.map(it) }.injectReactorContext()
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
    fun newPatientDelegations(@PathVariable patientId: String,
                              @RequestBody ds: List<DelegationDto>) = mono {
        try {
            patientLogic.addDelegations(patientId, ds.map { d -> delegationMapper.map(d) })
            val patientWithDelegations = patientLogic.getPatient(patientId)

            patientWithDelegations?.takeIf { it.delegations.isNotEmpty() }?.let(patientToPatientDto)
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
                .map { patientMapper.map(it) }
                .injectReactorContext()
    }

    @Operation(summary = "Get patient", description = "It gets patient administrative data.")
    @GetMapping("/{patientId}")
    fun getPatient(@PathVariable patientId: String) = mono {
        patientLogic.getPatient(patientId)?.let(patientToPatientDto)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.")
    }

    @Operation(summary = "Get patient by identifier", description = "It gets patient administrative data based on the identifier (root & extension) parameters.")
    @GetMapping("/{hcPartyId}/{id}")
    fun getPatientByHealthcarepartyAndIdentifier(@PathVariable hcPartyId: String, @PathVariable id: String, @RequestParam(required = false) system: String?) = mono {
        when {
            !system.isNullOrEmpty() -> {
                patientLogic.findByHealthcarepartyAndIdentifier(hcPartyId, system, id)
                        .map { patientMapper.map(it) }
                        .firstOrNull() ?: patientLogic.getPatient(id)?.let { patientMapper.map(it) }
            }
            else -> patientLogic.getPatient(id)?.let { patientMapper.map(it) }
        }
    }

    @Operation(summary = "Create patients in bulk", description = "Returns the id and _rev of created patients")
    @PostMapping("/bulk", "/batch")
    fun bulkCreatePatients(@RequestBody patientDtos: List<PatientDto>) = mono {
        try {
            val patients = patientLogic.createPatients(patientDtos.map { p -> patientMapper.map(p) }.toList())
            patients.map { p -> IdWithRevDto(id = p.id, rev = p.rev) }.toList()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "Modify patients in bulk", description = "Returns the id and _rev of modified patients")
    @PutMapping("/bulk", "/batch")
    fun bulkUpdatePatients(@RequestBody patientDtos: List<PatientDto>) = mono {
        try {
            val patients = patientLogic.modifyPatients(patientDtos.map { p -> patientMapper.map(p) }.toList())
            patients.map { p -> IdWithRevDto(id = p.id, rev = p.rev) }.toList()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "Modify a patient", description = "No particular return value. It's just a message.")
    @PutMapping
    fun modifyPatient(@RequestBody patientDto: PatientDto) = mono {
        patientLogic.modifyPatient(patientMapper.map(patientDto))?.let(patientToPatientDto)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting patient failed. Possible reasons: no such patient exists, or server error. Please try again or read the server log.").also { log.error(it.message) }
    }

    @Operation(summary = "Set a patient referral doctor")
    @PutMapping("/{patientId}/referral/{referralId}")
    fun modifyPatientReferral(@PathVariable patientId: String,
                              @Parameter(description = "The referal id. Accepts 'none' for referral removal.") @PathVariable referralId: String,
                              @Parameter(description = "Optional value for start of referral") @RequestParam(required = false) start: Long?,
                              @Parameter(description = "Optional value for end of referral") @RequestParam(required = false) end: Long?) = mono {
        patientLogic.getPatient(patientId)
                ?.let {
                    patientLogic.modifyPatientReferral(it, if (referralId == "none") null else referralId, if (start == null) null else Instant.ofEpochMilli(start), if (end == null) null else Instant.ofEpochMilli(end))?.let(patientToPatientDto)
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
            mergedPatient?.let(patientToPatientDto)
        } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find patient with ID $patientId in the database").also { log.error(it.message) }
    }

    // TODO MB add missing methods like findDuplicatesBySsin or findDuplicatesByName  (compare this controller with the master branch)


    @Operation(summary = "Provides a paginated list of patients with duplicate ssin for an hecparty")
    @PostMapping("/duplicates/ssin")
    fun findDuplicatesBySsin(
            @Parameter(description = "Healthcare party id") @RequestParam hcPartyId: String,
            @Parameter(description = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?
    ) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        patientLogic.getDuplicatePatientsBySsin(hcPartyId, paginationOffset).paginatedList(patientToPatientDto, realLimit)
    }

    @Operation(summary = "Provides a paginated list of patients with duplicate name for an hecparty")
    @PostMapping("/duplicates/name")
    fun findDuplicatesByName(
            @Parameter(description = "Healthcare party id") @RequestParam hcPartyId: String,
            @Parameter(description = "The start key for pagination, depends on the filters used") @RequestParam(required = false) startKey: String?,
            @Parameter(description = "A patient document ID") @RequestParam(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestParam(required = false) limit: Int?
    ) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val startKeyElements = startKey?.let { objectMapper.readValue<List<String>>(startKey, objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)) }
        val paginationOffset = PaginationOffset(startKeyElements, startDocumentId, null, realLimit+1)

        patientLogic.getDuplicatePatientsByName(hcPartyId, paginationOffset).paginatedList(patientToPatientDto, realLimit)
    }

    companion object {
        private val log = LoggerFactory.getLogger(javaClass)
        private const val DEFAULT_LIMIT = 1000
    }
}
