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

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.Sorting;
import org.taktik.icure.dto.filter.chain.FilterChain;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PatientLogic extends EntityPersister<Patient, String> {

	/**
	 */
	Patient addDelegation(String patientId, Delegation delegation);

	Patient addDelegations(String patientId, Collection<Delegation> delegations);

	Patient createPatient(Patient patient) throws MissingRequirementsException;

	List<Patient> fuzzySearchPatients(String healthcarePartyId, String firstName, String lastName, Integer dateOfBirth);

	Set<String> deletePatients(Set<String> ids) throws DocumentNotFoundException;

	PaginatedList<Patient> findDeletedPatientsByDeleteDate(Long start, Long end, boolean descending, PaginationOffset paginationOffset);

    List<Patient> findDeletedPatientsByNames(String firstName, String lastName);

    Set<String> undeletePatients(Set<String> ids) throws DocumentNotFoundException;

	PaginatedList<String> findByHcPartyIdsOnly(String healthcarePartyId, PaginationOffset offset);

	PaginatedList<Patient> findByHcPartyAndSsin(String ssin, String healthcarePartyId, PaginationOffset paginationOffset);

	PaginatedList<Patient> findByHcPartyDateOfBirth(Integer date, String hcPartyId, PaginationOffset pagination);

	PaginatedList<Patient> findByHcPartyModificationDate(Long start, Long end, String healthcarePartyId, boolean descending, PaginationOffset paginationOffset);

	PaginatedList<Patient> findOfHcPartyModificationDate(Long start, Long end, String healthcarePartyId, boolean descending, PaginationOffset paginationOffset);

	PaginatedList<Patient> findByHcPartyNameContainsFuzzy(String searchString, String hcPartyId, PaginationOffset pagination, boolean descending);

	PaginatedList<Patient> findOfHcPartyNameContainsFuzzy(String searchString, String hcPartyId, PaginationOffset pagination, boolean descending);

	List<Patient> listOfMergesAfter(Long date);

	PaginatedList<Patient> findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(String healthcarePartyId, PaginationOffset offset, String filterValue, Sorting sorting);

	PaginatedList<Patient> listPatients(PaginationOffset paginationOffset, FilterChain<Patient> filterChain, String sort, Boolean desc) throws LoginException;

	PaginatedList<Patient> findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(String hcPartyId, PaginationOffset offset, String filterValue, Sorting sorting);

	Integer countByHcParty(String healthcarePartyId);

	Integer countOfHcParty(String healthcarePartyId);

	List<String> listByHcPartyIdsOnly(String healthcarePartyId);

	List<String> listByHcPartyAndSsinIdsOnly(String ssin, String healthcarePartyId);

	List<String> listByHcPartyAndSsinsIdsOnly(Collection<String> ssins, String healthcarePartyId);

    List<String> listByHcPartyDateOfBirthIdsOnly(Integer date, String healthcarePartyId);

	List<String> listByHcPartyDateOfBirthIdsOnly(Integer startDate, Integer endDate, String healthcarePartyId);

	List<String> listByHcPartyNameContainsFuzzyIdsOnly(String searchString, String healthcarePartyId);

    List<String> listByHcPartyName(String searchString, String healthcarePartyId);

    List<String> listByHcPartyAndExternalIdsOnly(String externalId, String healthcarePartyId);

	List<String> listByHcPartyAndActiveIdsOnly(boolean active, String healthcarePartyId);

	Patient findByUserId(String id);

	Patient getPatient(String patientId);

	Map<String, Object> getPatientSummary(PatientDto patientDto, List<String> propertyExpressions);

	List<Patient> getPatients(List<String> selectedIds);

	Patient modifyPatient(Patient patient) throws MissingRequirementsException;

    void logAllPatients(String hcPartyId);

	Patient modifyPatientReferral(Patient patient, String referralId, Instant start, Instant end) throws MissingRequirementsException;

	Patient mergePatient(Patient patient, List<Patient> fromPatients);

	Patient getByExternalId(String externalId);

	void solveConflicts();

	PaginatedList<Patient> listOfPatientsModifiedAfter(Long date, Long startKey, String startDocumentId, Integer limit);
}
