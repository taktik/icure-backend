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
import kotlinx.coroutines.flow.*
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
import org.taktik.icure.exceptions.CreationException
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.exceptions.PersistenceException
import org.taktik.icure.services.external.rest.v1.dto.MessageReadStatus
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.firstOrNull
import java.util.*
import java.util.function.Consumer
import javax.security.auth.login.LoginException

@ExperimentalCoroutinesApi
@Service
class MessageLogicImpl(private val documentDAO: DocumentDAO, private val messageDAO: MessageDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Message, MessageDAO>(sessionLogic), MessageLogic {

    @Throws(LoginException::class)
    override fun listMessagesByHCPartySecretPatientKeys(secretPatientKeys: List<String>) = flow<Message> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.findByHcPartyPatient(dbInstanceUri, groupId, currentHealthCarPartyId(), secretPatientKeys))
    }

    @Throws(PersistenceException::class)
    override fun setStatus(messageIds: List<String>, status: Int) = flow<Message> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.save(dbInstanceUri, groupId, messageDAO.getList(dbInstanceUri, groupId, messageIds)
                .map {
                    it.status = if (it.status != null) it.status or status else status
                    it
                }.toList()))
    }

    @Throws(PersistenceException::class)
    override fun setReadStatus(messageIds: List<String>, userId: String, status: Boolean, time: Long) = flow<Message> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(
                messageDAO.save(dbInstanceUri, groupId, messageDAO.getList(dbInstanceUri, groupId, messageIds).map { m: Message ->
                    val readStatus = m.readStatus
                    if (readStatus[userId] == null || FuzzyValues.compare(readStatus[userId]!!.time, time) == -1) {
                        val userReadStatus = MessageReadStatus()
                        userReadStatus.read = status
                        userReadStatus.time = time
                        readStatus[userId] = userReadStatus
                    }
                    m
                }.toList())
        )
    }

    @Throws(LoginException::class)
    override fun findForCurrentHcParty(paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.findByHcParty(dbInstanceUri, groupId, currentHealthCarPartyId(), paginationOffset))
    }

    override fun findByFromAddress(partyId: String, fromAddress: String, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.findByFromAddress(dbInstanceUri, groupId, partyId, fromAddress, paginationOffset))
    }

    override fun findByToAddress(partyId: String, toAddress: String, paginationOffset: PaginationOffset<List<Any>>, reverse: Boolean?) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.findByToAddress(dbInstanceUri, groupId, partyId, toAddress, paginationOffset, reverse))
    }

    override fun findByTransportGuidReceived(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.findByTransportGuidReceived(dbInstanceUri, groupId, partyId, transportGuid, paginationOffset))
    }

    override fun findByTransportGuid(partyId: String, transportGuid: String?, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.findByTransportGuid(dbInstanceUri, groupId, partyId, transportGuid, paginationOffset))
    }

    override fun findByTransportGuidSentDate(partyId: String, transportGuid: String, fromDate: Long, toDate: Long, paginationOffset: PaginationOffset<List<Any>>) = flow<ViewQueryResultEvent> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.findByTransportGuidSentDate(dbInstanceUri, groupId, partyId, transportGuid, fromDate, toDate, paginationOffset))
    }

    override suspend fun addDelegation(messageId: String, delegation: Delegation): Message? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val message = messageDAO.get(dbInstanceUri, groupId, messageId)
        return message?.let {
            it.addDelegation(delegation.delegatedTo, delegation)
            messageDAO.save(dbInstanceUri, groupId, it)
        }
    }

    override suspend fun addDelegations(messageId: String, delegations: List<Delegation>): Message? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val message = messageDAO.get(dbInstanceUri, groupId, messageId)
        return message?.let {
            delegations.forEach(Consumer { d: Delegation -> message.addDelegation(d.delegatedTo, d) })
            messageDAO.save(dbInstanceUri, groupId, message)
        }
    }

    override fun getChildren(messageId: String) = flow<Message> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.getChildren(dbInstanceUri, groupId, messageId))
    }

    override fun getChildren(parentIds: List<String>) = flow<List<Message>> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.getChildren(dbInstanceUri, groupId, parentIds))
    }

    override fun getByTransportGuids(hcpId: String, transportGuids: Set<String>) = flow<Message> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.getByTransportGuids(dbInstanceUri, groupId, hcpId, transportGuids))
    }

    override fun listMessagesByInvoiceIds(ids: List<String>) = flow<Message> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.getByInvoiceIds(dbInstanceUri, groupId, ids.toSet()))
    }

    override fun listMessagesByExternalRefs(hcPartyId: String, externalRefs: List<String>) = flow<Message> {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(messageDAO.getByExternalRefs(dbInstanceUri, groupId, hcPartyId, externalRefs.toSet()))
    }

    fun createEntities(entities: Collection<Message>, createdEntities: Collection<Message>) = flow<Message> {
        val loggedUser = sessionLogic.getCurrentSessionContext().getUser()
        entities.map {
            if (it.fromAddress == null) {
                it.fromAddress = loggedUser?.email
            }
            if (it.fromHealthcarePartyId == null) {
                it.fromHealthcarePartyId = loggedUser?.healthcarePartyId
            }
            it
        }
        emitAll(super.createEntities(entities))
    }

    @Throws(CreationException::class, LoginException::class)
    override suspend fun createMessage(message: Message): Message? {
        val createdMessages: List<Message> = ArrayList(1)
        return createEntities(setOf(message), createdMessages).firstOrNull()
    }

    override suspend fun get(messageId: String): Message? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return messageDAO.get(dbInstanceUri, groupId, messageId)
    }

    @Throws(MissingRequirementsException::class)
    override suspend fun modifyMessage(message: Message) {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        messageDAO.save(dbInstanceUri, groupId, message)
    }

    override suspend fun solveConflicts() {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val messagesInConflict = messageDAO.listConflicts(dbInstanceUri, groupId)
                .map { messageDAO.get(dbInstanceUri, groupId, it.id, Option.CONFLICTS) }
                .filterNotNull()
                .onEach { msg ->
                    msg.conflicts.map { c ->
                        messageDAO.get(dbInstanceUri, groupId, msg.id, c)?.also { cp ->
                            msg.solveConflictWith(cp)
                            messageDAO.purge(dbInstanceUri, groupId, cp)
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
