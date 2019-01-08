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
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Patient;

import java.util.Collection;
import java.util.List;

public interface PatientDAO extends GenericDAO<Patient> {

	List<String> listIdsByHcPartyAndName(String searchString, String healthcarePartyId);
	List<String> listIdsOfHcPartyAndName(String searchString, String healthcarePartyId);
	List<String> listIdsByHcPartyAndSsin(String ssin, String healthcarePartyId);
	List<String> listIdsOfHcPartyAndSsin(String ssin, String healthcarePartyId);
	List<String> listIdsByActive(boolean active, String healthcarePartyId);

	List<Patient> listOfMergesAfter(Long date);

	Integer countByHcParty(String healthcarePartyId);

	Integer countOfHcParty(String healthcarePartyId);

	List<String> listIdsByHcParty(String healthcarePartyId);
	List<String> listIdsByHcPartyAndDateOfBirth(Integer date, String healthcarePartyId);
	List<String> listIdsByHcPartyAndDateOfBirth(Integer startDate, Integer endDate, String healthcarePartyId);
	List<String> listIdsForHcPartyDateOfBirth(Integer date, String healthcarePartyId);
	List<String> listIdsByHcPartyAndNameContainsFuzzy(String searchString, String healthcarePartyId);
	List<String> listIdsOfHcPartyNameContainsFuzzy(String searchString, String healthcarePartyId);

	List<String> listIdsByHcPartyAndExternalId(String externalId, String healthcarePartyId);

	PaginatedList<String> findIdsByHcParty(String healthcarePartyId, PaginationOffset pagination);
	PaginatedList<Patient> findPatientsByHcPartyAndName(String name, String healthcarePartyId, PaginationOffset pagination, boolean descending);
	PaginatedList<Patient> findPatientsOfHcPartyAndName(String name, String healthcarePartyId, PaginationOffset offset, boolean descending);
	PaginatedList<Patient> findPatientsByHcPartyAndSsin(String ssin, String healthcarePartyId, PaginationOffset pagination, boolean descending);
	PaginatedList<Patient> findPatientsOfHcPartyAndSsin(String ssin, String healthcarePartyId, PaginationOffset offset, boolean descending);

	PaginatedList<Patient> findPatientsByHcPartyModificationDate(Long startDate, Long endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending);

	PaginatedList<Patient> findPatientsOfHcPartyModificationDate(Long startDate, Long endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending);

	PaginatedList<Patient> findPatientsByHcPartyDateOfBirth(Integer startDate, Integer endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending);
	PaginatedList<Patient> findPatientsOfHcPartyDateOfBirth(Integer startDate, Integer endDate, String healthcarePartyId, PaginationOffset pagination, boolean descending);


	Patient findPatientsByUserId(String id);
	List<Patient> get(Collection<String> patIds);

	Patient getByExternalId(String externalId);

    PaginatedList<Patient> findDeletedPatientsByDeleteDate(Long start, Long end, boolean descending, PaginationOffset paginationOffset);

	List<Patient> findDeletedPatientsByNames(String firstName, String lastName);

	List<Patient> listConflicts();

	PaginatedList<Patient> listOfPatientsModifiedAfter(Long date, PaginationOffset<Long> paginationOffset);

	List<String> listIdsByHcPartyAndSsins(Collection<String> ssins, String healthcarePartyId);

    List<String> listByHcPartyName(String searchString, String healthcarePartyId);
}
