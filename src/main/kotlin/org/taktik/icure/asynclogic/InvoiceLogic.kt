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
import org.taktik.icure.asyncdao.InvoiceDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.dto.data.LabelledOccurence
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.MediumType

interface InvoiceLogic : EntityPersister<Invoice, String> {
    suspend fun createInvoice(invoice: Invoice): Invoice?
    suspend fun deleteInvoice(invoiceId: String): DocIdentifier?

    suspend fun getInvoice(invoiceId: String): Invoice?
    fun getInvoices(ids: List<String>): Flow<Invoice>

    suspend fun modifyInvoice(invoice: Invoice): Invoice?
    fun updateInvoices(invoices: List<Invoice>): Flow<Invoice>

    suspend fun addDelegation(invoiceId: String, delegation: Delegation): Invoice?
    fun findByAuthor(hcPartyId: String, fromDate: Long?, toDate: Long?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun listByHcPartyContacts(hcParty: String, contactIds: Set<String>): Flow<Invoice>
    fun listByHcPartyRecipientIds(hcParty: String, recipientIds: Set<String?>): Flow<Invoice>
    fun listByHcPartyPatientSks(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice>
    fun listByHcPartySentMediumTypeInvoiceTypeSentDate(hcParty: String, sentMediumType: MediumType, invoiceType: InvoiceType, sent: Boolean, fromDate: Long?, toDate: Long?): Flow<Invoice>
    fun listByHcPartySendingModeStatus(hcParty: String, sendingMode: String?, status: String?, fromDate: Long?, toDate: Long?): Flow<Invoice>
    fun listByHcPartyGroupId(hcParty: String, inputGroupId: String): Flow<Invoice>
    fun listByHcPartyRecipientIdsUnsent(hcParty: String, recipientIds: Set<String?>): Flow<Invoice>
    fun listByHcPartyPatientSksUnsent(hcParty: String, secretPatientKeys: Set<String>): Flow<Invoice>
    fun listByServiceIds(serviceIds: Set<String>): Flow<Invoice>

    suspend fun mergeInvoices(hcParty: String, invoices: List<Invoice>, destination: Invoice?): Invoice?

    suspend fun validateInvoice(hcParty: String, invoice: Invoice?, refScheme: String, forcedValue: String?): Invoice?
    fun appendCodes(hcPartyId: String, userId: String, insuranceId: String?, secretPatientKeys: Set<String>, type: InvoiceType, sentMediumType: MediumType, invoicingCodes: List<InvoicingCode>, invoiceId: String?, invoiceGraceTime: Int?): Flow<Invoice>

    suspend fun addDelegations(invoiceId: String, delegations: List<Delegation>): Invoice?
    fun removeCodes(userId: String, secretPatientKeys: Set<String>, serviceId: String, tarificationIds: List<String>): Flow<Invoice>
    fun listAllHcpsByStatus(status: String, from: Long?, to: Long?, hcpIds: List<String>): Flow<Invoice>

    fun solveConflicts(): Flow<Invoice>

    suspend fun getTarificationsCodesOccurences(hcPartyId: String, minOccurences: Long): List<LabelledOccurence>
    fun listIdsByTarificationsByCode(hcPartyId: String, codeCode: String, startValueDate: Long, endValueDate: Long): Flow<String>
    fun listInvoiceIdsByTarificationsByCode(hcPartyId: String, codeCode: String?, startValueDate: Long?, endValueDate: Long?): Flow<String>

    fun filter(filter: FilterChain<Invoice>): Flow<Invoice>
    fun getGenericDAO(): InvoiceDAO
}
