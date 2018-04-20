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

package org.taktik.icure.services.external.rest.v1.dto.filter.patient;

import java.util.Optional;

import com.google.common.base.Objects;
import org.taktik.icure.db.StringUtils;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import static org.taktik.icure.db.StringUtils.*;

@JsonPolymorphismRoot(Filter.class)
public class PatientByHcPartyNameContainsFuzzyFilter extends Filter<Patient> implements org.taktik.icure.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter {

	private String searchString;
	private String healthcarePartyId;

	public PatientByHcPartyNameContainsFuzzyFilter() {
	}

	public PatientByHcPartyNameContainsFuzzyFilter(String searchString, String healthcarePartyId) {
		this.searchString = searchString;
		this.healthcarePartyId = healthcarePartyId;
	}

	@Override
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@Override
	public String getHealthcarePartyId() {
		return healthcarePartyId;
	}

	public void setHealthcarePartyId(String healthcarePartyId) {
		this.healthcarePartyId = healthcarePartyId;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PatientByHcPartyNameContainsFuzzyFilter that = (PatientByHcPartyNameContainsFuzzyFilter) o;

		return Objects.equal(this.searchString, that.searchString) &&
				Objects.equal(this.healthcarePartyId, that.healthcarePartyId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(searchString, healthcarePartyId);
	}

	@Override
	public boolean matches(Patient item) {
		String ss = sanitizeString(searchString);
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
				&& (sanitizeString(Optional.of(item.getLastName()).orElse("") + Optional.of(item.getFirstName()).orElse("")).contains(ss) ||
				sanitizeString(Optional.of(item.getMaidenName()).orElse("")).contains(ss) ||
				sanitizeString(Optional.of(item.getPartnerName()).orElse("")).contains(ss));
	}

}
