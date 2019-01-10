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

package org.taktik.icure.dao.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.InvoiceDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.ektorp.CouchKeyValue;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.embed.InvoiceType;
import org.taktik.icure.entities.embed.MediumType;

@Repository("invoiceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted) emit( null, doc._id )}")
public class InvoiceDAOImpl extends GenericIcureDAOImpl<Invoice> implements InvoiceDAO {

	@Autowired
	public InvoiceDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector db, IDGenerator idGenerator) {
		super(Invoice.class, db, idGenerator);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "by_hcparty_date", map = "classpath:js/invoice/By_hcparty_date_map.js")
	public PaginatedList<Invoice> findByHcParty(String hcParty, Long fromDate, Long toDate, PaginationOffset<ComplexKey> paginationOffset) {
		ComplexKey startKey = paginationOffset == null || paginationOffset.getStartKey() == null ? ComplexKey.of(hcParty, fromDate) : paginationOffset.getStartKey();
		ComplexKey endKey = ComplexKey.of(hcParty, toDate == null ? ComplexKey.emptyObject(): toDate);
		return pagedQueryView("by_hcparty_date", startKey, endKey, paginationOffset, false);
	}

	@Override
	@View(name = "by_hcparty_contact", map = "classpath:js/invoice/By_hcparty_contact_map.js")
	public List<Invoice> listByHcPartyContacts(String hcParty, Set<String> contactId) {
		Set<String> invoiceIds = new HashSet<>(db.queryView(createQuery("by_hcparty_contact").includeDocs(false).keys(contactId.stream().map((c) -> ComplexKey.of(hcParty, c)).collect(Collectors.toList())), String.class));
		return getList(new TreeSet<>(invoiceIds));
	}

	@Override
	@View(name = "by_hcparty_reference", map = "classpath:js/invoice/By_hcparty_reference_map.js")
	public List<Invoice> listByHcPartyReferences(String hcParty, Set<String> invoiceReferences) {
		ViewQuery viewQuery = createQuery("by_hcparty_reference").includeDocs(true);
		if (invoiceReferences != null) {
			viewQuery.keys(invoiceReferences.stream().map((c) -> ComplexKey.of(hcParty, c)).collect(Collectors.toList()));
		}

		return db.queryView(viewQuery, Invoice.class);
	}

	@Override
	@View(name = "by_hcparty_reference", map = "classpath:js/invoice/By_hcparty_reference_map.js")
	public List<Invoice> listByHcPartyReferences(String hcParty, String from, String to, boolean descending, int limit) {

		ComplexKey startKey = ComplexKey.of(hcParty, (descending && from == null) ? ComplexKey.emptyObject() : from);
		ComplexKey endKey = ComplexKey.of(hcParty, (!descending && to == null) ? ComplexKey.emptyObject() : to);

		return db.queryView(createQuery("by_hcparty_reference").includeDocs(true).startKey(startKey).endKey(endKey).descending(descending).limit(limit), Invoice.class);
	}

	@Override
	@View(name = "by_hcparty_groupid", map = "classpath:js/invoice/By_hcparty_groupid_map.js")
	public List<Invoice> listByHcPartyGroupId(String hcParty, String groupId) {
		ComplexKey startKey = ComplexKey.of(hcParty, groupId);
		return queryResults(createQuery("by_hcparty_groupid").startKey(startKey).endKey(startKey).includeDocs(true));
	}

	@Override
	@View(name = "by_hcparty_recipient", map = "classpath:js/invoice/By_hcparty_recipient_map.js")
	public List<Invoice> listByHcPartyRecipientIds(String hcParty, Set<String> recipientIds) {
		return queryResults(createQuery("by_hcparty_recipient").includeDocs(true).keys(recipientIds.stream().map(id->ComplexKey.of(hcParty,id)).collect(Collectors.toList())));
	}

	@Override
	@View(name = "by_hcparty_patientfk", map = "classpath:js/invoice/By_hcparty_patientfk_map.js")
	public List<Invoice> listByHcPartyPatientFk(String hcParty, Set<String> secretPatientKeys) {
		ComplexKey[] keys = secretPatientKeys.stream().map(fk -> ComplexKey.of(hcParty, fk)).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);

		return queryResults(createQuery("by_hcparty_patientfk").includeDocs(true).keys(Arrays.asList(keys)));
	}

	@Override
	@View(name = "by_hcparty_recipient_unsent", map = "classpath:js/invoice/By_hcparty_recipient_unsent_map.js")
	public List<Invoice> listByHcPartyRecipientIdsUnsent(String hcParty, Set<String> recipientIds) {
		List<Invoice> res = queryResults(createQuery("by_hcparty_recipient_unsent").includeDocs(true).keys(recipientIds.stream().map(id -> ComplexKey.of(hcParty, id)).collect(Collectors.toList())));
		return res;
	}

	@Override
	@View(name = "by_hcparty_patientfk_unsent", map = "classpath:js/invoice/By_hcparty_patientfk_unsent_map.js")
	public List<Invoice> listByHcPartyPatientFkUnsent(String hcParty, Set<String> secretPatientKeys) {
		ComplexKey[] keys = secretPatientKeys.stream().map(fk -> ComplexKey.of(hcParty, fk)).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);

		return queryResults(createQuery("by_hcparty_patientfk_unsent").includeDocs(true).keys(Arrays.asList(keys)));
	}

    @Override
	@View(name = "by_hcparty_sentmediumtype_invoicetype_sent_date", map = "classpath:js/invoice/By_hcparty_sentmediumtype_invoicetype_sent_date.js")
    public List<Invoice> listByHcPartySentMediumTypeInvoiceTypeSentDate(String hcParty, MediumType sentMediumType, InvoiceType invoiceType, boolean sent, Long fromDate, Long toDate) {
		ComplexKey startKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent);
		ComplexKey endKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, "{}");
		if(fromDate != null){
			startKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, fromDate);
			if(toDate != null){
				endKey = ComplexKey.of(hcParty, sentMediumType, invoiceType, sent, toDate);
			}
		}
		return queryResults(createQuery("by_hcparty_sentmediumtype_invoicetype_sent_date").includeDocs(true).startKey(startKey).endKey(endKey));
    }

	@Override
	@View(name = "by_hcparty_sending_mode_status_date", map = "classpath:js/invoice/By_hcparty_sending_mode_status_date.js")
	public List<Invoice> listByHcPartySendingModeStatus(String hcParty, String sendingMode, String status, Long fromDate, Long toDate) {
		ComplexKey startKey = ComplexKey.of(hcParty);
		ComplexKey endKey = ComplexKey.of(hcParty, ComplexKey.emptyObject(), ComplexKey.emptyObject(), ComplexKey.emptyObject());
		if(fromDate != null && toDate != null) { // The full key is given
			startKey = ComplexKey.of(hcParty, sendingMode, status, fromDate);
			endKey = ComplexKey.of(hcParty, sendingMode, status, toDate);
		} else if(status != null)  {
			startKey = ComplexKey.of(hcParty, sendingMode, status);
			endKey = ComplexKey.of(hcParty, sendingMode, status, ComplexKey.emptyObject());
		} else if(sendingMode != null){
			startKey = ComplexKey.of(hcParty, sendingMode);
			endKey = ComplexKey.of(hcParty, sendingMode, ComplexKey.emptyObject(), ComplexKey.emptyObject());
		}

		return queryResults(createQuery("by_hcparty_sending_mode_status_date").includeDocs(true).startKey(startKey).endKey(endKey));
	}

	@Override
	@View(name = "by_serviceid", map = "classpath:js/invoice/By_serviceid_map.js")
	public List<Invoice> listByServiceIds(Set<String> serviceIds) {
		return queryResults(createQuery("by_serviceid").includeDocs(true).keys(serviceIds));
	}

	@Override
	@View(name = "by_status_hcps_sentdate", map = "classpath:js/invoice/By_status_hcps_sentdate_map.js")
	public List<Invoice> listAllHcpsByStatus(String status, Long from, Long to, List<String> hcpIds) {
		Set<String> ids = new HashSet<>();
		for (String hcpId : hcpIds) {
			ids.addAll(db.queryView(createQuery("by_status_hcps_sentdate").includeDocs(false)
					.startKey(ComplexKey.of(status,hcpId,from))
					.endKey(ComplexKey.of(status,hcpId,to!=null?to:ComplexKey.emptyObject())),String.class));
		}
		return getList(ids);
	}

	@Override
	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	public List<Invoice> listConflicts() {
		return queryView("conflicts");
	}


	@Override
	@View(name = "tarification_by_hcparty_code", map = "classpath:js/invoice/Tarification_by_hcparty_code.js", reduce = "_count")
	public List<String> findTarificationsByCode(String hcPartyId, String codeCode, Long startValueDate, Long endValueDate) {
		if (startValueDate != null && startValueDate<99999999) { startValueDate = startValueDate * 1000000 ; }
		if (endValueDate != null && endValueDate<99999999) { endValueDate = endValueDate * 1000000 ; }
		ComplexKey from = ComplexKey.of(
				hcPartyId,
				codeCode,
				startValueDate
		);
		ComplexKey to = ComplexKey.of(
				hcPartyId,
				codeCode == null ? ComplexKey.emptyObject() : codeCode,
				endValueDate  == null ? ComplexKey.emptyObject() : endValueDate
		);

		ViewQuery viewQuery = createQuery("service_by_hcparty_code")
				.startKey(from)
				.endKey(to)
				.reduce(false)
				.includeDocs(false);

		List<String> ids = db.queryView(viewQuery, String.class);
		return ids;
	}

	@Override
	public List<CouchKeyValue<Long>> listTarificationsFrequencies(String hcPartyId) {
		ComplexKey from = ComplexKey.of(
				hcPartyId,
				null
		);
		ComplexKey to = ComplexKey.of(
				hcPartyId,
				ComplexKey.emptyObject()
		);

		return ((CouchDbICureConnector) db).queryViewWithKeys(createQuery("tarification_by_hcparty_code").startKey(from).endKey(to).includeDocs(false).reduce(true).group(true).groupLevel(2), Long.class);
	}
}
