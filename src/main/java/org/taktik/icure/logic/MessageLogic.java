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

package org.taktik.icure.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Message;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;

import javax.security.auth.login.LoginException;

public interface MessageLogic extends EntityPersister<Message, String> {

	PaginatedList<Message> findByFromAddress(String partyId, String fromAddress, PaginationOffset<List<Object>> paginationOffset);

	PaginatedList<Message> findByToAddress(String partyId, String toAddress, PaginationOffset<List<Object>> paginationOffset, Boolean reverse);

	PaginatedList<Message> findByTransportGuidReceived(String partyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset);

	PaginatedList<Message> findByTransportGuid(String partyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset);

	PaginatedList<Message> findByTransportGuidSentDate(String partyId, String transportGuid, Long fromDate, Long toDate, PaginationOffset<List<Object>> paginationOffset);

	Message addDelegation(String messageId, Delegation delegation);

	Message createMessage(Message message) throws CreationException, LoginException;

	Message get(String messageId) throws LoginException;

	void modifyMessage(Message message) throws MissingRequirementsException;

	List<Message> listMessagesByHCPartySecretPatientKeys(List<String> secretPatientKeys) throws LoginException;

	List<Message> setStatus(List<String> messageIds, int status) ;

	PaginatedList<Message> findForCurrentHcParty(PaginationOffset paginationOffset) throws LoginException;

	Message addDelegations(String messageId, List<Delegation> delegations);

	List<Message> getChildren(String messageId);

	List<List<Message>> getChildren(List<String> parentIds);

	List<Message> getByTransportGuids(String hcpId, Set<String> transportGuids);

	List<Message> listMessagesByInvoiceIds(List<String> ids);

	List<Message> listMessagesByExternalRefs(String hcPartyId, List<String> externalRefs);

	void solveConflicts();

}
