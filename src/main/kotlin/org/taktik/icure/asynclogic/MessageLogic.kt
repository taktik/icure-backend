/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import javax.security.auth.login.LoginException

interface MessageLogic : EntityPersister<Message, String> {
    fun findByFromAddress(partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    fun findByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<Any>>, reverse: Boolean?): Flow<ViewQueryResultEvent>
    fun findByTransportGuidReceived(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    fun findByTransportGuid(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    fun findByTransportGuidSentDate(partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>
    suspend fun addDelegation(messageId: String, delegation: Delegation): Message?

    @Throws(CreationException::class, LoginException::class)
    suspend fun createMessage(message: Message): Message?

    @Throws(LoginException::class)
    suspend fun get(messageId: String): Message?

    @Throws(MissingRequirementsException::class)
    suspend fun modifyMessage(message: Message): Message?

    @Throws(LoginException::class)
    fun listMessagesByHCPartySecretPatientKeys(secretPatientKeys: List<String>): Flow<Message>

    fun setStatus(messageIds: List<String>, status: Int): Flow<Message>
    fun setReadStatus(messageIds: List<String>, userId: String, status: Boolean, time: Long): Flow<Message>

    @Throws(LoginException::class)
    fun findForCurrentHcParty(paginationOffset: PaginationOffset<List<Any>>): Flow<ViewQueryResultEvent>

    suspend fun addDelegations(messageId: String, delegations: List<Delegation>): Message?
    fun getChildren(messageId: String): Flow<Message>
    fun getChildren(parentIds: List<String>): Flow<List<Message>>
    fun getByTransportGuids(hcpId: String, transportGuids: Set<String>): Flow<Message>
    fun listMessagesByInvoiceIds(ids: List<String>): Flow<Message>
    fun listMessagesByExternalRefs(hcPartyId: String, externalRefs: List<String>): Flow<Message>
    suspend fun solveConflicts()
}
