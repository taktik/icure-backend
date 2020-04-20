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

package org.taktik.icure.services.external.rest.v1.dto.filter.contact;

import com.google.common.base.Objects;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import java.util.List;

@JsonPolymorphismRoot(Filter.class)
public class ContactByHcPartyTagCodeDateFilter extends Filter<Contact> implements org.taktik.icure.dto.filter.contact.ContactByHcPartyTagCodeDateFilter {
	String healthcarePartyId;
	String tagType;
	String tagCode;
	String codeType;
	String codeCode;
	Long startOfContactOpeningDate;
	Long endOfContactOpeningDate;

	public ContactByHcPartyTagCodeDateFilter() {
	}

	public ContactByHcPartyTagCodeDateFilter(String healthcarePartyId, List<String> patientSecretForeignKeys, String tagType, String tagCode, String codeType, String codeCode, Long startOfContactOpeningDate, Long endOfContactOpeningDate) {
		this.healthcarePartyId = healthcarePartyId;
		this.tagType = tagType;
		this.tagCode = tagCode;
		this.codeType = codeType;
		this.codeCode = codeCode;
		this.startOfContactOpeningDate = startOfContactOpeningDate;
		this.endOfContactOpeningDate = endOfContactOpeningDate;
	}

	@Override
	public String getHealthcarePartyId() {
		return healthcarePartyId;
	}

	public void setHealthcarePartyId(String healthcarePartyId) {
		this.healthcarePartyId = healthcarePartyId;
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

	public Long getStartOfContactOpeningDate() {
		return startOfContactOpeningDate;
	}

	public void setStartOfContactOpeningDate(Long startOfContactOpeningDate) {
		this.startOfContactOpeningDate = startOfContactOpeningDate;
	}

	public Long getEndOfContactOpeningDate() {
		return endOfContactOpeningDate;
	}

	public void setEndOfContactOpeningDate(Long endOfContactOpeningDate) {
		this.endOfContactOpeningDate = endOfContactOpeningDate;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(healthcarePartyId, tagType, tagCode, codeType, codeCode, startOfContactOpeningDate, endOfContactOpeningDate);
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
		return Objects.equal(this.healthcarePartyId, other.healthcarePartyId) && Objects.equal(this.tagType, other.tagType) && Objects.equal(this.tagCode, other.tagCode) && Objects.equal(this.codeType, other.codeType) && Objects.equal(this.codeCode, other.codeCode) && Objects.equal(this.startOfContactOpeningDate, other.startOfContactOpeningDate) && Objects.equal(this.endOfContactOpeningDate, other.endOfContactOpeningDate);
	}

	@Override
	public boolean matches(Contact item) {
		return (healthcarePartyId == null || item.getDelegations().keySet().contains(healthcarePartyId))
				&& (tagType == null || item.getServices().stream().filter(s ->
				(s.getTags().stream().filter(t -> tagType.equals(t.getType()) && (tagCode == null || tagCode.equals(t.getCode()))).findAny().isPresent())
						&& (codeType == null || (s.getCodes().stream().filter(c -> codeType.equals(c.getType()) && (codeCode == null || codeCode.equals(c.getCode()))).findAny().isPresent()))
						&& (startOfContactOpeningDate == null || (s.getValueDate() != null && s.getValueDate() > startOfContactOpeningDate) || (s.getOpeningDate() != null && s.getOpeningDate() > startOfContactOpeningDate))
						&& (endOfContactOpeningDate == null || (s.getValueDate() != null && s.getValueDate() < endOfContactOpeningDate) || (s.getOpeningDate() != null && s.getOpeningDate() < endOfContactOpeningDate))
		).findAny().isPresent());
	}
}
