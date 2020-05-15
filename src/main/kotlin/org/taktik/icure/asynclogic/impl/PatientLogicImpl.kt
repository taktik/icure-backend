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
package org.taktik.icure.asynclogic.impl

import com.thoughtworks.xstream.XStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ma.glasnost.orika.MapperFacade
import org.apache.commons.beanutils.PropertyUtilsBean
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dao.Option
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.Sorting
import org.taktik.icure.dto.filter.chain.FilterChain
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.bufferedChunks
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.utils.toComplexKeyPaginationOffset
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.util.function.Consumer
import kotlin.math.min


@FlowPreview
@ExperimentalCoroutinesApi
@Service
class PatientLogicImpl(
        private val sessionLogic: AsyncSessionLogic,
        private val patientDAO: PatientDAO,
        private val userLogic: UserLogic,
        private val filters: Filters) : GenericLogicImpl<Patient, PatientDAO>(sessionLogic), PatientLogic {

    override suspend fun countByHcParty(healthcarePartyId: String): Int {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.countByHcParty(dbInstanceUri, groupId, healthcarePartyId)
    }

    override suspend fun countOfHcParty(healthcarePartyId: String): Int {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.countOfHcParty(dbInstanceUri, groupId, healthcarePartyId)
    }

    override fun listByHcPartyIdsOnly(healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcParty(dbInstanceUri, groupId, healthcarePartyId))
    }

    override fun listByHcPartyAndSsinIdsOnly(ssin: String, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndSsin(dbInstanceUri, groupId, ssin, healthcarePartyId))
    }

    override fun listByHcPartyAndSsinsIdsOnly(ssins: Collection<String>, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndSsins(dbInstanceUri, groupId, ssins, healthcarePartyId))
    }

    override fun listByHcPartyDateOfBirthIdsOnly(date: Int, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndDateOfBirth(dbInstanceUri, groupId, date, healthcarePartyId))
    }

    override fun listByHcPartyGenderEducationProfessionIdsOnly(healthcarePartyId: String, gender: Gender?, education: String?, profession: String?) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyGenderEducationProfession(dbInstanceUri, groupId, healthcarePartyId, gender, education, profession))
    }

    override fun listByHcPartyDateOfBirthIdsOnly(startDate: Int?, endDate: Int?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndDateOfBirth(dbInstanceUri, groupId, startDate, endDate, healthcarePartyId))
    }

    override fun listByHcPartyNameContainsFuzzyIdsOnly(searchString: String?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndNameContainsFuzzy(dbInstanceUri, groupId, searchString, healthcarePartyId, null))
    }

    override fun listByHcPartyName(searchString: String?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listByHcPartyName(dbInstanceUri, groupId, searchString, healthcarePartyId))
    }

    override fun listByHcPartyAndExternalIdsOnly(externalId: String?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndExternalId(dbInstanceUri, groupId, externalId, healthcarePartyId))
    }

    override fun listByHcPartyAndActiveIdsOnly(active: Boolean, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByActive(dbInstanceUri, groupId, active, healthcarePartyId))
    }

    override fun listOfMergesAfter(date: Long?) = flow<Patient> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listOfMergesAfter(dbInstanceUri, groupId, date))
    }

    override fun findByHcPartyIdsOnly(healthcarePartyId: String, offset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findIdsByHcParty(dbInstanceUri, groupId, healthcarePartyId, offset.toComplexKeyPaginationOffset()))
    }

    override fun findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId: String, offset: PaginationOffset<List<String>>, searchString: String?, sorting: Sorting) = flow<ViewQueryResultEvent> {
        val descending = "desc" == sorting.direction
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()

        if (searchString == null || searchString.isEmpty()) {
            emitAll(
                    when (sorting.field) {
                        "ssin" -> {
                            patientDAO.findPatientsByHcPartyAndSsin(dbInstanceUri, groupId, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        "dateOfBirth" -> {
                            patientDAO.findPatientsByHcPartyDateOfBirth(dbInstanceUri, groupId, null, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        else -> {
                            patientDAO.findPatientsByHcPartyAndName(dbInstanceUri, groupId, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                    }
            )
        } else {
            emitAll(when {
                FuzzyValues.isSsin(searchString) -> {
                    patientDAO.findPatientsByHcPartyAndSsin(dbInstanceUri, groupId, searchString, healthcarePartyId, offset.toComplexKeyPaginationOffset(), false)
                }
                FuzzyValues.isDate(searchString) -> {
                    patientDAO.findPatientsByHcPartyDateOfBirth(dbInstanceUri, groupId, FuzzyValues.toYYYYMMDD(searchString), FuzzyValues.getMaxRangeOf(searchString), healthcarePartyId, offset.toComplexKeyPaginationOffset(), false)
                }
                else -> {
                    findByHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending)
                }
            }
            )
        }
    }

    override fun listPatients(paginationOffset: PaginationOffset<*>?, filterChain: FilterChain<Patient>, sort: String?, desc: Boolean?) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        var ids = filters.resolve(filterChain.getFilter()).toSet().sorted()
        var forPagination = patientDAO.getForPagination(dbInstanceUri, groupId, ids)
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
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        //TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
        //We will get partial results but at least we will not overload the servers
        val limit = if (offset.startKey == null) min(1000, offset.limit * 10) else null
        val ids = patientDAO.listIdsByHcPartyAndNameContainsFuzzy(dbInstanceUri, groupId, searchString, healthcarePartyId, limit)
        emitAll(
                patientDAO.getForPagination(dbInstanceUri, groupId, ids)
        )
    }

    override fun findOfHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String, offset: PaginationOffset<*>, descending: Boolean) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        //TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
        //We will get partial results but at least we will not overload the servers
        val limit = if (offset.startKey == null) min(1000, offset.limit * 10) else null
        val ids = patientDAO.listIdsOfHcPartyNameContainsFuzzy(dbInstanceUri, groupId, searchString, healthcarePartyId, limit)
        emitAll(
                patientDAO.getForPagination(dbInstanceUri, groupId, ids)
        )
    }

    override fun findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId: String, offset: PaginationOffset<List<String>>, searchString: String?, sorting: Sorting) = flow<ViewQueryResultEvent> {
        val descending = "desc" == sorting.direction
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(
                if (searchString == null || searchString.isEmpty()) {
                    when (sorting.field) {
                        "ssin" -> {
                            patientDAO.findPatientsOfHcPartyAndSsin(dbInstanceUri, groupId, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        "dateOfBirth" -> {
                            patientDAO.findPatientsOfHcPartyDateOfBirth(dbInstanceUri, groupId, null, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                        else -> {
                            patientDAO.findPatientsOfHcPartyAndName(dbInstanceUri, groupId, null, healthcarePartyId, offset.toComplexKeyPaginationOffset(), descending)
                        }
                    }
                } else {
                    when {
                        FuzzyValues.isSsin(searchString) -> {
                            patientDAO.findPatientsOfHcPartyAndSsin(dbInstanceUri, groupId, searchString, healthcarePartyId, offset.toComplexKeyPaginationOffset(), false)
                        }
                        FuzzyValues.isDate(searchString) -> {
                            patientDAO.findPatientsOfHcPartyDateOfBirth(dbInstanceUri, groupId, FuzzyValues.toYYYYMMDD(searchString),
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
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsByHcPartyAndSsin(dbInstanceUri, groupId, ssin!!, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), false))
    }

    override fun findByHcPartyDateOfBirth(date: Int?, healthcarePartyId: String, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsByHcPartyDateOfBirth(dbInstanceUri, groupId, date, date, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), false))
    }

    override fun findByHcPartyModificationDate(start: Long?, end: Long?, healthcarePartyId: String, descending: Boolean, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsByHcPartyModificationDate(dbInstanceUri, groupId, start, end, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), descending))
    }

    override fun findOfHcPartyModificationDate(start: Long?, end: Long?, healthcarePartyId: String, descending: Boolean, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsOfHcPartyModificationDate(dbInstanceUri, groupId, start, end, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset(), descending))
    }

    override suspend fun findByUserId(id: String): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.findPatientsByUserId(dbInstanceUri, groupId, id)
    }

    override suspend fun getPatient(patientId: String): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.get(dbInstanceUri, groupId, patientId)
    }

    override fun getPatientSummary(patientDto: PatientDto?, propertyExpressions: List<String?>?): Map<String, Any>? { //		return patientDtoBeans.getAsMapOfValues(patientDto, propertyExpressions);
        return null
    }

    override fun getPatients(patientIds: List<String>) = flow<Patient> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.get(dbInstanceUri, groupId, patientIds))
    }

    override suspend fun addDelegation(patientId: String, delegation: Delegation): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val patient = getPatient(patientId)
        return delegation.delegatedTo?.let { healthcarePartyId ->
            patient?.let { c ->
                patientDAO.save(dbInstanceUri, groupId, c.copy(delegations = c.delegations + mapOf(
                        healthcarePartyId to setOf(delegation)
                )))
            }
        } ?: patient
    }

    override suspend fun addDelegations(patientId: String, delegations: Collection<Delegation>): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val patient = getPatient(patientId)
        return patient?.let {
            return patientDAO.save(dbInstanceUri, groupId, it.copy(
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
                logPatient(createdPatient, "patient.create.")
                createdPatient
            }
        }
    }

    @Throws(MissingRequirementsException::class)
    override suspend fun modifyPatient(patient: Patient) = fix(patient) { patient ->
        log.debug("Modifying patient with id:" + patient.id)
        // checking requirements
        checkRequirements(patient)
        try {
            updateEntities(setOf(patient)).first().also { logPatient(it, "patient.modify.") }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid patient", e)
        }
    }

    override fun createEntities(entities: Collection<Patient>): Flow<Patient> {
        entities.forEach { checkRequirements(it) }
        return super.createEntities(entities)
    }

    override fun updateEntities(entities: Collection<Patient>): Flow<Patient> {
        entities.forEach { checkRequirements(it) }
        return super.updateEntities(entities)
    }

    private fun checkRequirements(patient: Patient) {
        if ((patient.firstName == null && patient.lastName == null) && patient.encryptedSelf == null && patient.deletionDate == null) {
            throw MissingRequirementsException("modifyPatient: Name, Last name  are required.")
        }
    }

    override suspend fun logAllPatients(hcPartyId: String) { //TODO MB ask : supend collect // launch ?
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        patientDAO.listIdsByHcParty(dbInstanceUri, groupId, hcPartyId)
                .bufferedChunks(100, 101)
                .onEach { getPatients(it).onEach { p -> logPatient(p, "patient.init.${p.id}.") } }
                .collect()
    }

    private fun logPatient(modifiedPatient: Patient, prefix: String) {
        val dir = File("/Library/Application Support/iCure/Patients")
        if (dir.exists() && dir.isDirectory) {
            val xs = XStream()
            val file = File(dir, prefix + System.currentTimeMillis() + ".xml")
            try {
                val out = BufferedOutputStream(FileOutputStream(file))
                xs.toXML(modifiedPatient, out)
                out.close()
            } catch (ignored: IOException) { //
            }
        }
    }

    override suspend fun modifyPatientReferral(patient: Patient, referralId: String?, start: Instant?, end: Instant?): Patient? {
        val startOrNow = start ?: Instant.now()
        val shouldSave = booleanArrayOf(false)
        //Close referrals relative to other healthcare parties
        val fixedPhcp = patient.patientHealthCareParties.map { phcp ->
            if (phcp.isReferral && (referralId == null || referralId != phcp.healthcarePartyId)) {
                phcp.copy(
                        isReferral = false,
                        referralPeriods = phcp.referralPeriods.map { p ->
                            if (p.endDate == null || p.endDate != startOrNow) {
                                p.copy(endDate = startOrNow)
                            } else p
                        }.toSortedSet()
                )
            } else if (referralId != null && referralId == phcp.healthcarePartyId) {
                (if (!phcp.isReferral) {
                    phcp.copy(isReferral = true)
                } else phcp).copy(
                        referralPeriods = phcp.referralPeriods.map {rp ->
                            if (start == rp.startDate) {
                                rp.copy(endDate = end)
                            } else rp
                        }.toSortedSet()
                )
            } else phcp
        }
        return (if (!fixedPhcp.any { it.isReferral && it.healthcarePartyId == referralId }) {
            fixedPhcp + PatientHealthCareParty(
                    isReferral = true,
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
        return fromPatients.fold(patient to listOf<Patient>() ) { (p, others), o -> p.merge(o) to others + modifyPatient(p.copy(
                mergeToPatientId = patient.id,
                deletionDate = now
        )) }.let { (p, others) ->
            modifyPatient(p.copy(mergedIds = p.mergedIds + others.map { it.id }))
        }
    }

    override suspend fun getByExternalId(externalId: String): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.getByExternalId(dbInstanceUri, groupId, externalId)
    }

    override suspend fun solveConflicts() {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        patientDAO.listConflicts(dbInstanceUri, groupId).map { it: Patient -> patientDAO.get(dbInstanceUri, groupId, it.id, Option.CONFLICTS) }
                .filterNotNull()
                .onEach { patient ->
                    patient.conflicts?.mapNotNull { patientDAO.get(dbInstanceUri, groupId, patient.id, it) }
                            ?.forEach {
                                patient.solveConflictsWith(it)
                                patientDAO.purge(dbInstanceUri, groupId, it)
                            }
                    patientDAO.save(dbInstanceUri, groupId, patient)
                }
    }

    override suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.getHcPartyKeysForDelegate(dbInstanceUri, groupId, healthcarePartyId)
    }

    override fun listOfPatientsModifiedAfter(date: Long, startKey: Long?, startDocumentId: String?, limit: Int?) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listOfPatientsModifiedAfter(dbInstanceUri, groupId, date, PaginationOffset(startKey, startDocumentId, 0, limit
                ?: 1000)))
    }

    override fun getDuplicatePatientsBySsin(healthcarePartyId: String, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.getDuplicatePatientsBySsin(dbInstanceUri, groupId, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset()))
    }

    override fun getDuplicatePatientsByName(healthcarePartyId: String, paginationOffset: PaginationOffset<List<String>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.getDuplicatePatientsByName(dbInstanceUri, groupId, healthcarePartyId, paginationOffset.toComplexKeyPaginationOffset()))
    }

    override fun fuzzySearchPatients(mapper: MapperFacade, firstName: String?, lastName: String?, dateOfBirth: Int?, healthcarePartyId: String?) = flow<Patient> {
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
        emitAll(deleteByIds(ids))
    }

    override fun findDeletedPatientsByDeleteDate(start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findDeletedPatientsByDeleteDate(dbInstanceUri, groupId, start, end, descending, paginationOffset))
    }

    override fun findDeletedPatientsByNames(firstName: String?, lastName: String?) = flow<Patient> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findDeletedPatientsByNames(dbInstanceUri, groupId, firstName, lastName))
    }

    override fun undeletePatients(ids: Set<String>) = flow<DocIdentifier> {
        emitAll(undeleteByIds(ids))
    }

    companion object {
        private val log = LoggerFactory.getLogger(PatientLogicImpl::class.java)
    }

    override fun getGenericDAO(): PatientDAO {
        return patientDAO
    }
}

