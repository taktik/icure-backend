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
import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.StringUtils
import org.ektorp.ComplexKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginatedDocumentKeyIdPair
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.db.Sorting
import org.taktik.icure.db.StringUtils.safeConcat
import org.taktik.icure.db.StringUtils.sanitizeString
import org.taktik.icure.dto.filter.chain.FilterChain
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.bufferedChunks
import org.taktik.icure.utils.firstOrNull
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.min

@FlowPreview
@ExperimentalCoroutinesApi
@Service
class PatientLogicImpl(
        private val sessionLogic: AsyncSessionLogic,
        private val patientDAO: PatientDAO,
        private val uuidGenerator: UUIDGenerator,
        private val mapper: MapperFacade,
        private val userLogic: UserLogic,
        private val filters: Filters) : GenericLogicImpl<Patient, PatientDAO>(sessionLogic), PatientLogic {

    suspend fun countByHcParty(healthcarePartyId: String): Int {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.countByHcParty(dbInstanceUri, groupId, healthcarePartyId)
    }

    suspend fun countOfHcParty(healthcarePartyId: String): Int {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.countOfHcParty(dbInstanceUri, groupId, healthcarePartyId)
    }

    fun listByHcPartyIdsOnly(healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcParty(dbInstanceUri, groupId, healthcarePartyId))
    }

    fun listByHcPartyAndSsinIdsOnly(ssin: String, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndSsin(dbInstanceUri, groupId, ssin, healthcarePartyId))
    }

    fun listByHcPartyAndSsinsIdsOnly(ssins: Collection<String>, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndSsins(dbInstanceUri, groupId, ssins, healthcarePartyId))
    }

    fun listByHcPartyDateOfBirthIdsOnly(date: Int, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndDateOfBirth(dbInstanceUri, groupId, date, healthcarePartyId))
    }

    fun listByHcPartyGenderEducationProfessionIdsOnly(healthcarePartyId: String, gender: Gender?, education: String?, profession: String?) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyGenderEducationProfession(dbInstanceUri, groupId, healthcarePartyId, gender, education, profession))
    }

    fun listByHcPartyDateOfBirthIdsOnly(startDate: Int?, endDate: Int?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndDateOfBirth(dbInstanceUri, groupId, startDate, endDate, healthcarePartyId))
    }

    fun listByHcPartyNameContainsFuzzyIdsOnly(searchString: String?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndNameContainsFuzzy(dbInstanceUri, groupId, searchString, healthcarePartyId, null))
    }

    fun listByHcPartyName(searchString: String?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listByHcPartyName(dbInstanceUri, groupId, searchString, healthcarePartyId))
    }

    fun listByHcPartyAndExternalIdsOnly(externalId: String?, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByHcPartyAndExternalId(dbInstanceUri, groupId, externalId, healthcarePartyId))
    }

    fun listByHcPartyAndActiveIdsOnly(active: Boolean, healthcarePartyId: String) = flow<String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listIdsByActive(dbInstanceUri, groupId, active, healthcarePartyId))
    }

    fun listOfMergesAfter(date: Long?) = flow<Patient> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listOfMergesAfter(dbInstanceUri, groupId, date))
    }

    fun findByHcPartyIdsOnly(healthcarePartyId: String, offset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findIdsByHcParty(dbInstanceUri, groupId, healthcarePartyId, offset))
    }

    fun findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId: String, offset: PaginationOffset<ComplexKey>, searchString: String?, sorting: Sorting) = flow<ViewQueryResultEvent> {
        val descending = "desc" == sorting.direction
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()

        if (searchString == null || searchString.isEmpty()) {
            emitAll(
                    when (sorting.field) {
                        "ssin" -> {
                            patientDAO.findPatientsByHcPartyAndSsin(dbInstanceUri, groupId, null, healthcarePartyId, offset, descending)
                        }
                        "dateOfBirth" -> {
                            patientDAO.findPatientsByHcPartyDateOfBirth(dbInstanceUri, groupId, null, null, healthcarePartyId, offset, descending)
                        }
                        else -> {
                            patientDAO.findPatientsByHcPartyAndName(dbInstanceUri, groupId, null, healthcarePartyId, offset, descending)
                        }
                    }
            )
        } else {
            emitAll(when {
                FuzzyValues.isSsin(searchString) -> {
                    patientDAO.findPatientsByHcPartyAndSsin(dbInstanceUri, groupId, searchString, healthcarePartyId, offset, false)
                }
                FuzzyValues.isDate(searchString) -> {
                    patientDAO.findPatientsByHcPartyDateOfBirth(dbInstanceUri, groupId, FuzzyValues.toYYYYMMDD(searchString), FuzzyValues.getMaxRangeOf(searchString), healthcarePartyId, offset, false)
                }
                else -> {
                    findByHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending)
                }
            }
            )
        }
    }

    fun listPatients(paginationOffset: PaginationOffset<*>?, filterChain: FilterChain<Patient>, sort: String?, desc: Boolean?) = flow<ViewQueryResultEvent> {
        var ids = filters.resolve(filterChain.getFilter()).toSet().sorted()
        if (filterChain.predicate != null || sort != null && sort != "id") {
            var patients = getPatients(ArrayList(ids)).toList()
            if (filterChain.predicate != null) {
                patients = patients.filter { filterChain.predicate.apply(it) }
            }
            val pub = PropertyUtilsBean()

            patients = patients.sortedWith(kotlin.Comparator { a, b ->
                try {
                    val ap = pub.getProperty(a, sort ?: "id") as Comparable<*>
                    val bp = pub.getProperty(b, sort ?: "id") as Comparable<*>
                    if (ap is String && bp is String) {
                        if (desc != null && desc) {
                            StringUtils.compareIgnoreCase(bp, ap)
                        } else {
                            StringUtils.compareIgnoreCase(ap, bp)
                        }
                    } else if (desc != null && desc) {
                        bp as Comparable<Comparable<*>> // TODO MB ask
                        ap as Comparable<Comparable<*>>
                        ObjectUtils.compare<Comparable<Comparable<*>>>(bp, ap)
                    } else {
                        bp as Comparable<Comparable<*>>
                        ap as Comparable<Comparable<*>>
                        ObjectUtils.compare(ap, bp)
                    }
                } catch (e: Exception) {
                }
                0
            })

            var firstIndex = paginationOffset?.takeIf { it.startDocumentId != null }?.let { patients.map { it.id }.toList().indexOf(paginationOffset.startDocumentId) }
                    ?: 0

            if (firstIndex != -1) {
                firstIndex += paginationOffset?.offset ?: 0
                val hasNextPage = paginationOffset != null && paginationOffset.limit != null && firstIndex + paginationOffset.limit < patients.size
                if (hasNextPage) PaginatedList(paginationOffset.limit, patients.size, patients.subList(firstIndex, firstIndex + paginationOffset.limit),
                        PaginatedDocumentKeyIdPair(null, patients[firstIndex + paginationOffset.limit].id)) else PaginatedList(patients.size - firstIndex, patients.size, patients.subList(firstIndex, patients.size), null)
            }
        } else {
            if (desc != null && desc) {
                ids = (ids as TreeSet<String?>).descendingSet()
            }
            if (paginationOffset != null && paginationOffset.startDocumentId != null) {
                ids = ids.subSet(paginationOffset.startDocumentId, (ids as TreeSet<*>).last().toString() + "\u0000")
            }
            var idsList: List<String?> = ArrayList(ids)
            if (paginationOffset != null && paginationOffset.offset != null) {
                idsList = idsList.subList(paginationOffset.offset, idsList.size)
            }
            val hasNextPage = paginationOffset != null && paginationOffset.limit != null && paginationOffset.limit < idsList.size
            if (hasNextPage) {
                idsList = idsList.subList(0, paginationOffset.limit + 1)
            }
            val patients = getPatients(idsList)
            PaginatedList(if (hasNextPage) paginationOffset.limit else patients.size, ids.size, if (hasNextPage) patients.subList(0, paginationOffset.limit) else patients, if (hasNextPage) PaginatedDocumentKeyIdPair(null, patients[patients.size - 1].id) else null)
        }
    }

    fun findByHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String?, offset: PaginationOffset<*>, descending: Boolean) = flow<ViewQueryResultEvent> {
        val sanSs = sanitizeString(searchString)
        //TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
//We will get partial results but at least we will not overload the servers
        val limit = if (offset.startKey == null && offset.limit != null) Math.min(1000, offset.limit * 10) else null
        val ids: Set<String> = HashSet(patientDAO.listIdsByHcPartyAndNameContainsFuzzy(searchString, healthcarePartyId, limit))
        val patients = patientDAO[ids].stream().sorted(getPatientComparator(sanSs, descending)).collect(Collectors.toList())
        val patientKeys = patients.stream().map { p: Patient -> sanitizeString(safeConcat(p.lastName, p.firstName)) }.collect(Collectors.toList())
        return buildPatientPaginatedList(healthcarePartyId, offset, patients, patientKeys, descending)
    }

    fun findOfHcPartyNameContainsFuzzy(searchString: String?, healthcarePartyId: String, offset: PaginationOffset<*>, descending: Boolean) = flow<ViewQueryResultEvent> {
        val sanSs = sanitizeString(searchString)
        //TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
//We will get partial results but at least we will not overload the servers
        val limit = if (offset.startKey == null && offset.limit != null) Math.min(1000, offset.limit * 10) else null
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val ids = patientDAO.listIdsOfHcPartyNameContainsFuzzy(dbInstanceUri, groupId, searchString, healthcarePartyId, limit).toList()
        val patients = patientDAO.get(dbInstanceUri, groupId, ids).toList().sortedWith(getPatientComparator(sanSs, descending))
        val patientKeys = patients.map { p: Patient -> sanitizeString(safeConcat(p.lastName, p.firstName)) }
        return buildPatientPaginatedList(healthcarePartyId, offset, patients, patientKeys, descending)
    }

    fun findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(healthcarePartyId: String?, offset: PaginationOffset<*>, searchString: String?, sorting: Sorting) = flow<ViewQueryResultEvent> {
        val descending = "desc" == sorting.direction
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        if (searchString == null || searchString.isEmpty()) {
            if ("ssin" == sorting.field) {
                patientDAO.findPatientsOfHcPartyAndSsin(dbInstanceUri, groupId, null, healthcarePartyId, offset, descending)
            } else if ("dateOfBirth" == sorting.field) {
                patientDAO.findPatientsOfHcPartyDateOfBirth(dbInstanceUri, groupId, null, null, healthcarePartyId, offset, descending)
            } else {
                patientDAO.findPatientsOfHcPartyAndName(dbInstanceUri, groupId, null, healthcarePartyId, offset, descending)
            }
        } else {
            if (FuzzyValues.isSsin(searchString)) {
                patientDAO.findPatientsOfHcPartyAndSsin(dbInstanceUri, groupId, searchString, healthcarePartyId, offset, false)
            } else if (FuzzyValues.isDate(searchString)) {
                patientDAO.findPatientsOfHcPartyDateOfBirth(dbInstanceUri, groupId, FuzzyValues.toYYYYMMDD(searchString),
                        FuzzyValues.getMaxRangeOf(searchString), healthcarePartyId, offset, false)
            } else {
                findOfHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending)
            }
        }
    }

    fun findByHcPartyAndSsin(ssin: String?, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsByHcPartyAndSsin(dbInstanceUri, groupId, ssin, healthcarePartyId, paginationOffset, false))
    }

    fun findByHcPartyDateOfBirth(date: Int?, healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsByHcPartyDateOfBirth(dbInstanceUri, groupId, date, date, healthcarePartyId, paginationOffset, false))
    }

    fun findByHcPartyModificationDate(start: Long?, end: Long?, healthcarePartyId: String, descending: Boolean, paginationOffset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsByHcPartyModificationDate(dbInstanceUri, groupId, start, end, healthcarePartyId, paginationOffset, descending))
    }

    fun findOfHcPartyModificationDate(start: Long?, end: Long?, healthcarePartyId: String, descending: Boolean, paginationOffset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findPatientsOfHcPartyModificationDate(dbInstanceUri, groupId, start, end, healthcarePartyId, paginationOffset, descending))
    }

    private fun getPatientComparator(sanitizedSearchString: String, descending: Boolean): Comparator<Patient> {
        return label@ Comparator { a: Patient?, b: Patient? ->
            if (a == null && b == null) {
                return@label 0
            }
            if (a == null) {
                return@label -1
            }
            if (b == null) {
                return@label 1
            }
            var res = ObjectUtils.compare(if (sanitizeString(safeConcat(a.lastName, a.firstName))?.startsWith(sanitizedSearchString)?: false) 0 else 1,
                    if (sanitizeString(safeConcat(b.lastName, b.firstName))?.startsWith(sanitizedSearchString)?:false) 0 else 1)
            if (res != 0) return@label res * if (descending) -1 else 1
            res = ObjectUtils.compare<String>(sanitizeString(a.lastName), sanitizeString(b.lastName))
            if (res != 0) return@label res * if (descending) -1 else 1
            res = ObjectUtils.compare<String>(sanitizeString(a.firstName), sanitizeString(b.firstName))
            res * if (descending) -1 else 1
        }
    }

    suspend fun findByUserId(id: String): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.findPatientsByUserId(dbInstanceUri, groupId, id)
    }

    suspend fun getPatient(patientId: String): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.get(dbInstanceUri, groupId, patientId)
    }

    fun getPatientSummary(patientDto: PatientDto?, propertyExpressions: List<String?>?): Map<String, Any>? { //		return patientDtoBeans.getAsMapOfValues(patientDto, propertyExpressions);
        return null
    }

    fun getPatients(patientIds: List<String>) = flow<Patient> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.get(dbInstanceUri, groupId, patientIds))
    }

    suspend fun addDelegation(patientId: String, delegation: Delegation): Patient? {
        val patient = getPatient(patientId)
        return patient?.let {
            patient.addDelegation(delegation.delegatedTo, delegation)
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            patientDAO.save(dbInstanceUri, groupId, patient)
        }
    }

    suspend fun addDelegations(patientId: String, delegations: Collection<Delegation>): Patient? {
        val patient = getPatient(patientId)
        return patient?.let { patient ->
            delegations.forEach { patient.addDelegation(it.delegatedTo, it) }
            val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
            patientDAO.save(dbInstanceUri, groupId, patient)
        }
    }

    @Throws(MissingRequirementsException::class)
    suspend fun createPatient(patient: Patient): Patient? { // checking requirements
        if (patient.preferredUserId != null && (patient.delegations == null || patient.delegations.isEmpty())) {
            patient.delegations = HashMap()
            val user: User? = userLogic.getUser(patient.preferredUserId as String) //TODO MB remove explicit cast when Patient is kotlinized
            user?.let {
                patient.delegations[it.healthcarePartyId] = HashSet()
                it.autoDelegations.values.forEach {
                    it.forEach { patient.delegations[it] = HashSet() }
                }
            }
        }
        return createEntities(setOf(patient)).firstOrNull()?.let {
            logPatient(it, "patient.create.")
            it
        }
    }

    @Throws(MissingRequirementsException::class)
    suspend fun modifyPatient(patient: Patient): Patient? {
        log.debug("Modifying patient with id:" + patient.id)
        // checking requirements
        if ((patient.firstName == null || patient.lastName == null) && patient.encryptedSelf == null) {
            throw MissingRequirementsException("modifyPatient: Name, Last name  are required.")
        }
        return try {
            updateEntities(setOf(patient))
            val modifiedPatient = getPatient(patient.id)
            modifiedPatient?.let {
                logPatient(modifiedPatient, "patient.modify.")
                it
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid patient", e)
        }
    }

    suspend fun logAllPatients(hcPartyId: String) { //TODO MB ask : supend collect // launch ?
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

    suspend fun modifyPatientReferral(patient: Patient, referralId: String?, start: Instant?, end: Instant?): Patient? {
        val startOrNow = start ?: Instant.now()
        val shouldSave = booleanArrayOf(false)
        //Close referrals relative to other healthcare parties
        patient.patientHealthCareParties.stream().filter { phcp: PatientHealthCareParty -> phcp.isReferral && (referralId == null || referralId == phcp.healthcarePartyId) }.forEach { phcp: PatientHealthCareParty ->
            phcp.isReferral = false
            shouldSave[0] = true
            phcp.referralPeriods.forEach(Consumer { p: ReferralPeriod ->
                if (p.endDate == null || p.endDate != startOrNow) {
                    p.endDate = startOrNow
                }
            })
        }
        if (referralId != null) {
            val patientHealthCareParty = patient.patientHealthCareParties?.stream()?.filter { phcp: PatientHealthCareParty -> referralId == phcp.healthcarePartyId }?.findFirst()?.orElse(null)
            if (patientHealthCareParty != null) {
                if (!patientHealthCareParty.isReferral) {
                    patientHealthCareParty.isReferral = true
                    shouldSave[0] = true
                }
                patientHealthCareParty.referralPeriods.stream().filter { rp: ReferralPeriod -> start == rp.startDate }.findFirst().ifPresent { rp: ReferralPeriod ->
                    if (end != rp.endDate) {
                        rp.endDate = end
                        shouldSave[0] = true
                    }
                }
            } else {
                val newRefPer = PatientHealthCareParty()
                newRefPer.setHealthcarePartyId(referralId)
                newRefPer.isReferral = true
                newRefPer.referralPeriods.add(ReferralPeriod(startOrNow, end))
                patient.patientHealthCareParties.add(newRefPer)
                shouldSave[0] = true
            }
        }
        return if (shouldSave[0]) modifyPatient(patient) else patient
    }

    suspend fun mergePatient(patient: Patient, fromPatients: List<Patient>): Patient? {
        for (from in fromPatients) {
            val entries: Set<Map.Entry<String, Set<Delegation>>> = from.delegations.entries
            for ((key, value) in entries) {
                val secondMapValue = patient.delegations[key]
                if (secondMapValue == null) {
                    patient.delegations[key] = value
                } else {
                    secondMapValue.addAll(value)
                }
            }
            from.setMergeToPatientId(patient.id)
            from.deletionDate = Instant.now().toEpochMilli()
            patient.mergeFrom(from)
            try {
                modifyPatient(from)
            } catch (e: MissingRequirementsException) {
                throw IllegalStateException(e)
            }
        }
        patient.mergedIds.addAll(fromPatients.map { p: Patient -> p.id })
        return try {
            modifyPatient(patient)
        } catch (e: MissingRequirementsException) {
            throw IllegalStateException(e)
        }
    }

    suspend fun getByExternalId(externalId: String): Patient? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.getByExternalId(dbInstanceUri, groupId, externalId)
    }

    suspend fun solveConflicts() {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val patientsInConflict = patientDAO.listConflicts(dbInstanceUri, groupId).map { it: Patient -> patientDAO.get(dbInstanceUri, groupId, it.id, Option.CONFLICTS) }
                .filterNotNull()
                .onEach { patient ->
                    patient.conflicts?.map { patientDAO.get(dbInstanceUri, groupId, patient.id, it) }
                            ?.filterNotNull()
                            ?.forEach {
                                patient.solveConflictWith(it)
                                patientDAO.purge(dbInstanceUri, groupId, it)
                            }
                    patientDAO.save(dbInstanceUri, groupId, patient)
                }
    }

    suspend fun getHcPartyKeysForDelegate(healthcarePartyId: String): Map<String, String> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return patientDAO.getHcPartyKeysForDelegate(dbInstanceUri, groupId, healthcarePartyId)
    }

    fun listOfPatientsModifiedAfter(date: Long, startKey: Long?, startDocumentId: String?, limit: Int?) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.listOfPatientsModifiedAfter(dbInstanceUri, groupId, date, PaginationOffset(startKey, startDocumentId, 0, limit
                ?: 1000)))
    }

    fun getDuplicatePatientsBySsin(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.getDuplicatePatientsBySsin(dbInstanceUri, groupId, healthcarePartyId, paginationOffset))
    }

    fun getDuplicatePatientsByName(healthcarePartyId: String, paginationOffset: PaginationOffset<ComplexKey>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.getDuplicatePatientsByName(dbInstanceUri, groupId, healthcarePartyId, paginationOffset))
    }

    fun fuzzySearchPatients(mapper: MapperFacade, healthcarePartyId: String, firstName: String?, lastName: String?, dateOfBirth: Int?) = flow<Patient> {
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

    fun deletePatients(ids: Set<String>) = flow<DocIdentifier> {
        emitAll(deleteByIds(ids))
    }

    fun findDeletedPatientsByDeleteDate(start: Long, end: Long?, descending: Boolean, paginationOffset: PaginationOffset<Long>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findDeletedPatientsByDeleteDate(dbInstanceUri, groupId, start, end, descending, paginationOffset))
    }

    fun findDeletedPatientsByNames(firstName: String?, lastName: String?) = flow<Patient> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(patientDAO.findDeletedPatientsByNames(dbInstanceUri, groupId, firstName, lastName))
    }

    fun undeletePatients(ids: Set<String>) = flow<DocIdentifier> {
        emitAll(undeleteByIds(ids))
    }

    companion object {
        private val log = LoggerFactory.getLogger(PatientLogicImpl::class.java)
    }
}
