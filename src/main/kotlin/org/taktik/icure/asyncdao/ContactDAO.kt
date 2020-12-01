package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.ContactIdServiceId
import org.taktik.icure.entities.Contact
import java.net.URI

interface ContactDAO: GenericDAO<Contact> {
    suspend fun getContact(id: String): Contact?
    fun get(contactIds: Collection<String>): Flow<Contact>
    fun get(contactIds: Flow<String>): Flow<Contact>
    fun listContactsByOpeningDate(hcPartyId: String, startOpeningDate: Long?, endOpeningDate: Long?, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>
    fun listContacts(hcPartyId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun getPaginatedContacts(contactIds: Flow<String>): Flow<ViewQueryResultEvent>
    fun getPaginatedContacts(contactIds: Collection<String>): Flow<ViewQueryResultEvent>
    fun listContactIds(hcPartyId: String): Flow<String>
    fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact>
    fun findByHcPartyFormId(hcPartyId: String, formId: String): Flow<Contact>
    fun findByHcPartyFormIds(hcPartyId: String, ids: List<String>): Flow<Contact>
    fun findServiceIdsByIdQualifiedLink(ids: List<String>, linkType: String?): Flow<String>
    fun listServiceIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServiceIdsByPatientTag(hcPartyId: String, patientSecretForeignKeys: List<String>, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listServiceIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listContactIdsByTag(hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listContactIdsByCode(hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listCodesFrequencies(hcPartyId: String, codeType: String): Flow<Pair<ComplexKey, Long?>>
    fun findServicesByForeignKeys(hcPartyId: String, patientSecretForeignKeys: List<String>, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun findServicesByForeignKeys(hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String>
    fun listByServices(services: Collection<String>): Flow<Contact>
    fun listIdsByServices(services: Collection<String>): Flow<ContactIdServiceId>
    fun relink(cs: Flow<Contact>): Flow<Contact>

    fun listConflicts(): Flow<Contact>
}
