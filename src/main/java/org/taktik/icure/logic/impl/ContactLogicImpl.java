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

import com.google.common.collect.Lists;
import org.ektorp.UpdateConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.ContactDAO;
import org.taktik.icure.dao.Option;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginatedDocumentKeyIdPair;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.dto.data.LabelledOccurence;
import org.taktik.icure.dto.filter.chain.FilterChain;
import org.taktik.icure.dto.filter.predicate.Predicate;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.entities.embed.SubContact;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.ContactLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.impl.filter.Filters;
import org.taktik.icure.utils.FuzzyValues;
import org.taktik.icure.validation.aspect.Check;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

@org.springframework.stereotype.Service
public class ContactLogicImpl extends GenericLogicImpl<Contact, ContactDAO> implements ContactLogic {
	private static final Logger logger = LoggerFactory.getLogger(ContactLogicImpl.class);

	private Validator validator;

	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	private ContactDAO contactDAO;
	private UUIDGenerator uuidGenerator;
	private ICureSessionLogic sessionLogic;
	private org.taktik.icure.logic.impl.filter.Filters filters;

	@Autowired
	public void setContactDAO(ContactDAO contactDAO) {
		this.contactDAO = contactDAO;
	}

	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	@Override
	public Contact getContact(String id) {
		return contactDAO.getContact(id);
	}

	@Override
	public List<Contact> getContacts(Collection<String> selectedIds) {
		return contactDAO.get(selectedIds);
	}

	@Override
	public List<Contact> findByHCPartyPatient(String hcPartyId, List<String> secretPatientKeys) {
		return contactDAO.findByHcPartyPatient(hcPartyId, secretPatientKeys);
	}

	@Override
	public Contact addDelegation(String contactId, Delegation delegation) {
		Contact contact = getContact(contactId);
		contact.addDelegation(delegation.getDelegatedTo(), delegation);

		return contactDAO.save(contact);
	}

	@Override
	public Contact createContact(@Check @NotNull Contact contact) throws MissingRequirementsException {
		List<Contact> createdContacts = new ArrayList<>(1);
		try {
			// Fetching the hcParty
			String healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId();

			// Setting contact attributes
			if (contact.getId() == null) {
				contact.setId(uuidGenerator.newGUID().toString());
			}
			if (contact.getOpeningDate() == null) {
				contact.setOpeningDate(FuzzyValues.getFuzzyDateTime(LocalDateTime.now(), ChronoUnit.SECONDS));
			}

			contact.setAuthor(sessionLogic.getCurrentUserId());
			if (contact.getResponsible() == null) {
				contact.setResponsible(healthcarePartyId);
			}

			contact.setHealthcarePartyId(healthcarePartyId);

			createEntities(Collections.singleton(contact), createdContacts);
		} catch (Exception e) {
			logger.error("createContact: " + e.getMessage());
			throw new IllegalArgumentException("Invalid contact", e);
		}
		return createdContacts.size() == 0 ? null : createdContacts.get(0);
	}

	@Override
	public Set<String> deleteContacts(Set<String> ids) {
		try {
			deleteEntities(ids);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return ids;
	}

	@Override
	public Contact modifyContact(@Check @NotNull Contact contact) throws MissingRequirementsException {
		try {
			return contactDAO.save(contact);
		} catch (UpdateConflictException e) {
			//	return resolveConflict(contact, e);
			logger.warn("Documents of class {} with id {} and rev {} could not be merged",contact.getClass().getSimpleName(),contact.getId(),contact.getRev());
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid contact", e);
		}
	}

	@Override
	public List<Service> getServices(Collection<String> selectedIds) {
		Set<String> serviceIdsFilter = new HashSet<>(selectedIds);
		List<String> contactIds = new ArrayList<>(contactDAO.listIdsByServices(selectedIds));

		Map<String, Service> m = new HashMap<>();

		//Treat contacts by 10
		for (List<String> contactIdsBy10 : Lists.partition(contactIds, 10)) {
			List<Contact> contacts = contactDAO.get(contactIdsBy10);
			for (Contact c : contacts) {
				for (Service s : c.getServices()) {
					if (serviceIdsFilter.contains(s.getId())) {
						Service ps = m.get(s.getId());
						if (ps == null || ps.getModified() == null || s.getModified() != null && s.getModified() > ps.getModified()) {
							m.put(s.getId(), s);

							pimpServiceWithContactInformation(s, c);
						}
					}
				}
			}
		}

		return selectedIds.stream().map(m::get).collect(Collectors.toList());
	}

	@Override
	public Service pimpServiceWithContactInformation(Service s, Contact c) {
		s.setContactId(c.getId());
		s.setSecretForeignKeys(c.getSecretForeignKeys());
		s.setCryptedForeignKeys(c.getCryptedForeignKeys());
		List<SubContact> subContacts = c.getSubContacts().stream().filter(sc -> sc.getServices().stream().anyMatch(sl -> sl.getServiceId().equals(s.getId()))).collect(Collectors.toList());
		s.setSubContactIds(subContacts.stream().map(SubContact::getId).collect(Collectors.toSet()));
		s.setPlansOfActionIds(subContacts.stream().map(SubContact::getPlanOfActionId).filter(Objects::nonNull).collect(Collectors.toSet()));
		s.setHealthElementsIds(subContacts.stream().map(SubContact::getHealthElementId).filter(Objects::nonNull).collect(Collectors.toSet()));
		s.setDelegations(c.getDelegations());
		s.setEncryptionKeys(c.getEncryptionKeys());

		return s;
	}

	@Override
	public List<String> findServicesByTag(String hcPartyId, String patientSecretForeignKey, String tagType, String tagCode, Long startValueDate, Long endValueDate) {
		return patientSecretForeignKey == null ? contactDAO.findServicesByTag(hcPartyId, tagType, tagCode, startValueDate, endValueDate) : contactDAO.findServicesByPatientTag(hcPartyId, patientSecretForeignKey, tagType, tagCode, startValueDate, endValueDate);
	}

	@Override
	public List<String> findServicesByCode(String hcPartyId, String patientSecretForeignKey, String codeType, String codeCode, Long startValueDate, Long endValueDate) {
		return patientSecretForeignKey == null ? contactDAO.findServicesByCode(hcPartyId, codeType, codeCode, startValueDate, endValueDate) : contactDAO.findServicesByPatientCode(hcPartyId, patientSecretForeignKey, codeType, codeCode, startValueDate, endValueDate);
	}

	@Override
	public List<String> findByServices(Collection<String> services) {
		return contactDAO.findByServices(services);
	}

	@Override
	public List<Contact> findContactsByHCPartyFormId(String hcPartyId, String formId) {
		return contactDAO.findByHcPartyFormId(hcPartyId, formId);
	}

	@Override
	public List<LabelledOccurence> getServiceCodesOccurences(String hcPartyId, String codeType, long minOccurences) {
		return contactDAO.listCodesFrequencies(hcPartyId, codeType).parallelStream()
				.filter(v -> v.getValue() != null && v.getValue() >= minOccurences)
				.map(v -> new LabelledOccurence((String) v.getKey().getComponents().get(2), v.getValue()))
				.sorted(Comparator.comparing(LabelledOccurence::getOccurence).reversed())
				.collect(Collectors.toList());
	}


	@Override
	public List<Contact> findContactsByHCPartyFormIds(String hcPartyId, List<String> ids) {
		return contactDAO.findByHcPartyFormIds(hcPartyId, ids);
	}

	@Override
	protected ContactDAO getGenericDAO() {
		return contactDAO;
	}

	private PaginatedList<Contact> createContactPaginatedList(PaginationOffset paginationOffset, SortedSet<String> ids, SortedSet<String> sortedIds, ArrayList<String> selectedIds, Predicate predicate) {
		boolean hasNextPage = paginationOffset != null && paginationOffset.getLimit() != null && paginationOffset.getLimit() < sortedIds.size();

		List<Contact> contactList = hasNextPage ? this.getContacts(selectedIds).subList(0, selectedIds.size() - 1) : this.getContacts(selectedIds);
		return new PaginatedList<>(
			hasNextPage ? paginationOffset.getLimit() : sortedIds.size(),
			ids.size(),
			predicate!= null ? contactList.stream().filter(predicate::apply).collect(Collectors.toList()) : contactList,
			hasNextPage ? new PaginatedDocumentKeyIdPair(null,selectedIds.get(selectedIds.size()-1)) : null
		);
	}

	private PaginatedList<Service> createServicePaginatedList(PaginationOffset paginationOffset, SortedSet<String> ids, SortedSet<String> sortedIds, ArrayList<String> selectedIds, FilterChain<Service> filter) {
		boolean hasNextPage = paginationOffset != null && paginationOffset.getLimit() != null && paginationOffset.getLimit() < sortedIds.size();

		List<Service> serviceList = hasNextPage ? this.getServices(selectedIds).subList(0, selectedIds.size() - 1) : this.getServices(selectedIds);

		return new PaginatedList<>(
			hasNextPage ? paginationOffset.getLimit() : sortedIds.size(),
			ids.size(),
			filter.applyTo(serviceList),
			hasNextPage ? new PaginatedDocumentKeyIdPair(null,selectedIds.get(selectedIds.size()-1)) : null
		);
	}


	@Override
	public PaginatedList<Contact> filterContacts(PaginationOffset paginationOffset, FilterChain<Contact> filter) {
		SortedSet<String> ids = new TreeSet<>(filters.resolve(filter.getFilter()));

		// Sorted Id's
		SortedSet<String> sortedIds;
		if (paginationOffset != null && paginationOffset.getStartDocumentId() != null) {
			// Sub-set starting from startDocId to the end (including last element)
			sortedIds = ids.subSet(paginationOffset.getStartDocumentId(), ((TreeSet) ids).last() + "\0");
		} else {
			sortedIds = ids;
		}

		// Selected Id's
		ArrayList<String> selectedIds;
		if (paginationOffset != null && paginationOffset.getLimit() != null) {
			// Fetching one more contacts for the start key of the next page
			selectedIds = sortedIds.size() > paginationOffset.getLimit() ? new ArrayList<>(new ArrayList<>(sortedIds).subList(0, paginationOffset.getLimit() + 1)) : new ArrayList<>(sortedIds);
		} else {
			selectedIds = new ArrayList<>(sortedIds);
		}

		return createContactPaginatedList(paginationOffset, ids, sortedIds, selectedIds, filter.getPredicate());
	}

	@Override
	public PaginatedList<Service> filterServices(PaginationOffset paginationOffset, FilterChain<Service> filter) {
		SortedSet<String> ids = new TreeSet<>(filters.resolve(filter.getFilter()));

		// Sorted Id's
		SortedSet<String> sortedIds;
		if (paginationOffset != null && paginationOffset.getStartDocumentId() != null) {
			// Sub-set starting from startDocId to the end (including last element)
			sortedIds = ids.subSet(paginationOffset.getStartDocumentId(), ((TreeSet) ids).last() + "\0");
		} else {
			sortedIds = ids;
		}

		// Selected Id's
		ArrayList<String> selectedIds;
		if (paginationOffset != null && paginationOffset.getLimit() != null) {
			// Fetching one more contacts for the start key of the next page
			selectedIds = sortedIds.size() > paginationOffset.getLimit() ? new ArrayList<>(new ArrayList<>(sortedIds).subList(0, paginationOffset.getLimit() + 1)) : new ArrayList<>(sortedIds);
		} else {
			selectedIds = new ArrayList<>(sortedIds);
		}

		return createServicePaginatedList(paginationOffset, ids, sortedIds, selectedIds, filter);
	}

	@Override
	public void solveConflicts() {
		List<Contact> contactsInConflict = contactDAO.listConflicts().stream().map(it -> contactDAO.get(it.getId(), Option.CONFLICTS)).collect(Collectors.toList());
		contactsInConflict.forEach(ctc -> {
			Arrays.stream(ctc.getConflicts()).map(c -> contactDAO.get(ctc.getId(), c)).forEach(cp -> {
				ctc.solveConflictWith(cp);
				contactDAO.purge(cp);
			});
			contactDAO.save(ctc);
		});
	}

}
