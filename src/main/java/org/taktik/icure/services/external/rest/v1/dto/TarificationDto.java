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

package org.taktik.icure.services.external.rest.v1.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.taktik.icure.entities.embed.LetterValue;
import org.taktik.icure.services.external.rest.v1.dto.embed.ValorisationDto;

public class TarificationDto extends CodeDto {
	Set<ValorisationDto> valorisations;
	Map<String,String> category;
	Boolean	consultationCode;
	Boolean hasRelatedCode;
	Boolean needsPrescriber;
	Set<String> relatedCodes;
	String nGroup;
	List<LetterValue> letterValues;

	public Set<ValorisationDto> getValorisations() {
		return valorisations;
	}

	public void setValorisations(Set<ValorisationDto> valorisations) {
		this.valorisations = valorisations;
	}

	public Map<String, String> getCategory() {
		return category;
	}

	public void setCategory(Map<String, String> category) {
		this.category = category;
	}

	public Boolean getConsultationCode() {
		return consultationCode;
	}

	public void setConsultationCode(Boolean consultationCode) {
		this.consultationCode = consultationCode;
	}

	public Boolean getHasRelatedCode() {
		return hasRelatedCode;
	}

	public void setHasRelatedCode(Boolean hasRelatedCode) {
		this.hasRelatedCode = hasRelatedCode;
	}

	public Boolean getNeedsPrescriber() {
		return needsPrescriber;
	}

	public void setNeedsPrescriber(Boolean needsPrescriber) {
		this.needsPrescriber = needsPrescriber;
	}

	public Set<String> getRelatedCodes() {
		return relatedCodes;
	}

	public void setRelatedCodes(Set<String> relatedCodes) {
		this.relatedCodes = relatedCodes;
	}

	public String getnGroup() { return nGroup; }

	public void setnGroup(String nGroup) { this.nGroup = nGroup; }

	public List<LetterValue> getLetterValues() { return letterValues; }

	public void setLetterValues(List<LetterValue> letterValues) { this.letterValues = letterValues; }
}
