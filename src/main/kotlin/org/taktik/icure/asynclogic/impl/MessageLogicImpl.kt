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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.Option
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.MessageReadStatus
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.exceptions.PersistenceException
import org.taktik.icure.utils.firstOrNull
import javax.security.auth.login.LoginException

@ExperimentalCoroutinesApi
@Service
class MessageLogicImpl(private val documentDAO: DocumentDAO, private val messageDAO: MessageDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Message, MessageDAO>(sessionLogic), MessageLogic {

    @Throws(LoginException::class)
    override fun listMessagesByHCPartySecretPatientKeys(secretPatientKeys: List<String>) = flow<Message> {
        emitAll(messageDAO.findByHcPartyPatient(currentHealthCarPartyId(), secretPatientKeys))
    }

    @Throws(PersistenceException::class)
    override fun setStatus(messageIds: List<String>, status: Int) = flow<Message> {
        emitAll(messageDAO.save(messageDAO.getList(messageIds)
                .map {
                    it.copy(status = status or (it.status ?: 0))
                }.toList()))
    }

    @Throws(PersistenceException::class)
    override fun setReadStatus(messageIds: List<String>, userId: String, status: Boolean, time: Long) = flow<Message> {
        emitAll(
            messageDAO.save(messageDAO.getList(messageIds).map { m: Message ->
                if ((m.readStatus[userId]?.time ?: 0) < time) m.copy(readStatus = m.readStatus + (userId to MessageReadStatus(
                        read = status,
                        time = time
                ))) else m
            }.toList())
        )
    }

    @Throws(LoginException::class)
    override fun findForCurrentHcParty(paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByHcParty(currentHealthCarPartyId(), paginationOffset))
    }

    override fun findMessagesByFromAddress(partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByFromAddress(partyId, fromAddress, paginationOffset))
    }

    override fun findMessagesByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<Any>>, reverse: Boolean?) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByToAddress(partyId, toAddress, paginationOffset, reverse))
    }

    override fun findMessagesByTransportGuidReceived(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByTransportGuidReceived(partyId, transportGuid, paginationOffset))
    }

    override fun findMessagesByTransportGuid(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByTransportGuid(partyId, transportGuid, paginationOffset))
    }

    override fun findMessagesByTransportGuidSentDate(partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByTransportGuidSentDate(partyId, transportGuid, fromDate, toDate, paginationOffset))
    }

    override suspend fun addDelegation(messageId: String, delegation: Delegation): Message? {
        val message = messageDAO.get(messageId)
        return delegation.delegatedTo?.let { healthcarePartyId ->
            message?.let { c -> messageDAO.save(c.copy(delegations = c.delegations + mapOf(
                    healthcarePartyId to setOf(delegation)
            )))}
        } ?: message
    }

    override suspend fun addDelegations(messageId: String, delegations: List<Delegation>): Message? {
        val message = messageDAO.get(messageId)
        return message?.let {
            return messageDAO.save(it.copy(
                    delegations = it.delegations +
                            delegations.mapNotNull { d -> d.delegatedTo?.let { delegateTo -> delegateTo to setOf(d) } }
            ))
        }
    }

    override fun getMessageChildren(messageId: String) = flow<Message> {
        emitAll(messageDAO.getChildren(messageId))
    }

    override fun getMessagesChildren(parentIds: List<String>) = flow<List<Message>> {
        emitAll(messageDAO.getChildren(parentIds))
    }

    override fun getMessagesByTransportGuids(hcpId: String, transportGuids: Set<String>) = flow<Message> {
        emitAll(messageDAO.getByTransportGuids(hcpId, transportGuids))
    }

    override fun listMessagesByInvoiceIds(ids: List<String>) = flow<Message> {
        emitAll(messageDAO.getByInvoiceIds(ids.toSet()))
    }

    override fun listMessagesByExternalRefs(hcPartyId: String, externalRefs: List<String>) = flow<Message> {
        emitAll(messageDAO.getByExternalRefs(hcPartyId, externalRefs.toSet()))
    }

    fun createMessages(entities: Collection<Message>, createdEntities: Collection<Message>) = flow {
        val loggedUser = sessionLogic.getCurrentSessionContext().getUser()

        emitAll(super.createEntities(entities.map {
            if (it.fromAddress == null || it.fromHealthcarePartyId == null)
                it.copy(
                        fromAddress = it.fromAddress ?: loggedUser.email,
                        fromHealthcarePartyId = it.fromHealthcarePartyId ?: loggedUser.healthcarePartyId
                )
            else it
        }))
    }

    @Throws(CreationException::class, LoginException::class)
    override suspend fun createMessage(message: Message) = fix(message) { message ->
        val createdMessages: List<Message> = ArrayList(1)
        createMessages(setOf(message), createdMessages).firstOrNull()
    }

    override suspend fun getMessage(messageId: String): Message? {
        return messageDAO.get(messageId)
    }

    @Throws(MissingRequirementsException::class)
    override suspend fun modifyMessage(message: Message) = fix(message) { message ->
        messageDAO.save(message)
    }

    override fun solveConflicts(): Flow<Message> =
            messageDAO.listConflicts().mapNotNull { messageDAO.get(it.id, Option.CONFLICTS)?.let { message ->
                message.conflicts?.mapNotNull { conflictingRevision -> messageDAO.get(message.id, conflictingRevision) }
                        ?.fold(message) { kept, conflict -> kept.merge(conflict).also { messageDAO.purge(conflict) } }
                        ?.let { mergedMessage -> messageDAO.save(mergedMessage) }
            } }

    override fun getGenericDAO(): MessageDAO {
        return messageDAO
    }

    private suspend fun currentHealthCarPartyId(): String = sessionLogic.getCurrentHealthcarePartyId()
            ?: throw LoginException("You must be logged to perform this action")

    companion object {
        private val logger = LoggerFactory.getLogger(MessageLogicImpl::class.java)
    }
}
