package org.taktik.icure.dto.filter.healthelement;

import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.entities.HealthElement;

public interface HealthElementByHcPartyTagCodeFilter extends Filter<String, HealthElement> {
	String getHealthCarePartyId();

	String getCodeType();

	String getCodeNumber();

	String getTagType();

	String getTagCode();

	Integer getStatus();
}
