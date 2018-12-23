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

import org.ektorp.ComplexKey;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.PatientDAO;
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.db.StringUtils;
import org.taktik.icure.entities.Patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository("patientDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) emit(doc._id )}")
class PatientDAOImpl extends GenericIcureDAOImpl<Patient> implements PatientDAO {
	private static final Logger log = LoggerFactory.getLogger(PatientDAOImpl.class);

	@Autowired
    public PatientDAOImpl(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbPatient") CouchDbICureConnector couchdb, IDGenerator idGenerator) {
        super(Patient.class, couchdb, idGenerator);
        initStandardDesignDocument();
    }

	@Override
	@View(name = "by_hcparty_name", map = "classpath:js/patient/By_hcparty_name_map.js")
	public List<String> listIdsByHcPartyAndName(String name, String healthcarePartyId) {
		return listIdsForName(name, healthcarePartyId, "by_hcparty_name");
	}

	@Override
	@View(name = "of_hcparty_name", map = "classpath:js/patient/Of_hcparty_name_map.js")
	public List<String> listIdsOfHcPartyAndName(String name, String healthcarePartyId) {
		return listIdsForName(name, healthcarePartyId, "of_hcparty_name");
	}

	@Override
	@View(name = "by_hcparty_ssin", map = "classpath:js/patient/By_hcparty_ssin_map.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
	public List<String> listIdsByHcPartyAndSsin(String ssin, String healthcarePartyId) {
		return listIdsForSsin(ssin, healthcarePartyId, "by_hcparty_ssin");
	}

	@Override
	@View(name = "of_hcparty_ssin", map = "classpath:js/patient/Of_hcparty_ssin_map.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
	public List<String> listIdsOfHcPartyAndSsin(String ssin, String healthcarePartyId) {
		return listIdsForSsin(ssin, healthcarePartyId, "of_hcparty_ssin");
	}

	@Override
	@View(name = "by_hcparty_active", map = "classpath:js/patient/By_hcparty_active.js", reduce = "function(keys, values, rereduce) {if (rereduce) {return sum(values);} else {return values.length;}}")
	public List<String> listIdsByActive(boolean active, String healthcarePartyId) {
		return listIdsForActive(active, healthcarePartyId, "by_hcparty_active");
	}

	@Override
	@View(name = "merged_by_date", map = "classpath:js/patient/Merged_by_date.js")
	public List<Patient> listOfMergesAfter(Long date) {
		ViewQuery viewQuery = createQuery("merged_by_date").startKey(date).includeDocs(true);
		return db.queryView(viewQuery, Patient.class);
	}

	@Override
	public Integer countByHcParty(String healthcarePartyId) {
		ViewQuery viewQuery = createQuery("by_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false);
		List<Integer> countArray = db.queryView(viewQuery, Integer.class);
		return (countArray == null || countArray.size()==0)?0:countArray.get(0);
	}

	@Override
	public Integer countOfHcParty(String healthcarePartyId) {
		ViewQuery viewQuery = createQuery("of_hcparty_ssin").reduce(true).startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false);
		List<Integer> countArray = db.queryView(viewQuery, Integer.class);
		return (countArray == null || countArray.size()==0)?0:countArray.get(0);
	}

	@Override
	public List<String> listIdsByHcParty(String healthcarePartyId) {
		ViewQuery viewQuery = createQuery("by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, null)).endKey(ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject())).includeDocs(false);
		return db.queryView(viewQuery, String.class);
	}


	@Override
	@View(name = "by_hcparty_date_of_birth", map = "classpath:js/patient/By_hcparty_date_of_birth_map.js")
	public List<String> listIdsByHcPartyAndDateOfBirth(Integer date, String healthcarePartyId) {
		ViewQuery viewQuery = createQuery("by_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false);
		return db.queryView(viewQuery, String.class);
	}

	@Override
	public List<String> listIdsByHcPartyAndDateOfBirth(Integer startDate, Integer endDate, String healthcarePartyId) {
		ViewQuery viewQuery = createQuery("by_hcparty_date_of_birth").startKey(ComplexKey.of(healthcarePartyId, startDate)).endKey(ComplexKey.of(healthcarePartyId, startDate)).includeDocs(false);
		return db.queryView(viewQuery, String.class);
	}

	@Override
	@View(name = "of_hcparty_date_of_birth", map = "classpath:js/patient/Of_hcparty_date_of_birth_map.js")
	public List<String> listIdsForHcPartyDateOfBirth(Integer date, String healthcarePartyId) {
		ViewQuery viewQuery = createQuery("of_hcparty_date_of_birth").key(ComplexKey.of(healthcarePartyId, date)).includeDocs(false);
		return db.queryView(viewQuery, String.class);
	}

	@Override
	@View(name = "by_hcparty_contains_name", map = "classpath:js/patient/By_hcparty_contains_name_map.js")
	public List<String> listIdsByHcPartyAndNameContainsFuzzy(String searchString, String healthcarePartyId) {
		String name = (searchString!=null)? StringUtils.sanitizeString(searchString):null;
		ViewQuery viewQuery = createQuery("by_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, name == null ? ComplexKey.emptyObject() : name + "\ufff0")).includeDocs(false);
		return new ArrayList<>(new TreeSet<>(db.queryView(viewQuery, String.class)));
	}

	@Override
	@View(name = "of_hcparty_contains_name", map = "classpath:js/patient/Of_hcparty_contains_name_map.js")
	public List<String> listIdsOfHcPartyNameContainsFuzzy(String searchString, String healthcarePartyId) {
		String name = (searchString!=null)? StringUtils.sanitizeString(searchString):null;
		ViewQuery viewQuery = createQuery("of_hcparty_contains_name").startKey(ComplexKey.of(healthcarePartyId, name)).endKey(ComplexKey.of(healthcarePartyId, name == null ? ComplexKey.emptyObject() : name + "\ufff0")).includeDocs(false);
		return new ArrayList<>(new TreeSet<>(db.queryView(viewQuery, String.class)));
	}

	private List<String> listIdsForName(String name, String healthcarePartyId, String viewName) {
		ComplexKey startKey;
		ComplexKey endKey;

		//Not transactional aware
		if (name != null) {
			name = StringUtils.sanitizeString(name);
			startKey = ComplexKey.of(healthcarePartyId, name);
			endKey = ComplexKey.of(healthcarePartyId, name + "\ufff0");
		} else {
			startKey = ComplexKey.of(healthcarePartyId, null);
			endKey = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject());
		}

		ViewQuery viewQuery = createQuery(viewName).startKey(startKey).endKey(endKey).includeDocs(false);

		return db.queryView(viewQuery, String.class);
	}

	private List<String> listIdsForSsin(String ssin, String healthcarePartyId, String viewName) {
		ComplexKey startKey;
		ComplexKey endKey;

		//Not transactional aware
		if (ssin != null) {
			ssin = ssin.replaceAll(" ", "").replaceAll("\\W", "");
			startKey = ComplexKey.of(healthcarePartyId, ssin);
			endKey = ComplexKey.of(healthcarePartyId, ssin + "\ufff0");
		} else {
			startKey = ComplexKey.of(healthcarePartyId, null);
			endKey = ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject());
		}

		ViewQuery viewQuery = createQuery(viewName).reduce(false).startKey(startKey).endKey(endKey).includeDocs(false);

		return db.queryView(viewQuery, String.class);
	}

	private List<String> listIdsForSsins(Collection<String> ssins, String healthcarePartyId, String viewName) {
		ViewQuery viewQuery = createQuery(viewName).reduce(false).keys(ssins.stream().map(ssin->ComplexKey.of(healthcarePartyId,ssin)).collect(Collectors.toList())).includeDocs(false);
		return db.queryView(viewQuery, String.class);
	}

	private List<String> listIdsForActive(boolean active, String healthcarePartyId, String viewName) {
		ComplexKey onlyKey = ComplexKey.of(healthcarePartyId, active ? 1 : 0);
		ViewQuery viewQuery = createQuery(viewName).reduce(false).startKey(onlyKey).endKey(onlyKey).includeDocs(false);
		return db.queryView(viewQuery,String.class);
	}

	@Override
	@View(name = "by_hcparty_externalid", map = "classpath:js/patient/By_hcparty_externalid_map.js")
	public List<String> listIdsByHcPartyAndExternalId(String externalId, String healthcarePartyId) {
		ComplexKey startKey;
		ComplexKey endKey;

		//Not transactional aware
		if (externalId != null) {
			externalId = externalId.replaceAll(" ", "").replaceAll("\\W", "");
			startKey = ComplexKey.of(healthcarePartyId, externalId);
			endKey = ComplexKey.of(healthcarePartyId, externalId + "\ufff0");
		} else {
			startKey = ComplexKey.of(healthcarePartyId, null);
			endKey = ComplexKey.of(healthcarePartyId, "\ufff0");
		}

		ViewQuery viewQuery = createQuery("by_hcparty_externalid").startKey(startKey).endKey(endKey).includeDocs(false);

		return db.queryView(viewQuery, String.class);
	}

	@Override
	public PaginatedList<String> findIdsByHcParty(String healthcarePartyId, PaginationOffset pagination) {
		return pagedQueryViewOfIds("by_hcparty_name", ComplexKey.of(healthcarePartyId, null),ComplexKey.of(healthcarePartyId, ComplexKey.emptyObject()), pagination);
	}

	@Override
	public PaginatedList<Patient> findPatientsByHcPartyAndName(String name, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findByName(name, healthcarePartyId, pagination, descending, "by_hcparty_name");
	}

	@Override
	public PaginatedList<Patient> findPatientsOfHcPartyAndName(String name, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findByName(name, healthcarePartyId, pagination, descending, "of_hcparty_name");
	}

	@Override
	public PaginatedList<Patient> findPatientsByHcPartyAndSsin(String ssin, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findBySsin(ssin, healthcarePartyId, pagination, descending, "by_hcparty_ssin");
	}

	@Override
	public PaginatedList<Patient> findPatientsOfHcPartyAndSsin(String ssin, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findBySsin(ssin, healthcarePartyId, pagination, descending, "of_hcparty_ssin");
	}

	@Override
	@View(name = "by_hcparty_modification_date", map = "classpath:js/patient/By_hcparty_modification_date_map.js")
	public PaginatedList<Patient> findPatientsByHcPartyModificationDate(Long startDate, Long endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findByModificationDate(startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_modification_date");
	}

	@Override
	@View(name = "of_hcparty_modification_date", map = "classpath:js/patient/Of_hcparty_modification_date_map.js")
	public PaginatedList<Patient> findPatientsOfHcPartyModificationDate(Long startDate, Long endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findByModificationDate(startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_modification_date");
	}

	@Override
	public PaginatedList<Patient> findPatientsByHcPartyDateOfBirth(Integer startDate, Integer endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findByDateOfBirth(startDate, endDate, healthcarePartyId, pagination, descending, "by_hcparty_date_of_birth");
	}

	@Override
	public PaginatedList<Patient> findPatientsOfHcPartyDateOfBirth(Integer startDate, Integer endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending) {
		return findByDateOfBirth(startDate, endDate, healthcarePartyId, pagination, descending, "of_hcparty_date_of_birth");
	}


	private PaginatedList<Patient> findByName(String name, String healthcarePartyId, PaginationOffset pagination, boolean descending, String viewName) {
		String startKeyNameKeySuffix = descending ? "\ufff0" : "\u0000";
		String endKeyNameKeySuffix = descending ? "\u0000" : "\ufff0";
		Object smallestKey = descending ? ComplexKey.emptyObject() : null;
		Object largestKey = descending ? null : ComplexKey.emptyObject();

		ComplexKey startKey, endKey;
		if (name == null) {
			startKey = pagination.getStartKey() == null ? ComplexKey.of(healthcarePartyId, smallestKey) : ComplexKey.of((Object[]) pagination.getStartKey());
			endKey = ComplexKey.of(healthcarePartyId, largestKey);
		} else {
			name = StringUtils.sanitizeString(name);
			startKey = pagination.getStartKey() == null ? ComplexKey.of(healthcarePartyId, name + startKeyNameKeySuffix) : ComplexKey.of((Object[]) pagination.getStartKey());
			endKey = ComplexKey.of(healthcarePartyId, name + endKeyNameKeySuffix);
		}
		return pagedQueryView(viewName, startKey, endKey, pagination, descending);
	}

	private PaginatedList<Patient> findBySsin(String ssin, String healthcarePartyId, PaginationOffset pagination, boolean descending, String viewName) {
		String startKeyNameKeySuffix = descending ? "\ufff0" : "\u0000";
		String endKeyNameKeySuffix = descending ? "\u0000" : "\ufff0";
		Object smallestKey = descending ? ComplexKey.emptyObject() : null;
		Object largestKey = descending ? null : ComplexKey.emptyObject();

		ComplexKey startKey, endKey;
		if (ssin == null) {
			startKey = pagination.getStartKey() == null ? ComplexKey.of(healthcarePartyId, smallestKey) : ComplexKey.of((Object[]) pagination.getStartKey());
			endKey = ComplexKey.of(healthcarePartyId, largestKey);
		} else {
			String ssinSearchString = ssin.replaceAll(" ", "").replaceAll("\\W", "");
			startKey = pagination.getStartKey() == null ? ComplexKey.of(healthcarePartyId, ssinSearchString + startKeyNameKeySuffix) : ComplexKey.of((Object[]) pagination.getStartKey());
			endKey = ComplexKey.of(healthcarePartyId, ssinSearchString + endKeyNameKeySuffix);
		}
		return pagedQueryView(viewName, startKey, endKey, pagination, descending);
	}

	private PaginatedList<Patient> findByDateOfBirth(Integer startDate, Integer endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending, String viewName) {
		Integer startKeyStartDate = descending ? endDate : startDate;
		Integer endKeyEndDate = descending ? startDate : endDate;
		Object smallestKey = descending ? ComplexKey.emptyObject() : null;
		Object largestKey = descending ? null : ComplexKey.emptyObject();

		ComplexKey from;
		if (pagination.getStartKey() == null) {
			//If both keys are null, search for null
			from = ComplexKey.of(healthcarePartyId, (startKeyStartDate == null && endKeyEndDate == null) ? null : startKeyStartDate == null ? smallestKey : startKeyStartDate);
		} else {
			from = ComplexKey.of((Object[]) pagination.getStartKey());
		}

		//If both keys are null, search for null
		ComplexKey to = ComplexKey.of(healthcarePartyId, (startKeyStartDate == null && endKeyEndDate == null) ? null : endKeyEndDate == null ? largestKey : endKeyEndDate);

		return pagedQueryView(viewName, from, to, pagination, descending);
	}

	private PaginatedList<Patient> findByModificationDate(Long startDate, Long endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending, String viewName) {
		Long startKeyStartDate = descending ? endDate : startDate;
		Long endKeyEndDate = descending ? startDate : endDate;
		Object smallestKey = descending ? ComplexKey.emptyObject() : null;
		Object largestKey = descending ? null : ComplexKey.emptyObject();

		ComplexKey from;
		if (pagination.getStartKey() == null) {
			from = ComplexKey.of(healthcarePartyId, startKeyStartDate == null ? smallestKey : startKeyStartDate);
		} else {
			from = ComplexKey.of((Object[]) pagination.getStartKey());
		}

		ComplexKey to = ComplexKey.of(healthcarePartyId, endKeyEndDate == null ? largestKey : endKeyEndDate);

		return pagedQueryView(viewName, from, to, pagination, descending);
	}
	@Override
	@View(name = "by_user_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.userId) emit( doc.userId, doc._id )}")
	public Patient findPatientsByUserId(String id) {
        List<Patient> patients = queryView("by_user_id", id);
		return patients.size()>0?patients.get(0):null;
	}

    @Override
	public List<Patient> get(Collection<String> patIds) {
		return getList(patIds);
	}

	@Override
	@View(name = "by_external_id", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.externalId) emit( doc.externalId, doc._id )}")
	public Patient getByExternalId(String externalId) {
		List<Patient> patients = queryView("by_external_id", externalId);
		return patients.size()>0?patients.get(0):null;
	}

	@Override
	@View(name = "deleted_by_delete_date", map =
			"function(doc) {\n" +
			"    if (doc.java_type == 'org.taktik.icure.entities.Patient' && doc.deleted){\n" +
			"      emit(doc.deleted)\n" +
			"    }\n" +
			"}")
	public PaginatedList<Patient> findDeletedPatientsByDeleteDate(Long start, Long end, boolean descending, PaginationOffset paginationOffset) {
    	return pagedQueryView("deleted_by_delete_date", start, end, paginationOffset, descending);
	}

	@Override
	@View(name = "deleted_by_names", map = "classpath:js/patient/Deleted_by_names.js")
	public List<Patient> findDeletedPatientsByNames(String _firstName, String _lastName) {
		String firstName = _firstName == null ? null : StringUtils.sanitizeString(_firstName);
		String lastName = _lastName == null ? null : StringUtils.sanitizeString(_lastName);

		ComplexKey startKey, endKey;
		if (lastName == null && firstName == null) {
			startKey = ComplexKey.of(null, null);
			endKey = ComplexKey.of(ComplexKey.of(), ComplexKey.emptyObject());
		} else if (lastName == null) {
			startKey = ComplexKey.of(ComplexKey.emptyObject(), firstName);
			endKey = ComplexKey.of(ComplexKey.emptyObject(), firstName + "\ufff0");
		} else if (firstName == null) {
			startKey = ComplexKey.of(lastName);
			endKey = ComplexKey.of(lastName + "\ufff0");
		} else {
			startKey = ComplexKey.of(lastName, firstName);
			endKey = ComplexKey.of(lastName + "\ufff0", firstName + "\ufff0");
		}
		List<Patient> deleted_by_names = queryView("deleted_by_names", startKey, endKey);
		if (firstName == null || lastName == null) {
			return deleted_by_names;
		} else {
			// filter firstName because main filtering is done on lastName
			return deleted_by_names.stream()
					.filter((Patient p) -> p.getFirstName() != null && StringUtils.sanitizeString(p.getFirstName()).startsWith(firstName))
					.collect(Collectors.toList());
		}
	}

	@Override
	@View(name = "conflicts", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc._conflicts) emit(doc._id )}")
	public List<Patient> listConflicts() {
    	return queryView("conflicts");
	}

	@Override
	@View(name = "by_modification_date", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Patient' && doc.modified) emit(doc.modified)}")
	public PaginatedList<Patient> listOfPatientsModifiedAfter(Long date, PaginationOffset<Long> paginationOffset) {
		return pagedQueryView("by_modification_date", date, Long.MAX_VALUE, paginationOffset, false);
	}

	@Override
	public List<String> listIdsByHcPartyAndSsins(Collection<String> ssins, String healthcarePartyId) {
		return listIdsForSsins(ssins, healthcarePartyId, "by_hcparty_ssin");
	}

    @Override
    @View(name = "by_hcparty_contains_name_delegate", map = "classpath:js/patient/By_hcparty_contains_name_delegate.js")
    public List<String> listByHcPartyName(String searchString, String healthcarePartyId) {
        String name = (searchString!=null)? StringUtils.sanitizeString(searchString):null;
        ViewQuery viewQuery = createQuery("by_hcparty_contains_name_delegate")
            .startKey(ComplexKey.of(healthcarePartyId, name))
            .endKey(ComplexKey.of(healthcarePartyId, name == null ? ComplexKey.emptyObject() : name + "\ufff0")).includeDocs(false);
        return new ArrayList<>(new TreeSet<>(db.queryView(viewQuery, String.class)));
    }
}
