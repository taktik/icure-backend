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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.ektorp.ComplexKey;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.MessageDAO;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Message;

@Repository("messageDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted) emit( null, doc._id )}")
public class MessageDAOImpl extends GenericIcureDAOImpl<Message> implements MessageDAO {

	@Autowired
	public MessageDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchDb, IDGenerator idGenerator) {
		super(Message.class, couchDb, idGenerator);
		initStandardDesignDocument();
	}

	@Override
	@View(name = "by_hcparty_from_address_actor", map = "classpath:js/message/By_hcparty_from_address_actor_map.js")
	public List<Message> findByFromAddressActor(String partyId, String fromAddress, List<String> actorKeys) {
		if (actorKeys != null && !actorKeys.isEmpty()) {
			ComplexKey[] keys = actorKeys.stream().map(k -> ComplexKey.of(partyId, fromAddress, k))
					.collect(Collectors.toList()).toArray(new ComplexKey[actorKeys.size()]);
			return queryView("by_from_address_actor",keys);
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	@View(name = "by_hcparty_to_address_actor", map = "classpath:js/message/By_hcparty_to_address_actor_map.js")
	public List<Message> findByToAddressActor(String partyId, String toAddress, List<String> actorKeys) {
		if (actorKeys != null && !actorKeys.isEmpty()) {
			ComplexKey[] keys = actorKeys.stream().map(k -> ComplexKey.of(partyId, toAddress, k))
					.collect(Collectors.toList()).toArray(new ComplexKey[actorKeys.size()]);
			return queryView("by_hcparty_to_address_actor",keys);
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	@View(name = "by_hcparty_transport_guid_actor", map = "classpath:js/message/By_hcparty_transport_guid_actor_map.js")
	public List<Message> findByTransportGuidActor(String partyId, String transportGuid, List<String> actorKeys) {
		if (actorKeys != null && !actorKeys.isEmpty()) {
			ComplexKey[] keys = actorKeys.stream().map(k -> ComplexKey.of(partyId, transportGuid, k))
					.collect(Collectors.toList()).toArray(new ComplexKey[actorKeys.size()]);
			return queryView("by_hcparty_transport_guid_actor",keys);
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	@View(name = "by_hcparty_from_address", map = "classpath:js/message/By_hcparty_from_address_map.js")
	public PaginatedList<Message> findByFromAddress(String partyId, String fromAddress, PaginationOffset<List<Object>> paginationOffset) {
		ComplexKey startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, fromAddress, null) : ComplexKey.of(paginationOffset.getStartKey().toArray());
		ComplexKey endKey = ComplexKey.of(startKey.getComponents().get(0),startKey.getComponents().get(1),ComplexKey.emptyObject());
		return pagedQueryView("by_hcparty_from_address", startKey, endKey, paginationOffset, false);
	}

	@Override
	@View(name = "by_hcparty_to_address", map = "classpath:js/message/By_hcparty_to_address_map.js")
	public PaginatedList<Message> findByToAddress(String partyId, String toAddress, PaginationOffset<List<Object>> paginationOffset, Boolean reverse) {
		if (reverse==null) { reverse=false; }
		ComplexKey startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, toAddress, null) : ComplexKey.of(paginationOffset.getStartKey().toArray());
		ComplexKey endKey = ComplexKey.of(partyId, toAddress, reverse?null:ComplexKey.emptyObject());
		return pagedQueryView("by_hcparty_to_address", startKey, endKey, paginationOffset, reverse);
	}

	@Override
	@View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
	public PaginatedList<Message> findByTransportGuid(String partyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset) {
		ComplexKey startKey;
		ComplexKey endKey;
		if (transportGuid != null && transportGuid.endsWith(":*")) {
			String prefix = transportGuid.substring(0, transportGuid.length() - 1);
			startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, prefix, null) : ComplexKey.of(paginationOffset.getStartKey().toArray());
			endKey = ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject());
		} else {
			startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, transportGuid, null) : ComplexKey.of(paginationOffset.getStartKey().toArray());
			endKey = ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject());
		}

		return pagedQueryView("by_hcparty_transport_guid_received", startKey, endKey, paginationOffset, false);
	}

	@Override
	@View(name = "by_hcparty_transport_guid_received", map = "classpath:js/message/By_hcparty_transport_guid_received_map.js")
	public PaginatedList<Message> findByTransportGuidReceived(String partyId, String transportGuid, PaginationOffset<List<Object>> paginationOffset) {
		ComplexKey startKey;
		ComplexKey endKey;
		if (transportGuid != null && transportGuid.endsWith(":*")) {
			String prefix = transportGuid.substring(0, transportGuid.length() - 1);
			startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, prefix, null) : ComplexKey.of(paginationOffset.getStartKey().toArray());
			endKey = ComplexKey.of(partyId, prefix + "\ufff0", ComplexKey.emptyObject());
		} else {
			startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, transportGuid, null) : ComplexKey.of(paginationOffset.getStartKey().toArray());
			endKey = ComplexKey.of(partyId, transportGuid, ComplexKey.emptyObject());
		}

		return pagedQueryView("by_hcparty_transport_guid_received", startKey, endKey, paginationOffset, false);
	}

	@Override
	@View(name = "by_hcparty_transport_guid_sent_date", map = "classpath:js/message/By_hcparty_transport_guid_sent_date.js")
	public PaginatedList<Message> findByTransportGuidSentDate(String partyId, String transportGuid, Long fromDate, Long toDate, PaginationOffset<List<Object>> paginationOffset) {
		ComplexKey startKey;
		ComplexKey endKey;
		startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, transportGuid, fromDate) : ComplexKey.of(paginationOffset.getStartKey().toArray());
		endKey = ComplexKey.of(partyId, transportGuid, toDate);

		return pagedQueryView("by_hcparty_transport_guid_sent_date", startKey, endKey, paginationOffset, false);
	}

	@Override
	@View(name = "by_hcparty", map = "classpath:js/message/By_hcparty_map.js")
	public PaginatedList<Message> findByHcParty(String partyId, PaginationOffset<List<Object>> paginationOffset) {
		ComplexKey startKey = paginationOffset.getStartKey() == null ? ComplexKey.of(partyId, null) : ComplexKey.of(paginationOffset.getStartKey().toArray());
		ComplexKey endKey = ComplexKey.of(startKey.getComponents().get(0),ComplexKey.emptyObject());
		return pagedQueryView("by_hcparty", startKey, endKey, paginationOffset, false);
	}

	@Override
	@View(name = "by_hcparty_patientfk", map = "classpath:js/message/By_hcparty_patientfk_map.js")
	public List<Message> findByHcPartyPatient(String hcPartyId, List<String> secretPatientKeys) {
		ComplexKey[] keys = secretPatientKeys.stream().map(
				fk -> ComplexKey.of(hcPartyId, fk)
		).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);
		List<Message> result = new ArrayList<>();
		queryView("by_hcparty_patientfk", keys).forEach((e)->{if (result.isEmpty() || !e.getId().equals(result.get(result.size()-1).getId())) {result.add(e); }});

		return result;
	}

	@Override
	@View(name = "by_parent_id", map = "classpath:js/message/By_parent_id_map.js")
	public List<Message> getChildren(String messageId) {
		return queryView("by_parent_id", messageId);
	}

	@Override
	public List<List<Message>> getChildren(List<String> parentIds) {

		List<Message> byParentId = queryResults(createQuery("by_parent_id")
				.includeDocs(true)
				.keys(parentIds));

		return parentIds.stream().map(id -> byParentId.stream().filter(c -> c.getParentId().equals(id)).collect(Collectors.toList())).collect(Collectors.toList());
	}

	@Override
	@View(name = "by_invoice_id", map = "classpath:js/message/By_invoice_id_map.js")
	public List<Message> getByInvoiceIds(Set<String> invoiceIds) {
		return queryView("by_invoice_id", invoiceIds.toArray(new String[invoiceIds.size()]));
	}

	@Override
	@View(name = "by_hcparty_transport_guid", map = "classpath:js/message/By_hcparty_transport_guid_map.js")
	public List<Message> getByTransportGuids(String hcPartyId, Collection<String> transportGuids) {
		return queryView("by_hcparty_transport_guid", new HashSet<>(transportGuids).stream().map(k->ComplexKey.of(hcPartyId,k)).collect(Collectors.toList()).toArray(new ComplexKey[transportGuids.size()]));
	}

	@Override
	@View(name = "by_external_ref", map = "classpath:js/message/By_hcparty_external_ref_map.js")
	public List<Message> getByExternalRefs(String hcPartyId, HashSet<String> externalRefs) {
		return queryView("by_hcparty_transport_guid", new HashSet<>(externalRefs).stream().map(k->ComplexKey.of(hcPartyId,k)).collect(Collectors.toList()).toArray(new ComplexKey[externalRefs.size()]));
	}

	@Override
	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	public List<Message> listConflicts() {
		return queryView("conflicts");
	}


}
