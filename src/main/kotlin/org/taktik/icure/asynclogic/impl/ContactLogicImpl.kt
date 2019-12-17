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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.ektorp.ComplexKey
import org.ektorp.UpdateConflictException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.ContactDAO
import org.taktik.icure.asynclogic.AsyncICureSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.dao.Option
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.db.PaginatedDocumentKeyIdPair
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.dto.data.LabelledOccurence
import org.taktik.icure.dto.filter.chain.FilterChain
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.bufferedChunks
import org.taktik.icure.utils.firstOrNull
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

@ExperimentalCoroutinesApi
@Service
class ContactLogicImpl(private val contactDAO: ContactDAO,
                       private val uuidGenerator: UUIDGenerator,
                       private val sessionLogic: AsyncICureSessionLogic,
                       private val filters: Filters) : GenericLogicImpl<Contact, ContactDAO>(sessionLogic), ContactLogic {

    override suspend fun getContact(id: String): Contact? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return contactDAO.getContact(dbInstanceUri, groupId, id)
    }

    override fun getContacts(selectedIds: Collection<String>): Flow<Contact> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.get(dbInstanceUri, groupId, selectedIds))
    }

    override fun getPaginatedContacts(selectedIds: Collection<String>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.getPaginatedContacts(dbInstanceUri, groupId, selectedIds))
    }

    override fun getPaginatedServices(selectedIds: Collection<String>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.getPaginatedServices(dbInstanceUri, groupId, selectedIds))
    }

    override fun findByHCPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.findByHcPartyPatient(dbInstanceUri, groupId, hcPartyId, secretPatientKeys))
    }

    override suspend fun addDelegation(contactId: String, delegation: Delegation): Contact? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val contact = getContact(contactId)
        contact?.addDelegation(delegation.delegatedTo, delegation)
        return contact?.let { contactDAO.save(dbInstanceUri, groupId, it) }
    }

    override suspend fun createContact(contact: Contact): Contact? {
        try { // Fetching the hcParty
            val healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId()
            // Setting contact attributes
            if (contact.id == null) {
                contact.id = uuidGenerator.newGUID().toString()
            }
            if (contact.openingDate == null) {
                contact.openingDate = FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS)
            }
            contact.author = sessionLogic.getCurrentUserId()
            if (contact.responsible == null) {
                contact.responsible = healthcarePartyId
            }
            contact.healthcarePartyId = healthcarePartyId
            return createEntities(setOf(contact)).firstOrNull()
        } catch (e: Exception) {
            logger.error("createContact: " + e.message)
            throw IllegalArgumentException("Invalid contact", e)
        }
    }

    override fun deleteContacts(ids: Set<String>): Flow<DocIdentifier> {
        return try {
            deleteByIds(ids)
        } catch (e: Exception) {
            logger.error(e.message, e)
            flowOf()
        }
    }

    override suspend fun modifyContact(contact: Contact): Contact? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return try {
            contactDAO.save(dbInstanceUri, groupId, contact)
        } catch (e: UpdateConflictException) { //	return resolveConflict(contact, e);
            logger.warn("Documents of class {} with id {} and rev {} could not be merged", contact.javaClass.simpleName, contact.id, contact.rev)
            null
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid contact", e)
        }
    }

    // TODO SH MB: make sure this works ('bufferedChunks')
    override fun getServices(selectedServiceIds: Collection<String>): Flow<org.taktik.icure.entities.embed.Service> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val serviceIds: Set<String> = HashSet(selectedServiceIds)
        val contactIds = contactDAO.listIdsByServices(dbInstanceUri, groupId, selectedServiceIds)
        //Treat contacts by 10
        val alreadyEmitted = mutableMapOf<String, org.taktik.icure.entities.embed.Service>()
        val toEmit = contactIds.bufferedChunks(5, 15).flatMapConcat { contactIdsBy10 ->
            val contacts = contactDAO.get(dbInstanceUri, groupId, contactIdsBy10)
            contacts.flatMapConcat { c ->
                c.services.asFlow().mapNotNull { s ->
                    val sId = s.id
                    val sModified = s.modified
                    if (sId != null && serviceIds.contains(sId)) {
                        val ps = alreadyEmitted[sId]
                        val psModified = ps?.modified
                        if (ps == null || psModified == null || sModified != null && sModified > psModified) {
                            alreadyEmitted[sId] = s
                            pimpServiceWithContactInformation(s, c)
                        }
                    }
                    null as org.taktik.icure.entities.embed.Service?
                }
            }
        }
        emitAll(toEmit)
    }

    override fun pimpServiceWithContactInformation(s: org.taktik.icure.entities.embed.Service, c: Contact): org.taktik.icure.entities.embed.Service {
        s.contactId = c.id
        s.secretForeignKeys = c.secretForeignKeys
        s.cryptedForeignKeys = c.cryptedForeignKeys
        val subContacts = c.subContacts.stream().filter { sc: SubContact -> sc.services.stream().filter { sc2: ServiceLink -> sc2.serviceId != null }.anyMatch { sl: ServiceLink -> sl.serviceId == s.id } }.collect(Collectors.toList())
        s.subContactIds = subContacts.stream().map { obj: SubContact -> obj.id }.collect(Collectors.toSet())
        s.plansOfActionIds = subContacts.stream().map { obj: SubContact -> obj.planOfActionId }.filter { obj: String? -> Objects.nonNull(obj) }.collect(Collectors.toSet())
        s.healthElementsIds = subContacts.stream().map { obj: SubContact -> obj.healthElementId }.filter { obj: String? -> Objects.nonNull(obj) }.collect(Collectors.toSet())
        s.formIds = subContacts.stream().map { obj: SubContact -> obj.formId }.filter { obj: String? -> Objects.nonNull(obj) }.collect(Collectors.toSet())
        s.delegations = c.delegations
        s.encryptionKeys = c.encryptionKeys
        s.author = c.author
        s.responsible = c.responsible
        return s
    }

    override fun listServiceIdsByTag(hcPartyId: String, patientSecretForeignKeys: List<String>?, tagType: String, tagCode: String, startValueDate: Long, endValueDate: Long): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val toEmit = if (patientSecretForeignKeys == null) contactDAO.listServiceIdsByTag(dbInstanceUri, groupId, hcPartyId, tagType, tagCode, startValueDate, endValueDate) else contactDAO.listServiceIdsByPatientTag(dbInstanceUri, groupId, hcPartyId, patientSecretForeignKeys, tagType, tagCode, startValueDate, endValueDate)
        emitAll(toEmit)
    }

    override fun listServiceIdsByCode(hcPartyId: String, patientSecretForeignKeys: List<String>?, codeType: String, codeCode: String, startValueDate: Long, endValueDate: Long): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val toEmit = if (patientSecretForeignKeys == null) contactDAO.listServiceIdsByCode(dbInstanceUri, groupId, hcPartyId, codeType, codeCode, startValueDate, endValueDate) else contactDAO.findServicesByForeignKeys(dbInstanceUri, groupId, hcPartyId, patientSecretForeignKeys, codeType, codeCode, startValueDate, endValueDate)
        emitAll(toEmit)
    }

    override fun listContactIds(hcPartyId: String): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.listContactIds(dbInstanceUri, groupId, hcPartyId))
    }

    override fun findByServices(services: Collection<String>): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.findByServices(dbInstanceUri, groupId, services))
    }

    override fun findServicesBySecretForeignKeys(hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.findServicesByForeignKeys(dbInstanceUri, groupId, hcPartyId, patientSecretForeignKeys))
    }

    override fun findContactsByHCPartyFormId(hcPartyId: String, formId: String): Flow<Contact> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.findByHcPartyFormId(dbInstanceUri, groupId, hcPartyId, formId))
    }

    override suspend fun getServiceCodesOccurences(hcPartyId: String, codeType: String, minOccurences: Long): List<LabelledOccurence> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val mapped = contactDAO.listCodesFrequencies(dbInstanceUri, groupId, hcPartyId, codeType)
                .filter { v -> v.value?.let { it >= minOccurences } == true }
                .map { v -> LabelledOccurence(v.key.components[2] as String, v.value) }.toList()
          return mapped.sortedByDescending { obj: LabelledOccurence -> obj.occurence }
    }

    override fun findContactsByHCPartyFormIds(hcPartyId: String, ids: List<String>): Flow<Contact> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.findByHcPartyFormIds(dbInstanceUri, groupId, hcPartyId, ids))
    }

    override fun getGenericDAO(): ContactDAO {
        return contactDAO
    }

    override suspend fun filterContacts(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<Contact>): Flow<ViewQueryResultEvent> {
        val ids = filters.resolve(filter.getFilter())

        val sortedIds = if (paginationOffset.startDocumentId != null) { // Sub-set starting from startDocId to the end (including last element)
            ids.dropWhile { it != paginationOffset.startDocumentId }
        } else {
            ids
        }
        val selectedIds = sortedIds.take(paginationOffset.limit!!+1) // Fetching one more contacts for the start key of the next page

        return getPaginatedContacts(selectedIds.toList())
    }

    override suspend fun filterServices(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<org.taktik.icure.entities.embed.Service>): Flow<ViewQueryResultEvent> {
        val ids= filters.resolve(filter.getFilter())

        val sortedIds = if (paginationOffset.startDocumentId != null) { // Sub-set starting from startDocId to the end (including last element)
            ids.dropWhile { it != paginationOffset.startDocumentId }
        } else {
            ids
        }

        val selectedIds = sortedIds.take(paginationOffset.limit!!+1) // Fetching one more contacts for the start key of the next page

        return getPaginatedServices(selectedIds.toList())
    }

    override suspend fun solveConflicts() {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val contactsInConflict = contactDAO.listConflicts(dbInstanceUri, groupId).mapNotNull { contactDAO.get(dbInstanceUri, groupId, it.id, Option.CONFLICTS) }
        contactsInConflict.collect { ctc ->
            ctc.conflicts.map { c: String -> contactDAO.get(dbInstanceUri, groupId, ctc.id, c) }.forEach { cp: Contact? ->
                if (cp != null) {
                    ctc.solveConflictWith(cp)
                    contactDAO.purge(dbInstanceUri, groupId, cp)
                }
            }
            contactDAO.save(dbInstanceUri, groupId, ctc)
        }
    }

    override fun listContactsByOpeningDate(hcPartyId: String, startOpeningDate: Long, endOpeningDate: Long, offset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(contactDAO.listContactsByOpeningDate(dbInstanceUri, groupId, hcPartyId, startOpeningDate, endOpeningDate, offset))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ContactLogicImpl::class.java)
    }
}
