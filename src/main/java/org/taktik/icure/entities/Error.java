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

package org.taktik.icure.entities;

import java.util.Map;

import org.taktik.icure.entities.base.StoredDocument;

public class Error extends StoredDocument {
	private String domain;
	private String userId;
	private Map<String,Object> errorDescription;
	private Map<String,Object> errorResolution;

	public Error() {
	}

	public Error(String domain, String userId, Map<String, Object> errorDescription) {
		this.domain = domain;
		this.userId = userId;
		this.errorDescription = errorDescription;
	}

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, Object> getErrorResolution() {
		return errorResolution;
	}

	public void setErrorResolution(Map<String, Object> errorResolution) {
		this.errorResolution = errorResolution;
	}
}
