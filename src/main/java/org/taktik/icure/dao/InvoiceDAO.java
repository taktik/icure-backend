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

import java.util.List;
import java.util.Set;

import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.taktik.icure.dao.impl.ektorp.CouchKeyValue;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.embed.InvoiceType;
import org.taktik.icure.entities.embed.MediumType;

public interface InvoiceDAO extends GenericDAO<Invoice> {
	PaginatedList<Invoice> findByHcParty(String hcParty, Long fromDate, Long toDate, PaginationOffset<ComplexKey> paginationOffset);
	List<Invoice> listByHcPartyContacts(String hcParty, Set<String> contactId);
	List<Invoice> listByHcPartyReferences(String hcParty, Set<String> invoiceReferences);
	List<Invoice> listByHcPartyReferences(String hcParty, String from, String to, boolean descending, int limit);

	List<Invoice> listByHcPartyGroupId(String hcParty, String groupId);
	List<Invoice> listByHcPartyRecipientIds(String hcParty, Set<String> recipientId);
	List<Invoice> listByHcPartyPatientFk(String hcParty, Set<String> secretPatientKeys);
	List<Invoice> listByHcPartyRecipientIdsUnsent(String hcParty, Set<String> recipientIds);
	List<Invoice> listByHcPartyPatientFkUnsent(String hcParty, Set<String> secretPatientKeys);
	
 	List<Invoice> listByHcPartySentMediumTypeInvoiceTypeSentDate(String hcParty, MediumType sentMediumType, InvoiceType invoiceType, boolean sent, Long fromDate, Long toDate);
	List<Invoice> listByHcPartySendingModeStatus(String hcParty, String sendingMode, String status, Long fromDate, Long toDate);

	List<Invoice> listByServiceIds(Set<String> serviceIds);

	List<Invoice> listAllHcpsByStatus(String status, Long from, Long to, List<String> hcpIds);
	List<Invoice> listConflicts();
	List<String> findTarificationsByCode(String hcPartyId, String codeCode, Long startValueDate, Long endValueDate);
	List<CouchKeyValue<Long>> listTarificationsFrequencies(String hcPartyId);

}
