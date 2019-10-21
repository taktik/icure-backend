package org.taktik.icure.services.external.rest.v1.dto.filter.healthelement;

import com.google.common.base.Objects;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

@JsonPolymorphismRoot(Filter.class)
public class HealthElementByHcPartyTagCodeFilter extends Filter<HealthElement> implements org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter {
	String healthCarePartyId;
	String codeType;
	String codeNumber;
	String tagType;
	String tagCode;
	Integer status;

	public HealthElementByHcPartyTagCodeFilter() {
	}

	public HealthElementByHcPartyTagCodeFilter(String healthCarePartyId, String codeType, String codeNumber, String tagType, String tagCode) {
		this.healthCarePartyId = healthCarePartyId;
		this.codeType = codeType;
		this.codeNumber = codeNumber;
		this.tagType = tagType;
		this.tagCode = tagCode;
	}

	@Override
	public String getHealthCarePartyId() {
		return healthCarePartyId;
	}

	@Override
	public String getCodeType() {
		return codeType;
	}

	@Override
	public String getCodeNumber() {
		return this.codeNumber;
	}

	@Override
	public String getTagType() {
		return this.tagType;
	}

	@Override
	public String getTagCode() {
		return this.tagCode;
	}

	@Override
	public Integer getStatus() {
		return this.status;
	}

	public void setHealthCarePartyId(String healthCarePartyId) {
		this.healthCarePartyId = healthCarePartyId;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public void setCodeNumber(String codeNumber) {
		this.codeNumber = codeNumber;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(healthCarePartyId, codeType, codeNumber, tagType, tagCode, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final HealthElementByHcPartyTagCodeFilter other = (HealthElementByHcPartyTagCodeFilter) obj;
		return Objects.equal(this.healthCarePartyId, other.healthCarePartyId) && Objects.equal(this.codeType, other.codeType) && Objects.equal(this.codeNumber, other.codeNumber)
				&& Objects.equal(this.tagType, other.tagType) && Objects.equal(this.tagCode, other.tagCode) && Objects.equal(this.status, other.status);
	}

	@Override
	public boolean matches(HealthElement item) {
		return (healthCarePartyId == null || item.getDelegations().keySet().contains(healthCarePartyId))
				&& (codeType == null || (item.getCodes().stream().filter(code -> codeType.equals(code.getType()) && codeNumber.equals(code.getCode())).findAny().isPresent())
				&& (tagType == null || item.getTags().stream().filter(t -> tagType.equals(t.getType()) && (tagCode == null || tagCode.equals(t.getCode()))).findAny().isPresent())
				&& (status == null || item.getStatus() == status));
	}
}