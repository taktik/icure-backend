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

import com.google.common.collect.Lists;
import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.ContactDAO;
import org.taktik.icure.dao.impl.ektorp.CouchKeyValue;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.embed.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by aduchate on 18/07/13, 13:36
 */
@Repository("contactDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Contact' && !doc.deleted) emit( null, doc._id )}")
public class ContactDAOImpl extends GenericIcureDAOImpl<Contact> implements ContactDAO {
    @Autowired
    public ContactDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbHealthdata") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(Contact.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

    @Override
    public Contact getContact(String id) {
        return get(id);
    }

    @Override
    public List<Contact> get(Collection<String> contactIds) {
		List<List<String>> lists = Lists.partition(new ArrayList<>(contactIds), 512);
		return lists.stream().map(ids -> {
			ViewQuery q = new ViewQuery().allDocs().includeDocs(true).keys(ids);
			q.setIgnoreNotFound(true);
			return queryResults(q);
		}).flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    @View(name = "by_hcparty_openingdate", map = "classpath:js/contact/By_hcparty_openingdate.js")
    public PaginatedList<Contact> listContactsByOpeningDate(String hcPartyId, Long openingDate, PaginationOffset<List<Serializable>> pagination) {
		ComplexKey startKey = pagination.getStartKey() == null ? ComplexKey.of(hcPartyId, openingDate) : ComplexKey.of(pagination.getStartKey().toArray());
		ComplexKey endKey = ComplexKey.of(hcPartyId, openingDate);

		return pagedQueryView(
                "by_hcparty_openingdate",
                startKey,
                endKey,
                pagination, false
		);
    }

    @Override
	@View(name = "by_hcparty", map = "classpath:js/contact/By_hcparty.js")
	public PaginatedList<Contact> listContacts(String hcPartyId, PaginationOffset pagination) {
		String key = pagination.getStartKey() == null ? hcPartyId : (String) pagination.getStartKey();

		return pagedQueryView(
				"by_hcparty",
				key,
				key,
				pagination, false
		);
	}

 	@Override
    @View(name = "by_hcparty_patientfk", map = "classpath:js/contact/By_hcparty_patientfk_map.js")
    public List<Contact> findByHcPartyPatient(String hcPartyId, List<String> secretPatientKeys) {
		ComplexKey[] keys = secretPatientKeys.stream().map(
				fk -> ComplexKey.of(hcPartyId, fk)
		).collect(Collectors.toList()).toArray(new ComplexKey[secretPatientKeys.size()]);

        List<Contact> result = new ArrayList<>();
        queryView("by_hcparty_patientfk", keys).forEach((e)->{if (result.isEmpty() || !e.getId().equals(result.get(result.size()-1).getId())) {result.add(e); }});

        return relink(result);
	}

    @Override
    @View(name = "by_hcparty_formid", map = "classpath:js/contact/By_hcparty_formid_map.js")
    public List<Contact> findByHcPartyFormId(String hcPartyId, String formId) {
        return relink(queryView("by_hcparty_formid", ComplexKey.of(hcPartyId,formId)));
    }

	@Override
	public List<Contact> findByHcPartyFormIds(String hcPartyId, List<String> ids) {
		return relink(get(new HashSet<>(db.queryView(createQuery("by_hcparty_formid")
						.includeDocs(false)
						.keys(ids.stream().map(k->ComplexKey.of(hcPartyId,k)).collect(Collectors.toList())),
				String.class)))); //Important to de deduplicate the contact ids
	}


	@Override
	@View(name = "service_by_hcparty_tag", map = "classpath:js/contact/Service_by_hcparty_tag.js")
    public List<String> findServicesByTag(String hcPartyId, String tagType, String tagCode, Long startValueDate, Long endValueDate) {
		if (startValueDate != null && startValueDate<99999999) { startValueDate = startValueDate * 1000000 ; }
		if (endValueDate != null && endValueDate<99999999) { endValueDate = endValueDate * 1000000 ; }
		ComplexKey from = ComplexKey.of(
                hcPartyId,
                tagType,
                tagCode,
				startValueDate
        );
        ComplexKey to = ComplexKey.of(
                hcPartyId,
                tagType == null ? ComplexKey.emptyObject() : tagType,
                tagCode == null ? ComplexKey.emptyObject() : tagCode,
				endValueDate  == null ? ComplexKey.emptyObject() : endValueDate
        );

        ViewQuery viewQuery = createQuery("service_by_hcparty_tag")
                .startKey(from)
                .endKey(to)
                .includeDocs(false);

        List<String> ids = db.queryView(viewQuery, String.class);
        return ids;
    }

    @Override
    @View(name = "service_by_hcparty_patient_tag", map = "classpath:js/contact/Service_by_hcparty_patient_tag.js")
    public List<String> findServicesByPatientTag(String hcPartyId, String patientSecretForeignKey, String tagType, String tagCode, Long startValueDate, Long endValueDate) {
		if (startValueDate != null && startValueDate<99999999) { startValueDate = startValueDate * 1000000 ; }
		if (endValueDate != null && endValueDate<99999999) { endValueDate = endValueDate * 1000000 ; }
		ComplexKey from = ComplexKey.of(
                hcPartyId,
                patientSecretForeignKey,
                tagType,
                tagCode,
				startValueDate
        );
        ComplexKey to = ComplexKey.of(
                hcPartyId,
                patientSecretForeignKey,
                tagType == null ? ComplexKey.emptyObject() : tagType,
                tagCode == null ? ComplexKey.emptyObject() : tagCode,
				endValueDate  == null ? ComplexKey.emptyObject() : endValueDate
        );

        ViewQuery viewQuery = createQuery("service_by_hcparty_patient_tag")
                .startKey(from)
                .endKey(to)
                .includeDocs(false);

        List<String> ids = db.queryView(viewQuery, String.class);
        return ids;
    }

    @Override
    @View(name = "service_by_hcparty_code", map = "classpath:js/contact/Service_by_hcparty_code.js", reduce = "_count")
    public List<String> findServicesByCode(String hcPartyId, String codeType, String codeCode, Long startValueDate, Long endValueDate) {
		if (startValueDate != null && startValueDate<99999999) { startValueDate = startValueDate * 1000000 ; }
		if (endValueDate != null && endValueDate<99999999) { endValueDate = endValueDate * 1000000 ; }
        ComplexKey from = ComplexKey.of(
                hcPartyId,
                codeType,
                codeCode,
				startValueDate
        );
        ComplexKey to = ComplexKey.of(
                hcPartyId,
                codeType == null ? ComplexKey.emptyObject() : codeType,
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
	public List<CouchKeyValue<Long>> listCodesFrequencies(String hcPartyId, String codeType) {
		ComplexKey from = ComplexKey.of(
				hcPartyId,
				codeType,
				null
		);
		ComplexKey to = ComplexKey.of(
				hcPartyId,
				codeType,
				ComplexKey.emptyObject()
		);

		return ((CouchDbICureConnector) db).queryViewWithKeys(createQuery("service_by_hcparty_code").startKey(from).endKey(to).includeDocs(false).reduce(true).group(true).groupLevel(3), Long.class);
    }


	@Override
    @View(name = "service_by_hcparty_patient_code", map = "classpath:js/contact/Service_by_hcparty_patient_code.js")
    public List<String> findServicesByPatientCode(String hcPartyId, String patientSecretForeignKey, String codeType, String codeCode, Long startValueDate, Long endValueDate) {
    	if (startValueDate != null && startValueDate<99999999) { startValueDate = startValueDate * 1000000 ; }
		if (endValueDate != null && endValueDate<99999999) { endValueDate = endValueDate * 1000000 ; }
        ComplexKey from = ComplexKey.of(
                hcPartyId,
                patientSecretForeignKey,
                codeType,
                codeCode,
				startValueDate
        );
        ComplexKey to = ComplexKey.of(
                hcPartyId,
                patientSecretForeignKey,
                codeType == null ? ComplexKey.emptyObject() : codeType,
                codeCode == null ? ComplexKey.emptyObject() : codeCode,
				endValueDate  == null ? ComplexKey.emptyObject() : endValueDate
        );

        ViewQuery viewQuery = createQuery("service_by_hcparty_patient_code")
                .startKey(from)
                .endKey(to)
                .includeDocs(false);

        List<String> ids = db.queryView(viewQuery, String.class);
        return ids;
    }

    @Override
    @View(name = "by_service", map = "classpath:js/contact/By_service.js")
    public List<String> findByServices(Collection<String> services) {
        ViewQuery viewQuery = createQuery("by_service")
                .keys(services)
                .includeDocs(false);

        return db.queryView(viewQuery, String.class);
	}

    @Override
    public List<Contact> listByServices(Collection<String> services) {
		return get(this.listIdsByServices(services));
    }

	@Override
	public Set<String> listIdsByServices(Collection<String> services) {
		ViewQuery viewQuery = createQuery("by_service").keys(services).includeDocs(false);
		return new TreeSet<>(db.queryView(viewQuery, String.class));

	}

	public List<Contact> relink(List<Contact> cs) {
        for (Contact c : cs) {
            Map<String, Service> services = new HashMap<>();
            if (c.getServices() != null) c.getServices().stream().forEach(s -> services.put(s.getId(), s));
			if (c.getSubContacts() != null) c.getSubContacts().forEach(ss -> ss.getServices().forEach(s -> {
                Service ssvc = services.get(s.getServiceId());
                //If it is null, leave it null...
                s.setService(ssvc);
            }));
        }
        return cs;
    }

	@Override
	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Contact' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	public List<Contact> listConflicts() {
		return queryView("conflicts");
	}

}
