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

package org.taktik.icure.services.external.rest.v1.dto;

import java.util.Map;
import java.util.Set;

import org.taktik.icure.services.external.rest.v1.dto.embed.ValorisationDto;

public class TarificationDto extends CodeDto {
	Set<ValorisationDto> valorisations;
	Map<String,String> category;
	Boolean	consultationCode;

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
}
