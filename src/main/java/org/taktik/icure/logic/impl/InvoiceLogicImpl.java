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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;
import org.ektorp.ComplexKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.InvoiceDAO;
import org.taktik.icure.dao.Option;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.dto.data.LabelledOccurence;
import org.taktik.icure.entities.Form;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.InvoiceType;
import org.taktik.icure.entities.embed.InvoicingCode;
import org.taktik.icure.entities.embed.MediumType;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.InvoiceLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.utils.FuzzyValues;

@Service
public class InvoiceLogicImpl extends GenericLogicImpl<Invoice, InvoiceDAO> implements InvoiceLogic {
	private static final Logger log = LoggerFactory.getLogger(InvoiceLogicImpl.class);

	private UserLogic userLogic;
	private InvoiceDAO invoiceDAO;

	@Override
	public Invoice createInvoice(Invoice invoice) {
		return invoiceDAO.create(invoice);
	}

	@Override
	public String deleteInvoice(String invoiceId) throws DeletionException {
		try {
			deleteEntities(Arrays.asList(invoiceId));
			return invoiceId;
		} catch (Exception e) {
			throw new DeletionException(e.getMessage(), e);
		}
	}

	@Override
	public Invoice getInvoice(String invoiceId) {
		return invoiceDAO.get(invoiceId);
	}

	@Override
	public List<Invoice> getInvoices(List<String> ids) {
		return invoiceDAO.getList(ids);
	}

	@Override
	public Invoice modifyInvoice(Invoice invoice) {
		return invoiceDAO.save(invoice);
	}

	@Override
	public List<Invoice> updateInvoices(List<Invoice> invoices) {
		return invoiceDAO.save(invoices);
	}

	@Override
	public Invoice addDelegation(String invoiceId, Delegation delegation) {
		Invoice invoice = invoiceDAO.get(invoiceId);
		invoice.addDelegation(delegation.getDelegatedTo(), delegation);
		return invoiceDAO.save(invoice);
	}

	@Override
	public PaginatedList<Invoice> findByAuthor(String hcParty, String userId, Long fromDate, Long toDate, PaginationOffset paginationOffset) {
		return invoiceDAO.findByHcParty(hcParty, fromDate, toDate, paginationOffset);
	}

	@Override
	public List<Invoice> listByHcPartyContacts(String hcParty, Set<String> contactIds) {
		return invoiceDAO.listByHcPartyContacts(hcParty, contactIds);
	}

	@Override
	public List<Invoice> listByHcPartyRecipientIds(String hcParty, Set<String> recipientIds) {
		return invoiceDAO.listByHcPartyRecipientIds(hcParty, recipientIds);
	}

	@Override
	public List<Invoice> listByHcPartyPatientSks(String hcParty, Set<String> secretPatientKeys) {
		return invoiceDAO.listByHcPartyPatientFk(hcParty, secretPatientKeys);
	}

	@Override
	public List<Invoice> listByHcPartySentMediumTypeInvoiceTypeSentDate(String hcParty, MediumType sentMediumType, InvoiceType invoiceType, boolean sent, Long fromDate, Long toDate) {
		return invoiceDAO.listByHcPartySentMediumTypeInvoiceTypeSentDate(hcParty, sentMediumType, invoiceType, sent, fromDate, toDate);
	}

	@Override
	public List<Invoice> listByHcPartySendingModeStatus(String hcParty, String sendingMode, String status, Long fromDate, Long toDate) {
		return invoiceDAO.listByHcPartySendingModeStatus(hcParty, sendingMode, status, fromDate, toDate);
	}

	@Override
    public List<Invoice> listByHcPartyGroupId(String hcParty, String groupId) {
        return invoiceDAO.listByHcPartyGroupId(hcParty, groupId);
    }

    @Override
	public List<Invoice> listByHcPartyRecipientIdsUnsent(String hcParty, Set<String> recipientIds) {
		return invoiceDAO.listByHcPartyRecipientIdsUnsent(hcParty, recipientIds);
	}

	@Override
	public List<Invoice> listByHcPartyPatientSksUnsent(String hcParty, Set<String> secretPatientKeys) {
		return invoiceDAO.listByHcPartyPatientFkUnsent(hcParty, secretPatientKeys);
	}

	@Override
	public List<Invoice> listByServiceIds(Set<String> serviceIds) {
		return invoiceDAO.listByServiceIds(serviceIds);
	}


	@Override
	public Invoice mergeInvoices(String hcParty, List<Invoice> invoices, Invoice destination) throws DeletionException {
		if (destination.getInvoicingCodes() == null) { destination.setInvoicingCodes(new ArrayList<>()); }
		for (Invoice i:invoices) {
			destination.getInvoicingCodes().addAll(i.getInvoicingCodes());
			deleteInvoice(i.getId());
		}

		return modifyInvoice(destination);
	}

	@Override
	public Invoice validateInvoice(String hcParty, Invoice invoice, String refScheme, String forcedValue) {
		if (forcedValue != null || !Strings.isNullOrEmpty(invoice.getInvoiceReference())) {
			invoice.setInvoiceReference(forcedValue);
		} else {
			if (refScheme == null) {
				refScheme = "yyyy00000";
			}
			LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(invoice.getInvoiceDate()), ZoneId.systemDefault());

			NumberFormat f = new DecimalFormat("00");

			String startScheme = refScheme.replaceAll("yyyy", "" + ldt.getYear()).replaceAll("MM", f.format(ldt.getMonthValue())).replaceAll("dd", "" + f.format(ldt.getDayOfMonth()));
			String endScheme = refScheme.replaceAll("0", "9").replaceAll("yyyy", "" + ldt.getYear()).replaceAll("MM", "" + f.format(ldt.getMonthValue())).replaceAll("dd", "" + f.format(ldt.getDayOfMonth()));

			List<Invoice> prevInvoices = invoiceDAO.listByHcPartyReferences(hcParty, endScheme, null, true, 1);
			invoice.setInvoiceReference("" + (prevInvoices.size() > 0 && prevInvoices.get(0).getInvoiceReference() != null ? Math.max(Long.valueOf(prevInvoices.get(0).getInvoiceReference()) + 1L,Long.valueOf(startScheme) + 1L) : Long.valueOf(startScheme) + 1L));
		}
		invoice.setSentDate(System.currentTimeMillis());
		return modifyInvoice(invoice);
	}

	@Override
	public List<Invoice> appendCodes(String hcPartyId, String userId, String insuranceId, Set<String> secretPatientKeys, InvoiceType type, MediumType sentMediumType, List<InvoicingCode> invoicingCodes, String invoiceId, Integer invoiceGraceTime) {
		if (sentMediumType == MediumType.efact) {
			invoicingCodes.forEach(c->c.setPending(true));
		}
		final int invoiceGraceTimeInDays = invoiceGraceTime == null ? 0 : invoiceGraceTime;
		Invoice selectedInvoice = (invoiceId != null) ? this.getInvoice(invoiceId) : null;

		List<Invoice> invoices = selectedInvoice != null ? new ArrayList<>() : this.listByHcPartyPatientSksUnsent(hcPartyId,secretPatientKeys).stream().filter(i->
				i.getInvoiceType() == type && i.getSentMediumType() == sentMediumType && (insuranceId == null ? i.getRecipientId() == null  : insuranceId.equals(i.getRecipientId()))
		).collect(Collectors.toList());

		if (selectedInvoice == null &&  invoices.isEmpty()) {
			invoices = this.listByHcPartyRecipientIdsUnsent(hcPartyId,Collections.singleton(insuranceId)).stream().filter(i->
					i.getInvoiceType() == type && i.getSentMediumType() == sentMediumType && i.getSecretForeignKeys().equals(secretPatientKeys)
			).collect(Collectors.toList());
		}

		Set<Invoice> modifiedInvoices = new HashSet<>();
		Set<Invoice> createdInvoices = new HashSet<>();

		for (InvoicingCode invoicingCode : new ArrayList<>(invoicingCodes)) {
			LocalDateTime icDateTime = FuzzyValues.getDateTime(invoicingCode.getDateCode());

			Optional<Invoice> unsentInvoice = selectedInvoice != null ? Optional.of(selectedInvoice) : invoices.stream().filter(i ->
					i.getInvoiceDate() != null && Math.abs(FuzzyValues.getDateTime(i.getInvoiceDate()).until(icDateTime, ChronoUnit.DAYS)) <= invoiceGraceTimeInDays
			).findAny();

			if (unsentInvoice.isPresent()) {
				Invoice invoice = unsentInvoice.get();
				invoice.getInvoicingCodes().add(invoicingCode);

				if (!createdInvoices.contains(invoice)) { modifiedInvoices.add(modifyInvoice(invoice)); }
			} else {
				Invoice newInvoice = new Invoice();
				newInvoice.setInvoiceDate(invoicingCode.getDateCode()!=null?invoicingCode.getDateCode():System.currentTimeMillis());
				newInvoice.setInvoiceType(type);
				newInvoice.setSentMediumType(sentMediumType);
				newInvoice.setRecipientId(insuranceId);
				newInvoice.setRecipientType((type == InvoiceType.mutualfund || type == InvoiceType.payingagency) ? Insurance.class.getName() : Patient.class.getName());
				newInvoice.setInvoicingCodes(invoicingCodes);
				newInvoice.setAuthor(userId);
				newInvoice.setResponsible(hcPartyId);
				newInvoice.setCreated(System.currentTimeMillis());
				newInvoice.setModified(newInvoice.getCreated());

				//The invoice must be completed with ids and delegations and created on the server
				createdInvoices.add(newInvoice);
				invoices.add(newInvoice);
			}
		}
		ArrayList<Invoice> result = new ArrayList<>(createdInvoices);

		result.addAll(modifiedInvoices);
		return result;
	}

	@Override
	public Invoice addDelegations(String invoiceId, List<Delegation> delegations) {
		Invoice invoice = invoiceDAO.get(invoiceId);
		delegations.forEach(d->invoice.addDelegation(d.getDelegatedTo(),d));
		return invoiceDAO.save(invoice);
	}

	@Override
	public List<Invoice> removeCodes(String userId, Set<String> secretPatientKeys, String serviceId, List<String> tarificationIds) {
		User user = this.userLogic.getUser(userId);
		List<Invoice> invoices = this.listByHcPartyPatientSksUnsent(user.getHealthcarePartyId(), secretPatientKeys).stream()
				.filter(i -> i.getInvoicingCodes().stream().anyMatch(ic -> serviceId.equals(ic.getServiceId()) && tarificationIds.contains(ic.getTarificationId()))
		).sorted((a,b)-> ((Long)(b.getInvoiceDate()!=null?b.getInvoiceDate():99999999999999L)).compareTo(a.getInvoiceDate())).collect(Collectors.toList());
		List<Invoice> result = new LinkedList<>();
		for (Invoice i:invoices) {
			boolean hasChanged = false;
			List<InvoicingCode> l = new LinkedList<>(i.getInvoicingCodes());
			for(InvoicingCode ic:i.getInvoicingCodes()) {
				if (tarificationIds.contains(ic.getTarificationId())) {
					l.remove(ic);
					tarificationIds.remove(ic.getTarificationId());
					hasChanged = true;
				}
			}
			if (hasChanged) {
				i.setInvoicingCodes(l);
				result.add(modifyInvoice(i));
			}
		}
		return result;
	}

	@Override
	public List<Invoice> listAllHcpsByStatus(String status, Long from, Long to, List<String> hcpIds) {
		if (status == null) {throw new IllegalArgumentException("Status cannot be null"); }
		return invoiceDAO.listAllHcpsByStatus(status,from,to,hcpIds);
	}

	@Override
	public void solveConflicts() {
		List<Invoice> invoicesInConflict = invoiceDAO.listConflicts().stream().map(it -> invoiceDAO.get(it.getId(), Option.CONFLICTS)).collect(Collectors.toList());

		invoicesInConflict.forEach(iv -> {
			Arrays.stream(iv.getConflicts()).map(c -> invoiceDAO.get(iv.getId(), c)).forEach(cp -> {
				iv.solveConflictWith(cp);
				invoiceDAO.purge(cp);
			});
			invoiceDAO.save(iv);
		});
	}

	@Override
	public List<LabelledOccurence> getTarificationsCodesOccurences(String hcPartyId, long minOccurences) {
		return invoiceDAO.listTarificationsFrequencies(hcPartyId).parallelStream()
				.filter(v -> v.getValue() != null && v.getValue() >= minOccurences)
				.map(v -> new LabelledOccurence((String) v.getKey().getComponents().get(1), v.getValue()))
				.sorted(Comparator.comparing(LabelledOccurence::getOccurence).reversed())
				.collect(Collectors.toList());
	}

	@Autowired
	public void setInvoiceDAO(InvoiceDAO invoiceDAO) {
		this.invoiceDAO = invoiceDAO;
	}

	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Override
	protected InvoiceDAO getGenericDAO() {
		return invoiceDAO;
	}
}
