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
package org.taktik.icure.asynclogic.impl


import kotlin.math.min
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.entity.Option
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.Sorting
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.toComplexKeyPaginationOffset
import java.time.Instant


@FlowPreview
@ExperimentalCoroutinesApi
@Service
class PatientLogicImpl(
        private val sessionLogic: AsyncSessionLogic,
        private val patientDAO: PatientDAO,
        private val userLogic: UserLogic,
        private val filters: Filters) : GenericLogicImpl<Patient, PatientDAO>(sessionLogic), PatientLogic {

    override suspend fun countByHcParty(healthcarePartyId: String): Int {
        return patientDAO.countByHcParty(healthcarePartyId)
    }

    override suspend fun countOfHcParty(healthcarePartyId: String): Int {
        return patientDAO.countOfHcParty(healthcarePartyId)
    }

    override fun listByHcPartyIdsOnly(healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcParty(healthcarePartyId))
    }

    override fun listByHcPartyAndSsinIdsOnly(ssin: String, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcPartyAndSsin(ssin, healthcarePartyId))
    }

    override fun listByHcPartyAndSsinsIdsOnly(ssins: Collection<String>, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcPartyAndSsins(ssins, healthcarePartyId))
    }

    override fun listByHcPartyDateOfBirthIdsOnly(date: Int, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcPartyAndDateOfBirth(date, healthcarePartyId))
    }

    override fun listByHcPartyGenderEducationProfessionIdsOnly(healthcarePartyId: String, gender: Gender?, education: String?, profession: String?) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcPartyGenderEducationProfession(healthcarePartyId, gender, education, profession))
    }

    override fun listByHcPartyDateOfBirthIdsOnly(startDate: Int?, endDate: Int?, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcPartyAndDateOfBirth(startDate, endDate, healthcarePartyId))
    }

    override fun listByHcPartyNameContainsFuzzyIdsOnly(searchString: String?, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcPartyAndNameContainsFuzzy(searchString, healthcarePartyId, null))
    }

    override fun listByHcPartyName(searchString: String?, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientsByHcPartyName(searchString, healthcarePartyId))
    }

    override fun listByHcPartyAndExternalIdsOnly(externalId: String?, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByHcPartyAndExternalId(externalId, healthcarePartyId))
    }

    override fun listByHcPartyAndActiveIdsOnly(active: Boolean, healthcarePartyId: String) = flow<String> {
        emitAll(patientDAO.listPatientIdsByActive(active, healthcarePartyId))
    }

    override fun listOfMergesAfter(date: Long?) = flow<Patient> {
        emitAll(patientDAO.listOfMergesAfter(date))
    }

    override fun findByHcPartyIdsOnly(healthcarePartyId: String, offset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.findPatientIdsByHcParty(healthcarePartyId, offset.toComplexKeyPaginationOffset()))
    }

    override fun findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId: String, offset: PaginationOffset<List<String>>, searchString: String?, sorting: Sorting) = flow<ViewQueryResultEvent> {
        val descending = "desc" == sorting.direction
        if (searchString == null || searchString.isEmpty()) {
            emitAll(
                    when (sorting.field) {
                        "ssin" -> {
                            patientDAO.findPatientsByHcPartyAndSsin(null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        "dateOfBirth" -> {
                            patientDAO.findPatientsByHcPartyDateOfBirth(null, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        else -> {
                            patientDAO.findPatientsByHcPartyAndName(null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                    }
            )
        } else {
            emitAll(when {
                FuzzyValues.isSsin(searchString) -> {
                    patientDAO.findPatientsByHcPartyAndSsin(searchString, healthcarePartyId, offset.toComplexKeyPaginationOffset(), false)
                }
                FuzzyValues.isDate(searchString) -> {
                    patientDAO.findPatientsByHcPartyDateOfBirth(FuzzyValues.toYYYYMMDD(searchString), FuzzyValues.getMaxRangeOf(searchString), healthcarePartyId, offset.toComplexKeyPaginationOffset(), false)
                }
                else -> {
                    findByHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending)
                }
            }
            )
        }
    }

    override fun listPatients(paginationOffset: PaginationOffset<*>?, filterChain: FilterChain<Patient>, sort: String?, desc: Boolean?) = flow<ViewQueryResultEvent> {
        var ids = filters.resolve(filterChain.filter).toSet().sorted()
        var forPagination = patientDAO.findPatients(ids)
        if (filterChain.predicate != null) {
            forPagination = forPagination.filterIsInstance<ViewRowWithDoc<*, *, *>>()
                    .filter { filterChain.predicate.apply(it.doc as Patient) }
        }
        if (sort != null && sort != "id") { // TODO MB is this the correct way to sort here ?
            var patientsListToSort = forPagination.toList()
            val pub = PropertyUtilsBean()
            patientsListToSort = patientsListToSort.sortedWith(
                    kotlin.Comparator { a, b ->
                        try {
                            val ap = pub.getProperty(a, sort) as Comparable<*>?
                            val bp = pub.getProperty(b, sort) as Comparable<*>?
                            if (ap is String && bp is String) {
                                if (desc != null && desc) {
                                    StringUtils.compareIgnoreCase(bp, ap)
                                } else {
                                    StringUtils.compareIgnoreCase(ap, bp)
                                }
                            } else {
                                ap as Comparable<Any>?
                                bp as Comparable<Any>?
                                if (desc != null && desc) {
                                    ap?.let { bp?.compareTo(it) ?: 1 } ?: bp?.let { -1 } ?: 0
                                } else {
                                    bp?.let { ap?.compareTo(it) ?: 1 } ?: bp?.let { -1 } ?: 0
                                }
                            }
                        } catch (e: Exception) {
                            0
                        }
                    }
            )
            emitAll(patientsListToSort.asFlow())
        } else {
            emitAll(forPagination)
        }
    }

    override fun findByHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String, offset: PaginationOffset<*>, descending: Boolean) = flow<ViewQueryResultEvent> {
        //TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
        //We will get partial results but at least we will not overload the servers
        val limit = if (offset.startKey == null) min(1000, offset.limit * 10) else null
        val ids = patientDAO.listPatientIdsByHcPartyAndNameContainsFuzzy(searchString, healthcarePartyId, limit)
        emitAll(
                patientDAO.findPatients(ids)
        )
    }

    override fun findOfHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String, offset: PaginationOffset<*>, descending: Boolean) = flow<ViewQueryResultEvent> {
        //TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
        //We will get partial results but at least we will not overload the servers
        val limit = if (offset.startKey == null) min(1000, offset.limit * 10) else null
        val ids = patientDAO.listPatientIdsOfHcPartyNameContainsFuzzy(searchString, healthcarePartyId, limit)
        emitAll(
                patientDAO.findPatients(ids)
        )
    }

    override fun findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId: String, offset: PaginationOffset<List<String>>, searchString: String?, sorting: Sorting) = flow<ViewQueryResultEvent> {
        val descending = "desc" == sorting.direction
        emitAll(
                if (searchString == null || searchString.isEmpty()) {
                    when (sorting.field) {
                        "ssin" -> {
                            patientDAO.findPatientsOfHcPartyAndSsin(null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        "dateOfBirth" -> {
                            patientDAO.findPatientsOfHcPartyDateOfBirth(null, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        else -> {
                            patientDAO.findPatientsOfHcPartyAndName(null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                    }
                } else {
                    when {
                        FuzzyValues.isSsin(searchString) -> {
                            patientDAO.findPatientsOfHcPartyAndSsin(searchString, healthcarePartyId, offset.toComplexKeyPaginationOffset(), false)
                        }
                        FuzzyValues.isDate(searchString) -> {
                            patientDAO.findPatientsOfHcPartyDateOfBirth(FuzzyValues.toYYYYMMDD(searchString),
                                    FuzzyValues.getMaxRangeOf(searchString), healthcarePartyId, offset.toComplexKeyPaginationOffset(), false)
                        }
                        else -> {
                            findOfHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending)
                        }
                    }
                }
        )
    }

    override fun findByHcPartyAndSsin(ssin: String?, healthcarePartyId: String, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.findPatientsByHcPartyAndSsin(ssin!!, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), false))
    }

    override fun findByHcPartyDateOfBirth(date: Int?, healthcarePartyId: String, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.findPatientsByHcPartyDateOfBirth(date, date, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), false))
    }

    override fun findByHcPartyModificationDate(start: Long?, end: Long?, healthcarePartyId: String, descending: Boolean, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.findPatientsByHcPartyModificationDate(start, end, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), descending))
    }

    override fun findOfHcPartyModificationDate(start: Long?, end: Long?, healthcarePartyId: String, descending: Boolean, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.findPatientsOfHcPartyModificationDate(start, end, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), descending))
    }

    override suspend fun findByUserId(id: String): Patient? {
        return patientDAO.findPatientsByUserId(id)
    }

    override suspend fun getPatient(patientId: String): Patient? {
        return patientDAO.get(patientId)
    }

    override fun findByHealthcarepartyAndIdentifier(healthcarePartyId: String, system: String, id: String) = patientDAO.listPatientsByHcPartyAndIdentifier(healthcarePartyId, system, id)

    override fun getPatientSummary(patientDto: PatientDto?, propertyExpressions: List<String?>?): Map<String, Any>? { //		return patientDtoBeans.getAsMapOfValues(patientDto, propertyExpressions);
        return null
    }

    override fun getPatients(patientIds: List<String>) = flow<Patient> {
        emitAll(patientDAO.getPatients(patientIds))
    }

    override suspend fun addDelegation(patientId: String, delegation: Delegation): Patient? {
        val patient = getPatient(patientId)
        return delegation.delegatedTo?.let { healthcarePartyId ->
            patient?.let { c ->
                patientDAO.save(c.copy(delegations = c.delegations + mapOf(
                        healthcarePartyId to setOf(delegation)
                )))
            }
        } ?: patient
    }

    override suspend fun addDelegations(patientId: String, delegations: Collection<Delegation>): Patient? {
        val patient = getPatient(patientId)
        return patient?.let {
            return patientDAO.save(it.copy(
                    delegations = it.delegations +
                            delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
            ))
        }
    }

    @Throws(MissingRequirementsException::class)
    override suspend fun createPatient(patient: Patient) = fix(patient) { patient ->
        checkRequirements(patient)
        (if (patient.preferredUserId != null && (patient.delegations.isEmpty())) {
            userLogic.getUser(patient.preferredUserId)?.let { user ->
                patient.copy(
                        delegations = (user.autoDelegations.values.flatMap { it.map { it to setOf<Delegation>() } }).toMap() +
                                (user.healthcarePartyId?.let { mapOf(it to setOf<Delegation>()) } ?: mapOf<String, Set<Delegation>>())
                )
            } ?: patient
        } else patient).let {
            createEntities(setOf(it)).firstOrNull()?.let { createdPatient ->
                createdPatient
            }
        }
    }

    @Throws(MissingRequirementsException::class)
    override suspend fun modifyPatient(patient: Patient): Patient? = fix(patient) { patient ->
        log.debug("Modifying patient with id:" + patient.id)
        // checking requirements
        checkRequirements(patient)
        try {
            modifyEntities(setOf(patient)).firstOrNull()
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid patient", e)
        }
    }

    override fun createEntities(entities: Collection<Patient>): Flow<Patient> {
        entities.forEach { checkRequirements(it) }
        return super.createEntities(entities)
    }

    override suspend fun modifyEntities(entities: Collection<Patient>): Flow<Patient> {
        entities.forEach { checkRequirements(it) }
        return super.modifyEntities(entities.map { fix(it) })
    }

    private fun checkRequirements(patient: Patient) {
        if ((patient.firstName == null && patient.lastName == null) && patient.encryptedSelf == null && patient.deletionDate == null) {
            throw MissingRequirementsException("modifyPatient: Name, Last name  are required.")
        }
    }


    override suspend fun modifyPatientReferral(patient: Patient, referralId: String?, start: Instant?, end: Instant?): Patient? {
        val startOrNow = start ?: Instant.now()
        val shouldSave = booleanArrayOf(false)
        //Close referrals relative to other healthcare parties
        val fixedPhcp = patient.patientHealthCareParties.map { phcp ->
            if (phcp.referral && (referralId == null || referralId != phcp.healthcarePartyId)) {
                phcp.copy(
                        referral = false,
                        referralPeriods = phcp.referralPeriods.map { p ->
                            if (p.endDate == null || p.endDate != startOrNow) {
                                p.copy(endDate = startOrNow)
                            } else p
                        }.toSortedSet()
                )
            } else if (referralId != null && referralId == phcp.healthcarePartyId) {
                (if (!phcp.referral) {
                    phcp.copy(referral = true)
                } else phcp).copy(
                        referralPeriods = phcp.referralPeriods.map {rp ->
                            if (start == rp.startDate) {
                                rp.copy(endDate = end)
                            } else rp
                        }.toSortedSet()
                )
            } else phcp
        }
        return (if (!fixedPhcp.any { it.referral && it.healthcarePartyId == referralId }) {
            fixedPhcp + PatientHealthCareParty(
                    referral = true,
                    healthcarePartyId = referralId,
                    referralPeriods = sortedSetOf(ReferralPeriod(startOrNow, end))
            )
        } else fixedPhcp).let {
            if (it != patient.patientHealthCareParties) {
                modifyPatient(patient.copy(patientHealthCareParties = it))
            } else
                patient
        }
    }

    override suspend fun mergePatient(patient: Patient, fromPatients: List<Patient>): Patient? {
        val now = Instant.now().toEpochMilli()
        return fromPatients.fold(patient to listOf<Patient>() ) { (p, others), o -> p.merge(o) to others + (modifyPatient(p.copy(
                mergeToPatientId = patient.id,
                deletionDate = now
        )) ?: error("Cannot modify patient ${p.id}")) }.let { (p, others) ->
            modifyPatient(p.copy(mergedIds = p.mergedIds + others.map { it.id }))
        }
    }

    override suspend fun getByExternalId(externalId: String): Patient? {
        return patientDAO.getPatientByExternalId(externalId)
    }

    override fun solveConflicts(): Flow<Patient> =
            patientDAO.listConflicts().mapNotNull { patientDAO.get(it.id, Option.CONFLICTS)?.let { patient ->
                patient.conflicts?.mapNotNull { conflictingRevision -> patientDAO.get(patient.id, conflictingRevision) }
                        ?.fold(patient) { kept, conflict -> kept.merge(conflict).also { patientDAO.purge(conflict) } }
                        ?.let { mergedPatient -> patientDAO.save(mergedPatient) }
            } }

    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
        return patientDAO.getHcPartyKeysForDelegate(healthcarePartyId)
    }

    override fun listOfPatientsModifiedAfter(date: Long, startKey: Long?, startDocumentId: String?, limit: Int?) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.findPatientsModifiedAfter(date, PaginationOffset(startKey, startDocumentId, 0, limit
                ?: 1000)))
    }

    override fun getDuplicatePatientsBySsin(healthcarePartyId: String, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.getDuplicatePatientsBySsin(healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset()))
    }

    override fun getDuplicatePatientsByName(healthcarePartyId: String, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.getDuplicatePatientsByName(healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset()))
    }

    override fun fuzzySearchPatients(firstName: String?, lastName: String?, dateOfBirth: Int?, healthcarePartyId: String?) = flow<Patient> {
        val healthcarePartyId = healthcarePartyId ?: sessionLogic.getCurrentHealthcarePartyId()
        if (dateOfBirth != null) { //Patients with the right date of birth
            val combined: Flow<Flow<ViewQueryResultEvent>>
            val patients = findByHcPartyDateOfBirth(dateOfBirth, healthcarePartyId, PaginationOffset(1000))

            //Patients for which the date of birth is unknown
            combined = if (firstName != null && lastName != null) {
                val patientsNoBirthDate = findByHcPartyDateOfBirth(null, healthcarePartyId, PaginationOffset(1000))
                flowOf(patients, patientsNoBirthDate)
            } else {
                flowOf(patients)
            }
            emitAll(
                    combined.flattenConcat()
                            .filterIsInstance<ViewRowWithDoc<*, *, *>>()
                            .map { it.doc as Patient } //TODO MB below remove explicit toString() when Patient is kotlinized
                            .filter { p: Patient -> firstName == null || p.firstName == null || p.firstName.toString().toLowerCase().startsWith(firstName.toLowerCase()) || firstName.toLowerCase().startsWith(p.firstName.toString().toLowerCase()) || StringUtils.getLevenshteinDistance(firstName.toLowerCase(), p.firstName.toString().toLowerCase()) <= 2 }
                            .filter { p: Patient -> lastName == null || p.lastName == null || StringUtils.getLevenshteinDistance(lastName.toLowerCase(), p.lastName.toString().toLowerCase()) <= 2 }
                            .filter { p: Patient -> p.firstName != null && p.firstName.toString().length >= 3 || p.lastName != null && p.lastName.toString().length >= 3 }

            )
        } else if (lastName != null) {
            emitAll(
                    findByHcPartyNameContainsFuzzy(lastName.substring(0, Math.min(Math.max(lastName.length - 2, 6), lastName.length)), healthcarePartyId, PaginationOffset<Any?>(1000), false)
                            .filterIsInstance<ViewRowWithDoc<*, *, *>>()
                            .map { it.doc as Patient } //TODO MB below remove explicit toString() when Patient is kotlinized
                            .filter { p: Patient -> firstName == null || p.firstName == null || p.firstName.toString().toLowerCase().startsWith(firstName.toLowerCase()) || firstName.toLowerCase().startsWith(p.firstName.toString().toLowerCase()) || StringUtils.getLevenshteinDistance(firstName.toLowerCase(), p.firstName.toString().toLowerCase()) <= 2 }
                            .filter { p: Patient -> p.lastName == null || StringUtils.getLevenshteinDistance(lastName.toLowerCase(), p.lastName.toString().toLowerCase()) <= 2 }
                            .filter { p: Patient -> p.firstName != null && p.firstName.toString().length >= 3 || p.lastName != null && p.lastName.toString().length >= 3 }
            )
        }
    }

    override fun deletePatients(ids: Set<String>) = flow<DocIdentifier> {
        emitAll(deleteEntities(ids))
    }

    override fun findDeletedPatientsByDeleteDate(start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>) = flow<ViewQueryResultEvent> {
        emitAll(patientDAO.findDeletedPatientsByDeleteDate(start, end, descending, paginationOffset))
    }

    override fun listDeletedPatientsByNames(firstName: String?, lastName: String?) = flow<Patient> {
        emitAll(patientDAO.findDeletedPatientsByNames(firstName, lastName))
    }

    override fun undeletePatients(ids: Set<String>) = flow<DocIdentifier> {
        emitAll(undeleteByIds(ids))
    }

    override fun listPatientIdsByHcpartyAndIdentifiers(healthcarePartyId: String, identifiers: List<Identifier>): Flow<String> = flow {
        emitAll(patientDAO.listPatientIdsByHcPartyAndIdentifiers(healthcarePartyId, identifiers))
    }

    companion object {
        private val log = LoggerFactory.getLogger(PatientLogicImpl::class.java)
    }

    override fun getGenericDAO(): PatientDAO {
        return patientDAO
    }
}

