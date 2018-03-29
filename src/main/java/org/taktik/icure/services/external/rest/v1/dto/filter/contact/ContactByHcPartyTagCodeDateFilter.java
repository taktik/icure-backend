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

package org.taktik.icure.services.external.rest.v1.dto.filter.contact;


import com.google.common.base.Objects;
import org.taktik.icure.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

@JsonPolymorphismRoot(Filter.class)
public class ContactByHcPartyTagCodeDateFilter extends Filter<Contact> implements ContactByHcPartyPatientTagCodeDateFilter {
	String healthcarePartyId;
	String patientSecretForeignKey;
	String tagType;
	String tagCode;
	String codeType;
	String codeCode;
	Long startServiceValueDate;
	Long endServiceValueDate;

	public ContactByHcPartyTagCodeDateFilter() {
	}

	public ContactByHcPartyTagCodeDateFilter(String healthcarePartyId, String patientSecretForeignKey, String tagType, String tagCode, String codeType, String codeCode, Long startServiceValueDate, Long endServiceValueDate) {
		this.healthcarePartyId = healthcarePartyId;
		this.patientSecretForeignKey = patientSecretForeignKey;
		this.tagType = tagType;
		this.tagCode = tagCode;
		this.codeType = codeType;
		this.codeCode = codeCode;
		this.startServiceValueDate = startServiceValueDate;
		this.endServiceValueDate = endServiceValueDate;
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

	public Long getStartServiceValueDate() {
		return startServiceValueDate;
	}

	public void setStartServiceValueDate(Long startServiceValueDate) {
		this.startServiceValueDate = startServiceValueDate;
	}

	public Long getEndServiceValueDate() {
		return endServiceValueDate;
	}

	public void setEndServiceValueDate(Long endServiceValueDate) {
		this.endServiceValueDate = endServiceValueDate;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(healthcarePartyId, tagType, tagCode, codeType, codeCode, startServiceValueDate, endServiceValueDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final ContactByHcPartyTagCodeDateFilter other = (ContactByHcPartyTagCodeDateFilter) obj;
		return Objects.equal(this.healthcarePartyId, other.healthcarePartyId) && Objects.equal(this.patientSecretForeignKey, other.patientSecretForeignKey) && Objects.equal(this.tagType, other.tagType) && Objects.equal(this.tagCode, other.tagCode) && Objects.equal(this.codeType, other.codeType) && Objects.equal(this.codeCode, other.codeCode) && Objects.equal(this.startServiceValueDate, other.startServiceValueDate) && Objects.equal(this.endServiceValueDate, other.endServiceValueDate);
	}

	@Override
	public boolean matches(Contact item) {
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
				&& (patientSecretForeignKey == null || (item.getSecretForeignKeys() != null && item.getSecretForeignKeys().contains(patientSecretForeignKey)))
				&& (tagType == null || item.getServices().stream().filter(s ->
				(s.getTags().stream().filter(t -> tagType.equals(t.getType()) && (tagCode == null || tagCode.equals(t.getCode()))).findAny().isPresent())
						&& (codeType == null || (s.getCodes().stream().filter(c -> codeType.equals(c.getType()) && (codeCode == null || codeCode.equals(c.getCode()))).findAny().isPresent()))
						&& (startServiceValueDate == null || (s.getValueDate() != null && s.getValueDate() > startServiceValueDate) || (s.getOpeningDate() != null && s.getOpeningDate() > startServiceValueDate))
						&& (endServiceValueDate == null || (s.getValueDate() != null && s.getValueDate() < endServiceValueDate) || (s.getOpeningDate() != null && s.getOpeningDate() < endServiceValueDate))
		).findAny().isPresent());
	}
}
