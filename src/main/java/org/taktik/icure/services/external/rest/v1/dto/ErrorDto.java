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

import java.util.Map;

public class ErrorDto {
	String domain;
	Map<String,Object> errorDescription;
	Map<String,Object> errorResolution;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Map<String, Object> getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(Map<String, Object> errorDescription) {
		this.errorDescription = errorDescription;
	}

	public Map<String, Object> getErrorResolution() {
		return errorResolution;
	}

	public void setErrorResolution(Map<String, Object> errorResolution) {
		this.errorResolution = errorResolution;
	}
}
