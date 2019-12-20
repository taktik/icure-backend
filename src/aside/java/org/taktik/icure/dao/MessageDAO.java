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

package org.taktik.icure.dao;

import org.ektorp.support.View;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Message;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface MessageDAO extends GenericDAO<Message> {

	List<Message> findByFromAddressActor(String partyId, String fromAddress, List<String> actorKeys);

	List<Message> findByToAddressActor(String partyId, String toAddress, List<String> actorKeys);

	List<Message> findByTransportGuidActor(String partyId, String transportGuid, List<String> actorKeys);

	PaginatedList<Message> findByFromAddress(String partyId, String fromAddress, PaginationOffset<List<Object>> paginationOffset);

	PaginatedList<Message> findByToAddress(String partyId, String toAddress, PaginationOffset<List<Object>> paginationOffset, Boolean reverse);

	PaginatedList<Message> findByHcParty(String healthcarePartyId, PaginationOffset<List<Object>> paginationOffset);

	PaginatedList<Message> findByTransportGuid(String healthcarePartyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset);

	PaginatedList<Message> findByTransportGuidReceived(String healthcarePartyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset);

	PaginatedList<Message> findByTransportGuidSentDate(String healthcarePartyId, String transportGuid, Long fromDate, Long toDate, PaginationOffset<List<Object>> paginationOffset);

	List<Message> findByHcPartyPatient(String hcPartyId, List<String> secretPatientKeys);

	List<Message> getChildren(String messageId);

	List<Message> getByInvoiceIds(Set<String> invoiceIds);

	List<Message> getByTransportGuids(String hcPartyId, Collection<String> transportGuids);

	List<Message> getByExternalRefs(String hcPartyId, HashSet<String> strings);

	List<Message> listConflicts();

	List<List<Message>> getChildren(List<String> parentIds);
}
