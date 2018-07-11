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
import org.taktik.icure.entities.embed.Valorisation;

import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tarification extends Code {
	Set<Valorisation> valorisations;
	Map<String,String> category;
	Boolean	consultationCode;

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
}
