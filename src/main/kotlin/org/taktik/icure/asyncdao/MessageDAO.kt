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
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import java.net.URI

interface MessageDAO: GenericDAO<Message> {
    fun findByFromAddressActor(partyId: String, fromAddress: String, actorKeys: List<String>?): Flow<Message>
    fun findByToAddressActor(partyId: String, toAddress: String, actorKeys: List<String>?): Flow<Message>
    fun findByTransportGuidActor(partyId: String, transportGuid: String, actorKeys: List<String>?): Flow<Message>
    fun findByFromAddress(partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    fun findByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<Any>>, reverse: Boolean?): Flow<ViewQueryResultEvent>
    fun findByHcParty(partyId: String, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    fun findByTransportGuid(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    fun findByTransportGuidReceived(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>):Flow<ViewQueryResultEvent>
    fun findByTransportGuidSentDate(partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    fun findByHcPartyPatient(hcPartyId: String, secretPatientKeys: List<String>): Flow<Message>
    fun getChildren(messageId: String): Flow<Message>
    fun getByInvoiceIds(invoiceIds: Set<String>): Flow<Message>
    fun getByTransportGuids(hcPartyId: String, transportGuids: Collection<String>): Flow<Message>
    fun getByExternalRefs(hcPartyId: String, externalRefs: Set<String>): Flow<Message>
    fun listConflicts(): Flow<Message>
    fun getChildren(parentIds: List<String>): Flow<List<Message>>
}
