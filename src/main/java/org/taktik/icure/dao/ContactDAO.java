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
import org.taktik.icure.dao.impl.ektorp.CouchKeyValue;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.db.PaginationOffset;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ContactDAO extends GenericDAO<Contact> {
    Contact getContact(String id);

    List<Contact> get(Collection<String> contactIds);

    List<String> listContactIds(String hcPartyId);

    List<Contact> findByHcPartyPatient(String hcPartyId, List<String> secretPatientKeys);

    List<String> listServiceIdsByTag(String hcPartyId, String tagType, String tagCode, Long startValueDate, Long endValueDate);

    List<String> listServiceIdsByPatientTag(String hcPartyId, List<String> patientSecretForeignKeys, String tagType, String tagCode, Long startValueDate, Long endValueDate);

    List<String> listServiceIdsByCode(String hcPartyId, String codeType, String codeCode, Long startValueDate, Long endValueDate);

    List<String> listContactIdsByTag(String hcPartyId, String tagType, String tagCode, Long startValueDate, Long endValueDate);

    List<String> listContactIdsByCode(String hcPartyId, String codeType, String codeCode, Long startValueDate, Long endValueDate);

    List<CouchKeyValue<Long>> listCodesFrequencies(String hcPartyId, String codeType);

	List<String> findServicesByForeignKeys(String hcPartyId, List<String> patientSecretForeignKeys, String codeType, String codeCode, Long startValueDate, Long endValueDate);

    List<String> findServicesByForeignKeys(String hcPartyId, Set<String> patientSecretForeignKeys);

    List<Contact> listByServices(Collection<String> services);

    List<String> findByServices(Collection<String> services);

    PaginatedList<Contact> listContacts(String hcPartyId, PaginationOffset<String> pagination);

    PaginatedList<Contact> listContactsByOpeningDate(String hcPartyId, Long startOpeningDate, Long endOpeningDate, PaginationOffset pagination);

    List<Contact> findByHcPartyFormId(String hcPartyId, String formId);

	Set<String> listIdsByServices(Collection<String> services);

	List<Contact> findByHcPartyFormIds(String hcPartyId, List<String> ids);

	List<Contact> listConflicts();
}
