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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.dto.data.LabelledOccurence;
import org.taktik.icure.dto.filter.chain.FilterChain;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.validation.aspect.Check;

/**
 * Created by emad7105 on 24/06/2014.
 */
public interface ContactLogic extends EntityPersister<Contact, String> {
	Contact getContact(String id);

	List<Contact> getContacts(Collection<String> selectedIds);

    List<Service> getServices(Collection<String> selectedIds);

    List<Contact> findByHCPartyPatient(String hcPartyId, List<String> secretPatientKeys);

	Contact addDelegation(String contactId, Delegation delegation);

	Contact createContact(@Check Contact contact) throws MissingRequirementsException;

	Set<String> deleteContacts(Set<String> ids);

	Contact modifyContact(Contact contact) throws MissingRequirementsException;


	Service pimpServiceWithContactInformation(Service s, Contact c);

	List<String> findServicesByTag(String hcPartyId, String patientSecretForeignKey, String tagType, String tagCode, Long startValueDate, Long endValueDate);

    List<String> findServicesByCode(String hcPartyId, String patientSecretForeignKey, String tagType, String tagCode, Long startValueDate, Long endValueDate);

    List<String> findByServices(Collection<String> services);

	List<Contact> findContactsByHCPartyFormId(String hcPartyId, String formId);

	List<LabelledOccurence> getServiceCodesOccurences(String hcPartyId, String codeType, long minOccurences);

	List<Contact> findContactsByHCPartyFormIds(String hcPartyId, List<String> ids);

	PaginatedList<Contact> filterContacts(PaginationOffset paginationOffset, FilterChain<Contact> filter);

	PaginatedList<Service> filterServices(PaginationOffset paginationOffset, FilterChain<Service> filter);

	void solveConflicts();
}
