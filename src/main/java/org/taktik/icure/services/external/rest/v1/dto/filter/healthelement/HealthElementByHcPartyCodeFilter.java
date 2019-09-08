package org.taktik.icure.services.external.rest.v1.dto.filter.healthelement;

import com.google.common.base.Objects;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

@JsonPolymorphismRoot(Filter.class)
public class HealthElementByHcPartyCodeFilter extends Filter<HealthElement> implements org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyCodeFilter {
	String healthCarePartyId;
	String codeType;
	String codeNumber;

	public HealthElementByHcPartyCodeFilter() {
	}

	public HealthElementByHcPartyCodeFilter(String healthCarePartyId, String codeType, String codeNumber) {
		this.healthCarePartyId = healthCarePartyId;
		this.codeType = codeType;
		this.codeNumber = codeNumber;
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

	public void setHealthCarePartyId(String healthCarePartyId) {
		this.healthCarePartyId = healthCarePartyId;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public void setCodeNumber(String codeNumber) {
		this.codeNumber = codeNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(healthCarePartyId, codeType, codeNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final HealthElementByHcPartyCodeFilter other = (HealthElementByHcPartyCodeFilter) obj;
		return Objects.equal(this.healthCarePartyId, other.healthCarePartyId) && Objects.equal(this.codeType, other.codeType) && Objects.equal(this.codeNumber, other.codeNumber);
	}

	@Override
	public boolean matches(HealthElement item) {
		return (healthCarePartyId == null || item.getDelegations().keySet().contains(healthCarePartyId))
				&& (item.getCodes().stream().filter(code -> codeType.equals(code.getType()) && codeNumber.equals(code.getCode())).findAny().isPresent());
	}
}