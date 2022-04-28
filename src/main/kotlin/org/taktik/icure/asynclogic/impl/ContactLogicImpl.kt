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

import java.util.TreeSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.Option
import org.taktik.couchdb.exception.UpdateConflictException
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.ContactDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.dto.data.LabelledOccurence
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.exceptions.BulkUpdateConflictException
import org.taktik.icure.utils.aggregateResults
import org.taktik.icure.utils.toComplexKeyPaginationOffset
import java.util.*

@ExperimentalCoroutinesApi
@Service
class ContactLogicImpl(
	private val contactDAO: ContactDAO,
	private val uuidGenerator: UUIDGenerator,
	private val sessionLogic: AsyncSessionLogic,
	private val filters: Filters
) : GenericLogicImpl<Contact, ContactDAO>(sessionLogic), ContactLogic {

	override suspend fun getContact(id: String): Contact? {
		return contactDAO.getContact(id)
	}

	override fun getContacts(selectedIds: Collection<String>): Flow<Contact> = flow {
		emitAll(contactDAO.getContacts(selectedIds))
	}

	override fun findContactsByIds(selectedIds: Collection<String>): Flow<ViewQueryResultEvent> = flow {
		emitAll(contactDAO.findContactsByIds(selectedIds))
	}

	override fun listContactsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact> = flow {
		emitAll(contactDAO.listContactsByHcPartyAndPatient(hcPartyId, secretPatientKeys))
	}

	override fun listContactIdsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<String> = flow {
		emitAll(contactDAO.listContactIdsByHcPartyAndPatient(hcPartyId, secretPatientKeys))
	}

	override suspend fun addDelegation(contactId: String, delegation: Delegation): Contact? {
		val contact = getContact(contactId)
		return delegation.delegatedTo?.let { healthcarePartyId ->
			contact?.let { c ->
				contactDAO.save(
					c.copy(
						delegations = c.delegations + mapOf(
							healthcarePartyId to setOf(delegation)
						)
					)
				)
			}
		} ?: contact
	}

	override suspend fun addDelegations(contactId: String, delegations: List<Delegation>): Contact? {
		val contact = getContact(contactId)
		return contact?.let {
			return contactDAO.save(
				it.copy(
					delegations = it.delegations +
						delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
				)
			)
		}
	}

	override suspend fun createContact(contact: Contact) = fix(contact) { contact ->
		try { // Fetching the hcParty
			val dataOwnerId = sessionLogic.getCurrentDataOwnerId()
			createEntities(
				setOf(
					if (contact.healthcarePartyId == null) contact.copy(
						healthcarePartyId = dataOwnerId,
					) else contact
				)
			).firstOrNull()
		} catch (e: BulkUpdateConflictException) {
			throw UpdateConflictException("Contact already exists")
		} catch (e: Exception) {
			logger.error("createContact: " + e.message)
			throw IllegalArgumentException("Invalid contact", e)
		}
	}

	override fun deleteContacts(ids: Set<String>): Flow<DocIdentifier> {
		return try {
			deleteEntities(ids)
		} catch (e: Exception) {
			logger.error(e.message, e)
			flowOf()
		}
	}

	override suspend fun modifyContact(contact: Contact) = fix(contact) { contact ->
		try {
			contactDAO.save(contact)
		} catch (e: UpdateConflictException) { //	return resolveConflict(contact, e);
			logger.warn("Documents of class {} with id {} and rev {} could not be merged", contact.javaClass.simpleName, contact.id, contact.rev)
			null
		} catch (e: Exception) {
			throw IllegalArgumentException("Invalid contact", e)
		}
	}

	// TODO SH MB: make sure this works ('bufferedChunks')
	override fun getServices(selectedServiceIds: Collection<String>): Flow<org.taktik.icure.entities.embed.Service> = flow {
		val orderedIds = selectedServiceIds.toSortedSet()
		val contactIds = contactDAO.listIdsByServices(orderedIds)

		val servicesToEmit = contactIds.bufferedChunksAtTransition(
			20,
			100
		) { p, n -> p.serviceId == null || n.serviceId == null || p.serviceId != n.serviceId }
			.fold(mutableMapOf<String, org.taktik.icure.entities.embed.Service>()) { toEmit, chunkedCids ->
				val sortedCids = chunkedCids
					.sortedWith(compareBy({ it.serviceId }, { it.modified }, { it.contactId }))
				val filteredCidSids = sortedCids
					.filterIndexed { idx, cidsid -> idx == chunkedCids.size - 1 || cidsid.serviceId != sortedCids[idx + 1].serviceId }

				val contacts = contactDAO.getContacts(HashSet(filteredCidSids.map { it.contactId }))

				contacts.collect { c ->
					c.services
						.forEach { s ->
							val sId = s.id
							val sModified = s.modified
							if (orderedIds.contains(sId) && filteredCidSids.any { it.contactId == c.id && it.serviceId == sId }) {
								val psModified = toEmit[sId]?.modified
								if (psModified == null || sModified != null && sModified > psModified) {
									toEmit[sId] = pimpServiceWithContactInformation(s, c)
								}
							}
						}
				}
				toEmit
			}
		servicesToEmit.values.toSortedSet(compareBy { service -> service.id }).forEach { emit(it) }
	}

	override fun getServicesLinkedTo(ids: List<String>, linkType: String?): Flow<org.taktik.icure.entities.embed.Service> = flow {
		emitAll(getServices(contactDAO.findServiceIdsByIdQualifiedLink(ids, linkType).toList()))
	}

	override fun listServicesByAssociationId(associationId: String): Flow<org.taktik.icure.entities.embed.Service> = flow {
		emitAll(contactDAO.findServiceIdsByAssociationId(associationId))
	}

	override fun pimpServiceWithContactInformation(s: org.taktik.icure.entities.embed.Service, c: Contact): org.taktik.icure.entities.embed.Service {
		val subContacts = c.subContacts.filter { sc: SubContact -> sc.services.filter { sc2: ServiceLink -> sc2.serviceId != null }.any { sl: ServiceLink -> sl.serviceId == s.id } }
		return s.copy(
			contactId = c.id,
			secretForeignKeys = c.secretForeignKeys,
			cryptedForeignKeys = c.cryptedForeignKeys,
			subContactIds = subContacts.mapNotNull { obj: SubContact -> obj.id }.toSet(),
			plansOfActionIds = subContacts.mapNotNull { obj: SubContact -> obj.planOfActionId }.toSet(),
			healthElementsIds = subContacts.mapNotNull { obj: SubContact -> obj.healthElementId }.toSet(),
			formIds = subContacts.mapNotNull { obj: SubContact -> obj.formId }.toSet(),
			delegations = c.delegations,
			encryptionKeys = c.encryptionKeys,
			author = c.author,
			responsible = c.responsible
		)
	}
	override fun listServiceIdsByHcParty(hcPartyId: String) = flow {
		emitAll(contactDAO.listServiceIdsByHcParty(hcPartyId))
	}

	override fun listServiceIdsByTag(hcPartyId: String, patientSecretForeignKeys: List<String>?, tagType: String, tagCode: String, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
		val toEmit = if (patientSecretForeignKeys == null) contactDAO.listServiceIdsByTag(hcPartyId, tagType, tagCode, startValueDate, endValueDate) else contactDAO.listServiceIdsByPatientAndTag(hcPartyId, patientSecretForeignKeys, tagType, tagCode, startValueDate, endValueDate)
		emitAll(toEmit)
	}

	override fun listServiceIdsByCode(hcPartyId: String, patientSecretForeignKeys: List<String>?, codeType: String, codeCode: String, startValueDate: Long?, endValueDate: Long?): Flow<String> = flow {
		val toEmit = if (patientSecretForeignKeys == null) contactDAO.listServiceIdsByCode(hcPartyId, codeType, codeCode, startValueDate, endValueDate) else contactDAO.listServicesIdsByPatientForeignKeys(hcPartyId, patientSecretForeignKeys, codeType, codeCode, startValueDate, endValueDate)
		emitAll(toEmit)
	}

	override fun listContactIdsByTag(hcPartyId: String, tagType: String, tagCode: String, startValueDate: Long?, endValueDate: Long?) = flow {
		emitAll(contactDAO.listContactIdsByTag(hcPartyId, tagType, tagCode, startValueDate, endValueDate))
	}

	override fun listServiceIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>): Flow<String> = flow {
		emitAll(contactDAO.listServiceIdsByHcPartyAndIdentifiers(hcPartyId, identifiers))
	}

	override fun listServicesByHcPartyAndHealthElementIds(hcPartyId: String, healthElementIds: List<String>) = flow {
		val serviceIds = listServiceIdsByHcPartyAndHealthElementIds(hcPartyId, healthElementIds)
		emitAll(getServices(serviceIds.toList()))
	}

	override fun listServiceIdsByHcPartyAndHealthElementIds(hcPartyId: String, healthElementIds: List<String>) = flow {
        emitAll(contactDAO.listServiceIdsByHcPartyHealthElementIds(hcPartyId, healthElementIds))
    }

    override fun listContactIdsByCode(hcPartyId: String, codeType: String, codeCode: String, startValueDate: Long?, endValueDate: Long?) = flow {
        emitAll(contactDAO.listContactIdsByTag(hcPartyId, codeType, codeCode, startValueDate, endValueDate))
    }

	override fun listContactIds(hcPartyId: String): Flow<String> = flow {
		emitAll(contactDAO.listContactIdsByHealthcareParty(hcPartyId))
	}

	override fun listIdsByServices(services: Collection<String>): Flow<String> = flow {
		emitAll(contactDAO.listIdsByServices(services).map { it.contactId })
	}

	override fun listServicesByHcPartyAndSecretForeignKeys(hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String> = flow {
		emitAll(contactDAO.listServicesIdsByPatientForeignKeys(hcPartyId, patientSecretForeignKeys))
	}

	override fun listContactsByHcPartyAndFormId(hcPartyId: String, formId: String): Flow<Contact> = flow {
		emitAll(contactDAO.listContactsByHcPartyAndFormId(hcPartyId, formId))
	}

	override fun listContactsByHcPartyServiceId(hcPartyId: String, formId: String): Flow<Contact> = flow {
		emitAll(contactDAO.findContactsByHcPartyServiceId(hcPartyId, formId))
	}

	override fun listContactsByExternalId(externalId: String): Flow<Contact> {
		TODO("Not yet implemented")
	}

	override suspend fun getServiceCodesOccurences(hcPartyId: String, codeType: String, minOccurences: Long): List<LabelledOccurence> {
		val mapped = contactDAO.listCodesFrequencies(hcPartyId, codeType)
			.filter { v -> v.second?.let { it >= minOccurences } == true }
			.map { v ->
				LabelledOccurence(
					v.first.components[2] as String,
					v.second ?: 0L
				)
			}.toList()
		return mapped.sortedByDescending { obj: LabelledOccurence -> obj.occurence }
	}

	override fun listContactsByHcPartyAndFormIds(hcPartyId: String, ids: List<String>): Flow<Contact> = flow {
		emitAll(contactDAO.listContactsByHcPartyAndFormIds(hcPartyId, ids))
	}

	override fun getGenericDAO(): ContactDAO {
		return contactDAO
	}

	override fun filterContacts(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<Contact>) = flow<ViewQueryResultEvent> {
		val ids = filters.resolve(filter.filter).toSet(TreeSet())

		val sortedIds = if (paginationOffset.startDocumentId != null) { // Sub-set starting from startDocId to the end (including last element)
			ids.dropWhile { it != paginationOffset.startDocumentId }
		} else {
			ids
		}
		val selectedIds = sortedIds.take(paginationOffset.limit + 1) // Fetching one more contacts for the start key of the next page
		emitAll(contactDAO.findContactsByIds(selectedIds))
	}

	override fun filterServices(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<org.taktik.icure.entities.embed.Service>) = flow {
		val ids = filters.resolve(filter.filter).toSet(TreeSet())

		aggregateResults(
			ids = ids,
			limit = paginationOffset.limit,
			supplier = { serviceIds: Collection<String> -> filter.applyTo(getServices(serviceIds.toList())) },
			startDocumentId = paginationOffset.startDocumentId
		).forEach { emit(it) }
	}

	override fun solveConflicts() = contactDAO.listConflicts().mapNotNull {
		contactDAO.get(it.id, Option.CONFLICTS)?.let { contact ->
			contact.conflicts?.mapNotNull { conflictingRevision -> contactDAO.get(contact.id, conflictingRevision) }
				?.fold(contact) { kept, conflict -> kept.merge(conflict).also { contactDAO.purge(conflict) } }
				?.let { mergedContact -> contactDAO.save(mergedContact) }
		}
	}

	override fun listContactsByOpeningDate(hcPartyId: String, startOpeningDate: Long, endOpeningDate: Long, offset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent> = flow {
		emitAll(contactDAO.listContactsByOpeningDate(hcPartyId, startOpeningDate, endOpeningDate, offset.toComplexKeyPaginationOffset()))
	}

	companion object {
		private val logger = LoggerFactory.getLogger(ContactLogicImpl::class.java)
	}
}

private fun <T> Flow<T>.bufferedChunksAtTransition(min: Int, max: Int, transition: (prev: T, cur: T) -> Boolean): Flow<List<T>> = channelFlow {
	require(min >= 2 && max >= 2 && max >= min) {
		"Min and max chunk sizes should be greater than 1, and max >= min"
	}
	val buffer = ArrayList<T>(max)
	collect {
		buffer += it
		if (buffer.size >= max) {
			var idx = buffer.size - 2
			while (idx >= 0 && !transition(buffer[idx], buffer[idx+1])) {
				idx--
			}
			if (idx >= 0) {
				if (idx == buffer.size - 2) {
					send(buffer.subList(0, idx + 1).toList())
					val kept = buffer[buffer.size - 1]
					buffer.clear()
					buffer += kept
				} else {
					//Slow branch
					send(buffer.subList(0, idx + 1).toList())
					val kept = buffer.subList(idx + 1, buffer.size).toList()
					buffer.clear()
					buffer += kept
				}
			} else {
				//Should we throw an exception ?
				send(buffer.toList())
				buffer.clear()
			}
		} else if (min <= buffer.size && transition(buffer[buffer.size - 2], buffer[buffer.size - 1])) {
			val offered = trySend(buffer.subList(0, buffer.size - 1).toList())
				.onClosed { throw it ?: ClosedSendChannelException("Channel was closed normally") }
				.isSuccess
			if (offered) {
				val kept = buffer[buffer.size - 1]
				buffer.clear()
				buffer += kept
			}
		}
	}
	if (buffer.size > 0) send(buffer.toList())
}.buffer(1)
