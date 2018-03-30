/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.util.List;

import org.taktik.icure.services.external.rest.v1.dto.CodeDto;

public class SubstanceproductDto {
	protected List<CodeDto> intendedcds;
	protected List<CodeDto> deliveredcds;
	protected String intendedname;
	protected Object deliveredname;

	public List<CodeDto> getIntendedcds() {
		return intendedcds;
	}

	public void setIntendedcds(List<CodeDto> intendedcds) {
		this.intendedcds = intendedcds;
	}

	public List<CodeDto> getDeliveredcds() {
		return deliveredcds;
	}

	public void setDeliveredcds(List<CodeDto> deliveredcds) {
		this.deliveredcds = deliveredcds;
	}

	public String getIntendedname() {
		return intendedname;
	}

	public void setIntendedname(String intendedname) {
		this.intendedname = intendedname;
	}

	public Object getDeliveredname() {
		return deliveredname;
	}

	public void setDeliveredname(Object deliveredname) {
		this.deliveredname = deliveredname;
	}
}
