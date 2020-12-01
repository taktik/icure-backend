package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.ViewRowNoDoc
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.MediumType
import java.net.URI

interface InvoiceDAO: GenericDAO<Invoice> {
    fun findByHcParty(hcParty: String, fromDate: Long?, toDate: Long?, paginationOffset: PaginationOffset<ComplexKey>): Flow<ViewQueryResultEvent>

    fun listByHcPartyContacts(hcParty: String, contactId: Set<String>): Flow<Invoice>

    fun listByHcPartyReferences(hcParty: String, invoiceReferences: Set<String>?): Flow<Invoice>

    fun listByHcPartyReferences(hcParty: String, from: String?, to: String?, descending: Boolean, limit: Int): Flow<Invoice>

    fun listByHcPartyGroupId(inputGroupId: String, hcParty: String): Flow<Invoice>

    fun listByHcPartyRecipientIds(hcParty: String, recipientIds: Set<String?>): Flow<Invoice>

    fun listByHcPartyPatientFk(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice>

    fun listByHcPartyRecipientIdsUnsent(hcParty: String, recipientIds: Set<String?>): Flow<Invoice>

    fun listByHcPartyPatientFkUnsent(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice>

    fun listByHcPartySentMediumTypeInvoiceTypeSentDate(hcParty: String, sentMediumType: MediumType, invoiceType: InvoiceType, sent: Boolean, fromDate: Long?, toDate: Long?): Flow<Invoice>

    fun listByHcPartySendingModeStatus(hcParty: String, sendingMode: String?, status: String?, fromDate: Long?, toDate: Long?): Flow<Invoice>

    fun listByServiceIds(serviceIds: Set<String>): Flow<Invoice>

    fun listAllHcpsByStatus(status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice>

    fun listConflicts(): Flow<Invoice>

    fun listIdsByTarificationsByCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

    fun listInvoiceIdsByTarificationsByCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>
    fun listTarificationsFrequencies(hcPartyId: String): Flow<ViewRowNoDoc<ComplexKey, Long>>
}
