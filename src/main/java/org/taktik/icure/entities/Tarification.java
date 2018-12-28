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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.embed.LetterValue;
import org.taktik.icure.entities.embed.Valorisation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tarification extends Code {
	Set<Valorisation> valorisations;
	Map<String,String> category;
	Boolean	consultationCode;
	Boolean hasRelatedCode;
	Boolean needsPrescriber;
	Set<String> relatedCodes;
	String nGroup;
	List<LetterValue> letterValues;

	public Tarification() {
	}

	public Tarification(String typeAndCodeAndVersion) {
		super(typeAndCodeAndVersion);
	}

	public Tarification(String type, String code, String version) {
		super(type, code, version);
	}

	public Tarification(Set<String> regions, String type, String code, String version) {
		super(regions, type, code, version);
	}

	public Tarification(String region, String type, String code, String version) {
		super(region, type, code, version);
	}

	public Tarification(Set<String> regions, String type, String code, String version, Map<String, String> label) {
		super(regions, type, code, version, label);
	}

	public Map<String,String> getCategory() {
		return category;
	}

	public void setCategory(Map<String,String> category) {
		this.category = category;
	}

	public Set<Valorisation> getValorisations() {
		return valorisations;
	}

	public void setValorisations(Set<Valorisation> valorisations) {
		this.valorisations = valorisations;
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

	public String getnGroup() {
		return nGroup;
	}

	public void setnGroup(String nGroup) {
		this.nGroup = nGroup;
	}

	public List<LetterValue> getLetterValues() {
		return letterValues;
	}

	public void setLetterValues(List<LetterValue> letterValues) {
		this.letterValues = letterValues;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Tarification that = (Tarification) o;
		return Objects.equals(valorisations, that.valorisations) &&
				Objects.equals(category, that.category) &&
				Objects.equals(consultationCode, that.consultationCode) &&
				Objects.equals(hasRelatedCode, that.hasRelatedCode) &&
				Objects.equals(needsPrescriber, that.needsPrescriber) &&
				Objects.equals(relatedCodes, that.relatedCodes) &&
				Objects.equals(nGroup, that.nGroup) &&
				Objects.equals(letterValues, that.letterValues);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), valorisations, category, consultationCode, hasRelatedCode, needsPrescriber, relatedCodes, nGroup, letterValues);
	}
}
