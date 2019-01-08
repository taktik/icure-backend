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

import java.util.List;
import java.util.Set;

import org.ektorp.ComplexKey;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.dto.data.LabelledOccurence;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.InvoiceType;
import org.taktik.icure.entities.embed.InvoicingCode;
import org.taktik.icure.entities.embed.MediumType;
import org.taktik.icure.exceptions.DeletionException;

public interface InvoiceLogic {
	Invoice createInvoice(Invoice invoice);
	String deleteInvoice(String invoiceId) throws DeletionException;
	Invoice getInvoice(String invoiceId);
	List<Invoice> getInvoices(List<String> strings);
	Invoice modifyInvoice(Invoice invoice);
	List<Invoice> updateInvoices(List<Invoice> invoices);

	Invoice addDelegation(String invoiceId, Delegation delegation);

	PaginatedList<Invoice> findByAuthor(String hcParty, String userId, Long fromDate, Long toDate, PaginationOffset<ComplexKey> paginationOffset);
	List<Invoice> listByHcPartyContacts(String hcParty, Set<String> contactIds);
	List<Invoice> listByHcPartyRecipientIds(String hcParty, Set<String> recipientIds);
	List<Invoice> listByHcPartyPatientSks(String hcParty, Set<String> patientSks);

	List<Invoice> listByHcPartySentMediumTypeInvoiceTypeSentDate(String hcParty, MediumType sentMediumType, InvoiceType invoiceType, boolean sent, Long fromDate, Long toDate);

	List<Invoice> listByHcPartySendingModeStatus(String hcParty, String sendingMode, String status, Long fromDate, Long toDate);

	List<Invoice> listByHcPartyGroupId(String hcParty, String groupId);

	List<Invoice> listByHcPartyRecipientIdsUnsent(String hcParty, Set<String> recipientIds);

	List<Invoice> listByHcPartyPatientSksUnsent(String hcParty, Set<String> secretPatientKeys);

	List<Invoice> listByServiceIds(Set<String> serviceIds);

	Invoice mergeInvoices(String hcParty, List<Invoice> invoices, Invoice destination) throws DeletionException;

	Invoice validateInvoice(String hcParty, Invoice invoice, String refScheme, String forceValue);

	List<Invoice> appendCodes(String hcParty, String userId, String insuranceId, Set<String> secretPatientKeys, InvoiceType type, MediumType sentMediumType, List<InvoicingCode> invoicingCodes, String invoiceId, Integer invoiceGraceTime);

	Invoice addDelegations(String invoiceId, List<Delegation> delegations);

	List<Invoice> removeCodes(String userId, Set<String> secretPatientKeys, String serviceId, List<String> tarificationIds);

	List<Invoice> listAllHcpsByStatus(String status, Long from, Long to, List<String> hcpIds);

	List<LabelledOccurence> getTarificationsCodesOccurences(String hcPartyId, long minOccurences);

	void solveConflicts();



}
