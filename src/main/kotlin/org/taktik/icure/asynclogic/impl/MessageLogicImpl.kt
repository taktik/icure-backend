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
package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.MessageDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.dao.Option
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.MessageReadStatus
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.exceptions.PersistenceException
import org.taktik.icure.utils.firstOrNull
import java.util.*
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

    override fun findByFromAddress(partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByFromAddress(partyId, fromAddress, paginationOffset))
    }

    override fun findByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<Any>>, reverse: Boolean?) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByToAddress(partyId, toAddress, paginationOffset, reverse))
    }

    override fun findByTransportGuidReceived(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByTransportGuidReceived(partyId, transportGuid, paginationOffset))
    }

    override fun findByTransportGuid(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        emitAll(messageDAO.findByTransportGuid(partyId, transportGuid, paginationOffset))
    }

    override fun findByTransportGuidSentDate(partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
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

    override fun getChildren(messageId: String) = flow<Message> {
        emitAll(messageDAO.getChildren(messageId))
    }

    override fun getChildren(parentIds: List<String>) = flow<List<Message>> {
        emitAll(messageDAO.getChildren(parentIds))
    }

    override fun getByTransportGuids(hcpId: String, transportGuids: Set<String>) = flow<Message> {
        emitAll(messageDAO.getByTransportGuids(hcpId, transportGuids))
    }

    override fun listMessagesByInvoiceIds(ids: List<String>) = flow<Message> {
        emitAll(messageDAO.getByInvoiceIds(ids.toSet()))
    }

    override fun listMessagesByExternalRefs(hcPartyId: String, externalRefs: List<String>) = flow<Message> {
        emitAll(messageDAO.getByExternalRefs(hcPartyId, externalRefs.toSet()))
    }

    fun createEntities(entities: Collection<Message>, createdEntities: Collection<Message>) = flow {
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
        createEntities(setOf(message), createdMessages).firstOrNull()
    }

    override suspend fun get(messageId: String): Message? {
        return messageDAO.get(messageId)
    }

    @Throws(MissingRequirementsException::class)
    override suspend fun modifyMessage(message: Message) = fix(message) { message ->
        messageDAO.save(message)
    }

    override suspend fun solveConflicts() {
        val messagesInConflict = messageDAO.listConflicts()
                .map { it.id?.let { it1 -> messageDAO.get(it1, Option.CONFLICTS) } }
                .filterNotNull()
                .onEach { msg ->
                    msg.conflicts?.map { c ->
                        msg.id?.let {
                            messageDAO.get(it, c)?.also { cp ->
                                msg.solveConflictsWith(cp)
                                messageDAO.purge(cp)
                            }
                        }
                    }
                }
                .collect()
    }

    override fun getGenericDAO(): MessageDAO {
        return messageDAO
    }

    private suspend fun currentHealthCarPartyId(): String = sessionLogic.getCurrentHealthcarePartyId()
            ?: throw LoginException("You must be logged to perform this action")

    companion object {
        private val logger = LoggerFactory.getLogger(MessageLogicImpl::class.java)
    }
}
