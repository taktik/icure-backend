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

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.ContactDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.dto.data.LabelledOccurence
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Service

interface ContactLogic : EntityPersister<Contact, String> {
    suspend fun getContact(id: String): Contact?
    fun getContacts(selectedIds: Collection<String>): Flow<Contact>
        fun findContactsByIds(selectedIds: Collection<String>): Flow<ViewQueryResultEvent>
    fun listContactsByHCPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact>

    suspend fun addDelegation(contactId: String, delegation: Delegation): Contact?

    suspend fun createContact(contact: Contact): Contact?
    fun deleteContacts(ids: Set<String>): Flow<DocIdentifier>

    suspend fun modifyContact(contact: Contact): Contact?
    fun getServices(selectedServiceIds: Collection<String>): Flow<Service>
    fun getServicesLinkedTo(ids: List<String>, linkType: String?): Flow<Service>

    fun pimpServiceWithContactInformation(s: Service, c: Contact): Service
    fun listServiceIdsByTag(hcPartyId: String, patientSecretForeignKeys: List<String>?, tagType: String, tagCode: String, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServiceIdsByCode(hcPartyId: String, patientSecretForeignKeys: List<String>?, codeType: String, codeCode: String, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listContactIdsByTag(hcPartyId: String, tagType: String, tagCode: String, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServiceIdsByIdentifier(hcPartyId: String, identifierSystem: String, identifierValue: String): Flow<String>
    fun listContactIdsByCode(hcPartyId: String, codeType: String, codeCode: String, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listContactIds(hcPartyId: String): Flow<String>
    fun listIdsByServices(services: Collection<String>): Flow<String>
    fun listServicesByHcPartyAndSecretForeignKeys(hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String>
    fun listContactsByHcPartyAndFormId(hcPartyId: String, formId: String): Flow<Contact>
    fun listContactsByHCPartyServiceId(hcPartyId: String, formId: String): Flow<Contact>
    fun listContactsByExternalId(externalId: String): Flow<Contact>
    fun listServicesByAssociationId(associationId: String): Flow<Service>


    suspend fun getServiceCodesOccurences(hcPartyId: String, codeType: String, minOccurences: Long): List<LabelledOccurence>
    fun listContactsByHcPartyAndFormIds(hcPartyId: String, ids: List<String>): Flow<Contact>
    fun getGenericDAO(): ContactDAO
    fun filterContacts(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<Contact>): Flow<ViewQueryResultEvent>
    fun filterServices(paginationOffset: PaginationOffset<Nothing>, filter: FilterChain<Service>): Flow<Service>

    fun solveConflicts(): Flow<Contact>
    fun listContactsByOpeningDate(hcPartyId: String, startOpeningDate: Long, endOpeningDate: Long, offset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    suspend fun addDelegations(contactId: String, delegations: List<Delegation>): Contact?
}
