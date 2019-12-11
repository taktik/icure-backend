package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.dao.impl.ektorp.CouchKeyValue
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Contact
import java.net.URI

interface ContactDAO: GenericDAO<Contact> {
    suspend fun getContact(dbInstanceUrl: URI, groupId: String, id: String): Contact?
    fun get(dbInstanceUrl: URI, groupId: String, contactIds: Collection<String>): Flow<Contact>

    fun listContactsByOpeningDate(dbInstanceUrl: URI, groupId: String, hcPartyId: String, startOpeningDate: Long?, endOpeningDate: Long?, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    fun listContacts(dbInstanceUrl: URI, groupId: String, hcPartyId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>

    fun getPaginatedContacts(dbInstanceUrl: URI, groupId: String, contactIds: Collection<String>, pagination: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>

    fun listContactIds(dbInstanceUrl: URI, groupId: String, hcPartyId: String): Flow<String>

    fun findByHcPartyPatient(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact>

    fun findByHcPartyFormId(dbInstanceUrl: URI, groupId: String, hcPartyId: String, formId: String): Flow<Contact>

    suspend fun findByHcPartyFormIds(dbInstanceUrl: URI, groupId: String, hcPartyId: String, ids: List<String>): Flow<Contact>

    fun listServiceIdsByTag(dbInstanceUrl: URI, groupId: String, hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

    fun listServiceIdsByPatientTag(dbInstanceUrl: URI, groupId: String, hcPartyId: String, patientSecretForeignKeys: List<String>, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

    fun listServiceIdsByCode(dbInstanceUrl: URI, groupId: String, hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

    fun listCodesFrequencies(dbInstanceUrl: URI, groupId: String, hcPartyId: String, codeType: String): Flow<CouchKeyValue<Long?>>

    fun findServicesByForeignKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, patientSecretForeignKeys: List<String>, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

    fun findServicesByForeignKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String>

    fun findByServices(dbInstanceUrl: URI, groupId: String, services: Collection<String>): Flow<String>

    suspend fun listByServices(dbInstanceUrl: URI, groupId: String, services: Collection<String>): Flow<Contact>
    fun listIdsByServices(dbInstanceUrl: URI, groupId: String, services: Collection<String>): Flow<String>
    fun relink(cs: Flow<Contact>): Flow<Contact>

    fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Contact>
}
