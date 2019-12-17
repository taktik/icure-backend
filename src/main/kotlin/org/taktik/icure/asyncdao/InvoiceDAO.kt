package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.ektorp.ComplexKey
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowNoDoc
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.MediumType
import java.net.URI

interface InvoiceDAO: GenericDAO<Invoice> {
    fun findByHcParty(dbInstanceUrl: URI, groupId: String, hcParty: String, fromDate: Long?, toDate: Long?, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    suspend fun listByHcPartyContacts(dbInstanceUrl: URI, groupId: String, hcParty: String, contactId: Set<String>): Flow<Invoice>

    fun listByHcPartyReferences(dbInstanceUrl: URI, groupId: String, hcParty: String, invoiceReferences: Set<String>?): Flow<Invoice>

    fun listByHcPartyReferences(dbInstanceUrl: URI, groupId: String, hcParty: String, from: String?, to: String?, descending: Boolean, limit: Int): Flow<Invoice>

    fun listByHcPartyGroupId(dbInstanceUrl: URI, groupId: String, hcParty: String): Flow<Invoice>

    fun listByHcPartyRecipientIds(dbInstanceUrl: URI, groupId: String, hcParty: String, recipientIds: Set<String?>): Flow<Invoice>

    fun listByHcPartyPatientFk(dbInstanceUrl: URI, groupId: String, hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice>

    fun listByHcPartyRecipientIdsUnsent(dbInstanceUrl: URI, groupId: String, hcParty: String, recipientIds: Set<String?>): Flow<Invoice>

    fun listByHcPartyPatientFkUnsent(dbInstanceUrl: URI, groupId: String, hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice>

    fun listByHcPartySentMediumTypeInvoiceTypeSentDate(dbInstanceUrl: URI, groupId: String, hcParty: String, sentMediumType: MediumType, invoiceType: InvoiceType, sent: Boolean, fromDate: Long?, toDate: Long?): Flow<Invoice>

    fun listByHcPartySendingModeStatus(dbInstanceUrl: URI, groupId: String, hcParty: String, sendingMode: String?, status: String?, fromDate: Long?, toDate: Long?): Flow<Invoice>

    fun listByServiceIds(dbInstanceUrl: URI, groupId: String, serviceIds: Set<String>): Flow<Invoice>

    suspend fun listAllHcpsByStatus(dbInstanceUrl: URI, groupId: String, status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice>

    fun listConflicts(dbInstanceUrl: URI, groupId: String): Flow<Invoice>

    fun listIdsByTarificationsByCode(dbInstanceUrl: URI, groupId: String, hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

    fun listInvoiceIdsByTarificationsByCode(dbInstanceUrl: URI, groupId: String, hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listTarificationsFrequencies(dbInstanceUrl: URI, groupId: String, hcPartyId: String): Flow<ViewRowNoDoc<ComplexKey, Long>>
}
