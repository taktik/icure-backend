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
import org.taktik.icure.domain.ContactIdServiceId
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service

interface ContactDAO: GenericDAO<Contact> {
    suspend fun getContact(id: String): Contact?
    fun getContacts(contactIds: Collection<String>): Flow<Contact>
    fun getContacts(contactIds: Flow<String>): Flow<Contact>
    fun listContactsByOpeningDate(hcPartyId: String, startOpeningDate: Long?, endOpeningDate: Long?, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>
    fun findContactsByHcParty(hcPartyId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findContactsByIds(contactIds: Flow<String>): Flow<ViewQueryResultEvent>
    fun findContactsByIds(contactIds: Collection<String>): Flow<ViewQueryResultEvent>
    fun listContactIdsByHealthcareParty(hcPartyId: String): Flow<String>
    fun listContactsByHcPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact>
    fun listContactIdsByHcPartyAndPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<String>
    fun listContactsByHcPartyAndFormId(hcPartyId: String, formId: String): Flow<Contact>
    fun listContactsByHcPartyAndFormIds(hcPartyId: String, ids: List<String>): Flow<Contact>
    fun findServiceIdsByIdQualifiedLink(ids: List<String>, linkType: String?): Flow<String>
    fun listServiceIdsByHcParty(hcPartyId: String): Flow<String>
    fun listServiceIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServiceIdsByPatientAndTag(hcPartyId: String, patientSecretForeignKeys: List<String>, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServiceIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listContactIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServiceIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>): Flow<String>
    fun listServiceIdsByHcPartyHealthElementIds(hcPartyId: String, healthElementIds: List<String>): Flow<String>
    fun listContactIdsByHcPartyAndIdentifiers(hcPartyId: String, identifiers: List<Identifier>): Flow<String>
    fun listContactIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listCodesFrequencies(hcPartyId: String, codeType: String): Flow<Pair<ComplexKey, Long?>>
    fun listServicesIdsByPatientForeignKeys(hcPartyId: String, patientSecretForeignKeys: List<String>, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServicesIdsByPatientForeignKeys(hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String>
    fun listContactsByServices(services: Collection<String>): Flow<Contact>
    fun listIdsByServices(services: Collection<String>): Flow<ContactIdServiceId>
    fun relink(cs: Flow<Contact>): Flow<Contact>
    fun findContactsByExternalId(externalId: String): Flow<Contact>
    fun findServiceIdsByAssociationId(associationId: String): Flow<Service>
    fun findContactsByHcPartyServiceId(hcPartyId: String, formId: String): Flow<Contact>


    fun listConflicts(): Flow<Contact>
}
