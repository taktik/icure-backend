package org.taktik.icure.logic.impl.filter.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.logic.CodeLogic;
import org.taktik.icure.logic.impl.filter.Filters;

import java.util.HashSet;
import java.util.Set;

public class CodeByRegionTypeLabelLanguageFilter implements org.taktik.icure.logic.impl.filter.Filter<String, Code, org.taktik.icure.dto.filter.code.CodeByRegionTypeLabelLanguageFilter> {
	CodeLogic codeLogic;

	@Override
	public Set<String> resolve(org.taktik.icure.dto.filter.code.CodeByRegionTypeLabelLanguageFilter filter, Filters context) {
		return new HashSet<>(codeLogic.listCodeIdsByLabel(filter.getRegion(), filter.getLanguage(), filter.getType(), filter.getLabel()));
	}

	@Autowired
	public void setCodeLogic(CodeLogic codeLogic) {
		this.codeLogic = codeLogic;
	}
}
