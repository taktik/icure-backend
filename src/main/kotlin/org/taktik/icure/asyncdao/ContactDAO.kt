package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.ComplexKey
import org.ektorp.support.View
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.dao.impl.ektorp.CouchKeyValue
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Contact
import java.net.URI

interface ContactDAO {
    suspend fun getContact(dbInstanceUrl: URI, groupId: String, id: String): Contact?
    fun get(dbInstanceUrl: URI, groupId: String, contactIds: Collection<String>): Flow<Contact>

    @View(name = "by_hcparty_openingdate", map = "classpath:js/contact/By_hcparty_openingdate.js")
    fun listContactsByOpeningDate(dbInstanceUrl: URI, groupId: String, hcPartyId: String, startOpeningDate: Long?, endOpeningDate: Long?, pagination: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    @View(name = "by_hcparty", map = "classpath:js/contact/By_hcparty.js")
    fun listContacts(dbInstanceUrl: URI, groupId: String, hcPartyId: String, pagination: PaginationOffset<String>): Flow<ViewQueryResultEvent>

    fun listContactIds(dbInstanceUrl: URI, groupId: String, hcPartyId: String): Flow<String?>

    @View(name = "by_hcparty_patientfk", map = "classpath:js/contact/By_hcparty_patientfk_map.js")
    fun findByHcPartyPatient(dbInstanceUrl: URI, groupId: String, hcPartyId: String, secretPatientKeys: List<String>): Flow<Contact>

    @View(name = "by_hcparty_formid", map = "classpath:js/contact/By_hcparty_formid_map.js")
    fun findByHcPartyFormId(dbInstanceUrl: URI, groupId: String, hcPartyId: String, formId: String): Flow<Contact>

    suspend fun findByHcPartyFormIds(dbInstanceUrl: URI, groupId: String, hcPartyId: String, ids: List<String>): Flow<Contact?>

    @View(name = "service_by_hcparty_tag", map = "classpath:js/contact/Service_by_hcparty_tag.js")
    fun listServiceIdsByTag(dbInstanceUrl: URI, groupId: String, hcPartyId: String, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String?>

    @View(name = "service_by_hcparty_patient_tag", map = "classpath:js/contact/Service_by_hcparty_patient_tag.js")
    fun listServiceIdsByPatientTag(dbInstanceUrl: URI, groupId: String, hcPartyId: String, patientSecretForeignKeys: List<String>, tagType: String?, tagCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String?>

    @View(name = "service_by_hcparty_code", map = "classpath:js/contact/Service_by_hcparty_code.js", reduce = "_count")
    fun listServiceIdsByCode(dbInstanceUrl: URI, groupId: String, hcPartyId: String, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String?>

    fun listCodesFrequencies(dbInstanceUrl: URI, groupId: String, hcPartyId: String, codeType: String): Flow<CouchKeyValue<Long?>>

    @View(name = "service_by_hcparty_patient_code", map = "classpath:js/contact/Service_by_hcparty_patient_code.js")
    fun findServicesByForeignKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, patientSecretForeignKeys: List<String>, codeType: String?, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String?>

    fun findServicesByForeignKeys(dbInstanceUrl: URI, groupId: String, hcPartyId: String, patientSecretForeignKeys: Set<String>): Flow<String>

    @View(name = "by_service", map = "classpath:js/contact/By_service.js")
    fun findByServices(dbInstanceUrl: URI, groupId: String, services: Collection<String>): Flow<String?>

    suspend fun listByServices(dbInstanceUrl: URI, groupId: String, services: Collection<String>): Flow<Contact>
    fun listIdsByServices(dbInstanceUrl: URI, groupId: String, services: Collection<String>): Flow<String>
    fun relink(cs: Flow<Contact>): Flow<Contact>

    @View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Contact' && !doc.deleted && doc._conflicts) emit(doc._id )}")
    fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Contact>
}
