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

import com.google.common.base.Objects;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

@JsonPolymorphismRoot(Filter.class)
public class PatientByHcPartyDateOfBirthFilter extends Filter<Patient> implements org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthFilter {
    private Integer dateOfBirth;
    private String healthcarePartyId;

    public PatientByHcPartyDateOfBirthFilter() {
    }

    public PatientByHcPartyDateOfBirthFilter(Integer dateOfBirth, String healthcarePartyId) {
        this.dateOfBirth = dateOfBirth;
        this.healthcarePartyId = healthcarePartyId;
    }

    @Override
    public Integer getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Integer dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

        PatientByHcPartyDateOfBirthFilter that = (PatientByHcPartyDateOfBirthFilter) o;

        return Objects.equal(this.dateOfBirth, that.dateOfBirth) &&
                Objects.equal(this.healthcarePartyId, that.healthcarePartyId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dateOfBirth, healthcarePartyId);
    }

	@Override
	public boolean matches(Patient item) {
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
				&& (dateOfBirth == null || item.getDateOfBirth()!=null && java.util.Objects.equals(item.getDateOfBirth(), dateOfBirth));
	}

}
