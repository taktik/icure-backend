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

import javax.security.auth.login.LoginException
import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException

interface MessageLogic : EntityPersister<Message, String> {
    fun findMessagesByFromAddress(
        partyId: String,
        fromAddress: String,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByToAddress(
        partyId: String,
        toAddress: String,
        paginationOffset: PaginationOffset<List<Any>>,
        reverse: Boolean?
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuidReceived(
        partyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuid(
        partyId: String,
        transportGuid: String?,
        paginationOffset: PaginationOffset<List<*>>
    ): Flow<ViewQueryResultEvent>

    fun findMessagesByTransportGuidSentDate(
        partyId: String,
        transportGuid: String,
        fromDate: Long,
        toDate: Long,
        paginationOffset: PaginationOffset<List<Any>>
    ): Flow<ViewQueryResultEvent>

    suspend fun addDelegation(messageId: String, delegation: Delegation): Message?

    @Throws(CreationException::class, LoginException::class)
    suspend fun createMessage(message: Message): Message?

    @Throws(LoginException::class)
    suspend fun getMessage(messageId: String): Message?

    @Throws(MissingRequirementsException::class)
    suspend fun modifyMessage(message: Message): Message?

    @Throws(LoginException::class)
    fun listMessagesByHCPartySecretPatientKeys(secretPatientKeys: List<String>): Flow<Message>

    fun setStatus(messageIds: List<String>, status: Int): Flow<Message>
    fun setReadStatus(messageIds: List<String>, userId: String, status: Boolean, time: Long): Flow<Message>

    @Throws(LoginException::class)
    fun findForCurrentHcParty(paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>

    suspend fun addDelegations(messageId: String, delegations: List<Delegation>): Message?
    fun getMessageChildren(messageId: String): Flow<Message>
    fun getMessagesChildren(parentIds: List<String>): Flow<List<Message>>
    fun getMessagesByTransportGuids(hcpId: String, transportGuids: Set<String>): Flow<Message>
    fun listMessagesByInvoiceIds(ids: List<String>): Flow<Message>
    fun listMessagesByExternalRefs(hcPartyId: String, externalRefs: List<String>): Flow<Message>
    fun solveConflicts(): Flow<Message>
}
