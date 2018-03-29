/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.filter.service;

import java.util.Objects;

import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

public class ServiceByHcPartyLabelFilter extends Filter<Service> implements org.taktik.icure.dto.filter.service.ServiceByHcPartyLabelFilter {
	String healthcarePartyId;
	String patientSecretForeignKey;
	String label;
	Long startValueDate;
	Long endValueDate;

	public ServiceByHcPartyLabelFilter() {
	}

	public ServiceByHcPartyLabelFilter(String healthcarePartyId, String patientSecretForeignKey, String label, Long startValueDate, Long endValueDate) {
		this.endValueDate = endValueDate;
		this.healthcarePartyId = healthcarePartyId;
		this.label = label;
		this.patientSecretForeignKey = patientSecretForeignKey;
		this.startValueDate = startValueDate;
	}

	@Override
	public Long getEndValueDate() {
		return endValueDate;
	}

	public void setEndValueDate(Long endValueDate) {
		this.endValueDate = endValueDate;
	}

	@Override
	public String getHealthcarePartyId() {
		return healthcarePartyId;
	}

	public void setHealthcarePartyId(String healthcarePartyId) {
		this.healthcarePartyId = healthcarePartyId;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getPatientSecretForeignKey() {
		return patientSecretForeignKey;
	}

	public void setPatientSecretForeignKey(String patientSecretForeignKey) {
		this.patientSecretForeignKey = patientSecretForeignKey;
	}

	@Override
	public Long getStartValueDate() {
		return startValueDate;
	}

	public void setStartValueDate(Long startValueDate) {
		this.startValueDate = startValueDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceByHcPartyLabelFilter that = (ServiceByHcPartyLabelFilter) o;
		return Objects.equals(healthcarePartyId, that.healthcarePartyId) &&
				Objects.equals(patientSecretForeignKey, that.patientSecretForeignKey) &&
				Objects.equals(label, that.label) &&
				Objects.equals(startValueDate, that.startValueDate) &&
				Objects.equals(endValueDate, that.endValueDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(healthcarePartyId, patientSecretForeignKey, label, startValueDate, endValueDate);
	}

	@Override
	public boolean matches(Service item) {
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
				&& (patientSecretForeignKey == null || (item.getSecretForeignKeys() != null && item.getSecretForeignKeys().contains(patientSecretForeignKey)))
				&& (label == null || (label.equals(item.getLabel())))
				&& (startValueDate == null || (item.getValueDate() != null && item.getValueDate() > startValueDate) || (item.getOpeningDate() != null && item.getOpeningDate() > startValueDate))
				&& (endValueDate == null || (item.getValueDate() != null && item.getValueDate() < endValueDate) || (item.getOpeningDate() != null && item.getOpeningDate() < endValueDate));
	}
}
