package org.taktik.icure.dto.filter.code;

import org.taktik.icure.dto.filter.Filter;
import org.taktik.icure.entities.base.Code;

public interface CodeByRegionTypeLabelLanguageFilter extends Filter<String, Code> {
	public String getRegion();
	public String getType();
	public String getLanguage();
	public String getLabel();
}
