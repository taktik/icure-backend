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

package org.taktik.icure.logic.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.DocumentDAO;
import org.taktik.icure.dao.MessageDAO;
import org.taktik.icure.dao.Option;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Message;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.MessageLogic;
import org.taktik.icure.validation.aspect.Check;

import javax.security.auth.login.LoginException;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageLogicImpl extends GenericLogicImpl<Message, MessageDAO> implements MessageLogic {
	private static Logger logger = LoggerFactory.getLogger(MessageLogicImpl.class);

	private DocumentDAO documentDAO;
	private MessageDAO messageDAO;
	private ICureSessionLogic sessionLogic;

	@Override
	public List<Message> listMessagesByHCPartySecretPatientKeys(List<String> secretPatientKeys) throws LoginException {
		User loggedUser = getLoggedHealthCarePartyUser();
		return messageDAO.findByHcPartyPatient(loggedUser.getHealthcarePartyId(), secretPatientKeys);
	}

	@Override
	public List<Message> setStatus(List<String> messageIds, int status) {
		return messageDAO.save(messageDAO.getList(messageIds).stream().map(m -> {
			m.setStatus(m.getStatus() != null ? (m.getStatus() | status) : status); return m;
		}).collect(Collectors.toList()));
	}


	@Override
	public PaginatedList<Message> findForCurrentHcParty(PaginationOffset paginationOffset) throws LoginException {
		User loggedUser = getLoggedHealthCarePartyUser();
		return messageDAO.findByHcParty(loggedUser.getHealthcarePartyId(), paginationOffset);
	}

	@Override
	public PaginatedList<Message> findByFromAddress(String partyId, String fromAddress, PaginationOffset<List<Object>> paginationOffset) {
		return messageDAO.findByFromAddress(partyId,fromAddress,paginationOffset);
	}

	@Override
	public PaginatedList<Message> findByToAddress(String partyId, String toAddress, PaginationOffset<List<Object>> paginationOffset, Boolean reverse) {
		return messageDAO.findByToAddress(partyId, toAddress, paginationOffset, reverse);
	}

	@Override
	public PaginatedList<Message> findByTransportGuidReceived(String partyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset) {
		return messageDAO.findByTransportGuidReceived(partyId, transportGuid, paginationOffset);
	}

	@Override
	public PaginatedList<Message> findByTransportGuid(String partyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset) {
		return messageDAO.findByTransportGuid(partyId, transportGuid, paginationOffset);
	}

	@Override
	public PaginatedList<Message> findByTransportGuidSentDate(String partyId, String transportGuid, Long fromDate, Long toDate, PaginationOffset<List<Object>> paginationOffset) {
		return messageDAO.findByTransportGuidSentDate(partyId, transportGuid, fromDate, toDate, paginationOffset);
	}

	@Override
	public Message addDelegation(String messageId, Delegation delegation) {
		Message message = messageDAO.get(messageId);
		message.addDelegation(delegation.getDelegatedTo(), delegation);
		return messageDAO.save(message);
	}

	@Override
	public Message addDelegations(String messageId, List<Delegation> delegations) {
		Message message = messageDAO.get(messageId);
		delegations.forEach(d->message.addDelegation(d.getDelegatedTo(),d));
		return messageDAO.save(message);
	}

	@Override
	public List<Message> getChildren(String messageId) {
		return messageDAO.getChildren(messageId);
	}

	@Override
	public List<List<Message>> getChildren(List<String> parentIds) {
		return messageDAO.getChildren(parentIds);
	}

	@Override
	public List<Message> getByTransportGuids(String hcpId, Set<String> transportGuids) {
		return messageDAO.getByTransportGuids(hcpId, transportGuids);
	}

	@Override
	public List<Message> listMessagesByInvoiceIds(List<String> ids) {
		return messageDAO.getByInvoiceIds(new HashSet<>(ids));
	}

	@Override
	public List<Message> listMessagesByExternalRefs(String hcPartyId, List<String> externalRefs) {
		return messageDAO.getByExternalRefs(hcPartyId, new HashSet<String>(externalRefs));
	}

	@Override
	public boolean createEntities(Collection<Message> entities, Collection<Message> createdEntities) throws Exception {
		User loggedUser = getLoggedHealthCarePartyUser();
		boolean success = true;
		for (Message message : entities) {
			if (message == null) {
				logger.error("Cannot create 'null' message. ");
			} else {
				if (message.getFromAddress()==null) { message.setFromAddress(loggedUser.getEmail()); }
				if (message.getFromHealthcarePartyId()==null) { message.setFromHealthcarePartyId(loggedUser.getHealthcarePartyId()); }

				success = success && super.createEntities(Collections.singletonList(message), createdEntities);
			}
		}
		return success;
	}

	@Override
	public Message createMessage(@Check @NotNull Message message) throws CreationException, LoginException {
		if (message == null) {
			throw new CreationException("Cannot create 'null' message. ");
		}

		List<Message> createdMessages = new ArrayList<>(1);
		try {
			createEntities(Collections.singleton(message), createdMessages);

		} catch (Exception e) {
			throw new CreationException("Could not create message. ", e);
		}
		return createdMessages.size() > 0 ? createdMessages.get(0) : null;
	}

	@Override
	public Message get(String messageId) {
		return messageDAO.get(messageId);
	}

	@Override
	public void modifyMessage(@Check @NotNull Message message) throws MissingRequirementsException {
		messageDAO.save(message);
	}

	@Override
	public void solveConflicts() {
		List<Message> messagesInConflict = messageDAO.listConflicts().stream().map(it -> messageDAO.get(it.getId(), Option.CONFLICTS)).collect(Collectors.toList());

		messagesInConflict.forEach(msg -> {
			Arrays.stream(msg.getConflicts()).map(c -> messageDAO.get(msg.getId(), c)).forEach(cp -> {
				msg.solveConflictWith(cp);
				messageDAO.purge(cp);
			});
			messageDAO.save(msg);
		});
	}


	@Autowired
	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	@Autowired
	public void setMessageDAO(MessageDAO messageDAO) {
		this.messageDAO = messageDAO;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	protected MessageDAO getGenericDAO() {
		return messageDAO;
	}

	private User getLoggedHealthCarePartyUser() throws LoginException {
		User user = sessionLogic.getCurrentSessionContext().getUser();
		if (user == null || user.getHealthcarePartyId() == null) {
			throw new LoginException("You must be logged to perform this action");
		}
		return user;
	}


}
