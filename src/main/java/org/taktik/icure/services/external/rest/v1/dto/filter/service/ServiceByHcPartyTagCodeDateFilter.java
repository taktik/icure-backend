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


import com.google.common.base.Objects;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

@JsonPolymorphismRoot(Filter.class)
public class ServiceByHcPartyTagCodeDateFilter extends Filter<Service> implements org.taktik.icure.dto.filter.service.ServiceByHcPartyTagCodeDateFilter {
	String healthcarePartyId;
	String patientSecretForeignKey;
	String tagType;
	String tagCode;
	String codeType;
	String codeCode;
	Long startValueDate;
	Long endValueDate;

	public ServiceByHcPartyTagCodeDateFilter() {
	}

	public ServiceByHcPartyTagCodeDateFilter(String healthcarePartyId, String patientSecretForeignKey, String tagType, String tagCode, String codeType, String codeCode, Long startServiceValueDate, Long endServiceValueDate) {
		this.healthcarePartyId = healthcarePartyId;
		this.patientSecretForeignKey = patientSecretForeignKey;
		this.tagType = tagType;
		this.tagCode = tagCode;
		this.codeType = codeType;
		this.codeCode = codeCode;
		this.startValueDate = startServiceValueDate;
		this.endValueDate = endServiceValueDate;
	}

	@Override
	public String getHealthcarePartyId() {
		return healthcarePartyId;
	}

	public void setHealthcarePartyId(String healthcarePartyId) {
		this.healthcarePartyId = healthcarePartyId;
	}

	@Override
	public String getPatientSecretForeignKey() {
		return patientSecretForeignKey;
	}

	public void setPatientSecretForeignKey(String patientSecretForeignKey) {
		this.patientSecretForeignKey = patientSecretForeignKey;
	}

	@Override
	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	@Override
	public String getTagCode() {
		return tagCode;
	}

	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}

	@Override
	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	@Override
	public String getCodeCode() {
		return codeCode;
	}

	public void setCodeCode(String codeCode) {
		this.codeCode = codeCode;
	}

	public Long getStartValueDate() {
		return startValueDate;
	}

	public void setStartValueDate(Long startValueDate) {
		this.startValueDate = startValueDate;
	}

	public Long getEndValueDate() {
		return endValueDate;
	}

	public void setEndValueDate(Long endValueDate) {
		this.endValueDate = endValueDate;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(healthcarePartyId, tagType, tagCode, codeType, codeCode, startValueDate, endValueDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final ServiceByHcPartyTagCodeDateFilter other = (ServiceByHcPartyTagCodeDateFilter) obj;
		return Objects.equal(this.healthcarePartyId, other.healthcarePartyId) && Objects.equal(this.patientSecretForeignKey, other.patientSecretForeignKey) && Objects.equal(this.tagType, other.tagType) && Objects.equal(this.tagCode, other.tagCode) && Objects.equal(this.codeType, other.codeType) && Objects.equal(this.codeCode, other.codeCode) && Objects.equal(this.startValueDate, other.startValueDate) && Objects.equal(this.endValueDate, other.endValueDate);
	}

	@Override
	public boolean matches(Service item) {
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
				&& (patientSecretForeignKey == null || (item.getSecretForeignKeys() != null && item.getSecretForeignKeys().contains(patientSecretForeignKey)))
				&& (tagType == null || item.getTags().stream().filter(t -> tagType.equals(t.getType()) && (tagCode == null || tagCode.equals(t.getCode()))).findAny().isPresent())
				&& (codeType == null || (item.getCodes().stream().filter(c -> codeType.equals(c.getType()) && (codeCode == null || codeCode.equals(c.getCode()))).findAny().isPresent()))
				&& (startValueDate == null || (item.getValueDate() != null && item.getValueDate() > startValueDate) || (item.getOpeningDate() != null && item.getOpeningDate() > startValueDate))
				&& (endValueDate == null || (item.getValueDate() != null && item.getValueDate() < endValueDate) || (item.getOpeningDate() != null && item.getOpeningDate() < endValueDate));
	}
}
