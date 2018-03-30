/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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
public class PatientByHcPartyAndExternalIdFilter extends Filter<Patient> implements org.taktik.icure.dto.filter.patient.PatientByHcPartyAndExternalIdFilter {
    String externalId;
    String healthcarePartyId;

    public PatientByHcPartyAndExternalIdFilter() {
    }

    public PatientByHcPartyAndExternalIdFilter(String ssin, String healthcarePartyId) {
        this.externalId = ssin;
        this.healthcarePartyId = healthcarePartyId;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

        PatientByHcPartyAndExternalIdFilter that = (PatientByHcPartyAndExternalIdFilter) o;

        return Objects.equal(this.externalId, that.externalId) &&
                Objects.equal(this.healthcarePartyId, that.healthcarePartyId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(externalId, healthcarePartyId);
    }

	@Override
	public boolean matches(Patient item) {
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId)) && (externalId==null || externalId.equals(item.getExternalId()));
	}
}
